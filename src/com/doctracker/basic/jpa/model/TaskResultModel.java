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

package com.doctracker.basic.jpa.model;

import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Taskresponse;
import com.doctracker.basic.pu.entities.Taskresponse_;
import java.util.List;
import com.bc.appbase.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 21, 2017 10:47:02 PM
 */
public class TaskResultModel extends DtbResultModel<Task> {
    
//    private transient static final Logger logger = Logger.getLogger(TaskResultModel.class.getName());

    public TaskResultModel(App app, List<String> columnNames, int serialColumnIndex) {
        super(app, Task.class, columnNames, serialColumnIndex);
    }

    @Override
    public Object get(Task entity, int rowIndex, String columnName) {
        final Object value;
        if("Response 1".equals(columnName)) {
            final Taskresponse res = this.getTaskresponse(entity, columnName, false);
            value = res == null ? null : res.getResponse();
        }else if("Response 2".equals(columnName)) {
            final Taskresponse res = this.getTaskresponse(entity, columnName, false);
            value = res == null ? null : res.getResponse();
        }else if("Remarks".equals(columnName)) {
            final Taskresponse res = this.getRemark(entity, columnName, false);
            value = res == null ? null : res.getResponse();
        }else{
            value = super.get(entity, rowIndex, columnName);
        }
        return value;
    }
    
    @Override
    public Pair<Object, String> getEntityRelation(Task task, int rowIndex, String columnName, Object value) {
        final Object target;
        if("Response 1".equals(columnName)) {
            Taskresponse res = this.getTaskresponse(task, columnName, true);
            target = res;
            columnName = Taskresponse_.response.getName();
        }else if("Response 2".equals(columnName)) {
            final Taskresponse res = this.getTaskresponse(task, columnName, true);
            target = res;
            columnName = Taskresponse_.response.getName();
        }else if("Remarks".equals(columnName)) {
            final Taskresponse res = this.getRemark(task, columnName, true);
            target = res;
            columnName = Taskresponse_.response.getName();
        }else{
            final Pair<Object, String> pair = super.getEntityRelation(task, rowIndex, columnName, value);
            target = pair.key;
            columnName = pair.value;
        }
        return new Pair(target, columnName);
    }
}
