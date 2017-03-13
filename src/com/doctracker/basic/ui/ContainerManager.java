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

package com.doctracker.basic.ui;

import java.awt.Container;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 10, 2017 7:15:41 PM
 * @param <T> The type of the container this manager handles
 */
public interface ContainerManager<T extends Container> {
    
    public static final ContainerManager<Container> NO_OP = new ContainerManager() {
        @Override
        public void init(Container container) { }
        @Override
        public void reset(Container container) { }
    };

    void init(T container);
    
    void reset(T container);
}
