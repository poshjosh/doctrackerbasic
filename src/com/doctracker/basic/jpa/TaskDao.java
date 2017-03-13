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
import com.doctracker.basic.pu.entities.Appointment;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Task_;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 16, 2017 3:29:18 PM
 */
public class TaskDao {

    private final JpaContext jpaContext;

    public TaskDao(JpaContext jpaContext) {
        this.jpaContext = jpaContext;
    }
    
    public Task create(Appointment author, String desc, Appointment resp, Date opened) {
        final Task task;
        task = new Task();
        task.setAuthor(author);
        task.setDescription(desc);
        task.setReponsibility(resp);
        task.setTimeopened(opened);
        return task;
    }
    
    public Task findOrCreateIfNone(Appointment author, String desc, Appointment resp, Date opened) {
        final List<Task> found = this.find(author, desc, resp, 0, 2);
        final Task task;
        if(found != null && found.size() > 1) {
            throw new javax.persistence.NonUniqueResultException();
        }else if(found != null && found.size() == 1) {
            task = found.get(0);
        }else {
            task = new Task();
            task.setAuthor(author);
            task.setDescription(desc);
            task.setReponsibility(resp);
            task.setTimeopened(opened);
        }
        return task;
    }
    
    public List<Task> find(Appointment author, String desc, Appointment resp) {
        return this.find(author, desc, resp, 0, Integer.MAX_VALUE);
    }
    
    public List<Task> find(Appointment author, String desc, 
            Appointment resp, int firstResult, int maxResults) {
        List<Task> found;
        Objects.requireNonNull(author);
        Objects.requireNonNull(desc);
        Objects.requireNonNull(resp);
        found = jpaContext.getBuilderForSelect(Task.class)
                .where(Task.class, Task_.author.getName(), author)
                .and().where(Task.class, Task_.description.getName(), desc)
                .and().where(Task.class, Task_.reponsibility.getName(), resp)
                .getResultsAndClose(firstResult, maxResults);
        return found;
    }
}
