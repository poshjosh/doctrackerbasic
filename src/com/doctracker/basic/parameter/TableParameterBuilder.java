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

package com.doctracker.basic.parameter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.JTable;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 17, 2017 11:04:32 AM
 */
public class TableParameterBuilder implements ParametersBuilder<JTable> {

    private App app;
    
    private JTable table;
    
    @Override
    public ParametersBuilder<JTable> app(App app) {
        this.app = app;
        return this;
    }

    @Override
    public ParametersBuilder<JTable> with(JTable table) {
        this.table = table;
        return this;
    }

    @Override
    public Map<String, Object> build() {
        Map<String, Object> params = new HashMap();
        Objects.requireNonNull(this.table);
        params.put(JTable.class.getName(), this.table);
        return params;
    }

}
