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

import com.bc.config.CompositeConfig;
import com.bc.appbase.ui.UILog;
import com.bc.config.Config;
import com.bc.config.ConfigService;
import com.bc.config.SimpleConfigService;
import com.bc.jpa.JpaContext;
import com.bc.jpa.JpaContextImpl;
import com.bc.jpa.dao.SelectDao;
import com.bc.jpa.search.SearchResults;
import com.bc.util.Util;
import com.doctracker.basic.pu.entities.Task;
import com.bc.appbase.ui.SearchResultsPanel;
import com.bc.appbase.ui.actions.SetLookAndFeel;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bc.appbase.ui.PopupImpl;
import com.bc.appbase.ui.actions.ParamNames;
import com.bc.appcore.ResourceContext;
import com.bc.appcore.ResourceContextImpl;
import com.bc.appcore.util.BlockingQueueThreadPoolExecutor;
import com.bc.appcore.util.LoggingConfigManagerImpl;
import com.bc.appcore.util.Settings;
import com.bc.appcore.util.SettingsImpl;
import com.bc.jpa.sync.JpaSync;
import com.bc.jpa.sync.impl.JpaSyncImpl;
import com.bc.jpa.sync.impl.RemoteUpdaterImpl;
import com.bc.jpa.sync.SlaveUpdates;
import com.bc.jpa.sync.impl.SlaveUpdatesImpl;
import com.bc.jpa.sync.predicates.PersistenceCommunicationsLinkFailureTest;
import com.doctracker.basic.jpa.predicates.MasterPersistenceUnitTest;
import com.doctracker.basic.jpa.predicates.SlavePersistenceUnitTest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import com.doctracker.basic.pu.entities.Unit;
import com.doctracker.basic.ui.DtbMainFrame;
import com.doctracker.basic.jpa.DtbSearchContext;
import com.doctracker.basic.ui.DtbUIContext;
import com.doctracker.basic.ui.actions.DtbActionCommands;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 7, 2017 11:10:16 PM
 */
public class Doctrackerbasic {
    
    public static final Boolean PRODUCTION_MODE = Boolean.TRUE;
    public static final Boolean ENABLE_SYNC = Boolean.FALSE;
    
