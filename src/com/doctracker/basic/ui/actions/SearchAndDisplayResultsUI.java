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
import com.bc.appbase.ui.SearchResultsFrame;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import com.bc.appcore.actions.Action;
import com.bc.appbase.App;
import com.bc.appcore.jpa.SearchContext;
import com.bc.appcore.parameter.ParameterException;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 10, 2017 3:10:11 PM
 */
public class SearchAndDisplayResultsUI implements Action<App,String> {
    
    @Override
    public String execute(final App app, final Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final SearchResults<Task> searchResults = 
                (SearchResults)app.getAction(DtbActionCommands.SEARCH).execute(app, params);
        
        final String KEY = UUID.randomUUID().toString();
        
        app.getAttributes().put(KEY, searchResults);
        
        final Object msg = app.getAction(DtbActionCommands.BUILD_SEARCHUI_MESSAGE).execute(app, params);
                
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
        
        return KEY;
    }
    
    private void createAndShowSearchResultsFrame(App app, SearchResults<Task> searchResults, String KEY, Object msg) {
        final SearchContext<Task> searchContext = app.getSearchContext(Task.class);
        final SearchResultsFrame frame = app.getUIContext().createSearchResultsFrame(
                searchContext, searchResults, KEY, 0, 1, msg.toString(), true);
        app.getUIContext().positionHalfScreenRight(frame);
        frame.pack();
        frame.setVisible(true);
    }
}

