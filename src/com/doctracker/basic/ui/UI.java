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
import com.doctracker.basic.ui.model.ColumnWidth;
import com.doctracker.basic.ui.model.ResultModel;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 10, 2017 3:17:53 PM
 */
public interface UI { 
    
    void dispose();
    
    TableCellRenderer getTableCellRenderer(Class resultType, Class columnClass);
    
    TableCellEditor getTableCellEditor(Class resultType, Class columnClass);
    
    DialogManager getDialogManager();
    
    Font getFont(Component comp);
    
    ImageIcon getImageIcon();
    
    ColumnWidth getColumnWidths(Class entityType, ColumnWidth outputIfNone);
    
    ColumnWidth getColumnWidths(ResultModel resultModel);
    
    TableModel getTableModel(SearchResults searchResults, 
            ResultModel resultModel, int firstPage, int numberOfPages);
    
    MouseListener getMouseListener(Container container);
    
    TableCellSizeManager getTableCellSizeManager();
    
    void addActionListeners(Container container, AbstractButton... buttons);
    
    ActionListener getActionListener(Container container, String actionCommand);
    
    <T extends Container> ContainerManager<T> getContainerManager(T container);
    
    boolean positionFullScreen(Window window);
    
    boolean positionHalfScreenLeft(Window window);
    
    boolean positionHalfScreenRight(Window window);
    
    DateUIUpdater getDateUIUpdater();
    
    DateFromUIBuilder getDateFromUIBuilder();
    
    MainFrame getMainFrame();
    
    TaskFrame getTaskFrame();
    
    AppointmentPanel createAppointmentPanel();
    
    UnitPanel createUnitPanel();
    
    TaskResponseFrame createTaskResponseFrame();
    
    <T> SearchResultsFrame createSearchResultsFrame(
            SearchResults<T> searchResults, String ID, int firstPage, int numberOfPages,
            Class<T> entityType, String msg, boolean emptyResultsAllowed);
    
    <T> Boolean loadSearchResultsUI(SearchResultsPanel resultsPanel, SearchResults<T> searchResults, 
            String ID, int firstPage, int numberOfPages, Class<T> entityType, boolean emptyResultsAllowed);
    
    void linkWindowToSearchResults(Window window, SearchResults searchResults, String KEY);
    
    void loadSearchResults(SearchResultsPanel resultsPanel, Class resultType, int firstPage, int numberOfPages);    
    
    EditorPaneFrame createEditorPaneFrame(SearchResultsPanel resultsPanel);
    
    void showErrorMessage(Throwable t, Object message);
    
    void showSuccessMessage(Object message);
}
