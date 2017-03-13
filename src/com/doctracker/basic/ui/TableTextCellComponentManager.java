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

import com.doctracker.basic.pu.entities.Task;
import java.awt.Component;
import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 4, 2017 3:32:23 PM
 */
public class TableTextCellComponentManager extends TableCellComponentManagerImpl {
    
    private final DateFormat dateFormat;
    
    private final int serialColumnIndex;
    
    public TableTextCellComponentManager(App app, JTextComponent component, TableCellSize tableCellSize) {
        super(app, component, tableCellSize);
        this.dateFormat = app.getDateTimeFormat();
        this.serialColumnIndex = app.getResultModel(Task.class, null).getSerialColumnIndex();
    }

    @Override
    public JTextComponent getComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        
        final JTextComponent component = (JTextComponent)super.getComponent(table, value, isSelected, hasFocus, row, column);
        
        if(column == this.serialColumnIndex) {
            value = value + ".";
        }else if( value instanceof Date) {
            Date date = (Date)value;
            if(date.getTime() < TimeUnit.DAYS.toMillis(1)) {
                value = null;
            }else{
                value = dateFormat.format(date); 
            }
        }
        
//System.out.println(this.getClass().getSimpleName()+". ["+row+':'+column+"] "+value);

        component.setText(value==null?null:value.toString());
        
        return component;
    }

    @Override
    public JTextComponent getComponent() {
        return (JTextComponent)super.getComponent();
    }
}
