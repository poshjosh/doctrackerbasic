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

import java.awt.Dimension;
import java.awt.FontMetrics;
import javax.swing.JTable;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 4, 2017 2:52:49 PM
 */
public class TableCellSizeImpl implements TableCellSize {

    private final App app;
    
    private final int minHeight;
    
    private final int maxHeight;
    
    private static final Dimension largestDimension = new Dimension();
    
    private static int previousRow;
    
    private int interCellSpacingHeight;
    
    public TableCellSizeImpl(App app, int minHeight, int maxHeight) {
        this.app = app;
        this.minHeight= minHeight;
        this.maxHeight = maxHeight;
    }
    
    @Override
    public Dimension getPreferedSize(JTable table,
            Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        
        interCellSpacingHeight = table.getIntercellSpacing().height;
        
        largestDimension.width = table.getColumnModel().getColumn(column).getWidth();
        
        if(row != previousRow) {
            largestDimension.height = minHeight;
        }
        
        final int cellHeight = this.computeCellHeight(
                table, value, row, column, largestDimension.width, minHeight, maxHeight);
// Don't call this here        
//        if(cellHeight > largestDimension.height) {
//            table.setRowHeight(row, cellHeight);
//        }

        largestDimension.height = Math.max(largestDimension.height, cellHeight);
        
//System.out.println("================Row: "+row+", computed: " + cellHeight + ", height" + largestDimension.height);

        previousRow = row;
        
        return largestDimension;
    }

    @Override
    public int computeCellHeight(JTable table, Object value, int row, int column, int cellWidth, int minRowHeight, int maxRowHeight) {
        
        final int cellHeight;
        if(value == null) {
            
            cellHeight = minRowHeight;
            
        }else{
            
            final String sval = String.valueOf(value);
            
            final FontMetrics fm = table.getGraphics().getFontMetrics();
            
            final int lineHeight = fm.getHeight(); 
            
            final int linesInCell = fm.stringWidth(sval) / cellWidth;

// @bug There was excess space in each cell below contents. 
// I added '-4' below a temporary measure
//
//            final int i = linesInCell * lineHeight;
            final int i = linesInCell * (lineHeight - 4);
            
            if(i < minRowHeight) {
                cellHeight = minRowHeight;
            }else if(i > maxRowHeight) {
                cellHeight = maxRowHeight;
            }else{
                cellHeight = i;
            }
        }
        
        return cellHeight + (interCellSpacingHeight * 2);
    }
}

