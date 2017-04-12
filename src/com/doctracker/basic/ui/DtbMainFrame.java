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

import com.bc.jpa.dao.SelectDao;
import com.bc.jpa.search.SearchResults;
import com.doctracker.basic.pu.entities.Task;
import java.awt.Font;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import com.doctracker.basic.DtbApp;
import com.bc.appbase.App;
import com.doctracker.basic.jpa.DtbSearchContext;
import com.doctracker.basic.ui.actions.DtbActionCommands;

/**
 * The help menu though created and set-up, is not added by default. Use 
 * {@link #addHelpMenu()} to add the help menu after adding all other menus 
 * to the menu bar.
 * @author Chinomso Bassey Ikwuagwu on Mar 30, 2017 12:33:53 PM
 */
public class DtbMainFrame extends com.bc.appbase.ui.MainFrame {

    private javax.swing.JMenuItem executeDeleteMenuItem;
    private javax.swing.JMenuItem executeSelectMenuItem;
    private javax.swing.JMenuItem executeUpdateMenuItem;
    private javax.swing.JMenuItem changeFontSizeForReportsMenuItem;
    private javax.swing.JMenuItem importMenuItem;
    private javax.swing.JMenuItem newtaskMenuItem;
    private javax.swing.JMenu reportsMenu;
    private javax.swing.JMenuItem refreshMenuItem;
    private javax.swing.JMenuItem refreshReportsMenuItem;
    private javax.swing.JMenu remoteMenu;
    private javax.swing.JMenuItem changeFolderForReportsMenuItem;
    private javax.swing.JMenu sqlMenu;
    private javax.swing.JMenuItem syncMenuItem;
    private javax.swing.JMenuItem openReportsFolderMenuItem;
    
    public DtbMainFrame() {
        this(null);
    }

    public DtbMainFrame(App app) {
        this(app, new SearchPanel(), new java.awt.Font("Segoe UI", 0, 18), DtbActionCommands.ABOUT);
    }
    
    public DtbMainFrame(App app, JPanel topPanel, Font menuFont, String aboutMenuItemActionCommand) {
        super(app, topPanel, menuFont, aboutMenuItemActionCommand);
    }

    @Override
    public void init(App app) {
        
        super.init(app);
        
        this.getNewtaskMenuItem().setActionCommand(DtbActionCommands.DISPLAY_ADD_TASK_UI);
        this.getRefreshMenuItem().setActionCommand(DtbActionCommands.REFRESH_MAIN_RESULTS);
        this.getImportMenuItem().setActionCommand(DtbActionCommands.IMPORT);
        this.getExecuteSelectMenuItem().setActionCommand(DtbActionCommands.EXECUTE_SELECT_QUERY);
        this.getExecuteUpdateMenuItem().setActionCommand(DtbActionCommands.EXECUTE_UPDATE_QUERY);
        this.getExecuteDeleteMenuItem().setActionCommand(DtbActionCommands.EXECUTE_DELETE_QUERY);
        this.getRefreshReportsMenuItem().setActionCommand(DtbActionCommands.REFRESH_REPORTS);
        this.getChangeFontSizeForReportsMenuItem().setActionCommand(DtbActionCommands.CHANGE_FONT_SIZE_OF_REPORTS);
        this.getChangeFolderForReportsMenuItem().setActionCommand(DtbActionCommands.CHANGE_FOLDER_FOR_REPORTS);
        this.getOpenReportsFolderMenuItem().setActionCommand(DtbActionCommands.OPEN_FOLDER_FOR_REPORTS);
        this.getSyncMenuItem().setActionCommand(DtbActionCommands.SYNC_DATABASE);
        
        app.getUIContext().addActionListeners(this, 
                this.getNewtaskMenuItem(), 
                this.getRefreshMenuItem(),
                this.getImportMenuItem(),
                this.getExecuteSelectMenuItem(), 
                this.getExecuteUpdateMenuItem(),
                this.getExecuteDeleteMenuItem(), 
                this.getRefreshReportsMenuItem(),
                this.getChangeFontSizeForReportsMenuItem(), 
                this.getChangeFolderForReportsMenuItem(),
                this.getOpenReportsFolderMenuItem(),
                this.getSyncMenuItem()
        );
    }
    
    @Override
    public void reset(App app) {
        
        super.reset(app);
        
        final DtbSearchContext<Task> searchContext = ((DtbApp)app).getSearchContext(Task.class);
        final SelectDao<Task> selectDao = searchContext.getSelectDaoBuilder(Task.class).closed(false).build();
        final SearchResults<Task> searchResults = searchContext.getSearchResults(selectDao);
        this.getSearchResultsPanel().reset(app, searchContext, searchResults);
    }
    
    @Override
    public void init(App app, JPanel topPanel) { 
        (((SearchPanel)topPanel)).init((DtbApp)app);
    }

    @Override
    public void reset(App app, JPanel topPanel) { 
        (((SearchPanel)topPanel)).reset((DtbApp)app);
    }

