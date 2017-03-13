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
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 10, 2017 5:23:51 PM
 */
public class TaskLatestDeadlineTest implements Predicate<Task> {
    
    private final Predicate<Date> dateTest;
    
    public TaskLatestDeadlineTest(Predicate<Date> dateTest) {
        this.dateTest = Objects.requireNonNull(dateTest);
    }
    
    @Override
    public boolean test(Task task) {
        final Date deadline = this.getLatestDeadline(task.getTaskresponseList());
        return deadline == null ? false : dateTest.test(deadline);
    }
    
    private Date getLatestDeadline(List<Taskresponse> trList) {
        Date latest = null;
        if(trList != null) {
            for(Taskresponse tr : trList) {
                final Date trDeadline = tr.getDeadline();
                if(trDeadline == null) {
                    continue;
                }
                if(latest == null) {
                    latest = trDeadline;
                }else{
                    latest = trDeadline.after(latest) ? trDeadline : latest;
                }
            }
        }
        return latest;
    }
}
