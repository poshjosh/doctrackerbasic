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

import com.doctracker.basic.jpa.SlaveUpdatesImpl;
import com.doctracker.basic.jpa.SlaveUpdates;
import com.doctracker.basic.util.DateTimeFormat;
import com.doctracker.basic.util.RawTextHandler;
import com.doctracker.basic.util.TextHandler;
import com.doctracker.basic.jpa.SearchManager;
import com.bc.config.Config;
import com.bc.config.ConfigService;
import com.bc.jpa.JpaContext;
import com.bc.jpa.dao.Criteria;
import com.bc.jpa.dao.Dao;
import com.bc.jpa.dao.DaoImpl;
import com.doctracker.basic.html.HtmlBuilder;
import com.doctracker.basic.jpa.predicates.IsPersistenceCommunicationsLinkFailure;
import com.doctracker.basic.parameter.AddAppointmentParametersBuilder;
import com.doctracker.basic.parameter.AddResponseParametersBuilder;
import com.doctracker.basic.parameter.AddTaskParametersBuilder;
import com.doctracker.basic.parameter.AddUnitParametersBuilder;
import com.doctracker.basic.parameter.ParametersBuilder;
import com.doctracker.basic.parameter.SearchParametersBuilder;
import com.doctracker.basic.parameter.SelectedTasksParametersBuilder;
import com.doctracker.basic.parameter.TableParameterBuilder;
import com.doctracker.basic.pu.entities.Appointment;
import com.doctracker.basic.pu.entities.Appointment_;
import com.doctracker.basic.pu.entities.Doc;
import com.doctracker.basic.pu.entities.Doc_;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Task_;
import com.doctracker.basic.pu.entities.Unit;
import com.doctracker.basic.jpa.JpaSync;
import com.doctracker.basic.jpa.JpaSyncImpl;
import com.doctracker.basic.jpa.RemoteUpdaterImpl;
import com.doctracker.basic.jpa.SearchManagerImpl;
import com.doctracker.basic.ui.AppointmentPanel;
import com.doctracker.basic.ui.DateFromUIBuilder;
import com.doctracker.basic.ui.DateFromUIBuilderImpl;
import com.doctracker.basic.ui.DateUIUpdater;
import com.doctracker.basic.ui.DateUIUpdaterImpl;
import com.doctracker.basic.ui.DialogManager;
import com.doctracker.basic.ui.DialogManagerImpl;
import com.doctracker.basic.ui.MainFrame;
import com.doctracker.basic.ui.TaskPanel;
import com.doctracker.basic.ui.SearchPanel;
import com.doctracker.basic.ui.SearchResultsPanel;
import com.doctracker.basic.ui.TaskFrame;
import com.doctracker.basic.ui.TaskResponsePanel;
import com.doctracker.basic.ui.UI;
import com.doctracker.basic.ui.UIImpl;
import com.doctracker.basic.ui.UnitPanel;
import com.doctracker.basic.ui.actions.Action;
import com.doctracker.basic.ui.actions.ActionCommands;
import com.doctracker.basic.ui.actions.TaskExecutionException;
import com.doctracker.basic.ui.model.ResultModel;
import com.doctracker.basic.ui.model.TaskResultModel;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.swing.ImageIcon;
import javax.swing.JTable;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 7, 2017 11:26:00 PM
 */
public class AppImpl implements App {
    
    private transient static final Logger logger = Logger.getLogger(AppImpl.class.getName());
    
    private final Path workingDir;
    
    private final JpaContext jpaContext;
    
    private final ConfigService configService;
    
    private final Config config;
    
    private final UI ui;
    
    private final Appointment CAS;
    
    private final Map<String, Object> attributes;
    
    private final DataOutputService updateOutputService;
    
    private final SlaveUpdates slaveUpdates;
    
    private final JpaSync jpaSync;
    
    public AppImpl(Path workingDir, ConfigService configService, Config config, JpaContext jpaContext) {
        this.workingDir = Objects.requireNonNull(workingDir);
        this.jpaContext = Objects.requireNonNull(jpaContext);
        this.configService = Objects.requireNonNull(configService);
        this.config = Objects.requireNonNull(config);
        this.CAS = jpaContext.getDao(Appointment.class).find(Appointment.class, 1);
        this.attributes = new HashMap<>(); 
        
        this.updateOutputService = new DataOutputService("Service_for_writing_excel_data_to_file");
        
        this.slaveUpdates = new SlaveUpdatesImpl(this, 
                        new RemoteUpdaterImpl(jpaContext),
                        new IsPersistenceCommunicationsLinkFailure());
        
        this.jpaSync = new JpaSyncImpl(this.jpaContext, 
                        new RemoteUpdaterImpl(jpaContext), 20, 
                        new IsPersistenceCommunicationsLinkFailure());
        
        this.ui = AppImpl.this.createUI();
        if(ui != null) {
            this.ui.getContainerManager(ui.getMainFrame()).init(ui.getMainFrame());
            final TaskPanel taskPanel = ui.getTaskFrame().getTaskPanel();
            this.ui.getContainerManager(taskPanel).init(taskPanel);
        }
    }
    
