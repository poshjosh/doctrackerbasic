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

package com.doctracker.basic.ui.model;

import com.bc.config.Config;
import com.doctracker.basic.ConfigPrefixNames;
import com.doctracker.basic.pu.entities.Doc_;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Task_;
import com.doctracker.basic.pu.entities.Taskresponse;
import com.doctracker.basic.pu.entities.Taskresponse_;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import com.doctracker.basic.App;
import com.doctracker.basic.pu.entities.Appointment;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 21, 2017 10:47:02 PM
 */
public class TaskResultModel implements ResultModel<Task> {
    
    private transient static final Logger logger = Logger.getLogger(TaskResultModel.class.getName());

    private final App app;
    
    private final List<String> columnNames;
    
    private final List<String> columnLabels;
    
    private final int serialColumnIndex;
    
    private final int responseColumnCount;
    
    public TaskResultModel(App app, List<String> columnNames) {
        this.app = app;
        this.columnNames = Collections.unmodifiableList(columnNames);
        this.columnLabels = this.getColumnLabels(app, columnNames);
        this.serialColumnIndex = TaskResultModel.this.getSerialColumnIndex(columnNames);
        this.responseColumnCount = 2;
    }
    
    private int getSerialColumnIndex(List<String> colNames) {
        final String serialColumnName = this.getSerialColumnName();
        return serialColumnName == null ? - 1 : colNames.indexOf(serialColumnName);
    }
    
    private List<String> getColumnLabels(App app, List<String> colNames) {
        final List<String> labels = new ArrayList<>();
        final Config config = app.getConfig();
        for(String colName : colNames) {
            final String label = config.getString(ConfigPrefixNames.COLUMNLABEL + '.' + Task.class.getSimpleName() + '.' + colName);
            labels.add(label == null ? colName : label);
        }
        return labels;
    }

