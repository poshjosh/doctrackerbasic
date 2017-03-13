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
import com.doctracker.basic.ui.model.ResultModel;
import com.doctracker.basic.util.TextHandler;
import java.awt.Component;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 4, 2017 2:38:51 PM
 */
public class TaskTableTextCellEditor extends AbstractCellEditor implements TableCellEditor {

    private transient static final Logger logger = Logger.getLogger(TaskTableTextCellEditor.class.getName());
    
    private final App app;
    
    private final DateFormat dateFormat;
    
    private final TableTextCellComponentManager textCellManager;
    
    private final ResultModel<Task> resultModel;
    
    private int editingColumn;

    public TaskTableTextCellEditor(App app, JTextComponent component, TableCellSize tableCellSize) {
        this(app, new TableTextCellComponentManager(app, component, tableCellSize));
    }

    public TaskTableTextCellEditor(App app, TableTextCellComponentManager textCellManager) {
        this.app = app;
        this.dateFormat = app.getDateTimeFormat();
        this.textCellManager = textCellManager;
        this.resultModel = Objects.requireNonNull(app.getResultModel(Task.class, null));
    }
    
    @Override
    public Object getCellEditorValue() {
        Object output;
        final String _$temp = textCellManager.getComponent().getText();
        final String sval = _$temp == null || _$temp.isEmpty() ? null : _$temp.trim();
        if(sval == null) {
            output = null;
        }else{
            final Class colClass = resultModel.getColumnClass(editingColumn);
            if(colClass == String.class) {
                output = sval;
            }else if(colClass == Integer.class || colClass == Long.class) {
                output = Long.parseLong(sval);
            }else if(colClass == Date.class) {
                try{
                    output = this.dateFormat.parse(sval);
                }catch(ParseException e) {
                    final TextHandler th = app.getTextHandler();
                    final String dateStr = th.getLastDateStr(sval);
                    if(!th.isNullOrEmpty(dateStr)) {
                        output = th.getDate(dateStr);
                    }else{
                        output = null;
                    }
                }
            }else if (colClass == Double.class || colClass == Float.class){
                output = Double.parseDouble(sval);
            }else{
                output = sval;
            }
        }
        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Value: {0}, converted to: {1}", new Object[]{sval, output});
        }
        return output;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.editingColumn = column;
        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "[{0}:{1}]. Raw type: {2}, value: {3}", 
                    new Object[]{row, column,
                        value == null ? null : value.getClass().getName(),
                        value == null ? null : value});
        }
        return textCellManager.getComponent(table, value, isSelected, isSelected, row, column);
    }
}
