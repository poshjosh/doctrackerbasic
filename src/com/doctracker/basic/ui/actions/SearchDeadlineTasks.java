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

import com.bc.jpa.dao.SelectDao;
import com.bc.jpa.search.SearchResults;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Taskresponse_;
import com.doctracker.basic.jpa.SearchManager;
import com.doctracker.basic.ui.SearchResultsFrame;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 14, 2017 10:39:51 PM
 */
public class SearchDeadlineTasks implements Action<Boolean> {
    
    @Override
    public Boolean execute(final App app, final Map<String, Object> params) throws TaskExecutionException {
        
        final Date deadline = (Date)params.get(Taskresponse_.deadline.getName());
        
        final Logger logger = Logger.getLogger(this.getClass().getName());
        
        logger.log(Level.FINE, "To search tasks with deadline after: {0}", deadline);
        
        final String KEY = Long.toString(deadline.getTime());
        
        final SearchResults searchResults = this.getSearchResults(app, KEY, deadline);
        
        StringBuilder msg = new StringBuilder();
        msg.append("<html>");
        msg.append("The following tasks have deadline in: ");
        msg.append(TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - deadline.getTime()));
        msg.append("hrs");
        
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
        
        return Boolean.TRUE;
    }
    
    private SearchResults<Task> getSearchResults(App app, String KEY, Date deadline) {
        
        SearchResults<Task> searchResults = (SearchResults<Task>)app.getAttributes().get(KEY);
        
        if(searchResults == null) {
            
            final SearchManager<Task> sm = app.getSearchManager(Task.class);

            final SelectDao<Task> selectDao = sm.getSelectDaoBuilder(Task.class)
                    .closed(false)
                    .deadlineTo(deadline).build();

            searchResults = sm.getSearchResults(selectDao);
            
            app.getAttributes().put(KEY, searchResults);
        }
        
        return searchResults;
    }

    private void createAndShowSearchResultsFrame(App app, SearchResults<Task> searchResults, String KEY, Object msg) {
        final SearchResultsFrame frame = app.getUI().createSearchResultsFrame(searchResults, KEY, 0, 1, Task.class, msg.toString(), true);
        app.getUI().positionHalfScreenRight(frame);
        frame.pack();
        frame.setVisible(true);
    }
}