    public static void main(String [] args) {
        
        try{
            
            final Logger logger = Logger.getLogger(Doctrackerbasic.class.getName());
            
            logger.log(Level.INFO, "Production mode: {0}", PRODUCTION_MODE);
            
            final UILog uiLog = new UILog("Startup Log");
            
            uiLog.show();
            
            uiLog.log("");
            uiLog.log("Doc Tracker Basic");
            uiLog.log("-----------------");
            uiLog.log("...Initializing");
            
            final String workingDir = Paths.get(System.getProperty("user.home"), FileNames.ROOT).toString();
            final String defaultLoggingConfigFile = "META-INF/properties/logging.properties";
            final String defaultPropsFile = "META-INF/properties/app.properties";
            final String loggingConfigFile;
            final String propsFile;
            if(PRODUCTION_MODE) {
                loggingConfigFile = Paths.get(workingDir, FileNames.CONFIGS, "logging.properties").toString();
                propsFile = Paths.get(workingDir, FileNames.CONFIGS, "app.properties").toString();
            }else{
                loggingConfigFile = "META-INF/properties/logging_devmode.properties";
                propsFile = "META-INF/properties/app_devmode.properties";
            }
            
            uiLog.log("Initializing folders");
            
            final String [] dirsToCreate = new String[]{
                    Paths.get(workingDir, FileNames.LOGS).toString(),
                    Paths.get(workingDir, FileNames.SLAVE_UPDATES_DIR).toString()
            };
            final String [] filesToCreate = new String[]{propsFile, loggingConfigFile};
            final ResourceContext fileManager = new ResourceContextImpl(dirsToCreate, filesToCreate);
            
            new LoggingConfigManagerImpl(fileManager).init(defaultLoggingConfigFile, loggingConfigFile);
            
            uiLog.log("Loading configurations");
            
            final ConfigService configService = new SimpleConfigService(
                    defaultPropsFile, propsFile);

            final Config config = new CompositeConfig(configService);
            
            uiLog.log("Setting look and feel");
            
            new SetLookAndFeel().execute(null, 
                    Collections.singletonMap(
                            ParamNames.LOOK_AND_FEEL, 
                            config.getString(ConfigNames.LOOK_AND_FEEL)));
            
            final boolean WAS_INSTALLED = config.getBoolean(ConfigNames.INSTALLED);
            
            uiLog.log("Initializing database");
        
            final String persistenceFile = config.getString(ConfigNames.PERSISTENCE_FILE);
            logger.log(Level.INFO, "Peristence file: {0}", persistenceFile);
            final URI peristenceURI = fileManager.getResource(persistenceFile).toURI();
            final JpaContext jpaContext = new JpaContextImpl(peristenceURI, null);
            jpaContext.getBuilderForSelect(Unit.class).from(Unit.class).getResultsAndClose(0, 10);

            final ExecutorService updateOutputService = 
                    new BlockingQueueThreadPoolExecutor("Service_for_writing_excel_data_to_disk_ThreadFactory", 1, 1, 1);

            final SlaveUpdates slaveUpdates = !ENABLE_SYNC ? SlaveUpdates.NO_OP :
                    new SlaveUpdatesImpl(
                            Paths.get(workingDir, FileNames.SLAVE_UPDATES_DIR), 
                            new RemoteUpdaterImpl(jpaContext, new MasterPersistenceUnitTest(), new SlavePersistenceUnitTest()),
                            new PersistenceCommunicationsLinkFailureTest());

            final JpaSync jpaSync = !ENABLE_SYNC ? JpaSync.NO_OP :
                    new JpaSyncImpl(jpaContext, 
                            new RemoteUpdaterImpl(jpaContext, new MasterPersistenceUnitTest(), new SlavePersistenceUnitTest()), 
                            20, 
                            new PersistenceCommunicationsLinkFailureTest());
            
            uiLog.log("Initializing application context");
            
            final Properties settingsMetaData = new Properties();
            
            try(Reader reader = new InputStreamReader(fileManager.getResourceAsStream("META-INF/properties/settings.properties"))) {
                settingsMetaData.load(reader);
            }
            
            final Settings settings = new SettingsImpl(configService, config, settingsMetaData);
            
            final DtbApp app = new DtbAppImpl(
                    Paths.get(workingDir), configService, config, settings, jpaContext,
                    updateOutputService, slaveUpdates, jpaSync
            );
            
            uiLog.log("Creating user interface");
            
            /* Create and display the UIContextBase */
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try{
                        
                        app.init();
                        
                        uiLog.log("Configuring user interface");
                        
                        final DtbUIContext ui = ((DtbApp)app).getUIContext();
                        
                        final DtbMainFrame mainFrame = ui.getMainFrame();
                        
                        final SearchResultsPanel resultsPanel = mainFrame.getSearchResultsPanel();
                        
                        resultsPanel.getAddButton().setActionCommand(DtbActionCommands.DISPLAY_ADD_TASK_UI);
                        ui.addActionListeners(resultsPanel, resultsPanel.getAddButton());
                        
                        uiLog.log("Loading search results");
                        
                        final Class<Task> entityType = Task.class;
                        
                        final DtbSearchContext<Task> searchContext = app.getSearchContext(entityType);
                        
                        final SelectDao<Task> selectDao = searchContext.getSelectDaoBuilder(entityType)
                                .resultType(entityType).closed(false).build();
                        
                        final SearchResults<Task> searchResults = searchContext.getSearchResults(selectDao);
                        
                        ui.positionFullScreen(mainFrame);
                        
                        ui.loadSearchResultsUI(resultsPanel, searchContext, 
                                searchResults, "AppMainFrame", 0, 1, true);

                        mainFrame.pack();
                        
                        uiLog.log("Displaying user interface");
                        
                        mainFrame.setVisible(true);
                        
                        app.getConfig().setBoolean(ConfigNames.INSTALLED, true);
                        
                        app.getConfigService().store();
                        
                        logger.log(Level.INFO, "Was installed: {0}, now installed: {1}",
                                new Object[]{WAS_INSTALLED, app.getConfig().getBoolean(ConfigNames.INSTALLED)});
                        
                        uiLog.log(WAS_INSTALLED ? "App Launch Successful" : "Installation Successful");
                        
                        Map<String, Object> params = new HashMap<>();
                        params.put(ConfigNames.DEADLINE_HOURS, config.getInt(ConfigNames.DEADLINE_HOURS));
                        params.put(ConfigNames.DEADLINE_REMINDER_INTERVAL_HOURS, config.getInt(ConfigNames.DEADLINE_REMINDER_INTERVAL_HOURS));
                        
                        final ExecutorService scheduleDeadlineTasksReminderSvc = 
                                (ExecutorService)app.getAction(DtbActionCommands.SCHEDULE_DEADLINE_TASKS_REMINDER).execute(app, params);
                        
                        app.updateOutput();
                        
                        if(!WAS_INSTALLED) {
                            
                            uiLog.querySaveLogThenSave();
                        }
                        
                        Runtime.getRuntime().addShutdownHook(new Thread("App_ShutdownHook_Thread") {
                            @Override
                            public void run() {
                                
                                if(!app.isShutdown()) {
                                    app.shutdown();
                                }
                                
                                Util.shutdownAndAwaitTermination(
                                        scheduleDeadlineTasksReminderSvc, 1, TimeUnit.SECONDS);
                            }
                        });
                    }catch(Throwable t) {
                        
                        uiLog.log("Error");
                        uiLog.log(t);
                        
                        showErrorMessageAndExit(t);
                        
                    }finally{
                        
                        uiLog.dispose();
                    }
                }
            });
        }catch(Throwable t) {
            
            showErrorMessageAndExit(t);
        }
    }
    
    private static void showErrorMessageAndExit(Throwable t) {
        showErrorMessageAndExit(t, "Failed to start application", 0);
    }
    
    private static void showErrorMessageAndExit(Throwable t, String description, int exitCode) {
        
        Logger.getLogger(Doctrackerbasic.class.getName()).log(Level.SEVERE, description, t);
        
        new PopupImpl(null).showErrorMessage(t, description);

        System.exit(exitCode);
    }
}
