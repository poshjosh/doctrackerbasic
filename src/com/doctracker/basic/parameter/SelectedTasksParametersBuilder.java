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

package com.doctracker.basic.parameter;

import com.bc.jpa.search.SearchResults;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Task_;
import com.doctracker.basic.ui.SearchResultsPanel;
import com.doctracker.basic.ui.model.ResultModel;
import java.awt.Window;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 11, 2017 4:04:19 PM
 */
public class SelectedTasksParametersBuilder implements ParametersBuilder<SearchResultsPanel> {
    
    private transient final Logger logger = Logger.getLogger(SelectedTasksParametersBuilder.class.getName());

    private App app;
    
    private SearchResultsPanel searchResultsPanel;
    
    @Override
    public ParametersBuilder<SearchResultsPanel> app(App app) {
        this.app = app;
        return this;
    }

    @Override
    public ParametersBuilder<SearchResultsPanel> with(SearchResultsPanel searchResultsPanel) {
        this.searchResultsPanel = searchResultsPanel;
        return this;
    }
    
    @Override
    public Map<String, Object> build() {
        
        final Map<String, Object> params;
        
        final JTable table = searchResultsPanel.getSearchResultsTable();

        final int [] selectedRowIndices = table.getSelectedRows();
      
        if(selectedRowIndices == null || selectedRowIndices.length == 0) {
            
            params = Collections.EMPTY_MAP;
            
        }else{
            
            params = new HashMap();
            
            final Integer [] selectedTaskids = new Integer[selectedRowIndices.length];
            
            final ResultModel<Task> resultModel = Objects.requireNonNull(app.getResultModel(Task.class, null));
            
            for(int i = 0; i < selectedRowIndices.length; i++) {
                
                selectedTaskids[i] = this.getTaskid(resultModel, selectedRowIndices[i]);
            }
            
            params.put(Task_.taskid.getName()+"List", Arrays.asList(selectedTaskids));
            
            params.put(SearchResultsPanel.class.getName(), this.searchResultsPanel);
            
            return params;
        }
        
        return params;
    }
    
    private Integer getTaskid(ResultModel<Task> resultModel, int rowIndex) {
        final Task task = this.getTask(rowIndex);
        final Integer taskid = (Integer)resultModel.get(task, rowIndex, Task_.taskid.getName());
        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Row: {0}, Taskid: {1}, Task.description: {2}", 
                    new Object[]{rowIndex, taskid, task.getDescription()});
        }
        return taskid;
    }
    
    private Task getTask(int tableRowIndex) {
        final SearchResults<Task> searchResults = this.getSearchResults();
        final List<Task> currentpage = searchResults.getCurrentPage();
        if(tableRowIndex < currentpage.size()) {
            return currentpage.get(tableRowIndex);
        }else{
            final int resultsRowIndex = (searchResults.getPageNumber() * searchResults.getPageSize()) + tableRowIndex;
            if(logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Page: {0}, table row index: {1}, result row index: {2}", 
                        new Object[]{searchResults.getPageNumber(), tableRowIndex, resultsRowIndex});
            }
            return searchResults.get(resultsRowIndex);
        }
    }
    
    private SearchResults<Task> getSearchResults() {
        final Window window = (Window)searchResultsPanel.getTopLevelAncestor();
        final SearchResults<Task> searchResults = (SearchResults<Task>)app.getAttributes().get(window.getName());
        return searchResults;
    }
}
