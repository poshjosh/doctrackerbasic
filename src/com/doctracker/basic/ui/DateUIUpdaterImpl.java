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

import com.doctracker.basic.App.Months;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.text.JTextComponent;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 11, 2017 1:52:36 PM
 */
public class DateUIUpdaterImpl implements DateUIUpdater {

    @Override
    public void updateField(JTextComponent tf, Calendar cal, int field) {
        final int val = cal.get(field);
        final String text;
        if(val < 10) {
            text = "0" + val;
        }else{
            text = String.valueOf(val);
        }
        tf.setText(text);
    }
    
    @Override
    public void updateMonth(JComboBox cb, Calendar cal) {
        final boolean firstMonthNull = true;
        final Months [] values;
        final int currMonth;
        if(firstMonthNull) {
            Months [] months = App.Months.values();
            values = new Months[1 + months.length];
            values[0] = null;
            System.arraycopy(months, 0, values, 1, months.length);
            currMonth = cal.get(Calendar.MONTH) + 1;
        }else{
            values = App.Months.values();
            currMonth = cal.get(Calendar.MONTH);
        }
        cb.setModel(new DefaultComboBoxModel<>(values));
        cb.setSelectedIndex(currMonth);
    }
    
    @Override
    public void updateYear(JComboBox cb, Calendar cal) {
        final boolean firstYearNull = true;
        final int YEAR = cal.get(Calendar.YEAR); 
        final List<String> list = new ArrayList(Arrays.asList(String.valueOf(YEAR -1), String.valueOf(YEAR), String.valueOf(YEAR + 1)));
        final String [] values;
        final int currYear;
        if(firstYearNull) {
            list.add(0, null);
            values = list.toArray(new String[0]);
            currYear = 2;
        }else{
            values = list.toArray(new String[0]);
            currYear = 1;
        }
        cb.setModel(new DefaultComboBoxModel<>(values));
        cb.setSelectedIndex(currYear);
    }
}
