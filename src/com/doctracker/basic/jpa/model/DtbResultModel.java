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
import com.bc.appcore.typeprovider.TypeProvider;
import com.bc.appcore.jpa.model.ResultModelImpl;
import com.bc.appcore.predicates.AcceptAll;
import com.doctracker.basic.DtbApp;
import com.doctracker.basic.jpa.predicates.AppointmentTest;
import com.bc.appcore.jpa.predicates.MasterPersistenceUnitTest;
import com.doctracker.basic.pu.entities.Appointment;
import com.doctracker.basic.pu.entities.Doc;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Taskresponse;
import com.doctracker.basic.pu.entities.Taskresponse_;
import com.doctracker.basic.pu.entities.Unit;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 8, 2017 1:17:28 PM
 */
public class DtbResultModel<T> extends ResultModelImpl<T> {
    
    private transient static final Logger logger = Logger.getLogger(DtbResultModel.class.getName());

    public DtbResultModel(App app, Class<T> coreEntityType, List<String> columnNames, int serialColumnIndex) {
        super(app, coreEntityType, columnNames, serialColumnIndex, app.getOrException(TypeProvider.class), new MasterPersistenceUnitTest());
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        final Class value;
        final String columnName = this.getColumnName(columnIndex);
        if(columnName.equals(this.getSerialColumnName())) {
            value = Integer.class;
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
    public Object get(T entity, int rowIndex, String columnName) {
        final Object value;
        if(entity instanceof Task) {
            final Task task = (Task)entity;
            if("Response 1".equals(columnName)) {
                final Taskresponse res = this.getTaskresponse(task, columnName, false);
                value = res == null ? null : res.getResponse();
            }else if("Response 2".equals(columnName)) {
                final Taskresponse res = this.getTaskresponse(task, columnName, false);
                value = res == null ? null : res.getResponse();
            }else if("Remarks".equals(columnName)) {
                final Taskresponse res = this.getRemark(task, columnName, false);
                value = res == null ? null : res.getResponse();
            }else{
                value = super.get(entity, rowIndex, columnName);
            }
        }else{
            value = super.get(entity, rowIndex, columnName);
        }
        return value;
    }
    
    @Override
    public Pair<Object, String> getEntityRelation(T entity, int rowIndex, String columnName, Object value) {
        final Object target;
        if(entity instanceof Task) {
            final Task task = (Task)entity;
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
                final Pair<Object, String> pair = super.getEntityRelation(entity, rowIndex, columnName, value);
                target = pair.key;
                columnName = pair.value;
            }
        }else{
            final Pair<Object, String> pair = super.getEntityRelation(entity, rowIndex, columnName, value);
            target = pair.key;
            columnName = pair.value;
        }
        return new Pair(target, columnName);
    }
    
    @Override
    public void update(Object entity, String entityColumn, Object target, String targetColumn, Object targetValue) {

        final int response = JOptionPane.showConfirmDialog(
                this.getApp().getUIContext().getMainFrame(), "Update: "+targetColumn+'?', 
                "Update?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if(response == JOptionPane.YES_OPTION) {
            try{

                super.update(entity, entityColumn, target, targetColumn, targetValue);
                    
                this.getApp().updateReports(true);
                
            }catch(RuntimeException e) {
                final String msg = "Error updating "+targetColumn;
                logger.log(Level.WARNING, msg, e);
                this.getApp().getUIContext().showErrorMessage(e, msg);
            }
        }
    }
    
    @Override
    public String getUpdateActionId(Object entity, String columnName, Object columnValue) {
        final String output;
        if(entity instanceof Taskresponse && 
                Taskresponse_.response.getName().equals(columnName) && 
                columnValue == null) {
            output = REMOVE;
        }else{
            output = super.getUpdateActionId(entity, columnName, columnValue);
        }
        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Action ID: {0}, entity: {1}, update: {2} = {3}", new Object[]{output, entity, columnName, columnValue});
        }
        return output;
    }
    
    @Override
    public void update(Object entity, String entityColumn, Object target, 
            String targetColumn, Object targetValue, String actionId) {
        
        if(target instanceof Doc && 
                "subject".equals(targetColumn) &&
                targetValue == null) {
            
            targetValue = "";
        }
        
        super.update(entity, entityColumn, target, targetColumn, targetValue, actionId);
    }
    
    public Taskresponse getRemark(Task task, String columnName, boolean createIfNone) {
        return this.getTaskresponse(task, task.getAuthor(), columnName, createIfNone);
    }
    
    public Taskresponse getTaskresponse(Task task, String columnName, boolean createIfNone) {
        return this.getTaskresponse(task, task.getReponsibility(), columnName, createIfNone);
    }
    
    public Taskresponse getTaskresponse(Task task, Appointment appt, String columnName, boolean createIfNone) {
        final int pos = this.getPos(columnName);
        final List<Taskresponse> list = this.filter(task.getTaskresponseList(), appt==null?new AcceptAll():new AppointmentTest(appt));
        Taskresponse res = this.getFromEnd(list, columnName, pos, 2);
        if(res == null && createIfNone) {
            res = new Taskresponse();
            res.setAuthor(appt);
            res.setTask(task);
        }
        return res;
    }
    
    @Override
    public int getPos(String columnName) {
        int pos = -1;
        switch(columnName) {
            case "Response 1": 
            case "Remarks":
                pos = 0; break;
            case "Response 2": 
                pos = 1; break;
            default: throw new UnsupportedOperationException("Unexpected response column name: "+columnName); 
        }
        return pos;
    }
    
    @Override
    public DtbApp getApp() {
        return (DtbApp)super.getApp(); 
    }
}
