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

import com.bc.jpa.search.SearchResults;
import com.doctracker.basic.jpa.SearchManager;
import com.doctracker.basic.jpa.LoadPageThread;
import com.doctracker.basic.ui.actions.ActionCommands;
import com.doctracker.basic.ui.actions.ActionListenerImpl;
import com.doctracker.basic.ui.model.ColumnWidth;
import com.doctracker.basic.ui.model.ColumnWidthImpl;
import com.doctracker.basic.ui.model.SearchResultsTableModel;
import com.doctracker.basic.ui.model.ResultModel;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 10, 2017 4:51:32 PM
 */
public class UIImpl implements UI {
    
    private transient final Logger logger = Logger.getLogger(UIImpl.class.getName());

    private final App app;
    
    private final MainFrame mainFrame;
    
    private final TaskFrame newTaskFrame;
    
    private final DialogManager dialogManager;

    private final DateUIUpdater dateUIUpdater;
    
    private final DateFromUIBuilder dateFromUIBuilder;
    
    private final ImageIcon imageIcon;
    
    public UIImpl(App app, ImageIcon icon, MainFrame mainFrame, TaskFrame newTaskFrame, 
            DialogManager dialogManager, DateUIUpdater dateUIUpdater, DateFromUIBuilder dateFromUIBuilder) {
        this.app = Objects.requireNonNull(app);
        this.mainFrame = Objects.requireNonNull(mainFrame);
        this.newTaskFrame = Objects.requireNonNull(newTaskFrame);
        this.dialogManager = Objects.requireNonNull(dialogManager);
        this.dateUIUpdater = Objects.requireNonNull(dateUIUpdater);
        this.dateFromUIBuilder = Objects.requireNonNull(dateFromUIBuilder);
        this.imageIcon = Objects.requireNonNull(icon);
    }
    
    @Override
    public void dispose() {
        if(this.mainFrame.isVisible()) {
            this.mainFrame.setVisible(false);
        }
        this.mainFrame.dispose();
        if(this.newTaskFrame.isVisible()) {
            this.newTaskFrame.setVisible(false);
        }
        this.newTaskFrame.dispose();
    }
    
    @Override
    public TableCellRenderer getTableCellRenderer(Class resultType, Class columnClass) {
        return new TaskTableCellRenderer(app, new TableCellTextArea(), this.getTableCellSize(resultType, columnClass));
    }
    
    @Override
    public TableCellEditor getTableCellEditor(Class resultType, Class columnClass) {
        TableTextCellComponentManager textCellManager = new TableTextCellComponentManager(
                app, new TableCellTextArea(), this.getTableCellSize(resultType, columnClass));
        return new TaskTableTextCellEditor(app, textCellManager);
    }
    
    private TableCellSize getTableCellSize(Class resultType, Class columnClass) {
        return new TableCellSizeImpl(app, 36, 720);
    }

    @Override
    public DialogManager getDialogManager() {
        return dialogManager;
    }
    
    @Override
    public Font getFont(Component comp) {
        String fontString = app.getConfig().getString(comp.getClass().getName()+".font");
        if(fontString == null || fontString.isEmpty()) {
            fontString = app.getConfig().getString(".font");
            if(fontString == null || fontString.isEmpty()) {
                throw new NullPointerException("Property: .font");
            }
        }
        final Font font = Font.decode(fontString);
        return font;
    }

    @Override
    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    @Override
    public TableCellSizeManager getTableCellSizeManager() {
        return new TableCellSizeManagerImpl(app);
    }

    @Override
    public ColumnWidth getColumnWidths(Class entityType, ColumnWidth outputIfNone) {
        final ColumnWidth columnWidths = this.getColumnWidths(app.getResultModel(entityType, null));
        return columnWidths == null ? outputIfNone : columnWidths;
    }
    
    @Override
    public ColumnWidth getColumnWidths(ResultModel resultModel) {
        return new ColumnWidthImpl(resultModel);
    }

