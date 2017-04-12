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
import com.doctracker.basic.parameter.SearchParametersBuilder;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Task_;
import java.util.Date;
import java.util.Map;
import com.doctracker.basic.jpa.SelectDaoBuilder;
import com.bc.appcore.actions.Action;
import com.doctracker.basic.DtbApp;
import com.bc.appbase.App;
import com.doctracker.basic.jpa.DtbSearchContext;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 6, 2017 10:49:16 PM
 */
public class Search implements Action<App,SearchResults> {
    
    @Override
    public SearchResults execute(final App app, final Map<String, Object> params) throws TaskExecutionException {
        
        final String s = (String)params.get("query");
        final String query = s == null || s.isEmpty() ? null : s;
        
        final Date from = (Date)params.get("from");
        final Date to = (Date)params.get("to");
        final Boolean b = (Boolean)params.get(SearchParametersBuilder.CLOSED_TASKS);
        final boolean closed = b == null ? false : b;
        final String who = (String)params.get(Task_.reponsibility.getName());
        final Date deadlineFrom = (Date)params.get(SearchParametersBuilder.DEADLINE_FROM);
        final Date deadlineTo = (Date)params.get(SearchParametersBuilder.DEADLINE_TO);
        
        final DtbSearchContext<Task> searchContext = ((DtbApp)app).getSearchContext(Task.class);
                    
        final SelectDaoBuilder<Task> selectionBuilder = searchContext.getSelectDaoBuilder(Task.class);
        
        final SelectDao<Task> selectDao = selectionBuilder
                .query(query)
                .from(from)
                .to(to)
                .closed(closed)
                .who(who)
                .deadlineFrom(deadlineFrom)
                .deadlineTo(deadlineTo).build();
        
        final SearchResults<Task> searchResults = searchContext.getSearchResults(selectDao);

        return searchResults;
    }
}

