/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.doctracker.basic.jpa;

import com.bc.jpa.dao.BuilderForSelect;
import com.bc.jpa.dao.BuilderForSelectImpl;
import com.bc.jpa.dao.SelectDao;
import com.doctracker.basic.pu.entities.Appointment;
import com.doctracker.basic.pu.entities.Doc;
import com.doctracker.basic.pu.entities.Doc_;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Task_;
import com.doctracker.basic.pu.entities.Taskresponse;
import com.doctracker.basic.pu.entities.Taskresponse_;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 3, 2017 11:39:11 PM
 */
public class SelectDaoBuilderImpl<T> 
        extends AbstractSelectionBuilder<SelectDao<T>> 
        implements SelectDaoBuilder<T> {
    
    private Class<T> resultType;

    public SelectDaoBuilderImpl() { }
    
    @Override
    public SelectDaoBuilder resultType(Class<T> resultType) {
        this.resultType = resultType;
        return this;
    }

    @Override
    public SelectDao<T> build() {
        
        this.checkBuildAttempted();
        
        final App app = this.getApp();
        final String query = this.getQuery();
        final Date deadlineFrom = this.getDeadlineFrom();
        final Date deadlineTo = this.getDeadlineTo();
        final Appointment appointment = this.getAppointment();
        final Boolean opened = this.getOpened();
        final Boolean closed = this.getClosed();
        final Date from = this.getFrom();
        final Date to = this.getTo();
        
        final boolean hasQuery = query != null && !query.isEmpty();
        final boolean joinDoc = hasQuery;
        final boolean joinTr = hasQuery || deadlineFrom != null || deadlineTo != null;
        
        final String q = !hasQuery ? null : '%'+query+'%';
        
        final BuilderForSelect<T> dao = new BuilderForSelectImpl(
                app.getEntityManager(), resultType);
        
        final CriteriaBuilder cb = dao.getCriteriaBuilder();
        
        final CriteriaQuery cq = dao.getCriteriaQuery();
        
        cq.distinct(true);
        
        final List<Predicate> likes = !hasQuery ? null : new ArrayList();
        
        final Root task = cq.from(Task.class); 
        if(likes != null) {
            likes.add(cb.like(task.get(Task_.description), q));
        }
        
        final Join<Task, Doc> taskDoc = !joinDoc ? null : task.join(Task_.doc); 
        if(likes != null && taskDoc != null) {
            likes.add(cb.like(taskDoc.get(Doc_.subject), q));
            likes.add(cb.like(taskDoc.get(Doc_.referencenumber), q));
        }
        
        // If you don't specify lef join here then some searches will return incorrect results
        final Join<Task, Taskresponse> taskTr = !joinTr ? null : task.join(Task_.taskresponseList, JoinType.LEFT);
        if(likes != null && taskTr != null) {
            likes.add(cb.like(taskTr.get(Taskresponse_.response), q));  
        }

        final List<Predicate> where = new ArrayList<>();
        
        if(likes != null) {
            where.add(cb.or(likes.toArray(new Predicate[0])));
        }
        
        if(appointment != null) {
            where.add(cb.equal(task.get(Task_.reponsibility), appointment));
        }
        if(opened != null) {
            if(opened) {
                where.add(cb.isNotNull(task.get(Task_.timeopened)));
            }else{
                where.add(cb.isNull(task.get(Task_.timeopened)));
            }
        }
        if(closed != null) {
            if(closed) {
                where.add(cb.isNotNull(task.get(Task_.timeclosed)));
            }else{
                where.add(cb.isNull(task.get(Task_.timeclosed)));
            }
        }
        if(from != null) {
            where.add(cb.greaterThanOrEqualTo(task.get(Task_.timeopened), from));
        }        
        if(to != null) {
            where.add(cb.lessThan(task.get(Task_.timeopened), to));
        } 
        
        if(deadlineFrom != null || deadlineTo != null) {
            where.add(cb.isNotNull(taskTr.get(Taskresponse_.deadline)));
        }
        
        if(deadlineFrom != null) {
            final Subquery<Integer> subquery = cq.subquery(Integer.class);
            final Root<Taskresponse> subqueryRoot = subquery.from(Taskresponse.class);
            subquery.select(subqueryRoot.get(Taskresponse_.taskresponseid));
            subquery.where(cb.equal(task, subqueryRoot.get(Taskresponse_.task)));
//            subquery.groupBy(subqueryRoot.get(Taskresponse_.taskresponseid)); 
            subquery.having(cb.greaterThanOrEqualTo(cb.greatest(subqueryRoot.<Date>get(Taskresponse_.deadline)), deadlineFrom));
            where.add(cb.exists(subquery));
        }        
        
        if(deadlineTo != null) {
            final Subquery<Integer> subquery = cq.subquery(Integer.class);
            final Root<Taskresponse> subqueryRoot = subquery.from(Taskresponse.class);
            subquery.select(subqueryRoot.get(Taskresponse_.taskresponseid));
            subquery.where(cb.equal(task, subqueryRoot.get(Taskresponse_.task)));
//            subquery.groupBy(subqueryRoot.get(Taskresponse_.taskresponseid)); 
            subquery.having(cb.lessThan(cb.greatest(subqueryRoot.<Date>get(Taskresponse_.deadline)), deadlineTo));
            where.add(cb.exists(subquery));
        } 

        if(!where.isEmpty()) {
            cq.where( cb.and(where.toArray(new Predicate[0])) );
        }

        cq.orderBy(cb.desc(task.get(Task_.taskid))); 

        return dao;
    }

    private boolean buildAttempted;
    private void checkBuildAttempted() {
        if(this.buildAttempted) {
            throw new java.lang.IllegalStateException("build() method may only be called once");
        }
        this.buildAttempted = true;
    }
}
