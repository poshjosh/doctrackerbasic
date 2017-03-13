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

import com.bc.jpa.dao.BuilderForSelectImpl;
import static com.bc.jpa.dao.Criteria.AND;
import com.bc.jpa.dao.DatabaseFormat;
import com.doctracker.basic.pu.entities.Doc;
import com.doctracker.basic.pu.entities.Doc_;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Task_;
import com.doctracker.basic.pu.entities.Taskresponse;
import com.doctracker.basic.pu.entities.Taskresponse_;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 20, 2017 6:40:48 PM
 */
public class JoinDocTaskTaskresponse<T> extends BuilderForSelectImpl<T> {

    public JoinDocTaskTaskresponse(EntityManager em, Class<T> resultType) {
        this(em, resultType, null);
    }

    public JoinDocTaskTaskresponse(EntityManager em, Class<T> resultType, DatabaseFormat databaseFormat) {
        super(em, resultType, databaseFormat);
    }
    
    public void joinTaskToDoc() {
        final CriteriaBuilder cb = this.getCriteriaBuilder();
        this.join(Task.class, Task_.doc.getName(), Doc.class);
        final Predicate taskidEqualsDocid = cb.equal(getJoin().get(Task_.taskid), getJoin().get(Doc_.docid)); 
        this.buildPredicate(cb, AND, taskidEqualsDocid);
    }
    
    public void joinTaskToTaskresonse() {
        final CriteriaBuilder cb = this.getCriteriaBuilder();
        this.join(Task.class, Task_.taskresponseList.getName(), Taskresponse.class);
        final Predicate taskidEqualsTaskresponseid = cb.equal(getJoin().get(Task_.taskid), getJoin().get(Taskresponse_.taskresponseid)); 
        this.buildPredicate(cb, AND, taskidEqualsTaskresponseid);
    }
    
    public void joinDocToTask() {
        final CriteriaBuilder cb = this.getCriteriaBuilder();
        this.join(Doc.class, Doc_.taskList.getName(), Task.class); 
        final Predicate docidEqualsTaskid = cb.equal(getJoin().get(Doc_.docid), getJoin().get(Task_.taskid)); 
        this.buildPredicate(cb, AND, docidEqualsTaskid);
    }

    public void joinTaskresponseToTask() {
        final CriteriaBuilder cb = this.getCriteriaBuilder();
        this.join(Taskresponse.class, Taskresponse_.task.getName(), Task.class);
        final Predicate taskidEqualsTaskresponseid = cb.equal(getJoin().get(Taskresponse_.taskresponseid), getJoin().get(Task_.taskid)); 
        this.buildPredicate(cb, AND, taskidEqualsTaskresponseid);
    }
}
