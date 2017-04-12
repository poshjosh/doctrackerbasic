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
import com.doctracker.basic.pu.entities.Task_;
import com.doctracker.basic.pu.entities.Taskresponse_;
import com.bc.appbase.ui.DateFromUIBuilder;
import com.doctracker.basic.ui.TaskResponsePanel;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.bc.appcore.AppCore;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 11, 2017 1:34:17 PM
 */
public class AddResponseParametersBuilder implements ParametersBuilder<TaskResponsePanel> {

    private AppCore app;
    
    private TaskResponsePanel taskResponsePanel;
    
    @Override
    public ParametersBuilder<TaskResponsePanel> context(AppCore app) {
        this.app = app;
        return this;
    }

    @Override
    public ParametersBuilder<TaskResponsePanel> with(TaskResponsePanel newTaskResponsePanel) {
        this.taskResponsePanel = newTaskResponsePanel;
        return this;
    }
    
    @Override
    public Map<String, Object> build() throws ParameterException {
        
        final Map<String, Object> params = new HashMap();
        
        final String taskidStr = taskResponsePanel.getTaskidLabel().getText();
        params.put(Task_.taskid.getName(), Integer.parseInt(taskidStr));
        
        final String response = taskResponsePanel.getResponseTextArea().getText();
        if(this.isNullOrEmpty(response)) {
            throw new ParameterNotFoundException(Taskresponse_.response.getName());
        }else{
            params.put(Taskresponse_.response.getName(), response);
        }
        
        final Object author = taskResponsePanel.getAuthorCombobox().getSelectedItem();
        if(this.isNullOrEmpty(author)) {
            throw new ParameterNotFoundException(Taskresponse_.author.getName());
        }else{
            params.put(Taskresponse_.author.getName(), author);
        }
        
        final Calendar cal = app.getCalendar();
        
        final DateFromUIBuilder builder = app.get(DateFromUIBuilder.class);
        final Date deadline = builder.calendar(cal)
                .ui(taskResponsePanel.getDeadlinePanel())
                .build(null);
        if(deadline != null) {
            params.put(Taskresponse_.deadline.getName(), deadline);
        }
                
        return params;
    }
    
    private boolean isNullOrEmpty(Object text) {
        return text == null || "".equals(text);
    }
}

