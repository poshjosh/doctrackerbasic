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
import com.bc.jpa.search.SearchResults;
import com.doctracker.basic.pu.entities.Task;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import com.bc.appcore.actions.Action;
import com.bc.appbase.App;
import com.bc.appbase.ui.ResultsFrame;
import com.bc.appbase.ui.UIContext;
import com.bc.appcore.jpa.SearchContext;
import com.bc.appcore.parameter.ParameterException;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 10, 2017 3:10:11 PM
 */
public class SearchAndDisplayResultsUI implements Action<App,String> {

    private static final Logger logger = Logger.getLogger(SearchAndDisplayResultsUI.class.getName());
    
    @Override
    public String execute(final App app, final Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final SearchResults<Task> searchResults = 
                (SearchResults)app.getAction(DtbActionCommands.SEARCH).execute(app, params);
        
        final String KEY = UUID.randomUUID().toString();
        
        final Object msg = app.getAction(DtbActionCommands.BUILD_SEARCHUI_MESSAGE).execute(app, params);
                
        createAndShowSearchResultsFrame(app, searchResults, KEY, msg);
        
        return KEY;
    }
    
    private void createAndShowSearchResultsFrame(App app, SearchResults<Task> searchResults, String KEY, Object msg) {

        final ResultsFrame frame = new ResultsFrame();
        
        final UIContext uiContext = app.getUIContext();
        
        final SearchContext<Task> searchContext = app.getSearchContext(Task.class);
        
        frame.loadSearchResults(uiContext, searchContext, searchResults, KEY, msg, true, true);
        
        frame.setVisible(true);
    }
}

