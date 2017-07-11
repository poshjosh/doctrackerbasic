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

import com.bc.appbase.App;
import com.bc.appbase.AppLauncher;
import com.bc.appbase.FilenamesDefault;
import com.bc.appbase.FilenamesDevMode;
import com.bc.appbase.ui.MainFrame;
import com.bc.appbase.ui.SearchResultsPanel;
import com.bc.appbase.ui.UIContext;
import com.bc.appcore.actions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.util.BlockingQueueThreadPoolExecutor;
import com.bc.appcore.util.ExpirableCache;
import com.bc.config.Config;
import com.bc.config.ConfigService;
import com.bc.jpa.JpaContext;
import com.bc.jpa.sync.JpaSync;
import com.bc.jpa.sync.SlaveUpdates;
import com.bc.util.Util;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Unit;
import com.doctracker.basic.ui.actions.DtbActionCommands;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import com.bc.appcore.Filenames;

/**
 * @author Chinomso Bassey Ikwuagwu on May 5, 2017 9:01:02 PM
 */
public class DtbAppLauncher extends AppLauncher {

    private ExecutorService updateOutputService;
    
    private ExecutorService scheduleDeadlineTasksReminderSvc;
    
    public DtbAppLauncher(boolean enableSync, boolean productionMode) {
        this(enableSync, productionMode, Paths.get(System.getProperty("user.home"), FileNames.ROOT).toString());
    }
    
    public DtbAppLauncher(boolean enableSync, boolean productionMode, String workingDir) {
        this(enableSync, productionMode ? 
                new FilenamesProductionMode(workingDir) :  new FilenamesDevMode(workingDir));
    }
    
    public DtbAppLauncher(boolean enableSync, Filenames filenames) {
        super(enableSync, Task.class, 
                new FilenamesDefault(filenames.getWorkingDir()), 
                filenames, 
                new String[]{
                        Paths.get(filenames.getWorkingDir(), FileNames.REPORT_BACKUP_DIR).toString(),
                        Paths.get(filenames.getWorkingDir(), FileNames.CONFIGS).toString(),
                        Paths.get(filenames.getWorkingDir(), FileNames.LOGS).toString(),
                        Paths.get(filenames.getWorkingDir(), FileNames.SLAVE_UPDATES_DIR).toString()
                }, 
                !enableSync ? null : Paths.get(filenames.getWorkingDir(), FileNames.SLAVE_UPDATES_DIR),
                "META-INF/properties/settings.properties",
                "TASK TRACKER"
        );
    }

    @Override
    public boolean isInstalled(Config config) {
        return config.getBoolean(ConfigNames.INSTALLED);
    }

    @Override
    public void setInstalled(Config config, boolean installed) {
        config.setBoolean(ConfigNames.INSTALLED, true);
    }

    @Override
    public String getLookAndFeel(Config config) {
        return config.getString(ConfigNames.LOOK_AND_FEEL, "Nimbus");
    }

    @Override
    public String getPersistenceFile(Config config) {
        return config.getString(ConfigNames.PERSISTENCE_FILE);
    }

    @Override
    public App createApplication(Filenames filenames, 
            ConfigService configService, Config config, Properties settingsConfig, 
            JpaContext jpaContext, SlaveUpdates slaveUpdates, JpaSync jpaSync, ExpirableCache expirableCache) {
        final DtbApp app = new DtbAppImpl(
                filenames, configService, config, settingsConfig, jpaContext,
                this.updateOutputService, slaveUpdates, jpaSync, expirableCache 
        );
        return app;
    }

    @Override
    public void before() {
        this.updateOutputService = 
                new BlockingQueueThreadPoolExecutor("Service_for_writing_excel_data_to_disk_ThreadFactory", 1, 1, 1);
    }
    
    @Override
    public void onShutdown(App app) {
        Util.shutdownAndAwaitTermination(
                scheduleDeadlineTasksReminderSvc, 1, TimeUnit.SECONDS);
    }

    @Override
    public void onLaunchCompleted(App app) {
        
        final Map<String, Object> params = new HashMap<>();
        final Config config = app.getConfig();
        params.put(ConfigNames.DEADLINE_HOURS, config.getInt(ConfigNames.DEADLINE_HOURS));
        params.put(ConfigNames.DEADLINE_REMINDER_INTERVAL_HOURS, config.getInt(ConfigNames.DEADLINE_REMINDER_INTERVAL_HOURS));

        try{
            this.scheduleDeadlineTasksReminderSvc = 
                    (ExecutorService)app.getAction(DtbActionCommands.SCHEDULE_DEADLINE_TASKS_REMINDER).execute(app, params);
        }catch(ParameterException | TaskExecutionException e) {
            throw new RuntimeException(e);
        }

        ((DtbApp)app).updateReports(false);
    }

    @Override
    public void configureUI(UIContext uiContext) {

        final MainFrame mainFrame = (MainFrame)uiContext.getMainFrame();

        final SearchResultsPanel resultsPanel = mainFrame.getSearchResultsPanel();

        resultsPanel.getAddButton().setActionCommand(DtbActionCommands.DISPLAY_ADD_TASK_UI);
        uiContext.addActionListeners(resultsPanel, resultsPanel.getAddButton());
    }

    @Override
    public void validateJpaContext(JpaContext jpaContext) {
        jpaContext.getBuilderForSelect(Unit.class).from(Unit.class).getResultsAndClose(0, 10);
    }
}