    @Override
    public TableModel getTableModel(SearchResults searchResults, 
            ResultModel resultModel, int firstPage, int numberOfPages) {
        return new SearchResultsTableModel(app, searchResults, resultModel, firstPage, numberOfPages);
    }

    @Override
    public MouseListener getMouseListener(Container container) {
        if(container instanceof SearchResultsPanel) {
            return new SearchResultsPanelMouseRightClickListener(app, (SearchResultsPanel)container);
        }else{
            return new MouseAdapter() {};
        }
    }
    
    @Override
    public void showErrorMessage(Throwable t, Object message) {
        this.dialogManager.showErrorMessage(t, message);
    }

    @Override
    public void showSuccessMessage(Object message) {
        this.dialogManager.showSuccessMessage(message);
    }

    @Override
    public void addActionListeners(Container container, AbstractButton... buttons) {
        for(AbstractButton button : buttons) {
            final String actionCommand = Objects.requireNonNull(button.getActionCommand());
            if(actionCommand.equals(button.getName())) {
                throw new UnsupportedOperationException("Action command not set for button with name: " + button.getName());
            }
            button.addActionListener(this.getActionListener(container, actionCommand));
        }
    }



    @Override
    public ActionListener getActionListener(Container container, String actionCommand) {
        return new ActionListenerImpl(app, container, actionCommand);
    }

