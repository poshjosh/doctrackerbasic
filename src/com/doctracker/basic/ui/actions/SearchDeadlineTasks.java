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

import com.bc.appcore.actions.TaskExecutionException;
import com.bc.jpa.dao.SelectDao;
import com.bc.jpa.search.SearchResults;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Taskresponse_;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bc.appcore.actions.Action;
import com.doctracker.basic.DtbApp;
import com.bc.appbase.App;
import com.bc.appbase.ui.ResultsFrame;
import com.bc.appbase.ui.UIContext;
import com.doctracker.basic.jpa.DtbSearchContext;
import com.bc.appcore.jpa.SearchContext;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 14, 2017 10:39:51 PM
 */
public class SearchDeadlineTasks implements Action<App,Boolean> {
    
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
        
        this.createAndShowSearchResultsFrame(app, searchResults, KEY, msg);
        
        return Boolean.TRUE;
    }
    
    private SearchResults<Task> getSearchResults(App app, String KEY, Date deadline) {
        
        SearchResults<Task> searchResults = app.getUIContext().getLinkedSearchResults(KEY, null);
        
        if(searchResults == null) {
            
            final DtbSearchContext<Task> searchContext = ((DtbApp)app).getSearchContext(Task.class);

            final SelectDao<Task> selectDao = searchContext.getSelectDaoBuilder()
                    .closed(false)
                    .deadlineTo(deadline).build();

            searchResults = searchContext.getSearchResults(selectDao);
            
            app.getAttributes().put(KEY, searchResults);
        }
        
        return searchResults;
    }

    private void createAndShowSearchResultsFrame(App app, SearchResults<Task> searchResults, String KEY, Object msg) {
        
        final ResultsFrame frame = new ResultsFrame();
        
        final UIContext uiContext = app.getUIContext();
        
        final SearchContext<Task> searchContext = app.getSearchContext(Task.class);
        
        frame.loadSearchResults(uiContext, searchContext, searchResults, KEY, msg, true, false);
        
        frame.setVisible(true);
    }
}

