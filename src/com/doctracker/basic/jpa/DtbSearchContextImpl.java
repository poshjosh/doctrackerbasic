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

import com.bc.appcore.jpa.SearchContextImpl;
import com.doctracker.basic.ConfigNames;
import com.bc.appcore.jpa.model.ResultModel;
import java.util.Objects;
import com.doctracker.basic.DtbApp;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 20, 2017 8:05:21 PM
 */
public class DtbSearchContextImpl<T> extends SearchContextImpl<T> implements DtbSearchContext<T>  {
    
    private final DtbApp app;
    
    public DtbSearchContextImpl(DtbApp app, ResultModel<T> resultModel) {
        super(app, resultModel,
                app.getConfig().getInt(ConfigNames.SEARCHRESULTS_PAGESIZE, 20),
                app.getConfig().getBoolean(ConfigNames.SEARCHRESULTS_USECACHE, true));
        this.app = Objects.requireNonNull(app);
    }

    @Override
    public SelectDaoBuilder<T> getSelectDaoBuilder(Class<T> resultType) {
        SelectDaoBuilder builder = new SelectDaoBuilderImpl();
        builder.resultType(resultType).app(app);
        return builder;
    }
}