    protected UI createUI() {
        final MainFrame mainFrame = new MainFrame();
        final TaskFrame taskFrame = new TaskFrame();
        final DialogManager dialogManager = new DialogManagerImpl(this);
        final DateUIUpdater dateUIUpdater = new DateUIUpdaterImpl();
        final DateFromUIBuilder dateFromUIBuilder = new DateFromUIBuilderImpl();
        final URL iconURL = App.class.getResource("naflogo.jpg");
        final ImageIcon imageIcon = new ImageIcon(iconURL, "NAF Logo");
        final UI output = new UIImpl(
                this, imageIcon,
                mainFrame, taskFrame, dialogManager,
                dateUIUpdater, dateFromUIBuilder
        );
        return output;
    }

    @Override
    public void shutdown() {
        this.attributes.clear();
        this.slaveUpdates.requestStop();
        if(this.jpaContext.isOpen()) {
            this.jpaContext.close();
        }
    }

    @Override
    public JpaSync getJpaSync() {
        return this.jpaSync;
    }
    
    @Override
    public SlaveUpdates getSlaveUpdates() {
        return this.slaveUpdates;
    }

    @Override
    public String[] getAppointmentValuesForComboBox() {
        final List<Appointment> list = 
                this.getDao().forSelect(Appointment.class).getResultsAndClose();
        final String [] names = new String[1 + list.size()];
        names[0] = null;
        for(int i = 0; i < names.length - 1; i++) {
            names[i + 1] = list.get(i).getAppointment();
        }
        return names;
    }

    @Override
    public String[] getUnitValuesForComboBox() {
        final List<Unit> list = 
                this.getDao().forSelect(Unit.class).getResultsAndClose();
        final String [] names = new String[1 + list.size()];
        names[0] = null;
        for(int i = 0; i < names.length - 1; i++) {
            names[i + 1] = list.get(i).getUnit();
        }
        return names;
    }
    
    @Override
    public List<Appointment> getBranchChiefs() {
        final List<Appointment> branchChiefs = 
                this.getJpaContext().getBuilderForSelect(Appointment.class)
                .where(Appointment.class, Appointment_.appointmentid.getName(), Criteria.GT, 1)
                .and().where(Appointment.class, Appointment_.appointmentid.getName(), Criteria.LT, 11)
                .getResultsAndClose();
        return branchChiefs;
    }

    @Override
    public void updateOutput() {
        final List<Appointment> branchChiefs = this.getBranchChiefs();
        this.updateOutput(branchChiefs);
    }
    
    @Override
    public void updateOutput(List<Appointment> appointmentList) {

        final Callable<List<File>> updateOutputTask = new Callable() {
            @Override
            public List<File> call() {
                try{
                    return (List<File>)AppImpl.this.getAction(ActionCommands.SAVE_OUTPUT).execute(
                            AppImpl.this, Collections.singletonMap(Appointment.class.getName()+"List", appointmentList));
                }catch(TaskExecutionException | RuntimeException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Unexpected exception", e);
                    return null;
                }
            }
        };

        this.updateOutputService.submit(updateOutputTask);
    }
    
