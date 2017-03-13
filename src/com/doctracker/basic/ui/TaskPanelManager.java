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

import com.doctracker.basic.ui.actions.ActionCommands;
import com.doctracker.basic.ConfigNames;
import java.util.Calendar;
import javax.swing.DefaultComboBoxModel;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 7, 2017 11:14:08 PM
 */
public class TaskPanelManager implements ContainerManager<TaskPanel> {

    private final App app;
    
    public TaskPanelManager(App app) {
        this.app = app;
    }
    
    @Override
    public void reset(TaskPanel taskPanel) {
        this.setFieldsToInitialValues(taskPanel);
    }
    
    @Override
    public void init(TaskPanel taskPanel) {
        
        final String [] values = app.getAppointmentValuesForComboBox();
        
        taskPanel.getResponsiblityCombobox().setModel(new DefaultComboBoxModel<>(values));
        
        taskPanel.getAddTaskAndDocButton().setActionCommand(ActionCommands.ADD_TASK_AND_DOC);
        taskPanel.getAddTaskToDocButton().setActionCommand(ActionCommands.ADD_TASK_TO_DOC);
        taskPanel.getCleartaskButton().setActionCommand(ActionCommands.CLEAR_TASK);
        
        app.getUI().addActionListeners(
                taskPanel,
                taskPanel.getAddTaskAndDocButton(), 
                taskPanel.getAddTaskToDocButton(),
                taskPanel.getCleartaskButton());
        
        this.setFieldsToInitialValues(taskPanel);
    }
    
    private void setFieldsToInitialValues(TaskPanel taskPanel) {
        
        taskPanel.getDocidLabel().setText("DOC ID");
        taskPanel.getReferencenumberTextfield().setText(null);
        taskPanel.getSubjectTextfield().setText(null);
        taskPanel.getTaskTextArea().setText(null);
        taskPanel.getDeadlineExpectationTextfield().setText(null);
        
        taskPanel.getResponsiblityCombobox().setSelectedIndex(0);
        
        final DateUIUpdater updater = app.getUI().getDateUIUpdater();
        
        final Calendar cal = app.getCalendar();
        
//        updater.updateField(taskPanel.getDatesignedDayTextfield(), cal, Calendar.DAY_OF_MONTH);
        taskPanel.getDatesignedDayTextfield().setText(null);
        updater.updateMonth(taskPanel.getDatesignedMonthCombobox(), cal);
        updater.updateYear(taskPanel.getDatesignedYearCombobox(), cal);
        
        updater.updateField(taskPanel.getTimeopenedHoursTextfield(), cal, Calendar.HOUR_OF_DAY);
        updater.updateField(taskPanel.getTimeopenedMinutesTextfield(), cal, Calendar.MINUTE);
        
//        updater.updateField(taskPanel.getTimeopenedDayTextfield(), cal, Calendar.DAY_OF_MONTH);
        updater.updateMonth(taskPanel.getTimeopenedMonthCombobox(), cal);
        updater.updateYear(taskPanel.getTimeopenedYearCombobox(), cal);
        
        final int deadlineHours = app.getConfig().getInt(ConfigNames.DEFAULT_DEADLINE_HOURS, 24);
        cal.add(Calendar.HOUR_OF_DAY, deadlineHours);
        
        updater.updateField(taskPanel.getDeadlineHoursTextfield(), cal, Calendar.HOUR_OF_DAY);
        updater.updateField(taskPanel.getDeadlineMinutesTextfield(), cal, Calendar.MINUTE);
        
//        updater.updateField(taskPanel.getDeadlineDayTextfield(), cal, Calendar.DAY_OF_MONTH);
        updater.updateMonth(taskPanel.getDeadlineMonthCombobox(), cal);
        updater.updateYear(taskPanel.getDeadlineYearCombobox(), cal);
    }
}
