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

import com.bc.jpa.JpaContext;
import com.bc.jpa.dao.Criteria;
import com.bc.jpa.dao.SelectDao;
import com.doctracker.basic.pu.entities.Appointment;
import com.doctracker.basic.pu.entities.Doc;
import com.doctracker.basic.pu.entities.Doc_;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Task_;
import com.doctracker.basic.pu.entities.Taskresponse;
import com.doctracker.basic.pu.entities.Taskresponse_;
import java.util.Date;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 20, 2017 6:54:22 PM
 */
public class SelectDaoBuilderImplOld<T> extends AbstractSelectDaoBuilder<T> {
    
    public SelectDaoBuilderImplOld() { }
    
    @Override
    public SelectDao<T> build() {
        
        this.checkBuildAttempted();
        
        final JpaContext jpaContext = this.getJpaContext();
        final Class resultType = this.getResultType();
        final String query = this.getQuery();
        final Date deadlineFrom = this.getDeadlineFrom();
        final Date deadlineTo = this.getDeadlineTo();
        final Appointment appointment = this.getAppointment();
        final Boolean opened = this.getOpened();
        final Boolean closed = this.getClosed();
        final Date from = this.getFrom();
        final Date to = this.getTo();
        
        Objects.requireNonNull(resultType);
        
        final JoinDocTaskTaskresponse<T> dao = new JoinDocTaskTaskresponse(
                jpaContext.getEntityManager(resultType), resultType, null);
        
//        dao.from(Task.class);
        
        dao.distinct(true);
        
        final boolean hasQuery = query != null && !query.isEmpty();
        
        final boolean joinDoc = hasQuery; 
        final boolean joinTaskresponse = deadlineFrom != null || deadlineTo != null;

//        if(joinDoc) {
            dao.joinDocToTask();
//            dao.joinTaskToDoc();
//        }
        
//        if(joinTaskresponse) {
            dao.joinTaskToTaskresonse();
//            dao.joinTaskresponseToTask();  // Causes long 
//        }
        
        dao.descOrder(Task.class, Task_.taskid.getName());
        
        if(hasQuery) {
            dao.search(
                    Doc.class, query,
                    Doc_.subject.getName(),
                    Doc_.referencenumber.getName()
            ).search(Task.class, query, Task_.description.getName())
                    .search(Taskresponse.class, query, Taskresponse_.response.getName());
        }
        
        if(appointment != null) {
            dao.and().where(Task.class, Task_.reponsibility.getName(), Criteria.ComparisonOperator.EQUALS, appointment);
        }

        if(opened != null) {
            if(opened) {
                dao.and().where(Task.class, Task_.timeopened.getName(), Criteria.ComparisonOperator.NOT_EQUALS, null);
            }else{
                dao.and().where(Task.class, Task_.timeopened.getName(), Criteria.ComparisonOperator.EQUALS, null);
            }
        }
        if(closed != null) {
            if(closed) {
                dao.and().where(Task.class, Task_.timeclosed.getName(), Criteria.ComparisonOperator.NOT_EQUALS, null);
            }else{
                dao.and().where(Task.class, Task_.timeclosed.getName(), Criteria.ComparisonOperator.EQUALS, null);
            }
        }
        if(from != null) {
            dao.and().where(Task.class, Task_.timeopened.getName(), Criteria.ComparisonOperator.GREATER_OR_EQUALS, from);
        }        
        if(to != null) {
            dao.and().where(Task.class, Task_.timeopened.getName(), Criteria.ComparisonOperator.LESS_THAN, to);
        } 
        if(deadlineFrom != null) {
            dao.and().where(Taskresponse.class, Taskresponse_.deadline.getName(), Criteria.ComparisonOperator.GREATER_OR_EQUALS, deadlineFrom);
        }        
        if(deadlineTo != null) {
            dao.and().where(Taskresponse.class, Taskresponse_.deadline.getName(), Criteria.ComparisonOperator.LESS_THAN, deadlineTo);
        } 
        
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
