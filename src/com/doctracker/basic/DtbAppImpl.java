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

import com.bc.appbase.AbstractApp;
import com.bc.appbase.parameter.SelectedRecordsParametersBuilder;
import com.bc.appbase.ui.SearchResultsPanel;
import com.bc.appbase.ui.UIContext;
import com.bc.appbase.ui.actions.ActionCommands;
import com.bc.appcore.actions.TaskExecutionException;
import com.bc.appcore.html.HtmlBuilder;
import com.bc.appcore.jpa.model.ResultModel;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.parameter.ParametersBuilder;
import com.bc.appcore.util.ExpirableCache;
import com.bc.config.Config;
import com.bc.config.ConfigService;
import com.bc.jpa.JpaContext;
import com.bc.jpa.dao.Criteria;
import com.bc.jpa.sync.JpaSync;
import com.bc.jpa.sync.SlaveUpdates;
import com.bc.util.Util;
import com.doctracker.basic.html.TaskHtmlBuilder;
import com.doctracker.basic.html.TaskresponseHtmlBuilder;
import com.doctracker.basic.jpa.DtbSearchContext;
import com.doctracker.basic.jpa.DtbSearchContextImpl;
import com.doctracker.basic.jpa.model.DtbResultModel;
import com.doctracker.basic.parameter.AddAppointmentParametersBuilder;
import com.doctracker.basic.parameter.AddResponseParametersBuilder;
import com.doctracker.basic.parameter.AddTaskParametersBuilder;
import com.doctracker.basic.parameter.AddUnitParametersBuilder;
import com.doctracker.basic.parameter.SearchParametersBuilder;
import com.doctracker.basic.pu.entities.Appointment;
import com.doctracker.basic.pu.entities.Appointment_;
import com.doctracker.basic.pu.entities.Doc_;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Task_;
import com.doctracker.basic.pu.entities.Taskresponse;
import com.doctracker.basic.pu.entities.Unit;
import com.doctracker.basic.ui.AppointmentPanel;
import com.doctracker.basic.ui.DtbMainFrame;
import com.doctracker.basic.ui.DtbUIContext;
import com.doctracker.basic.ui.DtbUIContextImpl;
import com.doctracker.basic.ui.SearchPanel;
import com.doctracker.basic.ui.TaskFrame;
import com.doctracker.basic.ui.TaskPanel;
import com.doctracker.basic.ui.TaskResponsePanel;
import com.doctracker.basic.ui.UnitPanel;
import com.doctracker.basic.util.DateTimeFormat;
import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.swing.ImageIcon;
import com.doctracker.basic.ui.actions.DtbActionCommands;
import java.util.concurrent.TimeUnit;
import com.bc.appcore.Filenames;
import com.bc.appcore.ObjectFactory;
import com.bc.appcore.jpa.predicates.MasterPersistenceUnitTest;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 7, 2017 11:26:00 PM
 */
public class DtbAppImpl extends AbstractApp implements DtbApp {
    
    private transient static final Logger logger = Logger.getLogger(DtbAppImpl.class.getName());
    
    private final Appointment CAS;
    
    private final ExecutorService updateOutputService;
    
    private ResultModel<Task> taskResultModel;
    
    public DtbAppImpl(
            Filenames filenames, ConfigService configService, Config config, Properties settingsConfig, JpaContext jpaContext,
            ExecutorService dataOutputService, SlaveUpdates slaveUpdates, JpaSync jpaSync, ExpirableCache expirableCache) {
        
        super(filenames, configService, config, settingsConfig, jpaContext, slaveUpdates, jpaSync, expirableCache);
        
        this.updateOutputService = Objects.requireNonNull(dataOutputService);
        
        this.CAS = jpaContext.getDao(Appointment.class).find(Appointment.class, 1);
    }
    
    @Override
    public void init() {
        
        super.init();
        
        this.getUIContext().getTaskFrame().getTaskPanel().init(this);
        
        this.initDefaultResultModel();
    }
    
    @Override
    protected ObjectFactory createObjectFactory() {
        return new DtbObjectFactory(this);
    }
    
    @Override
    protected UIContext createUIContext() {
        final DtbMainFrame mainFrame = new DtbMainFrame();
        final TaskFrame taskFrame = new TaskFrame();
        final URL iconURL = DtbApp.class.getResource("naflogo.jpg");
        final ImageIcon imageIcon = new ImageIcon(iconURL, "NAF Logo");
        return new DtbUIContextImpl(this, imageIcon, mainFrame, taskFrame);
    }
    
    private void initDefaultResultModel() {
        final int serialColumnIndex = 0;
        final List<String> columnNames = Arrays.asList(
                    this.getConfig().getString(ConfigNames.SERIAL_COLUMNNAME), 
//                        Task_.taskid.getName(), 
                    Doc_.subject.getName(), Doc_.referencenumber.getName(),
                    Doc_.datesigned.getName(), Task_.reponsibility.getName(),
                    Task_.description.getName(), Task_.timeopened.getName(),
                    "Response 1", "Response 2", "Remarks"
            );
        this.taskResultModel = new DtbResultModel(
                this, Task.class, columnNames, serialColumnIndex    
        );
    }

    @Override
    public void shutdown() {
        try{
            super.shutdown();
        }finally{
            Util.shutdownAndAwaitTermination(this.updateOutputService, 1, TimeUnit.SECONDS);
        }
    }

    @Override
    public Predicate<String> getPersistenceUnitNameTest() {
        return new MasterPersistenceUnitTest();
    }

    @Override
    public EntityManager getEntityManager(Class resultType) {
        return this.getJpaContext().getEntityManager(resultType);
    }

