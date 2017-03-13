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

package com.doctracker.basic.predicates;

import java.util.Date;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 10, 2017 4:37:46 PM
 */
public class DateRangeTest implements Predicate<Date> {

    private final Date from;
    
    private final Date to;

    public DateRangeTest(Date from, Date to) {
        this.from = from;
        this.to = to;
    }
    
    @Override
    public boolean test(Date date) {
        boolean accept = false;
        if(from != null && to != null) {
            if(date != null && from.before(date) && to.after(date)) {
                accept = true;
            }
        }else if(from == null && to != null){
            if(date != null && to.after(date)) {
                accept = true;
            }
        }else if(from != null && to == null){
            if(date != null && from.before(date)) {
                accept = true;
            }
        }else{
            accept = true;
        }
        return accept;
    }
}
