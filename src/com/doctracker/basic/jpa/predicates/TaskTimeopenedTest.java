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
import java.util.Date;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 10, 2017 5:23:07 PM
 */
public class TaskTimeopenedTest implements Predicate<Task> {
    private final Predicate<Date> dateTest;
    public TaskTimeopenedTest(Predicate<Date> dateTest) {
        this.dateTest = Objects.requireNonNull(dateTest);
    }
    @Override
    public boolean test(Task task) {
        return dateTest.test(task.getTimeopened());
    }
}
