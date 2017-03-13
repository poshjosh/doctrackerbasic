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

import com.doctracker.basic.pu.entities.Task_;
import com.doctracker.basic.ui.DateFromUIBuilder;
import com.doctracker.basic.ui.SearchPanel;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 10, 2017 2:48:38 PM
 */
public class SearchParametersBuilder implements ParametersBuilder<SearchPanel> {
    
    public static final String CLOSED_TASKS = "closedtasks";
    public static final String DEADLINE_FROM = "deadlineFrom";
    public static final String DEADLINE_TO = "deadlineTo";
    

    private App app;
    
    private SearchPanel searchPanel;
    
    @Override
    public ParametersBuilder<SearchPanel> app(App app) {
        this.app = app;
        return this;
    }

    @Override
    public ParametersBuilder<SearchPanel> with(SearchPanel SearchPanel) {
        this.searchPanel = SearchPanel;
        return this;
    }
    
    @Override
    public Map<String, Object> build() throws ParameterException {
        
//        final Logger logger = Logger.getLogger(this.getClass().getName());
        
        final Map<String, Object> params = new HashMap();
        
        Object responsibility = searchPanel.getResponsiblityCombobox().getSelectedItem();
        if(!this.isNullOrEmpty(responsibility)) {
            params.put(Task_.reponsibility.getName(), responsibility);
        }
        
        final String text = searchPanel.getSearchTextfield().getText();
        
        params.put(CLOSED_TASKS, searchPanel.getClosedTasksCheckBox().isSelected());
        
        if(!this.isNullOrEmpty(text)) {
            params.put("query", text);
        }
        
        final DateFromUIBuilder builder = app.getUI().getDateFromUIBuilder();
        
        final Calendar cal = app.getCalendar();
        
        final Date from = builder.calendar(cal)
                .defaultMinutes(00)
                .defaultHousrs(00)
                .ui(searchPanel.getFromDateTimePanel())
                .build(null);
        if(from != null) {
            params.put("from", from);
        }
        
        final Date to = builder.calendar(cal)
                .defaultMinutes(00)
                .defaultHousrs(00)
                .ui(searchPanel.getToDateTimePanel())
                .build(null);
        if(to != null) {
            params.put("to", to);
        }
        
        final Date deadlineFrom = builder.calendar(cal)
                .defaultMinutes(00)
                .defaultHousrs(00)
                .ui(searchPanel.getFromDeadlineDateTimePanel())
                .build(null);
        if(deadlineFrom != null) {
            params.put(DEADLINE_FROM, deadlineFrom);
        }
        
        final Date deadlineTo = builder.calendar(cal)
                .defaultMinutes(00)
                .defaultHousrs(00)
                .ui(searchPanel.getToDeadlineDateTimePanel())
                .build(null);
        if(deadlineTo != null) {
            params.put(DEADLINE_TO, deadlineTo);
        }
        
        return params;
    }

    private boolean isNullOrEmpty(Object text) {
        return text == null || "".equals(text);
    }
}