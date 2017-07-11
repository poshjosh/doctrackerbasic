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

import com.bc.appcore.actions.TaskExecutionException;
import com.bc.config.Config;
import com.bc.jpa.dao.SelectDao;
import com.bc.jpa.search.SearchResults;
import com.doctracker.basic.ConfigNames;
import com.doctracker.basic.FileNames;
import com.doctracker.basic.pu.entities.Appointment;
import com.doctracker.basic.pu.entities.Task;
import com.bc.appbase.ui.table.model.EntityTableModel;
import com.bc.appcore.jpa.model.ResultModel;
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
import com.doctracker.basic.ConfigSuffixNames;
import com.bc.appbase.ui.actions.ParamNames;
import com.doctracker.basic.jpa.SelectTaskresponseBuilder;
import com.doctracker.basic.jpa.predicates.TaskLatestDeadlineTest;
import com.doctracker.basic.jpa.predicates.TaskTimeopenedTest;
import com.bc.appcore.predicates.AcceptAll;
import com.bc.appcore.predicates.DateIsWithinRange;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import com.bc.appcore.actions.Action;
import javax.swing.JTable;
import com.doctracker.basic.DtbApp;
import com.bc.appbase.App;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.util.Util;
import com.doctracker.basic.jpa.DtbSearchContext;
import com.doctracker.basic.jpa.predicates.TaskDocTaskresponseContainsText;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 2, 2017 6:20:01 PM
 */
public class SaveReports implements Action<App,List<File>> {
    
    public static final String REPORT_PREFIX_NAME = "report";
    
    private long startTime;
    
    private static long lastStartTime;
    
    private static final AtomicInteger busyCount = new AtomicInteger();
    
    private static final AtomicInteger completedAttempts = new AtomicInteger();
    
    private static final AtomicInteger totalPeriod = new AtomicInteger();
    
    private transient static final Logger logger = Logger.getLogger(SaveReports.class.getName());
    
    @Override
    public List<File> execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        try{
            
            startTime = lastStartTime = System.currentTimeMillis();
            
            busyCount.incrementAndGet();
        
            final List<File> output = new ArrayList();

            final String targetFilename = app.getConfig().getString(ConfigNames.REPORT_FOLDER_PATH);

            logger.log(Level.FINE, "Target folder: {0}", targetFilename);

            if(!this.isNullOrEmpty(targetFilename)) {

                final Map<Path, Map<String, TableModel>> tableModels = this.getTableModels(app, params);

                logger.log(Level.FINE, "Number of reports: {0}", (tableModels==null?null:tableModels.size()));

                for(Path path : tableModels.keySet()) {

                    final Map saveTableParams = this.getParams(path, tableModels.get(path));
                    saveTableParams.put(java.awt.Font.class.getName(), app.getUIContext().getFont(JTable.class));

                    final File file = (File)app.getAction(DtbActionCommands.SAVE_TABLE_MODEL).execute(
                            app, saveTableParams);

                    logger.log(Level.FINER, "Param names: {0}", saveTableParams.keySet());

                    if(file != null) {
                        output.add(file);
                    }
                }
                
                logger.log(Level.FINE, "Saved reports: {0}", output);

                if(!output.isEmpty()) {
                    app.getAction(DtbActionCommands.REFRESH_REPORTS_FROM_BACKUP).execute(
                            app, Collections.EMPTY_MAP);
                }
                
                completedAttempts.incrementAndGet();
                
                totalPeriod.addAndGet((int)(System.currentTimeMillis() - startTime));
            }

            return output;
            
        }finally{
            
            busyCount.decrementAndGet();
        }
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
//        final List<Appointment> apptList = new ArrayList();
        apptList.add(0, null);
        
        final Map<Path, Map<String, TableModel>> output = new HashMap();
        
        final Calendar today = app.getCalendar();
//        today.set(2017, 1, 28);
//        calendar.set(2017, 2, 3);

