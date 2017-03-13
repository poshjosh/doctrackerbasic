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
import java.awt.event.ActionListener;
import java.util.Calendar;
import javax.swing.DefaultComboBoxModel;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 10, 2017 2:22:05 PM
 */
public class SearchPanelManager implements ContainerManager<SearchPanel> {

    private final App app;
    
    public SearchPanelManager(App app) {
        this.app = app;
    }
    
    @Override
    public void init(SearchPanel searchPanel) {
        
        final String [] values = app.getAppointmentValuesForComboBox();
        
        searchPanel.getResponsiblityCombobox().setModel(new DefaultComboBoxModel<>(values));
        
        final ActionListener actionListener = app.getUI().getActionListener(searchPanel, ActionCommands.SEARCH_AND_DISPLAY_RESULTS_UI);
        searchPanel.getSearchTextfield().setActionCommand(ActionCommands.SEARCH_AND_DISPLAY_RESULTS_UI);
        searchPanel.getSearchTextfield().addActionListener(actionListener);
        searchPanel.getSearchButton().setActionCommand(ActionCommands.SEARCH_AND_DISPLAY_RESULTS_UI);
        searchPanel.getSearchButton().addActionListener(actionListener);
        
        this.setToDefaults(searchPanel);
    }

    @Override
    public void reset(final SearchPanel container) {
        
        container.getResponsiblityCombobox().setSelectedIndex(0);
        
        container.getSearchTextfield().setText(null);
        
        container.getClosedTasksCheckBox().setSelected(false);
        
        DateTimePanel dateTimePanel = container.getFromDateTimePanel();
        dateTimePanel.getHoursTextfield().setText(null);
        dateTimePanel.getMinutesTextfield().setText(null);
        dateTimePanel.getDayTextfield().setText(null);
        
        this.setToDefaults(container);
    }
    
    private void setToDefaults(final SearchPanel container) {
        
        final DateUIUpdater updater = app.getUI().getDateUIUpdater();
        
        final Calendar cal = app.getCalendar();
        
        final DateTimePanel fromDatePanel = container.getFromDateTimePanel();
        
        updater.updateMonth(fromDatePanel.getMonthCombobox(), cal);
        updater.updateYear(fromDatePanel.getYearCombobox(), cal);
        
        updater.updateField(container.getToDateTimePanel().getHoursTextfield(), cal, Calendar.HOUR_OF_DAY);
        updater.updateField(container.getToDateTimePanel().getMinutesTextfield(), cal, Calendar.MINUTE);
        
//        updater.updateField(container.getToDateTimePanel().getDayTextfield(), cal, Calendar.DAY_OF_MONTH);
        updater.updateMonth(container.getToDateTimePanel().getMonthCombobox(), cal);
        updater.updateYear(container.getToDateTimePanel().getYearCombobox(), cal);

/////////////////

        updater.updateMonth(container.getFromDeadlineDateTimePanel().getMonthCombobox(), cal);
        updater.updateYear(container.getFromDeadlineDateTimePanel().getYearCombobox(), cal);
        
        updater.updateField(container.getToDeadlineDateTimePanel().getHoursTextfield(), cal, Calendar.HOUR_OF_DAY);
        updater.updateField(container.getToDeadlineDateTimePanel().getMinutesTextfield(), cal, Calendar.MINUTE);
        
//        updater.updateField(container.getToDeadlineDateTimePanel().getDayTextfield(), cal, Calendar.DAY_OF_MONTH);
        updater.updateMonth(container.getToDeadlineDateTimePanel().getMonthCombobox(), cal);
        updater.updateYear(container.getToDeadlineDateTimePanel().getYearCombobox(), cal);
    }
}
