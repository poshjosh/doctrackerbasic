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

package com.doctracker.basic.ui;

import com.doctracker.basic.ConfigNames;
import com.doctracker.basic.ui.actions.ActionCommands;
import java.util.Calendar;
import javax.swing.DefaultComboBoxModel;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 11, 2017 2:50:07 PM
 */
public class TaskResponsePanelManager implements ContainerManager<TaskResponsePanel> {

    private final App app;
    
    public TaskResponsePanelManager(App app) {
        this.app = app;
    }
    
    @Override
    public void reset(TaskResponsePanel TaskResponsePanel) {
        this.setFieldsToInitialValues(TaskResponsePanel);
    }
    
    @Override
    public void init(TaskResponsePanel taskResponsePanel) {
        
        final String [] values = app.getAppointmentValuesForComboBox();
        
        taskResponsePanel.getAuthorCombobox().setModel(new DefaultComboBoxModel<>(values));
        
        taskResponsePanel.getAddresponseButton().setActionCommand(ActionCommands.ADD_TASKRESPONSE);
        
        app.getUI().addActionListeners(
                taskResponsePanel,
                taskResponsePanel.getAddresponseButton());
        
        this.setFieldsToInitialValues(taskResponsePanel);
    }
    
    private void setFieldsToInitialValues(TaskResponsePanel taskResponsePanel) {

        taskResponsePanel.getTaskidLabel().setText(null);
        
        taskResponsePanel.getResponseTextArea().setText(null);
        
        taskResponsePanel.getAuthorCombobox().setSelectedIndex(0);
        
        final DateUIUpdater updater = app.getUI().getDateUIUpdater();
        
        final Calendar cal = app.getCalendar();
        
        final int deadlineHours = app.getConfig().getInt(ConfigNames.DEFAULT_DEADLINE_EXTENSION_HOURS, 6);
        cal.add(Calendar.HOUR_OF_DAY, deadlineHours);
        
        DateTimePanel deadlinePanel = taskResponsePanel.getDeadlinePanel();
        updater.updateField(deadlinePanel.getHoursTextfield(), cal, Calendar.HOUR_OF_DAY);
        updater.updateField(deadlinePanel.getMinutesTextfield(), cal, Calendar.MINUTE);
        
//        updater.updateField(deadlinePanel.getDayTextfield(), cal, Calendar.DAY_OF_MONTH);
        updater.updateMonth(deadlinePanel.getMonthCombobox(), cal);
        updater.updateYear(deadlinePanel.getYearCombobox(), cal);
    }
}

