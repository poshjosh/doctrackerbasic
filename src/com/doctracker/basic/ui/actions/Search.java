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
import java.util.Date;
import java.util.Map;
import com.doctracker.basic.jpa.SelectDaoBuilder;
import com.bc.appcore.actions.Action;
import com.doctracker.basic.DtbApp;
import com.bc.appbase.App;
import com.doctracker.basic.jpa.DtbSearchContext;
import com.doctracker.basic.parameter.SearchParameters;
import com.doctracker.basic.pu.entities.Task;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 6, 2017 10:49:16 PM
 */
public class Search implements Action<App,SearchResults> {
    
    @Override
    public SearchResults execute(final App app, final Map<String, Object> params) throws TaskExecutionException {
        
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Parameters: {0}", params);
        
        final Class resultType = (Class)params.get(SearchParameters.PARAM_RESULT_TYPE);
        final String query = (String)params.get(SearchParameters.PARAM_QUERY);
        final Date from = (Date)params.get(SearchParameters.PARAM_FROM);
        final Date to = (Date)params.get(SearchParameters.PARAM_TO);
        final Boolean opened = (Boolean)params.get(SearchParametersBuilder.PARAM_OPENED);
        final Boolean closed = (Boolean)params.get(SearchParametersBuilder.PARAM_CLOSED);
        final String who = (String)params.get(SearchParameters.PARAM_WHO);
        final Date deadlineFrom = (Date)params.get(SearchParametersBuilder.PARAM_DEADLINE_FROM);
        final Date deadlineTo = (Date)params.get(SearchParametersBuilder.PARAM_DEADLINE_TO);
        
        final DtbApp dtbApp = (DtbApp)app;
        
        final DtbSearchContext searchContext = dtbApp.getSearchContext(resultType);
                    
        final SelectDaoBuilder selectionBuilder = searchContext.getSelectDaoBuilder();
        
        final SelectDao selectDao = selectionBuilder
                .jpaContext(dtbApp.getJpaContext())
                .resultType(resultType==null?Task.class:resultType)
                .textToFind(query==null || query.isEmpty() ? null : query)
                .deadlineFrom(deadlineFrom)
                .deadlineTo(deadlineTo)
                .from(from)
                .to(to)
                .opened(opened==null?Boolean.TRUE:opened)
                .closed(closed==null?Boolean.FALSE:closed)
                .who(who)
                .build();
        
        final SearchResults searchResults = searchContext.getSearchResults(selectDao);

        return searchResults;
    }
}

