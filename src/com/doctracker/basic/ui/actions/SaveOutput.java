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

package com.doctracker.basic.ui.actions;

import com.bc.jpa.dao.SelectDao;
import com.bc.jpa.search.SearchResults;
import com.doctracker.basic.ConfigNames;
import com.doctracker.basic.io.FileNames;
import com.doctracker.basic.pu.entities.Appointment;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.jpa.SearchManager;
import com.doctracker.basic.ui.model.EntityTableModel;
import com.doctracker.basic.ui.model.ResultModel;
import com.doctracker.basic.util.Util;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableModel;
import com.doctracker.basic.App;
import com.doctracker.basic.ParamNames;
import com.doctracker.basic.jpa.predicates.TaskLatestDeadlineTest;
import com.doctracker.basic.jpa.predicates.TaskTimeopenedTest;
import com.doctracker.basic.predicates.AcceptAll;
import com.doctracker.basic.predicates.DateRangeTest;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 2, 2017 6:20:01 PM
 */
public class SaveOutput implements Action<List<File>> {
    
    private transient static final Logger logger = Logger.getLogger(SaveOutput.class.getName());
    
    @Override
    public List<File> execute(App app, Map<String, Object> params) throws TaskExecutionException {

        final List<File> output = new ArrayList();
            
        final String targetFilename = app.getConfig().getString(ConfigNames.REPORT_FOLDER_PATH);
        
        logger.log(Level.FINE, "Target folder: {0}", targetFilename);
        
        if(!this.isNullOrEmpty(targetFilename)) {
            
            final Map<Path, Map<String, TableModel>> tableModels = this.getTableModels(app, params);
            
            for(Path path : tableModels.keySet()) {
                
                final Map saveTableParams = this.getParams(path, tableModels.get(path));
                
                final File file = (File)app.getAction(ActionCommands.SAVE_TABLE_MODEL).execute(
                        app, saveTableParams);
                
                logger.log(Level.FINE, "Param names: {0}", saveTableParams.keySet());
                
                if(file != null) {
                    output.add(file);
                }
            }
            
            if(!output.isEmpty()) {
                app.getAction(ActionCommands.REFRESH_OUTPUT_FROM_BACKUP).execute(
                        app, Collections.EMPTY_MAP);
            }
        }
       
        return output;
    }
    
    private Map<String, Object> getParams(Path path, Map<String, TableModel> data) {
        
        final Map<String, Object> output = new HashMap(4, 1.0f);

        output.put(java.io.File.class.getName(), path.toFile());

        output.put(ParamNames.APPEND, Boolean.FALSE);

        output.put(ParamNames.DATA, data);
        
        return output;
    } 
    
