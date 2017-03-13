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

import com.doctracker.basic.io.FileNames;
import com.doctracker.basic.io.ResourceContextImpl;
import com.bc.config.CompositeConfig;
import com.doctracker.basic.util.UILog;
import com.doctracker.basic.jpa.SearchManager;
import com.bc.config.Config;
import com.bc.config.ConfigService;
import com.bc.config.SimpleConfigService;
import com.bc.jpa.JpaContext;
import com.bc.jpa.JpaContextImpl;
import com.bc.jpa.dao.SelectDao;
import com.bc.jpa.search.SearchResults;
import com.doctracker.basic.io.LoggingConfigManagerImpl;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.ui.MainFrame;
import com.doctracker.basic.ui.SearchResultsPanel;
import com.doctracker.basic.ui.UI;
import com.doctracker.basic.ui.actions.ActionCommands;
import com.doctracker.basic.ui.actions.SetLookAndFeel;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import com.doctracker.basic.io.ResourceContext;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 7, 2017 11:10:16 PM
 */
public class Doctrackerbasic {
    
    public static final Boolean PRODUCTION_MODE = Boolean.TRUE;
    
    public static void main(String [] args) {
        
        try{
            
            final Logger logger = Logger.getLogger(Doctrackerbasic.class.getName());
            
            logger.log(Level.INFO, "Production mode: {0}", PRODUCTION_MODE);
            
            final String workingDir = Paths.get(System.getProperty("user.home"), FileNames.ROOT).toString();
            final String defaultLoggingConfigFile = "META-INF/properties/logging.properties";
            final String defaultPropsFile = "META-INF/properties/appdefaults.properties";
            final String loggingConfigFile;
            final String propsFile;
            if(PRODUCTION_MODE) {
                loggingConfigFile = Paths.get(workingDir, FileNames.CONFIGS, "logging.properties").toString();
                propsFile = Paths.get(workingDir, FileNames.CONFIGS, "app.properties").toString();
            }else{
                loggingConfigFile = "META-INF/properties/logging_devmode.properties";
                propsFile = "META-INF/properties/app_devmode.properties";
            }
            
            final String [] dirsToCreate = new String[]{
                    Paths.get(workingDir, FileNames.LOGS).toString(),
                    Paths.get(workingDir, FileNames.SLAVE_UPDATES_DIR).toString()
            };
            final String [] filesToCreate = new String[]{propsFile, loggingConfigFile};
            final ResourceContext fileManager = new ResourceContextImpl(dirsToCreate, filesToCreate);
            
            new LoggingConfigManagerImpl(fileManager).init(defaultLoggingConfigFile, loggingConfigFile);
            
            final ConfigService configService = new SimpleConfigService(
                    defaultPropsFile, propsFile);

            final Config config = new CompositeConfig(configService);
            
            new SetLookAndFeel().execute(null, 
                    Collections.singletonMap(
                            ConfigNames.LOOK_AND_FEEL, 
                            config.getString(ConfigNames.LOOK_AND_FEEL)));
            
            final boolean WAS_INSTALLED = config.getBoolean(ConfigNames.INSTALLED);
            
            final UILog uiLog = new UILog("Startup Log", WAS_INSTALLED ? 300 : 600, WAS_INSTALLED ? 225 : 450);
        
            uiLog.log("...Launching app");
        
            final String persistenceFile = config.getString(ConfigNames.PERSISTENCE_FILE);
            logger.log(Level.INFO, "Peristence file: {0}", persistenceFile);
            final URI peristenceURI = fileManager.getResource(persistenceFile).toURI();
            final JpaContext jpaContext = new JpaContextImpl(peristenceURI, null);

            uiLog.log("Creating UI");
            
            /* Create and display the UI */
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try{
                        
                        final App app = new AppImpl(
                                Paths.get(workingDir), configService, config, jpaContext);
                        
                        uiLog.log("SUCCESS creating UI");
                        
                        final UI ui = app.getUI();
                        
                        final MainFrame mainFrame = ui.getMainFrame();
                        
                        final SearchResultsPanel resultsPanel = mainFrame.getSearchResultsPanel();
                        
                        uiLog.log("Loading search results");
                        
                        final Class<Task> entityType = Task.class;
                        
                        final SearchManager<Task> sm = app.getSearchManager(entityType);
                        
                        final SelectDao<Task> selectDao = sm.getSelectDaoBuilder(entityType).closed(false).build();
                        
                        final SearchResults<Task> searchResults = sm.getSearchResults(selectDao);
                        
                        ui.positionFullScreen(mainFrame);
                        
                        ui.loadSearchResultsUI(resultsPanel, searchResults, "AppMainFrame", 0, 1, entityType, true);
                        
                        mainFrame.pack();
                        
                        mainFrame.setVisible(true);
                        
                        uiLog.log("Displayed UI");
                        
                        app.getConfig().setBoolean(ConfigNames.INSTALLED, true);
                        
                        app.getConfigService().store();
                        
                        logger.log(Level.FINE, "{0} = {1}",
                                new Object[]{ConfigNames.INSTALLED, app.getConfig().getBoolean(ConfigNames.INSTALLED)});
                        
                        uiLog.log(WAS_INSTALLED ? "App Launch Successful" : "Installation Successful");
                        
                        Map<String, Object> params = new HashMap<>();
                        params.put(ConfigNames.DEADLINE_HOURS, config.getInt(ConfigNames.DEADLINE_HOURS));
                        params.put(ConfigNames.DEADLINE_REMINDER_INTERVAL_HOURS, config.getInt(ConfigNames.DEADLINE_REMINDER_INTERVAL_HOURS));
                        
                        app.getAction(ActionCommands.SCHEDULE_DEADLINE_TASKS_REMINDER).execute(app, params);
                        
                        app.updateOutput();
                        
//                        app.getAction(ActionCommands.SYNC_DATABASE).execute(app, Collections.EMPTY_MAP);
                        
                        if(!WAS_INSTALLED) {
                            
                            uiLog.querySaveLogThenSave();
                        }
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
        
        String title;
        Object message;
        if(t != null) {
            title = description;
            message = t;
        }else{
            title = "Error";
            message = description;
        }
        
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);

        System.exit(exitCode);
    }
}
