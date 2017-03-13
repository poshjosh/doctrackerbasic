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
import com.doctracker.basic.ui.actions.TaskExecutionException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 10, 2017 6:32:34 PM
 */
public class MainFrameManager implements ContainerManager<MainFrame> {

    private final App app;
    
    public MainFrameManager(App app) {
        this.app = app;
    }

    @Override
    public void init(MainFrame container) {
        
        container.getNewtaskMenuItem().setActionCommand(ActionCommands.DISPLAY_ADD_TASK_UI);
        container.getRefreshMenuItem().setActionCommand(ActionCommands.REFRESH_RESULTS);
        container.getAboutMenuItem().setActionCommand(ActionCommands.ABOUT);
        container.getImportMenuItem().setActionCommand(ActionCommands.IMPORT);
        container.getExecuteSelectMenuItem().setActionCommand(ActionCommands.EXECUTE_SELECT_QUERY);
        container.getExecuteUpdateMenuItem().setActionCommand(ActionCommands.EXECUTE_UPDATE_QUERY);
        container.getExecuteDeleteMenuItem().setActionCommand(ActionCommands.EXECUTE_DELETE_QUERY);
        container.getExitMenuItem().setActionCommand(ActionCommands.EXIT);
        container.getRefreshOutputMenuItem().setActionCommand(ActionCommands.REFRESH_OUTPUT);
        container.getFontSizeMenuItem().setActionCommand(ActionCommands.CHANGE_FONT_SIZE);
        container.getSelectFolderMenuItem().setActionCommand(ActionCommands.CHANGE_OUTPUT_DIR);
        container.getSyncMenuItem().setActionCommand(ActionCommands.SYNC_DATABASE);
        
        app.getUI().addActionListeners(container, 
                container.getNewtaskMenuItem(), container.getRefreshMenuItem(),
                container.getAboutMenuItem(), container.getImportMenuItem(),
                container.getExecuteSelectMenuItem(), container.getExecuteUpdateMenuItem(),
                container.getExecuteDeleteMenuItem(), container.getExitMenuItem(),
                container.getRefreshOutputMenuItem(),
                container.getFontSizeMenuItem(), container.getSelectFolderMenuItem(),
                container.getSyncMenuItem()
        );
        
        container.getSaveAsMenuItem().setActionCommand(ActionCommands.SAVE_TABLE_AS);
        container.getPrintMenuItem().setActionCommand(ActionCommands.PRINT);
        app.getUI().addActionListeners(container.getSearchResultsPanel().getSearchResultsTable(), 
                container.getSaveAsMenuItem(), container.getPrintMenuItem());
        
        container.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try{
                    app.getAction(ActionCommands.EXIT).execute(app, Collections.EMPTY_MAP);
                }catch(RuntimeException | TaskExecutionException exception) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Unexpected error", exception);
                    System.exit(0);
                }
            }
        });

        final SearchPanel searchPanel = container.getSearchPanel();
        
        app.getUI().getContainerManager(searchPanel).init(searchPanel);
        
        final SearchResultsPanel searchResultsPanel = container.getSearchResultsPanel();
        
        app.getUI().getContainerManager(searchResultsPanel).init(searchResultsPanel);
        
        container.setIconImage(app.getUI().getImageIcon().getImage());
    }

    @Override
    public void reset(MainFrame container) {
        
        final SearchPanel searchPanel = container.getSearchPanel();
        
        app.getUI().getContainerManager(searchPanel).reset(searchPanel);
        
        final SearchResultsPanel searchResultsPanel = container.getSearchResultsPanel();
        
        app.getUI().getContainerManager(searchResultsPanel).reset(searchResultsPanel);
    }
}
