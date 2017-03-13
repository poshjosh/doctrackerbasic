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

package com.doctracker.basic.ui.actions;

import com.bc.jpa.search.SearchResults;
import com.doctracker.basic.pu.entities.Task;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 17, 2017 1:40:15 PM
 */
public class ExecuteSelectQuery extends AbstractExecuteQuery {

    @Override
    public Integer execute(App app, String sql) {
        
        if(!sql.startsWith("SELECT") && !sql.startsWith("select")) {

            app.getUI().showErrorMessage(null, "Only SELECT queries are allowed for this request");

            return -1;
        }
            
        final String KEY = sql;
        
        final SearchResults<Task> searchResults = this.getSearchResults(app, KEY, sql);
        
        final StringBuilder msg = new StringBuilder();
        msg.append("<html>");
        msg.append(sql);
        
        final int SIZE = searchResults.getSize();
        final String RESULTS_STR = SIZE == 1 ? "result" : "results";
        msg.append("<br/><tt>").append(SIZE).append(' ').append(RESULTS_STR).append("</tt>");
        
        msg.append("</html>");
        
        if(SwingUtilities.isEventDispatchThread()) {
            this.createAndShowSearchResultsFrame(app, searchResults, KEY, msg);
        }else{
            java.awt.EventQueue.invokeLater(() -> {
                try{
                    createAndShowSearchResultsFrame(app, searchResults, KEY, msg);
                }catch(RuntimeException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Unexpected exception", e);
                }
            });
        }
        
        return SIZE;
    }
    
    private void createAndShowSearchResultsFrame(App app, SearchResults<Task> searchResults, String KEY, Object msg) {
        JFrame frame = app.getUI().createSearchResultsFrame(
                searchResults, KEY, 0, 1, Task.class, msg.toString(), false);
        app.getUI().positionHalfScreenRight(frame);
        frame.pack();
        frame.setVisible(true);
    }
    
    private SearchResults<Task> getSearchResults(App app, String KEY, String sql) {
        
        SearchResults<Task> searchResults = (SearchResults)app.getAttributes().get(KEY);
        
        if(searchResults == null) {
            searchResults = app.getSearchManager(Task.class).getSearchResults(sql, Task.class);
            app.getAttributes().put(KEY, searchResults);
        }
        
        return searchResults;
    }
}
