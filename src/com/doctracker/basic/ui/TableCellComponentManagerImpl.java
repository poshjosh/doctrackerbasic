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

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JTable;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 4, 2017 3:24:36 PM
 */
public class TableCellComponentManagerImpl implements TableCellComponentManager {
    
    private final App app;
    
    private final Component component;
    
    private final TableCellSize cellSize;

    public TableCellComponentManagerImpl(App app, Component component, TableCellSize tableCellSize) {
        this.app = app;
        this.component = component;
        this.cellSize = tableCellSize;
    }

    @Override
    public Component getComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        
        component.setFont(table.getFont());

        if (isSelected) {
            component.setForeground(table.getSelectionForeground());
            component.setBackground(table.getSelectionBackground());
        } else {
            component.setForeground(table.getForeground());
            component.setBackground(table.getBackground());
        }
        
        final Dimension dim = cellSize.getPreferedSize(table, value, isSelected, hasFocus, row, column);
        
        this.component.setPreferredSize(dim);
        
        return component;
    }

    @Override
    public Component getComponent() {
        return component;
    }
}
