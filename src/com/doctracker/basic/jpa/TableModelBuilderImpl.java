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

import com.bc.appcore.predicates.DateIsWithinRange;
import com.bc.jpa.search.SearchResults;
import com.doctracker.basic.pu.entities.Appointment;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Taskresponse;
import com.bc.appbase.ui.model.EntityTableModel;
import com.bc.appcore.jpa.model.ResultModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableModel;
import com.doctracker.basic.DtbApp;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 10, 2017 3:35:00 PM
 */
public class TableModelBuilderImpl 
        extends AbstractSelectionBuilder<TableModel>
        implements TableModelBuilder<SearchResults<Task>> {

    private transient static final Logger logger = Logger.getLogger(TableModelBuilderImpl.class.getName());
    
    private SearchResults<Task> source;

    @Override
    public TableModelBuilder<SearchResults<Task>> with(SearchResults<Task> source) {
        this.source = source;
        return this;
    }

    @Override
    public TableModel build() {
        
        this.checkBuildAttempted();
        
        final DtbApp app = this.getApp();
        final String query = this.getQuery();
        final Date deadlineFrom = this.getDeadlineFrom();
        final Date deadlineTo = this.getDeadlineTo();
        final Appointment appointment = this.getAppointment();
        final Boolean opened = this.getOpened();
        final Boolean closed = this.getClosed();
        final Date from = this.getFrom();
        final Date to = this.getTo();
        
        final ResultModel<Task> resultModel = app.getResultModel(Task.class, null);
        
        final Set<Task> toSave = new HashSet<>();
        
        for(int i=0; i<source.getSize(); i++) {

            final Task task = source.get(i);
            
            if(appointment == null || task.getReponsibility().equals(appointment)) {

                if(from == null && to == null && deadlineFrom == null && deadlineTo == null) {
                    toSave.add(task);
                    continue;
                }
                
                if(from != null || to != null) {
                    
                    final boolean acceptTimeopened = new DateIsWithinRange(from, to).test(task.getTimeopened());
                    if(acceptTimeopened) {
                        toSave.add(task);
                        continue;
                    }
                }
                
                if(deadlineFrom != null || deadlineTo != null) {
                    final List<Taskresponse> trList = task.getTaskresponseList();
                    final Date deadline = this.getLatestDeadline(trList);

                    final boolean acceptDeadline = new DateIsWithinRange(deadlineFrom, deadlineTo).test(deadline);

                    if(acceptDeadline) {
                        toSave.add(task);
                        continue;
                    }
                }
            }
        }
        
        final TableModel tableModel = new EntityTableModel(app, new ArrayList(toSave), resultModel);
        
        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Rows: {0}, appointment: {1}, openedFrom: {2}, opendedTo: {3}, null timeopened: {4}, deadlineFrom: {5}, deadlineTo: {6}, null deadline: {7}", 
                    new Object[]{tableModel.getRowCount(), (appointment == null ? null : appointment.getAbbreviation()), 
                        from, to, null, deadlineFrom, deadlineTo, null});
        }
        
        return tableModel;
    }
    
    private Date getLatestDeadline(List<Taskresponse> trList) {
        Date latest = null;
        for(Taskresponse tr : trList) {
            final Date deadline = tr.getDeadline();
            if(deadline == null) {
                continue;
            }
            if(latest == null) {
                latest = deadline;
            }else{
                latest = deadline.after(latest) ? deadline : latest;
            }
        }
        return latest;
    }
    
    private boolean buildAttempted;
    private void checkBuildAttempted() {
        if(this.buildAttempted) {
            throw new java.lang.IllegalStateException("build() method may only be called once");
        }
        this.buildAttempted = true;
    }
}