    private Map<Path, Map<String, TableModel>> getTableModels(App app, Map<String, Object> params) {
        
        final List<Appointment> apptList = new ArrayList(
                (List<Appointment>)params.get(Appointment.class.getName()+"List"));
        apptList.add(0, null);
        
        final Map<Path, Map<String, TableModel>> output = new HashMap();
        
        final Calendar cal = app.getCalendar();
        
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 
                (cal.get(Calendar.DAY_OF_MONTH) - 1), 23, 59, 0);
        final Date date_start = cal.getTime();
        
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 
                (cal.get(Calendar.DAY_OF_MONTH) + 1), 00, 01, 0); 
        final Date date_end = cal.getTime();
        
        final Predicate<Date> dateRangeTest = new DateRangeTest(date_start, date_end);
       
        final DateFormat dateFormat = app.getDateFormat();
        
        final ResultModel<Task> resultModel = app.getResultModel(Task.class, null);
        
        final String workingDir = app.getWorkingDir().toString();

        final SearchManager<Task> sm = app.getSearchManager(Task.class);
        final SelectDao<Task> dao = sm.getSelectDaoBuilder(Task.class).closed(false).build();
        final SearchResults searchResults = sm.getSearchResults(dao);

        logger.log(Level.FINE, "Search results size {0}", searchResults.getSize());
            
        this.addTableModels(app, searchResults, resultModel, apptList, new AcceptAll(), 
                getBackupPath(workingDir, FileNames.REPORT_BACKUP_FILENAME), 
                output);
        
        this.addTableModels(app, searchResults, resultModel, apptList, new TaskTimeopenedTest(dateRangeTest), 
                getBackupPath(workingDir, FileNames.REPORT_TRACK_START_TODAY_FILE_ID+'.'+FileNames.REPORT_BACKUP_FILE_EXT), 
                output);
        
        this.addTableModels(app, searchResults, resultModel, apptList, new TaskLatestDeadlineTest(dateRangeTest), 
                getBackupPath(workingDir, FileNames.REPORT_QUERY_TODAY_FILE_ID+'.'+FileNames.REPORT_BACKUP_FILE_EXT), 
                output);

        final Predicate startOrDeadlineRange = 
                new TaskTimeopenedTest(dateRangeTest).or(new TaskLatestDeadlineTest(dateRangeTest));
        this.addTableModels(app, searchResults, resultModel, apptList, startOrDeadlineRange, 
                getBackupPath(workingDir, FileNames.REPORT_TODAY_FILE_ID+'.'+FileNames.REPORT_BACKUP_FILE_EXT), 
                output);
        
        final Predicate<Date> dateLimitTest = new DateRangeTest(null, date_end);
        final Predicate startOrDeadlineLimit = 
                new TaskTimeopenedTest(dateLimitTest).or(new TaskLatestDeadlineTest(dateLimitTest));
        this.addTableModels(app, searchResults, resultModel, apptList, startOrDeadlineLimit, 
                getBackupPath(workingDir, FileNames.REPORT_ALL_OUTSTANDING_FILE_ID+'.'+FileNames.REPORT_BACKUP_FILE_EXT), 
                output);

        final Predicate<Task> noDeadlineTest = new TaskLatestDeadlineTest(new AcceptAll()).negate();
        this.addTableModels(app, searchResults, resultModel, apptList, noDeadlineTest, 
                getBackupPath(workingDir, FileNames.REPORT_NODEADLINE_FILE_ID+'.'+FileNames.REPORT_BACKUP_FILE_EXT), 
                output);
        
        final int range_days = 2;
        final Date [] date_range2 = this.getDateRange(cal, 2);
        
        Map<String, String[]> input = new HashMap();
        input.put("feedback", new String[]{"brief ", "report back", "feedback", "feed back"});
        input.put("track_closely", new String[]{"closely track", "track closely", "watch closely", "closely watch"});
        input.put("track_very_closely", new String[]{"very closely", "very closely"});
        for(String fname : input.keySet()) {
            
            final String [] queries = input.get(fname);
            
            this.addDeadlineTableModels(app, sm, resultModel, apptList, null, null, 
                    queries, getBackupPath(workingDir, fname+'.'+FileNames.REPORT_BACKUP_FILE_EXT), 
                    output);
            
            this.addDeadlineTableModels(app, sm, resultModel, apptList, date_start, date_end, 
                    queries, getBackupPath(workingDir, fname+"_today."+FileNames.REPORT_BACKUP_FILE_EXT), 
                    output);

            this.addDeadlineTableModels(app, sm, resultModel, apptList, null, date_end, 
                    queries, getBackupPath(workingDir, fname+"_outstanding."+FileNames.REPORT_BACKUP_FILE_EXT), 
                    output);
            
            this.addDeadlineTableModels(app, sm, resultModel, apptList, date_range2[0], date_range2[1], 
                    queries, getBackupPath(workingDir, fname + '_' + (TimeUnit.DAYS.toHours(range_days)) + "_hours." + FileNames.REPORT_BACKUP_FILE_EXT), 
                    output);
        }
        
        return output;
    }
    
    private Date [] getDateRange(Calendar cal, int range_days) {
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 
                (cal.get(Calendar.DAY_OF_MONTH) - 1), 23, 59, 0);
        final Date start = cal.getTime();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 
                (cal.get(Calendar.DAY_OF_MONTH) + range_days), 00, 01, 0);
        final Date end = cal.getTime();
        return new Date[]{start, end};
    }
    
    private Path getBackupPath(String workingDir, String name) {
        return Paths.get(workingDir, FileNames.REPORT_BACKUP_DIR, name);
    }
    
    private String getPath(Path path, DateFormat dateFormat, Date deadlineFrom, Date deadlineTo) {
        final String suffix = this.getSuffix(dateFormat, deadlineFrom, deadlineTo);
        return Util.constructFilename(path.toString(), suffix);
    }
    
    private String getSuffix(DateFormat dateFormat, Date deadlineFrom, Date deadlineTo) {
        StringBuilder b = new StringBuilder();
        if(deadlineFrom != null) {
            b.append(this.getId(dateFormat, deadlineFrom));
        }
        if(deadlineFrom != null || deadlineTo != null) {
            b.append("_to_");
        }
        if(deadlineTo != null) {
            b.append(getId(dateFormat, deadlineTo));
        }
        return b.toString();
    }
    
    private String getId(DateFormat dateFormat, Date date) {
        final String dateStr = dateFormat.format(date);
        return dateStr.replaceAll(":", "").replaceAll("\\s", "_");
    }
    
    private void addTableModels(
            App app, SearchResults<Task> sr, final ResultModel<Task> resultModel, 
            List<Appointment> apptList, Predicate<Task> test,
            Path path, final Map<Path, Map<String, TableModel>> appendTo) {
        
        appendTo.put(path, this.getTableModels(app, sr, resultModel, apptList, test));
    }
    private void addDeadlineTableModels(
            App app, SearchManager<Task> sm, final ResultModel<Task> resultModel, 
            List<Appointment> apptList, Date deadlineFrom, Date deadlineTo, String [] queries,
            Path path, final Map<Path, Map<String, TableModel>> appendTo) {
        
        appendTo.put(path, this.getDeadlineTableModels(app, sm, resultModel, apptList, deadlineFrom, deadlineTo, queries));
    }
    
    private Map<String, TableModel> getTableModels(App app, SearchResults<Task> sr, 
            final ResultModel<Task> resultModel, List<Appointment> apptList, Predicate<Task> test) {
        final Map<String, TableModel> output = new HashMap();
        for(Appointment appt : apptList) {
            final String sheetName = this.getSheetName(appt);
            final TableModel allModel = this.getTableModel(app, sr, resultModel, appt, test);
            output.put(sheetName, allModel);
        }
        return output;
    }
    private Map<String, TableModel> getDeadlineTableModels(
            App app, SearchManager<Task> sm, ResultModel<Task> resultModel, 
            List<Appointment> apptList, Date deadlineFrom, Date deadlineTo, String[] queries) {
        final Map<String, TableModel> output = new HashMap();
        for(Appointment appt : apptList) {
            final String sheetName = this.getSheetName(appt);
            final TableModel allModel = this.getDeadlineTableModel(
                    app, sm, resultModel, appt, deadlineFrom, deadlineTo, queries);
            output.put(sheetName, allModel);
        }
        return output;
    }
    
    private TableModel getTableModel(App app, SearchResults<Task> searchResults, 
            final ResultModel<Task> resultModel, Appointment appt, Predicate<Task> test) {
        
        final Set<Task> toSave = new HashSet<>();
        
        for(int i=0; i<searchResults.getSize(); i++) {

            final Task task = searchResults.get(i);
            
            if(appt == null || task.getReponsibility().equals(appt)) {
                
                if(test.test(task)) {
                    toSave.add(task);
                }
            }
        }
        
        final TableModel tableModel = new EntityTableModel(app, new ArrayList(toSave), resultModel);

        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "{0} results filtered to {1} for appointment: {2} using predicate: {3}",
                    new Object[]{searchResults.getSize(), tableModel.getRowCount(), 
                        appt == null ? null : appt.getAbbreviation(), test.getClass().getSimpleName()});
        }
        
        return tableModel;
    }
    private TableModel getDeadlineTableModel(
            App app, SearchManager<Task> sm, ResultModel<Task> resultModel, 
            Appointment appt, Date deadlineFrom, Date deadlineTo, String[] queries) {
        final List<Task> results = this.getDeadlineResults(
                sm, appt, deadlineFrom, deadlineTo, queries);
        return new EntityTableModel(app, results, resultModel);
    }
    private List<Task> getDeadlineResults(SearchManager<Task> sm, Appointment appt, 
            Date deadlineFrom, Date deadlineTo, String[] queries) {
        final Set<Task> results = new HashSet();
        for(String query : queries) {
            
            final SelectDao<Task> dao = sm.getSelectDaoBuilder(Task.class)
                    .closed(false)
                    .deadlineFrom(deadlineFrom).deadlineTo(deadlineTo)
                    .who(appt).query(query).build();
            
            final List<Task> queryResults = dao.getResultsAndClose();
            results.addAll(queryResults);
        }
        return new ArrayList(results);
    }
    
    private String getSheetName(Appointment appt) {
        String output;
        if(appt == null) {
            output = "ALL";
        }else{
            output = appt.getAbbreviation();
        }
        return output;
    }
    
    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
