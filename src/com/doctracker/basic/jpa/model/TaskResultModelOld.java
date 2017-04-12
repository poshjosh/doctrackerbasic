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

import com.bc.appbase.App;
import com.doctracker.basic.pu.entities.Appointment;
import com.doctracker.basic.pu.entities.Doc_;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Task_;
import com.doctracker.basic.pu.entities.Taskresponse;
import com.doctracker.basic.pu.entities.Taskresponse_;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 1, 2017 9:52:26 PM
 */
public class TaskResultModelOld extends DtbResultModel<Task> {
    
    private transient static final Logger logger = Logger.getLogger(TaskResultModelOld.class.getName());

    public TaskResultModelOld(App app, List<String> columnNames, int serialColumnIndex) {
        super(app, Task.class, columnNames, serialColumnIndex);
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        final Class value;
        final String columnName = this.getColumnName(columnIndex);
        if(columnName.equals(this.getSerialColumnName())) {
            value = Integer.class;
        }else if(Task_.taskid.getName().equals(columnName)) {
            value = Integer.class;
        }else if(Doc_.subject.getName().equals(columnName)) {
            value = String.class;
        }else if(Doc_.referencenumber.getName().equals(columnName)) {
            value = String.class;
        }else if(Doc_.datesigned.getName().equals(columnName)) {
            value = Date.class;
        }else if(Task_.reponsibility.getName().equals(columnName)) {
            value = Appointment.class;
        }else if(Task_.description.getName().equals(columnName)) {
            value = String.class;
        }else if(Task_.timeopened.getName().equals(columnName)) {
            value = Date.class;
        }else if("Response 1".equals(columnName)) {
            value = String.class;
        }else if("Response 2".equals(columnName)) {
            value = String.class;
        }else if("Remarks".equals(columnName)) {
            value = String.class;
        }else{
            value = super.getColumnClass(columnIndex);
        }
        return value;
    }

    @Override
    public Object get(Task entity, int rowIndex, String columnName) {
        final Object value;
        if(columnName.equals(this.getSerialColumnName())) {
            value = rowIndex + 1;
        }else if(Task_.taskid.getName().equals(columnName)) {
            value = entity.getTaskid();
        }else if(Doc_.subject.getName().equals(columnName)) {
            value = entity.getDoc().getSubject();
        }else if(Doc_.referencenumber.getName().equals(columnName)) {
            value = entity.getDoc().getReferencenumber();
        }else if(Doc_.datesigned.getName().equals(columnName)) {
            value = entity.getDoc().getDatesigned();
        }else if(Task_.reponsibility.getName().equals(columnName)) {
            value = entity.getReponsibility().getAbbreviation();
        }else if(Task_.description.getName().equals(columnName)) {
            value = entity.getDescription();
        }else if(Task_.timeopened.getName().equals(columnName)) {
            value = entity.getTimeopened();
        }else if("Response 1".equals(columnName)) {
            final Taskresponse res = this.getTaskresponse(entity, columnName, false);
            value = res == null ? null : res.getResponse();
        }else if("Response 2".equals(columnName)) {
            final Taskresponse res = this.getTaskresponse(entity, columnName, false);
            value = res == null ? null : res.getResponse();
        }else if("Remarks".equals(columnName)) {
            final Taskresponse res = this.getRemark(entity, columnName, false);
            value = res == null ? null : res.getResponse();
        }else{
            throw new IllegalArgumentException("Unexpected column name: "+columnName);
        }
        return value;
    }
    
    @Override
    public Pair<Object, String> getEntityRelation(Task task, int rowIndex, String columnName, Object value) {
        final Object target;
        if(columnName.equals(this.getSerialColumnName())) {
            target = null;
            columnName = null;
        }else if(Task_.taskid.getName().equals(columnName)) {
            target = task;
        }else if(Doc_.subject.getName().equals(columnName)) {
            target = task.getDoc();
        }else if(Doc_.referencenumber.getName().equals(columnName)) {
            target = task.getDoc();
        }else if(Doc_.datesigned.getName().equals(columnName)) {
            target = task.getDoc();
        }else if(Task_.reponsibility.getName().equals(columnName)) {
            target = task;
        }else if(Task_.description.getName().equals(columnName)) {
            target = task;
        }else if(Task_.timeopened.getName().equals(columnName)) {
            target = task;
        }else if("Response 1".equals(columnName)) {
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
            throw new IllegalArgumentException("Unexpected column name: "+columnName);
        }
        return new Pair(target, columnName);
    }
}
