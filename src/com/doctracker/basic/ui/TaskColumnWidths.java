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

import com.doctracker.basic.pu.entities.Task_;
import com.bc.ui.table.cell.ColumnWidthsImpl;
import javax.swing.table.TableModel;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 3, 2017 10:33:14 AM
 */
public class TaskColumnWidths extends ColumnWidthsImpl {
    
    public TaskColumnWidths() { }
    
    @Override
    public int getMinChars(TableModel tableModel, int columnIndex) {
        if(Task_.reponsibility.getName().equals(tableModel.getColumnName(columnIndex))) {
            return 8;
        }else{
            return super.getMinChars(tableModel, columnIndex);
        }
    }
    
    @Override
    public int getMaxChars(TableModel tableModel, int columnIndex) {
        if(Task_.reponsibility.getName().equals(tableModel.getColumnName(columnIndex))) {
            return Short.MAX_VALUE;
        }else{
            return super.getMaxChars(tableModel, columnIndex);
        }
    }
}
