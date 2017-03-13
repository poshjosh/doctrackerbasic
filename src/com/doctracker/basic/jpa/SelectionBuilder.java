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

import com.doctracker.basic.App;
import com.doctracker.basic.pu.entities.Appointment;
import java.util.Date;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 10, 2017 3:43:25 PM
 */
public interface SelectionBuilder<T> {

    SelectionBuilder<T> app(App app);
    
    SelectionBuilder<T> query(String query);
    
    SelectionBuilder<T> from(Date from);
    
    SelectionBuilder<T> to(Date to);
    
    SelectionBuilder<T> opened(boolean opened);
    
    SelectionBuilder<T> closed(boolean closed);
    
    SelectionBuilder<T> who(String who);
    
    SelectionBuilder<T> who(Appointment who);
    
    SelectionBuilder<T> deadlineFrom(Date deadlineFrom);
    
    SelectionBuilder<T> deadlineTo(Date deadlineTo);
    
    T build();
}