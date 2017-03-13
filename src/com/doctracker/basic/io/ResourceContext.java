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

package com.doctracker.basic.io;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 7, 2017 12:09:39 PM
 */
public interface ResourceContext {
    
    Path getPath(String first, String... more);

    URL getResource(String path);
    
    InputStream getResourceAsStream(String path);
}