    @Override
    public <T> HtmlBuilder<T> getHtmlBuilder(Class<T> entityType) {
        final HtmlBuilder output;
        if(entityType == Task.class) {
            output = new TaskHtmlBuilder(this);
        }else if (entityType == Taskresponse.class) {
            output = new TaskresponseHtmlBuilder(this);
        }else{
            throw new UnsupportedOperationException("Not supported yet.");
        }
        return output;
    }

    @Override
    public DtbUIContext getUIContext() {
        return (DtbUIContext)super.getUIContext();
    }
    
    @Override
    public String[] getAppointmentValuesForComboBox() {
        
        final List<Appointment> list = this.getJpaContext()
                .getBuilderForSelect(Appointment.class)
                .from(Appointment.class)
                .getResultsAndClose();
        final String [] names = new String[1 + list.size()];
        names[0] = null;
        for(int i = 0; i < names.length - 1; i++) {
            names[i + 1] = list.get(i).getAppointment();
        }
        return names;
    }

    @Override
    public String[] getUnitValuesForComboBox() {
        final List<Unit> list = this.getJpaContext()
                .getBuilderForSelect(Unit.class)
                .getResultsAndClose();
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
    public void updateReports(boolean refreshDisplay) {
        final List<Appointment> branchChiefs = this.getBranchChiefs();
        this.updateReports(branchChiefs, refreshDisplay);
    }
    
    @Override
    public void updateReports(List<Appointment> appointmentList, boolean refreshDisplay) {
        
        final Callable<List<File>> updateOutputTask = new Callable() {
            @Override
            public List<File> call() {
                try{
                    
                    final DtbApp app = DtbAppImpl.this;
                    
                    if(refreshDisplay) {
                        app.getAction(ActionCommands.REFRESH_ALL_RESULTS).execute(app, Collections.EMPTY_MAP);
                    }
                    
                    return (List<File>)app.getAction(DtbActionCommands.SAVE_REPORTS).execute(
                            app, Collections.singletonMap(Appointment.class.getName()+"List", appointmentList));
                    
                }catch(ParameterException | TaskExecutionException | RuntimeException e) {
                    logger.log(Level.WARNING, "Unexpected exception", e);
                    return null;
                }
            }
        };

        this.updateOutputService.submit(updateOutputTask);
    }
    
    @Override
    public User getUser() {
        return new UserImpl(CAS, "admin", false);
    }

    @Override
    public <T> DtbSearchContext<T> getSearchContext(Class<T> entityType) {
        final ResultModel<T> resultModel = this.getResultModel(entityType, null);
        return new DtbSearchContextImpl<>(this, Objects.requireNonNull(resultModel));
    }

    @Override
    public <T> ResultModel<T> getResultModel(Class<T> type, ResultModel<T> outputIfNone) {
        final ResultModel resultModel;
        if(type == Task.class) {
            resultModel = this.getTaskResultModel();
        }else{
            resultModel = this.getTaskResultModel();
        }
        return resultModel;
    }

    public ResultModel<Task> getTaskResultModel() {
        return this.taskResultModel;
    }
    
    @Override
    public <T> ParametersBuilder<T> getParametersBuilder(T source, String actionCommand) {
        
        final ParametersBuilder builder;
        
        if(source instanceof TaskPanel && 
                (DtbActionCommands.ADD_TASK_AND_DOC.equals(actionCommand) ||
                DtbActionCommands.ADD_TASK_TO_DOC.equals(actionCommand))) {
            builder = new AddTaskParametersBuilder();
        }else if(source instanceof TaskResponsePanel && 
                DtbActionCommands.ADD_TASKRESPONSE.equals(actionCommand)) {
            builder = new AddResponseParametersBuilder();
        }else if(source instanceof SearchPanel && DtbActionCommands.SEARCH_AND_DISPLAY_RESULTS_UI.equals(actionCommand)) {    
            builder = new SearchParametersBuilder();
        }else if(source instanceof SearchResultsPanel && DtbActionCommands.DISPLAY_TASKEDITORPANE.equals(actionCommand)) {    
            builder = new SelectedRecordsParametersBuilder();
        }else if(source instanceof SearchResultsPanel && 
                (DtbActionCommands.CLOSE_TASK.equals(actionCommand) || DtbActionCommands.OPEN_TASK.equals(actionCommand))) {    
            builder = new SelectedRecordsParametersBuilder();
        }else if(source instanceof SearchResultsPanel && DtbActionCommands.DELETE_TASK.equals(actionCommand)) {    
            builder = new SelectedRecordsParametersBuilder();
        }else if(source instanceof SearchResultsPanel && 
                (DtbActionCommands.DISPLAY_ADD_RESPONSE_UI.equals(actionCommand) ||
                DtbActionCommands.DISPLAY_ADD_REMARK_UI.equals(actionCommand))) {    
            builder = new SelectedRecordsParametersBuilder();
        }else if(source instanceof AppointmentPanel && DtbActionCommands.ADD_APPOINTMENT.equals(actionCommand)) {
            builder = new AddAppointmentParametersBuilder();
        }else if(source instanceof UnitPanel && DtbActionCommands.ADD_UNIT.equals(actionCommand)) {
            builder = new AddUnitParametersBuilder();
        }else {
            builder = super.getParametersBuilder(source, actionCommand);
        }
        
        builder.context(this).with(source);

        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Container: {0}, actionCommand: {1}, ParametersBuilder: {2}", 
                    new Object[]{source.getClass().getSimpleName(), actionCommand, builder.getClass().getSimpleName()});
        }
        
        return builder;
    }

    @Override
    public DateFormat getDateTimeFormat() {
        return new DateTimeFormat(this);
    }

    @Override
    public DateFormat getDateFormat() {
        return new com.doctracker.basic.util.DateFormat(this);
    }
}
