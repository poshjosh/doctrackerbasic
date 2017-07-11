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

package com.doctracker.basic.parameter;

import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.parameter.ParameterNotFoundException;
import com.bc.appcore.parameter.ParametersBuilder;
import com.doctracker.basic.pu.entities.Doc_;
import com.doctracker.basic.pu.entities.Task_;
import com.bc.appbase.ui.DateFromUIBuilder;
import com.doctracker.basic.ui.TaskPanel;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bc.appcore.AppCore;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 9, 2017 12:10:53 AM
 */
public class AddTaskParametersBuilder implements ParametersBuilder<TaskPanel> {

    private AppCore app;
    
    private TaskPanel newTaskPanel;
    
    @Override
    public ParametersBuilder<TaskPanel> context(AppCore app) {
        this.app = app;
        return this;
    }

    @Override
    public ParametersBuilder<TaskPanel> with(TaskPanel newTaskPanel) {
        this.newTaskPanel = newTaskPanel;
        return this;
    }
    
    @Override
    public Map<String, Object> build() throws ParameterException {
        
        final Logger logger = Logger.getLogger(this.getClass().getName());
        
        final Map<String, Object> params = new HashMap();
        
        final String text = newTaskPanel.getDocidLabel().getText();
        if(!this.isNullOrEmpty(text)) {
            try{
                logger.log(Level.FINER, "docid: {0}", text);
                params.put(Doc_.docid.getName(), Integer.valueOf(text));
            }catch(NumberFormatException ignored) { }
        }
        
        final String referencenumber = newTaskPanel.getReferencenumberTextfield().getText();
        if(!this.isNullOrEmpty(referencenumber)) {
            params.put(Doc_.referencenumber.getName(), referencenumber);
        }
        
        final String subject = newTaskPanel.getSubjectTextfield().getText();
        if(this.isNullOrEmpty(subject)) {
            throw new ParameterNotFoundException(Doc_.subject.getName());
        }else{
            params.put(Doc_.subject.getName(), subject);
        }
        
        final String description = newTaskPanel.getTaskTextArea().getText();
        if(this.isNullOrEmpty(description)) {
            throw new ParameterNotFoundException(Task_.description.getName());
        }else{
            params.put(Task_.description.getName(), description);
        }
        
        final Object responsibility = newTaskPanel.getResponsiblityCombobox().getSelectedItem();
        if(this.isNullOrEmpty(responsibility)) {
            throw new ParameterNotFoundException(Task_.reponsibility.getName());
        }else{
            params.put(Task_.reponsibility.getName(), responsibility);
        }
        
        final DateFromUIBuilder builder = app.getOrException(DateFromUIBuilder.class);
        
        final Calendar cal = app.getCalendar();
        
        final Date datesigned = builder.calendar(cal)
                .defaultHousrs(00)
                .hoursTextField(null)
                .defaultMinutes(00)
                .minutesTextField(null)
                .dayTextField(newTaskPanel.getDatesignedDayTextfield())
                .monthComboBox(newTaskPanel.getDatesignedMonthCombobox())
                .yearComboBox(newTaskPanel.getDatesignedYearCombobox())
                .build(null);
        if(datesigned != null) {
            params.put(Doc_.datesigned.getName(), datesigned);
        }
        
        
        final Date timeopened = builder.calendar(cal)
                .hoursTextField(newTaskPanel.getTimeopenedHoursTextfield())
                .minutesTextField(newTaskPanel.getTimeopenedMinutesTextfield())
                .dayTextField(newTaskPanel.getTimeopenedDayTextfield())
                .monthComboBox(newTaskPanel.getTimeopenedMonthCombobox())
                .yearComboBox(newTaskPanel.getTimeopenedYearCombobox())
                .build(null);
        if(timeopened != null) {
            params.put(Task_.timeopened.getName(), timeopened);
        }
        
        return params;
    }
    
    private boolean isNullOrEmpty(Object obj) {
        return obj == null || "".equals(obj);
    }
}
