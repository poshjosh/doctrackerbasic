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

package com.doctracker.basic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 16, 2017 8:19:01 AM
 */
public class DateMillisTest {

    public static void main(String [] args) throws ParseException {
        
        final SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("YYYY MMM dd HH:mm:ss.SS");
        final Calendar cal_0 = Calendar.getInstance();
System.out.println("0 - Time in millis: "+cal_0.getTimeInMillis()+", date: "+sdf.format(cal_0.getTime()));        

        final Calendar clone_0 = (Calendar)cal_0.clone();
System.out.println("1 - Time in millis: "+clone_0.getTimeInMillis()+", date: "+sdf.format(clone_0.getTime()));        

        cal_0.set(2017, 2, 16, 8, 32, 30);
System.out.println("2 - Time in millis: "+cal_0.getTimeInMillis()+", date: "+sdf.format(cal_0.getTime()));        

        cal_0.clear();
System.out.println("3 - Time in millis: "+cal_0.getTimeInMillis()+", date: "+sdf.format(cal_0.getTime()));        
        

        cal_0.set(2017, 2, 16, 8, 32, 30);
System.out.println("4 - Time in millis: "+cal_0.getTimeInMillis()+", date: "+sdf.format(cal_0.getTime()));        

        final SimpleDateFormat sdf1 = new SimpleDateFormat();
        sdf1.applyPattern("YYYY MMM dd HH:mm:ss");
        final Date d = sdf1.parse("2017 Mar 16 08:32:30");
System.out.println("5 - Time in millis: "+d.getTime()+", date: "+sdf.format(d));        
        
//        final Calendar cal_1 = Calendar.getInstance();
        
//        final Calendar clone_1 = (Calendar)cal_1.clone();
    }
}