    @Override
    public Path getWorkingDir() {
        return workingDir;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public User getUser() {
        return new UserImpl(this, CAS, "admin", false);
    }

    @Override
    public <T> SearchManager<T> getSearchManager(Class<T> entityType) {
        ResultModel<T> resultModel = this.getResultModel(entityType, null);
        return new SearchManagerImpl<>(this, Objects.requireNonNull(resultModel));
    }

    @Override
    public <T> ResultModel<T> getResultModel(Class<T> type, ResultModel<T> outputIfNone) {
        ResultModel resultModel;
        if(type == Task.class) {
            resultModel = new TaskResultModel(this, 
                Arrays.asList(
                        this.getConfig().getString(ConfigNames.SERIAL_COLUMNNAME), 
//                        Task_.taskid.getName(), 
                        Doc_.subject.getName(), Doc_.referencenumber.getName(),
                        Doc_.datesigned.getName(), Task_.reponsibility.getName(),
                        Task_.description.getName(), Task_.timeopened.getName(),
                        "Response 1", "Response 2", "Remarks"
                )
            );
        }else{
            resultModel = outputIfNone;
        }
        return resultModel;
    }

    @Override
    public <T> HtmlBuilder<T> getHtmlBuilder(Class<T> entityType) {
        final String className = HtmlBuilder.class.getPackage().getName() + '.' + entityType.getSimpleName() + "HtmlBuilder";
        return (HtmlBuilder<T>)this.dynamicallyCreateAppObject(className);
    }

    @Override
    public final UI getUI() {
        return ui;
    }

    @Override
    public <T> ParametersBuilder<T> getParametersBuilder(T source, String actionCommand) {
        
        final ParametersBuilder builder;
        if(source instanceof TaskPanel && 
                (ActionCommands.ADD_TASK_AND_DOC.equals(actionCommand) ||
                ActionCommands.ADD_TASK_TO_DOC.equals(actionCommand))) {
            builder = new AddTaskParametersBuilder();
        }else if(source instanceof TaskResponsePanel && 
                ActionCommands.ADD_TASKRESPONSE.equals(actionCommand)) {
            builder = new AddResponseParametersBuilder();
        }else if(source instanceof SearchPanel && ActionCommands.SEARCH_AND_DISPLAY_RESULTS_UI.equals(actionCommand)) {    
            builder = new SearchParametersBuilder();
        }else if(source instanceof JTable && 
                (ActionCommands.SAVE_TABLE_AS.equals(actionCommand) || ActionCommands.PRINT.equals(actionCommand) ||
                ActionCommands.NEXT_RESULT.equals(actionCommand) || ActionCommands.PREVIOUS_RESULT.equals(actionCommand)) ||
                ActionCommands.FIRST_RESULT.equals(actionCommand) || ActionCommands.LAST_RESULT.equals(actionCommand)) {    
            builder = new TableParameterBuilder();
        }else if(source instanceof SearchResultsPanel && ActionCommands.DISPLAY_TASKEDITORPANE.equals(actionCommand)) {    
            builder = new SelectedTasksParametersBuilder();
        }else if(source instanceof SearchResultsPanel && ActionCommands.CLOSE_TASK.equals(actionCommand)) {    
            builder = new SelectedTasksParametersBuilder();
        }else if(source instanceof SearchResultsPanel && ActionCommands.DELETE_TASK.equals(actionCommand)) {    
            builder = new SelectedTasksParametersBuilder();
        }else if(source instanceof SearchResultsPanel && 
                (ActionCommands.DISPLAY_ADD_RESPONSE_UI.equals(actionCommand) ||
                ActionCommands.DISPLAY_ADD_REMARK_UI.equals(actionCommand))) {    
            builder = new SelectedTasksParametersBuilder();
        }else if(source instanceof AppointmentPanel && ActionCommands.ADD_APPOINTMENT.equals(actionCommand)) {
            builder = new AddAppointmentParametersBuilder();
        }else if(source instanceof UnitPanel && ActionCommands.ADD_UNIT.equals(actionCommand)) {
            builder = new AddUnitParametersBuilder();
        }else {
            builder = ParametersBuilder.NO_OP;
        }
        
        builder.app(this).with(source);

        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Container: {0}, actionCommand: {1}, ParametersBuilder: {2}", 
                    new Object[]{source.getClass().getSimpleName(), actionCommand, builder.getClass().getSimpleName()});
        }
        
        return builder;
    }

    @Override
    public Action getAction(String actionCommand) {
        try{
            final Class aClass = Class.forName(actionCommand);
            final Action action = (Action)aClass.getConstructor().newInstance();
            Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Created action: {0}", action);
            return action;
        }catch(ClassNotFoundException | NoSuchMethodException | SecurityException | 
                InstantiationException | IllegalAccessException | 
                IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Object dynamicallyCreateAppObject(String className) {
        try{
            final Class aClass = Class.forName(className);
            return aClass.getConstructor(App.class).newInstance(this);
        }catch(ClassNotFoundException | NoSuchMethodException | SecurityException | 
                InstantiationException | IllegalAccessException | 
                IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public Calendar getCalendar() {
        return Calendar.getInstance(this.getTimeZone(), this.getLocale());
    }

    @Override
    public EntityManager getEntityManager() {
        final Class anyClassInDatabase = Doc.class;
        return this.jpaContext.getEntityManager(anyClassInDatabase);
    }

    @Override
    public Dao getDao() {
        return new DaoImpl(this.getEntityManager());
    }

    @Override
    public JpaContext getJpaContext() {
        return this.jpaContext;
    }

    @Override
    public ConfigService getConfigService() {
        return this.configService;
    }

    @Override
    public Config getConfig() {
        return this.config;
    }

    @Override
    public DateFormat getDateTimeFormat() {
        return new DateTimeFormat(this);
    }

    @Override
    public DateFormat getDateFormat() {
        return new com.doctracker.basic.util.DateFormat(this);
    }
    
    @Override
    public TimeZone getTimeZone() {
        return TimeZone.getDefault();
    }

    @Override
    public Locale getLocale() {
        return Locale.getDefault();
    }

    @Override
    public TextHandler getTextHandler() {
        return new RawTextHandler();
    }
}
