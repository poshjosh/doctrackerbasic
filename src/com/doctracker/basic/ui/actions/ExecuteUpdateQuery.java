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
import java.util.Map;
import com.doctracker.basic.DtbApp;
import com.bc.appbase.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 24, 2017 4:40:20 PM
 */
public class ExecuteUpdateQuery extends com.bc.appbase.ui.actions.ExecuteUpdateQuery {

    @Override
    public Integer execute(App app, Map<String, Object> params) throws TaskExecutionException {

        final Integer output = super.execute(app, params); 
        
        ((DtbApp)app).updateReports(true);
        
        return output;
    }
}