        final Date date_today_start = this.getStartDate(today).getTime();
        final Date date_today_end = this.getEndDate(today, 1).getTime();
        
        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "From: {0}, to: {1}", new Object[]{date_today_start, date_today_end});
        }
        
        final Predicate<Date> dateWithinToday = new DateIsWithinRange(date_today_start, date_today_end);
       
        final ResultModel<Task> resultModel = app.getResultModel(Task.class, null);
        
        final String workingDir = app.getFilenames().getWorkingDir();

        final DtbSearchContext<Task> searchContext = ((DtbApp)app).getSearchContext(Task.class);
        final SearchResults searchResults = searchContext.getSearchResults();

        logger.log(Level.FINER, "All search results size {0}", searchResults.getSize());
            
        this.addTableModels(app, searchResults, resultModel, apptList, new AcceptAll(), 
                getBackupPath(workingDir, FileNames.REPORT_BACKUP_FILENAME), 
                output);
        
        this.addTableModels(app, searchResults, resultModel, apptList, new TaskTimeopenedTest(dateWithinToday), 
                getBackupPath(workingDir, FileNames.REPORT_TRACK_START_TODAY_FILE_ID+'.'+FileNames.REPORT_BACKUP_FILE_EXT), 
                output);
        
        final DateFormat dateFormat = new SimpleDateFormat("dd MMM");
        dateFormat.setCalendar(today);
        dateFormat.setTimeZone(app.getTimeZone());
        final String queryTodaySearchText = dateFormat.format(today.getTime());
        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Query on {0}", queryTodaySearchText);
        }

        final Predicate<Task> containsQueryTodyText = new TaskDocTaskresponseContainsText("Query on " + queryTodaySearchText, true);
        final Predicate<Task> deadlineWithinToday = new TaskLatestDeadlineTest(dateWithinToday);
        final Predicate<Task> queryToday = deadlineWithinToday.or(containsQueryTodyText);
        this.addTableModels(app, searchResults, resultModel, apptList, queryToday, 
                getBackupPath(workingDir, FileNames.REPORT_QUERY_TODAY_FILE_ID+'.'+FileNames.REPORT_BACKUP_FILE_EXT), 
                output);
        
        final Predicate startOrDeadlineRange = 
                new TaskTimeopenedTest(dateWithinToday).or(new TaskLatestDeadlineTest(dateWithinToday));
        this.addTableModels(app, searchResults, resultModel, apptList, startOrDeadlineRange, 
                getBackupPath(workingDir, FileNames.REPORT_TODAY_FILE_ID+'.'+FileNames.REPORT_BACKUP_FILE_EXT), 
                output);
        
        final Predicate<Date> dateLimitTest = new DateIsWithinRange(null, date_today_end);
        final Predicate startOrDeadlineLimit = 
                new TaskTimeopenedTest(dateLimitTest).or(new TaskLatestDeadlineTest(dateLimitTest));
        this.addTableModels(app, searchResults, resultModel, apptList, startOrDeadlineLimit, 
                getBackupPath(workingDir, FileNames.REPORT_ALL_OUTSTANDING_FILE_ID+'.'+FileNames.REPORT_BACKUP_FILE_EXT), 
                output);

        final Predicate<Task> noDeadlineTest = new TaskLatestDeadlineTest(new AcceptAll()).negate();
        this.addTableModels(app, searchResults, resultModel, apptList, noDeadlineTest, 
                getBackupPath(workingDir, FileNames.REPORT_NODEADLINE_FILE_ID+'.'+FileNames.REPORT_BACKUP_FILE_EXT), 
                output);

        final Map<String, String[]> input = new HashMap();
        final Config config = app.getConfig();
        for(int i=0; i<20; i++) {
            final String prefix = REPORT_PREFIX_NAME + '.' + i + '.';
            final String name = config.getString(prefix + ConfigSuffixNames.NAME, null);
            if(name == null) {
                continue;
            }
            final String [] keywords = config.getArray(prefix + ConfigSuffixNames.KEYWORDS);
            input.put(name, keywords);
            if(logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "Report name: {0}, keywords: {1}", 
                        new Object[]{name, keywords==null?null:Arrays.toString(keywords)});
            }
        }
        
        final int range_days = 2;
        final Date date_48hrs_start =  date_today_start;
        final Date date_48hrs_end = this.getEndDate(today, 2).getTime();
        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Range days: {0}, start: {1}. end: {2}", 
                    new Object[]{range_days, date_48hrs_start, date_48hrs_end});
        }
        
        for(String fname : input.keySet()) {
            
            final String [] queries = input.get(fname);
            
            this.addDeadlineTableModels(app, searchContext, resultModel, apptList, null, null, 
                    queries, getBackupPath(workingDir, fname+'.'+FileNames.REPORT_BACKUP_FILE_EXT), 
                    output);
            
            this.addDeadlineTableModels(app, searchContext, resultModel, apptList, date_today_start, date_today_end, 
                    queries, getBackupPath(workingDir, fname+"_today."+FileNames.REPORT_BACKUP_FILE_EXT), 
                    output);

            this.addDeadlineTableModels(app, searchContext, resultModel, apptList, null, date_today_end, 
                    queries, getBackupPath(workingDir, fname+"_outstanding."+FileNames.REPORT_BACKUP_FILE_EXT), 
                    output);
            
            this.addDeadlineTableModels(app, searchContext, resultModel, apptList, date_48hrs_start, date_48hrs_end, 
                    queries, getBackupPath(workingDir, fname + '_' + (TimeUnit.DAYS.toHours(range_days)) + "_hours." + FileNames.REPORT_BACKUP_FILE_EXT), 
                    output);
        }
        
        return output;
    }

    private Calendar getStartDate(Calendar offset) {
        final Calendar output = Calendar.getInstance();
        output.clear();
        output.set(offset.get(Calendar.YEAR), offset.get(Calendar.MONTH), 
                (offset.get(Calendar.DAY_OF_MONTH)));
        return output;
    }
    
    private Calendar getEndDate(Calendar offset, int days_to_add) {
        final Calendar output = Calendar.getInstance();
        output.clear();
        output.set(offset.get(Calendar.YEAR), offset.get(Calendar.MONTH), 
                (offset.get(Calendar.DAY_OF_MONTH) + days_to_add));
        return output;
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
        
        logger.log(Level.FINER, "Adding TableModels for: {0}", path);
        
        appendTo.put(path, this.getTableModels(app, sr, resultModel, apptList, test));
    }
    private void addDeadlineTableModels(
            App app, DtbSearchContext<Task> sm, final ResultModel<Task> resultModel, 
            List<Appointment> apptList, Date deadlineFrom, Date deadlineTo, String [] queries,
            Path path, final Map<Path, Map<String, TableModel>> appendTo) {
        
        logger.log(Level.FINER, "Adding TableModels for: {0}", path);
        
        appendTo.put(path, this.getDeadlineTableModels(app, sm, resultModel, apptList, deadlineFrom, deadlineTo, queries));
    }
    
    private Map<String, TableModel> getTableModels(App app, SearchResults<Task> sr, 
            final ResultModel<Task> resultModel, List<Appointment> apptList, Predicate<Task> test) {
        
        final Map<String, TableModel> output = new HashMap();
        for(Appointment appt : apptList) {
            
            final String sheetName = this.getSheetName(appt);
            
            final TableModel allModel = this.getTableModel(app, sr, resultModel, appt, test);
            
            if(logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "Appointment: {0}, sheet name: {1}, rows: {2}",
                        new Object[]{appt, sheetName, allModel.getRowCount()});
            }
                    
            output.put(sheetName, allModel);
        }
        return output;
    }
    private Map<String, TableModel> getDeadlineTableModels(
            App app, DtbSearchContext<Task> sm, ResultModel<Task> resultModel, 
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
            
            if(appt == null || task.getReponsibility().getAppointmentid().equals(appt.getAppointmentid())) {
                
                if(test.test(task)) {
                    
                    toSave.add(task);
                }
            }
        }
        
        final TableModel tableModel = new EntityTableModel(new ArrayList(toSave), resultModel);

        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "{0} results filtered to {1} for appointment: {2} using predicate: {3}",
                    new Object[]{searchResults.getSize(), tableModel.getRowCount(), 
                        appt == null ? null : appt.getAbbreviation(), test.getClass().getSimpleName()});
        }
        
        return tableModel;
    }
    private TableModel getDeadlineTableModel(
            App app, DtbSearchContext<Task> sm, ResultModel<Task> resultModel, 
            Appointment appt, Date deadlineFrom, Date deadlineTo, String[] queries) {
        final List<Task> results = this.getDeadlineResults(
                app, sm, appt, deadlineFrom, deadlineTo, queries);
        return new EntityTableModel(results, resultModel);
    }
    private List<Task> getDeadlineResults(App app, DtbSearchContext<Task> sm, Appointment appt, 
            Date deadlineFrom, Date deadlineTo, String[] queries) {
        final Set<Task> results = new HashSet();
        for(String query : queries) {
            final SelectDao<Task> dao = (SelectDao<Task>)new SelectTaskresponseBuilder()
                    .resultType(Task.class)
                    .jpaContext(app.getJpaContext())
                    .closed(false)
                    .deadlineFrom(deadlineFrom).deadlineTo(deadlineTo)
                    .who(appt).textToFind(query).build();
            
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
    
    public ActionStatus getLastStatus() {
        return new ActionStatusImpl(lastStartTime, busyCount, completedAttempts, totalPeriod);
    }
}
/**
 * 
        input.put("feedback", new String[]{"brief ", "report back", "feedback", "feed back"});
        input.put("track_closely", new String[]{"closely track", "track closely", 
            "watch closely", "closely watch", "closely follow", "follow closely", 
            "closely monitor", "monitor closely"});
        input.put("track_very_closely", new String[]{"very closely", "very closely"});
 * 
 */