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

package com.doctracker.basic.jpa.predicates;

import java.io.Serializable;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 23, 2017 9:07:17 PM
 */
public class SlavePersistenceUnitTest implements Predicate<String>, Serializable {

    @Override
    public boolean test(String persistenceUnitName) {
        return persistenceUnitName.contains("slave");
    }
}
