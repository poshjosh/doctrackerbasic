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

import java.util.Calendar;
import java.util.Date;
import javax.swing.JComboBox;
import javax.swing.text.JTextComponent;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 11, 2017 1:37:19 PM
 */
public interface DateFromUIBuilder {
    
    DateFromUIBuilder ui(DateTimePanel ui);
    
    DateFromUIBuilder calendar(Calendar calendar);

    DateFromUIBuilder defaultHousrs(int hours);

    DateFromUIBuilder defaultMinutes(int minutes);

    DateFromUIBuilder hoursTextField(JTextComponent hoursTextField);
    
    DateFromUIBuilder minutesTextField(JTextComponent minutesTextField);

    DateFromUIBuilder dayTextField(JTextComponent dayTextField);
    
    DateFromUIBuilder monthComboBox(JComboBox monthComboBox);
    
    DateFromUIBuilder yearComboBox(JComboBox yearComboBox);
    
    Date build(Date outputIfNone);
}
