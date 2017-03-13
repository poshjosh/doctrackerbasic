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

package com.doctracker.basic.jpa;

import com.bc.jpa.dao.SelectDao;
import com.bc.jpa.search.BaseSearchResults;
import com.bc.jpa.search.QuerySearchResults;
import com.bc.jpa.search.SearchResults;
import com.bc.util.BatchUtils;
import com.doctracker.basic.ConfigNames;
import com.doctracker.basic.ui.model.ResultModel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 20, 2017 8:05:21 PM
 */
public class SearchManagerImpl<T> implements SearchManager<T>  {
    
    private final App app;
    
    private final ResultModel<T> resultModel;

    public SearchManagerImpl(App app, ResultModel<T> resultModel) {
        this.app = app;
        this.resultModel = resultModel;
    }

    @Override
    public String getPaginationMessage(SearchResults<T> searchResults, int numberOfPages, boolean forward, boolean firstElementZero) {
        final int PAGESIZE = searchResults.getPageSize();
        final int SIZE = searchResults.getSize();
        final int currentPage = searchResults.getPageNumber();
        final int start = BatchUtils.getStart(currentPage, PAGESIZE, SIZE, forward, firstElementZero);
        final int end = BatchUtils.getEnd((currentPage + numberOfPages) - 1, PAGESIZE, SIZE, forward, firstElementZero);
        final StringBuilder builder = new StringBuilder();
        final String RESULTS = SIZE == 1 ? " result" : " results";
        if(SIZE <= PAGESIZE) {
            builder.append(end).append(RESULTS);
        }else{
            builder.append(start).append(" to ").append(end).append(" of ").append(SIZE).append(RESULTS);
        }
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Pagination Message: {0}", builder);
        return builder.toString();
    }

    @Override
    public ResultModel<T> getResultModel() {
        return resultModel;
    }

    @Override
    public SelectDaoBuilder<T> getSelectDaoBuilder(Class<T> resultType) {
        SelectDaoBuilder builder = new SelectDaoBuilderImpl();
        builder.resultType(resultType).app(app);
        return builder;
    }

    @Override
    public SearchResults<T> getSearchResults(String sql, Class<T> resultType) {
        final EntityManager em = app.getEntityManager();
        final Query query = em.createNativeQuery(sql, resultType);
        final SearchResults searchResults = new AutoCloseableQuerySearchResults(
                em, query, this.getPageSize(), this.isUseCache());
        return searchResults;
    }
    
    private static class AutoCloseableQuerySearchResults 
            extends QuerySearchResults implements AutoCloseable {
        private final EntityManager em;
        public AutoCloseableQuerySearchResults(EntityManager em, Query query, int batchSize, boolean useCache) {
            super(query, batchSize, useCache);
            this.em = em;
        }
        @Override
        public void close() {
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public SearchResults<T> getSearchResults(SelectDao<T> dao) {
        return new BaseSearchResults(dao, this.getPageSize(), this.isUseCache());
    }

    private int getPageSize() {
        return app.getConfig().getInt(ConfigNames.SEARCHRESULTS_PAGESIZE, 20);
    }
    
    private boolean isUseCache() {
        return app.getConfig().getBoolean(ConfigNames.SEARCHRESULTS_USECACHE, true);
    }
}