    @Override
    public <T extends Container> ContainerManager<T> getContainerManager(T container) {
        final String className = container.getClass().getName() + "Manager";
        return (ContainerManager<T>)this.dynamicallyCreateAppObject(className);
    }
    private Object dynamicallyCreateAppObject(String className) {
        try{
            final Class aClass = Class.forName(className);
            return aClass.getConstructor(App.class).newInstance(app);
        }catch(ClassNotFoundException | NoSuchMethodException | SecurityException | 
                InstantiationException | IllegalAccessException | 
                IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean positionFullScreen(Window window) {
        try{
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final Dimension custom = new Dimension(screenSize.width, screenSize.height - 50);
            window.setLocation(0, 0);
            window.setSize(custom); 
            window.setPreferredSize(custom);
            return true;
        }catch(Exception ignored) { 
            return false;
        }
    }
    
    @Override
    public boolean positionHalfScreenLeft(Window window) {
        try{
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final Dimension custom = new Dimension(screenSize.width/2, screenSize.height - 50);
            window.setLocation(0, 0);
            window.setSize(custom); 
            window.setPreferredSize(custom);
            return true;
        }catch(Exception ignored) { 
            return false;
        }
    }

    @Override
    public boolean positionHalfScreenRight(Window window) {
        try{
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final Dimension custom = new Dimension(screenSize.width/2, screenSize.height - 50);
            window.setLocation(custom.width, 0);
            window.setSize(custom); 
            window.setPreferredSize(custom);
            return true;
        }catch(Exception ignored) { 
            return false;
        }
    }

    @Override
    public AppointmentPanel createAppointmentPanel() {
        return new AppointmentPanel(app);
    }

    @Override
    public UnitPanel createUnitPanel() {
        return new UnitPanel(app);
    }

    @Override
    public TaskResponseFrame createTaskResponseFrame() {
        TaskResponseFrame frame = new TaskResponseFrame();
        this.getContainerManager(frame.getTaskResponsePanel()).init(frame.getTaskResponsePanel());
        return frame;
    }

    @Override
    public EditorPaneFrame createEditorPaneFrame(SearchResultsPanel resultsPanel) {
        EditorPaneFrame frame = new EditorPaneFrame();
        frame.setIconImage(this.getImageIcon().getImage());
        frame.getAddResponseButton().setActionCommand(ActionCommands.DISPLAY_ADD_RESPONSE_UI);
        frame.getAddRemarkButton().setActionCommand(ActionCommands.DISPLAY_ADD_REMARK_UI);
        frame.getCloseTaskButton().setActionCommand(ActionCommands.CLOSE_TASK);
        frame.getDeleteTaskButton().setActionCommand(ActionCommands.DELETE_TASK);
        this.addActionListeners(resultsPanel, 
                frame.getAddResponseButton(),
                frame.getAddRemarkButton(),
                frame.getCloseTaskButton(),
                frame.getDeleteTaskButton());
        return frame;
    }

    @Override
    public <T> SearchResultsFrame createSearchResultsFrame(
            SearchResults<T> searchResults, String ID, int firstPage, int numberOfPages,
            Class<T> entityType, String msg, boolean emptyResultsAllowed) {
        
        logger.log(Level.FINE, "#createSearchResultsFrame(...) Message: {0}", msg);
        
        final SearchResultsFrame resultsFrame;
        
        if(!emptyResultsAllowed && searchResults.getSize() == 0) {
            resultsFrame = null;
        }else{
            resultsFrame = new SearchResultsFrame();
            resultsFrame.setIconImage(this.getImageIcon().getImage());

            final JMenuItem saveAs = resultsFrame.getSaveAsMenuItem();
            saveAs.setActionCommand(ActionCommands.SAVE_TABLE_AS);
            final JMenuItem print = resultsFrame.getPrintMenuItem();
            print.setActionCommand(ActionCommands.PRINT);
            final JTable table = resultsFrame.getSearchResultsPanel().getSearchResultsTable();
            this.addActionListeners(table, print, saveAs);

            final SearchResultsPanel resultsPanel = resultsFrame.getSearchResultsPanel();

            resultsFrame.getSearchResultsLabel().setText(msg);

            this.getContainerManager(resultsPanel).init(resultsPanel);
            
            this.loadSearchResultsUI(
                    resultsPanel, searchResults, ID, firstPage, numberOfPages, entityType, emptyResultsAllowed);
        }
        
        return resultsFrame;
    }
    
    @Override
    public <T> Boolean loadSearchResultsUI(
            SearchResultsPanel resultsPanel, SearchResults<T> searchResults, 
            String ID, int firstPage, int numberOfPages, Class<T> entityType, boolean emptyResultsAllowed) {

        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "#loadSearchResults(...) Page: {0}, ID: {1}", 
                    new Object[]{firstPage, ID});
        }
        
        final Boolean output;
        if(!emptyResultsAllowed && searchResults.getSize() == 0) {
            output = Boolean.FALSE;
        }else{
            if(SwingUtilities.isEventDispatchThread()) {
                output = this.doLoadSearchResultsUI(resultsPanel, searchResults, ID, firstPage, numberOfPages, entityType, true);
            }else{
                java.awt.EventQueue.invokeLater(new Runnable(){
                    @Override
                    public void run() {
                        try{
                            doLoadSearchResultsUI(resultsPanel, searchResults, ID, firstPage, numberOfPages, entityType, true);
                        }catch(RuntimeException e) {
                            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Unexpected exception", e);
                        }
                    }
                });
                output = Boolean.TRUE;
            }
        }
        
        return output;
    }
    private <T> Boolean doLoadSearchResultsUI(
           SearchResultsPanel resultsPanel, SearchResults<T> searchResults, 
            String ID, int firstPage, int numberOfPages, Class<T> entityType, boolean emptyResultsAllowed) {
        final Boolean output;
        if(!emptyResultsAllowed && searchResults.getSize() == 0) {
            output = Boolean.FALSE;
        }else{
            
            this.loadSearchResults(resultsPanel, searchResults, firstPage, numberOfPages, entityType);
            
            final Window resultsWindow = (Window)resultsPanel.getTopLevelAncestor();

            if(ID != null) {
                app.getUI().linkWindowToSearchResults(resultsWindow, searchResults, ID);
            }
            
            output = Boolean.TRUE;
        }
        
        return output;
    }