    @Override
    public void initComponents() {
        
        super.initComponents(); 
        
        newtaskMenuItem = new javax.swing.JMenuItem();
        refreshMenuItem = new javax.swing.JMenuItem();
        importMenuItem = new javax.swing.JMenuItem();
        sqlMenu = new javax.swing.JMenu();
        executeSelectMenuItem = new javax.swing.JMenuItem();
        executeUpdateMenuItem = new javax.swing.JMenuItem();
        executeDeleteMenuItem = new javax.swing.JMenuItem();
        reportsMenu = new javax.swing.JMenu();
        refreshReportsMenuItem = new javax.swing.JMenuItem();
        changeFolderForReportsMenuItem = new javax.swing.JMenuItem();
        openReportsFolderMenuItem = new javax.swing.JMenuItem();
        changeFontSizeForReportsMenuItem = new javax.swing.JMenuItem();
        remoteMenu = new javax.swing.JMenu();
        syncMenuItem = new javax.swing.JMenuItem();

        JMenuBar menuBar = this.getJMenuBar();
        JMenu fileMenu = this.getFileMenu();

        final Font menuFont = this.getMenuFont();
        newtaskMenuItem.setFont(menuFont);
        newtaskMenuItem.setMnemonic('o');
        newtaskMenuItem.setText("New Task");
        fileMenu.add(newtaskMenuItem);

        refreshMenuItem.setFont(menuFont);
        refreshMenuItem.setText("Refresh");
        fileMenu.add(refreshMenuItem);

        importMenuItem.setFont(menuFont);
        importMenuItem.setText("Import");
        fileMenu.add(importMenuItem);

        menuBar.add(fileMenu);

        sqlMenu.setText("SQL");
        sqlMenu.setFont(menuFont);

        executeSelectMenuItem.setFont(menuFont);
        executeSelectMenuItem.setText("Execute SELECT");
        sqlMenu.add(executeSelectMenuItem);

        executeUpdateMenuItem.setFont(menuFont);
        executeUpdateMenuItem.setText("Execute UPDATE");
        executeUpdateMenuItem.setEnabled(false);
        sqlMenu.add(executeUpdateMenuItem);

        executeDeleteMenuItem.setFont(menuFont);
        executeDeleteMenuItem.setText("Execute DELETE");
        executeDeleteMenuItem.setEnabled(false);
        sqlMenu.add(executeDeleteMenuItem);

        menuBar.add(sqlMenu);

        reportsMenu.setText("Reports");
        reportsMenu.setFont(menuFont);

        refreshReportsMenuItem.setFont(menuFont);
        refreshReportsMenuItem.setText("Refresh");
        reportsMenu.add(refreshReportsMenuItem);

        changeFolderForReportsMenuItem.setFont(menuFont);
        changeFolderForReportsMenuItem.setText("Select Folder");
        reportsMenu.add(changeFolderForReportsMenuItem);

        openReportsFolderMenuItem.setFont(menuFont);
        openReportsFolderMenuItem.setText("Open Folder");
        reportsMenu.add(openReportsFolderMenuItem);
        
        changeFontSizeForReportsMenuItem.setFont(menuFont);
        changeFontSizeForReportsMenuItem.setText("Font Size");
        reportsMenu.add(changeFontSizeForReportsMenuItem);

        menuBar.add(reportsMenu);

        remoteMenu.setText("Remote");
        remoteMenu.setFont(menuFont);

        syncMenuItem.setFont(menuFont);
        syncMenuItem.setText("Sync");
        remoteMenu.add(syncMenuItem);

        menuBar.add(remoteMenu);
        
        this.addHelpMenu();
    }

    public JMenuItem getExecuteDeleteMenuItem() {
        return executeDeleteMenuItem;
    }

    public JMenuItem getExecuteSelectMenuItem() {
        return executeSelectMenuItem;
    }

    public JMenuItem getExecuteUpdateMenuItem() {
        return executeUpdateMenuItem;
    }

    public JMenuItem getChangeFontSizeForReportsMenuItem() {
        return changeFontSizeForReportsMenuItem;
    }

    public JMenuItem getImportMenuItem() {
        return importMenuItem;
    }

    public JMenuItem getNewtaskMenuItem() {
        return newtaskMenuItem;
    }

    public JMenu getReportsMenu() {
        return reportsMenu;
    }

    public JMenuItem getRefreshMenuItem() {
        return refreshMenuItem;
    }

    public JMenuItem getRefreshReportsMenuItem() {
        return refreshReportsMenuItem;
    }

    public JMenu getRemoteMenu() {
        return remoteMenu;
    }

    public JMenuItem getChangeFolderForReportsMenuItem() {
        return changeFolderForReportsMenuItem;
    }

    public JMenuItem getOpenReportsFolderMenuItem() {
        return openReportsFolderMenuItem;
    }

    public JMenu getSqlMenu() {
        return sqlMenu;
    }

    public JMenuItem getSyncMenuItem() {
        return syncMenuItem;
    }
}