    @Override
    public Object get(Task entity, int rowIndex, int columnIndex) {
        final String columnName = this.getColumnName(columnIndex);
        final Object value = this.get(entity, rowIndex, columnName);
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
    public Object set(Task task, int rowIndex, int columnIndex, Object value) {
        final String columnName = this.getColumnName(columnIndex);
        final Object oldValue = this.set(task, rowIndex, columnName, value);
        return oldValue;
    }

    @Override
    public Object set(Task task, int rowIndex, String columnName, Object value) {
        
        logger.log(Level.FINE, "Value type: {0}, value: {1}",
                new Object[]{value==null?null:value.getClass().getName(), value});
        
        final Object entity;
        final Object oldValue;
        if(columnName.equals(this.getSerialColumnName())) {
            entity = null;
            oldValue = null;
        }else if(Task_.taskid.getName().equals(columnName)) {
            entity = task;
            oldValue = task.getTaskid();
        }else if(Doc_.subject.getName().equals(columnName)) {
            entity = task.getDoc();
            oldValue = task.getDoc().getSubject();
        }else if(Doc_.referencenumber.getName().equals(columnName)) {
            entity = task.getDoc();
            oldValue = task.getDoc().getReferencenumber();
        }else if(Doc_.datesigned.getName().equals(columnName)) {
            entity = task.getDoc();
            oldValue = task.getDoc().getDatesigned();
        }else if(Task_.reponsibility.getName().equals(columnName)) {
            entity = task;
            oldValue = task.getReponsibility().getAbbreviation();
        }else if(Task_.description.getName().equals(columnName)) {
            entity = task;
            oldValue = task.getDescription();
        }else if(Task_.timeopened.getName().equals(columnName)) {
            entity = task;
            oldValue = task.getTimeopened();
        }else if("Response 1".equals(columnName)) {
            Taskresponse res = this.getTaskresponse(task, columnName, true);
            entity = res;
            columnName = Taskresponse_.response.getName();
            oldValue = res.getResponse();
        }else if("Response 2".equals(columnName)) {
            final Taskresponse res = this.getTaskresponse(task, columnName, true);
            entity = res;
            columnName = Taskresponse_.response.getName();
            oldValue = res.getResponse();
        }else if("Remarks".equals(columnName)) {
            final Taskresponse res = this.getRemark(task, columnName, true);
            entity = res;
            oldValue = res.getResponse();
        }else{
            throw new IllegalArgumentException("Unexpected column name: "+columnName);
        }
        final boolean update;
        if(entity != null) {
            if(value == null && oldValue == null) {
                update = false;
            }else if(value != null && oldValue != null) {
                update = !value.equals(oldValue);
            }else{
                update = true;
            }
        }else{
            update = false;
        }
        if(update) {
//System.out.println(columnName+" = "+value+", for entity: "+entity);   
            final int answer = JOptionPane.showConfirmDialog(
                    app.getUI().getMainFrame(), "Update: "+columnName+'?', "Update?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(answer == JOptionPane.YES_OPTION) {
                final Class entityClass = entity.getClass();
                try{
                    this.set(app, entityClass, entity, columnName, value);
                    this.updateDatabase(app, entityClass, entity);
                }catch(RuntimeException e) {
                    final String msg = "Error updating "+columnName;
                    logger.log(Level.WARNING, msg, e);
                    app.getUI().showErrorMessage(e, msg);
                }
            }
        }
        return oldValue;
    }
    
    public Taskresponse getRemark(Task task, String columnName, boolean createIfNone) {
        return this.getTaskresponse(task, task.getAuthor(), columnName, createIfNone);
    }
    
    public Taskresponse getTaskresponse(Task task, String columnName, boolean createIfNone) {
        return this.getTaskresponse(task, task.getReponsibility(), columnName, createIfNone);
    }
    
    public Taskresponse getTaskresponse(Task task, Appointment appt, String columnName, boolean createIfNone) {
        int index = -1;
        switch(columnName) {
            case "Response 1": 
                index = 1; break;
            case "Response 2": 
            case "Remarks":
                index = 0; break;
            default: throw new UnsupportedOperationException("Unexpected response column name: "+columnName); 
        }
        Taskresponse res = this.getFromEnd(appt, task.getTaskresponseList(), columnName, index);
        if(res == null && createIfNone) {
            res = new Taskresponse();
            res.setAuthor(app.getUser().getAppointment());
            res.setTask(task);
        }
        return res;
    }
    
    public void set(App app, Class entityClass, Object entity, String columnName, Object columnValue) {
        app.getJpaContext().getEntityUpdater(entityClass).setValue(entity, columnName, columnValue);
        app.getSlaveUpdates().addMerge(entity);
    }
    
    public void updateDatabase(App app, Class entityClass, Object entity) {
        app.getJpaContext().getDao(entityClass).begin().mergeAndClose(entity);
        app.getSlaveUpdates().addMerge(entity);
        app.updateOutput();
    }
    
    private Taskresponse getFromEnd(Appointment appt, List<Taskresponse> list, String columnName, int index) {
        Taskresponse output;
        if(list == null || list.isEmpty() || list.size() < index) {
            output = null;
        }else{
            List<Taskresponse> filtered = new ArrayList(list.size());
            for(Taskresponse tr : list) {
                if(appt == null || tr.getAuthor().equals(appt)) {
                    filtered.add(tr);
                }
            }
            list = null;
            if(filtered.size() <= index) {
                output = null;
            }else{
                Collections.reverse(filtered);
                output = filtered.get(index);
            }
        }
        return output;
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
            value = String.class;
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
            throw new IllegalArgumentException("Unexpected column name: "+columnName);
        }
        return value;
    }
  
    private String getSerialColumnName() {
        return this.serialColumnIndex == -1 ? null : this.columnNames.get(this.serialColumnIndex);
    }

    @Override
    public int getSerialColumnIndex() {
        return this.serialColumnIndex;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames.get(columnIndex);
    }

    @Override
    public String getColumnLabel(int columnIndex) {
        return columnLabels.get(columnIndex);
    }

    @Override
    public Set<String> getColumnNames() {
        return new LinkedHashSet(columnNames);
    }

    @Override
    public Set<String> getColumnLabels() {
        return new LinkedHashSet(columnLabels);
    }
}