    @Override
    public void linkWindowToSearchResults(Window window, SearchResults searchResults, String KEY) {
        
        logger.log(Level.FINE, "#linkWindowToSearchResults(...) ID: {0}", KEY);
        
        if(SwingUtilities.isEventDispatchThread()) {
            this.doLinkWindowToSearchResults(window, searchResults, KEY);
        }else{
            java.awt.EventQueue.invokeLater(() -> {
                try{
                    doLinkWindowToSearchResults(window, searchResults, KEY);
                }catch(RuntimeException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Unexpected exception", e);
                }
            });
        }
    }
    private void doLinkWindowToSearchResults(Window window, SearchResults searchResults, String KEY) {
        
        app.getAttributes().put(KEY, searchResults);
        
        window.setName(KEY);

        window.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent we) {
                logger.log(Level.FINE, "Window closing: {0}. Closing linked search results and removing attribute", window.getName());
                try{
                    if(searchResults instanceof AutoCloseable) {
                        ((AutoCloseable)searchResults).close();
                    }
                }catch(Exception exception) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Error closing dao", exception);
                }finally{
                    app.getAttributes().remove(KEY);
                }
            }
        });
    }

    @Override
    public void loadSearchResults(SearchResultsPanel resultsPanel, 
            Class resultType, int firstPage, int numberOfPages) {
        if(SwingUtilities.isEventDispatchThread()) {
            this.doLoadSearchResults(resultsPanel, resultType, firstPage, numberOfPages);
        }else{
            java.awt.EventQueue.invokeLater(() -> {
                try{
                    this.doLoadSearchResults(resultsPanel, resultType, firstPage, numberOfPages);
                }catch(RuntimeException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Unexpected exception", e);
                }
            });
        }
    }
    private void doLoadSearchResults(
            SearchResultsPanel resultsPanel, Class resultType, int firstPage, int numberOfPages) {

        final Window window = (Window)resultsPanel.getTopLevelAncestor();

        final String KEY = window.getName();

        final SearchResults searchResults = (SearchResults)app.getAttributes().get(KEY);

        this.loadSearchResults(resultsPanel, searchResults, firstPage, numberOfPages, resultType);
    }
    
    private void loadSearchResults(SearchResultsPanel resultsPanel, 
            SearchResults searchResults, int firstPage, int numberOfPages, Class resultType) {
        
        final SearchManager sm = app.getSearchManager(resultType);

        final ResultModel resultModel = sm.getResultModel();

        final UI ui = app.getUI();
        
        final JTable table = resultsPanel.getSearchResultsTable();

        if(firstPage == 0 && searchResults.getSize() == 0) {
            
            final TableModel tableModel = ui.getTableModel(searchResults, resultModel, firstPage, 0);

            table.setModel(tableModel);
            
        }else{
            
            if(firstPage < 0 || firstPage >= searchResults.getPageCount()) {
                return;
            }

            logger.log(Level.FINE, "Setting page number to: {0}", firstPage);
            searchResults.setPageNumber(firstPage);

            final TableModel tableModel = ui.getTableModel(searchResults, resultModel, firstPage, numberOfPages);

            final ColumnWidth columnWidths = ui.getColumnWidths(resultModel);
            
            ui.getTableCellSizeManager().updateCellSizes(table, tableModel, columnWidths, null);

            new LoadPageThread(searchResults, firstPage + numberOfPages).start();
        }

        final String paginationMessage = sm.getPaginationMessage(searchResults, numberOfPages, true, false);
        resultsPanel.getPaginationLabel().setText(paginationMessage);
    }
    
    @Override
    public DateUIUpdater getDateUIUpdater() {
        return this.dateUIUpdater;
    }

    @Override
    public DateFromUIBuilder getDateFromUIBuilder() {
        return this.dateFromUIBuilder;
    }

    @Override
    public MainFrame getMainFrame() {
        return mainFrame;
    }

    @Override
    public TaskFrame getTaskFrame() {
        return newTaskFrame;
    }
}
