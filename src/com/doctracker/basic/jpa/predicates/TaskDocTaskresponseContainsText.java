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

package com.doctracker.basic.jpa.predicates;

import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Taskresponse;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 14, 2017 7:56:03 PM
 */
public class TaskDocTaskresponseContainsText implements Predicate<Task> {

    private final String query;
    
    private final String queryLowerCase;
    
    private final boolean ignoreCase;

    public TaskDocTaskresponseContainsText(String query, boolean ignoreCase) {
        this.query = Objects.requireNonNull(query);
        this.ignoreCase = ignoreCase;
        this.queryLowerCase = query.toLowerCase();
    }

    @Override
    public boolean test(Task task) {
        if(this.contains(task.getDescription(), this.query)) {
            return true;
        }
        if(this.contains(task.getDoc().getSubject(), this.query)) {
            return true;
        }
        if(this.contains(task.getDoc().getReferencenumber(), this.query)) {
            return true;
        }
        for(Taskresponse tr : task.getTaskresponseList()) {
            if(this.contains(tr.getResponse(), this.query)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean contains(String text, String query) {
        text = this.ignoreCase && text != null ? text.toLowerCase() : text;
        query = this.ignoreCase ? this.queryLowerCase : query;
        return text != null && text.contains(query);
    }
}
