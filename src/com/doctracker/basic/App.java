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

package com.doctracker.basic;

import com.doctracker.basic.jpa.SlaveUpdates;
import com.doctracker.basic.util.TextHandler;
import com.doctracker.basic.jpa.SearchManager;
import com.bc.config.Config;
import com.bc.config.ConfigService;
import com.bc.jpa.JpaContext;
import com.bc.jpa.dao.Dao;
import com.doctracker.basic.html.HtmlBuilder;
import com.doctracker.basic.parameter.ParametersBuilder;
import com.doctracker.basic.pu.entities.Appointment;
import com.doctracker.basic.jpa.JpaSync;
import com.doctracker.basic.ui.UI;
import com.doctracker.basic.ui.actions.Action;
import com.doctracker.basic.ui.model.ResultModel;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.persistence.EntityManager;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 7, 2017 11:10:58 PM
 */
public interface App {
    
    enum Months{Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec}
    
    void shutdown();
    
    SlaveUpdates getSlaveUpdates();
    
    JpaSync getJpaSync();
    
    void updateOutput();
    
    void updateOutput(List<Appointment> appointmentList);
    
    Path getWorkingDir();
    
    Map<String, Object> getAttributes();
    
    <T> ResultModel<T> getResultModel(Class<T> entityType, ResultModel<T> outputIfNone);
    
    ConfigService getConfigService();
    
    User getUser();
    
    <T> HtmlBuilder<T> getHtmlBuilder(Class<T> entityType);
    
    <T> SearchManager<T> getSearchManager(Class<T> resultType);
    
    UI getUI();
    
    Action getAction(String actionCommand);
    
    <T> ParametersBuilder<T> getParametersBuilder(T source, String actionCommand);
    
    String [] getAppointmentValuesForComboBox();
    
    String [] getUnitValuesForComboBox();
    
    List<Appointment> getBranchChiefs();
    
    Config getConfig();
    
    EntityManager getEntityManager();
    
    Dao getDao();
    
    JpaContext getJpaContext();
    
    DateFormat getDateTimeFormat();
    
    DateFormat getDateFormat();
    
    Calendar getCalendar();
    
    TimeZone getTimeZone();
    
    Locale getLocale();
    
    TextHandler getTextHandler();
}
