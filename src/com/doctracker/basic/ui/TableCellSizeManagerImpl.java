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
import com.doctracker.basic.ui.model.ColumnWidth;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 22, 2017 7:47:04 PM
 */
public class TableCellSizeManagerImpl implements TableCellSizeManager {

    private final App app;

    public TableCellSizeManagerImpl(App app) {
        this.app = app;
    }
    
    @Override
    public void updateCellSizes(JTable table, TableModel tableModel, ColumnWidth columnWidths, Double tableWidth) {
        
        table.setModel(tableModel);
        
        for(int col=0; col<table.getColumnCount(); col++) {
            
            final Class colClass = table.getColumnClass(col);
//System.out.println(this.getClass().getSimpleName()+". Column: "+col+", class: "+aClass);            

            final TableCellRenderer cellRenderer = app.getUI().getTableCellRenderer(Task.class, colClass);
            
            table.setDefaultRenderer(colClass, cellRenderer);
            
            final TableCellEditor cellEditor = app.getUI().getTableCellEditor(Task.class, colClass);
            
            table.setDefaultEditor(colClass, cellEditor);
        }
        
        this.updateColumnWidths(table, columnWidths, tableWidth);

        this.updateRowHeights(table, 0, table.getRowCount()); 
        
        int totalHeight = 0;
        for(int i=0; i<table.getRowCount(); i++) {
            totalHeight += table.getRowHeight(i);
        }
        
        final Dimension size = new Dimension(table.getPreferredSize().width, totalHeight);
                
        table.setPreferredSize(size);

        table.setPreferredScrollableViewportSize(size);
        
        tableModel.addTableModelListener(new UpdateRowHeightsTableAndColumnModelListener(app, table));
        
        table.getColumnModel().addColumnModelListener(new UpdateRowHeightsTableAndColumnModelListener(app, table));
    }

    /*
     * http://stackoverflow.com/questions/21723025/how-to-set-the-rowheight-dynamically-in-a-jtable
     * Auto adjust the height of rows in a JTable.
     * The only way to know the row height for sure is to render each cell
     * to determine the rendered height. After your table is populated with
     * data you may call this method.
     *
     */
    @Override
    public void updateRowHeights(JTable table, final int first, final int last) {
        for (int row = first; row < last; row++) {
            int rowHeight = table.getRowHeight(row);
            for (int column = 0; column < table.getColumnCount(); column++) {
                Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
                rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
            }
            if(rowHeight != table.getRowHeight(row)) {
//System.out.println("Row: "+row+", row height: "+rowHeight);                                
                table.setRowHeight(row, rowHeight);
            }
        }
    }
    
    @Override
    public void updateColumnWidths(JTable table, ColumnWidth columnWidths, Double tableWidth) {
        
        final float fontSize = table.getFont().getSize2D();
//System.out.println("Font size: "+fontSize);        
        
        if(tableWidth != null && !tableWidth.equals(table.getPreferredSize().width)) {
            final Dimension size = new Dimension(tableWidth.intValue(), table.getHeight());
            table.setPreferredSize(size);
            table.setPreferredScrollableViewportSize(size);
        }
//System.out.println("Table width: "+table.getPreferredSize().width);
        final double [] widths = new double[table.getColumnCount()];
        
        final int spacing = table.getIntercellSpacing().width * 2;
        
        for(int col=0; col<table.getColumnCount(); col++) {
            
            final int widthInChars = columnWidths.getColumnPreferredWidthInChars(col);
//System.out.println("Column: "+col+", width in chars: "+widthInChars);            
            widths[col] = (fontSize * widthInChars) + spacing;
        }
        
        this.setJTableColumnsWidth(table, table.getPreferredSize().width, widths);
    }
    
    public void setJTableColumnsWidth(JTable table, int tablePreferredWidth, double... percentages) {
//System.out.println(this.getClass().getSimpleName()+". Table preferred width: "+tablePreferredWidth);            
        
        double total = 0;
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            total += percentages[i];
        }

        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            
            TableColumn column = table.getColumnModel().getColumn(i);
            
            final int preferredWidth = (int)(tablePreferredWidth * (percentages[i] / total));

            column.setPreferredWidth(preferredWidth);
        }
    }
}
