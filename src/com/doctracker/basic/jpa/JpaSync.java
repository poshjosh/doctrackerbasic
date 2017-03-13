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

import java.util.Collections;
import java.util.Map;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 8, 2017 10:12:08 PM
 */
public interface JpaSync {
    
    JpaSync NO_OP = new JpaSync() {
        @Override
        public synchronized boolean isRunning() { return false; }
        @Override
        public Map<Class, Integer> sync(String puName) { return Collections.EMPTY_MAP; }
        @Override
        public Map<Class, Integer> sync(Class[] entityTypes) { return Collections.EMPTY_MAP; }
        @Override
        public Integer sync(Class entityType) { return 0; }
    };
    
    boolean isRunning();

    Map<Class, Integer> sync(String puName);

    Map<Class, Integer> sync(Class[] entityTypes);

    Integer sync(Class entityType);

}
