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

import java.util.Collections;
import java.util.Map;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 9, 2017 12:09:03 AM
 * @param <T> The type this class uses to build a Map of parameters
 */
public interface ParametersBuilder<T> {
    
    public static final ParametersBuilder NO_OP = new ParametersBuilder() {
        @Override
        public ParametersBuilder app(App app) {
            return this;
        }
        @Override
        public ParametersBuilder with(Object source) {
            return this;
        }
        @Override
        public Map build() {
            return Collections.EMPTY_MAP;
        }
    };
    
    ParametersBuilder<T> app(App app);
    
    ParametersBuilder<T> with(T source);

    Map<String, Object> build() throws ParameterException;
}
