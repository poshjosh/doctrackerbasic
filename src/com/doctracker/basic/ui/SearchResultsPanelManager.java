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
import com.doctracker.basic.jpa.SearchManager;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.ui.actions.ActionCommands;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 9, 2017 4:12:26 PM
 */
public class SearchResultsPanelManager implements ContainerManager<SearchResultsPanel> {

    private final App app;
    
    public SearchResultsPanelManager(App app) {
        this.app = app;
    }
    
    @Override
    public void init(SearchResultsPanel container) {
        
        final JTable table = container.getSearchResultsTable();
        
        final Font font = app.getUI().getFont(table);
        table.getTableHeader().setFont(font);
        table.setFont(font);
        
        table.setIntercellSpacing(new Dimension(4, 4));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setShowGrid(true);
        table.setGridColor(Color.DARK_GRAY);

        table.setAutoCreateRowSorter(true);

        container.getAddTaskButton().setActionCommand(ActionCommands.DISPLAY_ADD_TASK_UI);
        container.getNextPageButton().setActionCommand(ActionCommands.NEXT_RESULT);
        container.getPreviousPageButton().setActionCommand(ActionCommands.PREVIOUS_RESULT);
        container.getLastPageButton().setActionCommand(ActionCommands.LAST_RESULT);
        container.getFirstPageButton().setActionCommand(ActionCommands.FIRST_RESULT);
        
        app.getUI().addActionListeners(table, container.getAddTaskButton(),
                container.getNextPageButton(), container.getPreviousPageButton(),
                container.getLastPageButton(), container.getFirstPageButton());

        table.addMouseListener(app.getUI().getMouseListener(container));
    }

    @Override
    public void reset(SearchResultsPanel container) {
        
        final SearchManager<Task> sm = app.getSearchManager(Task.class);
        
        final SelectDao<Task> selectDao = sm.getSelectDaoBuilder(Task.class).closed(false).build();
        
        final SearchResults<Task> searchResults = sm.getSearchResults(selectDao);
        
        final Window topLevelAncestor = (Window)container.getTopLevelAncestor();
        
        final String KEY = topLevelAncestor.getName();
        
        final SearchResults previous = (SearchResults)app.getAttributes().get(KEY);
        if(previous instanceof AutoCloseable) {
            try{
                ((AutoCloseable)previous).close();
            }catch(Exception e) {
                Logger.getLogger(this.getClass().getName()).log(
                        Level.WARNING, "Error closing search results table", e);
            }
        }
        
        app.getUI().loadSearchResultsUI(container, searchResults, KEY, 0, 1, Task.class, true);
        
        topLevelAncestor.pack();
        
        topLevelAncestor.setVisible(true);
    }
}
