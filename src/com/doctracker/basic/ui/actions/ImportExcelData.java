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

package com.doctracker.basic.ui.actions;

import com.bc.appbase.App;
import com.bc.appbase.ui.ScreenLog;
import com.bc.appbase.ui.actions.ActionCommands;
import com.bc.appbase.ui.actions.ParamNames;
import com.bc.appbase.ui.table.model.WorksheetTableModel;
import com.bc.appcore.actions.Action;
import com.bc.appcore.actions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import com.doctracker.basic.excel.ExcelSheetProcessor;
import java.util.LinkedHashSet;
import java.util.function.Function;
import com.doctracker.basic.excel.ExcelRowProcessor;

/**
 * @author Chinomso Bassey Ikwuagwu on May 1, 2017 8:28:32 PM
 */
public abstract class ImportExcelData implements Action<App,Boolean> {
    
    public abstract ExcelRowProcessor getRowImporter(App app, ScreenLog uiLog);

    public abstract ExcelRowProcessor getRowVerifier(App app, ScreenLog uiLog);
    
    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        try{
            
            final File file = (File)app.getAction(ActionCommands.PROMPT_SELECT_EXCEL_FILE).execute(app, params);

            if(file == null) {
                return Boolean.FALSE;
            }

            final Workbook workbook = Workbook.getWorkbook(file);

            final String sheetName = (String)app.getAction(ActionCommands.PROMPT_SELECT_SHEETNAME).execute(
                    app, Collections.singletonMap(Workbook.class.getName(), workbook));

            if(sheetName == null) {
                return Boolean.FALSE;
            }

            final Sheet sheet = workbook.getSheet(sheetName);
            
            final int rowOffset = this.promptSelectRowOffset(app, sheet, 0);
            
            final Map<String, Object> d2eParams = new HashMap(8, 0.75f);
            d2eParams.put(ParamNames.SHEET, sheet);
            d2eParams.put(ParamNames.OFFSET, rowOffset);
            
//            final Map<Integer, String> output = (Map<Integer, String>)app.getAction(
//                    ActionCommands.MATCH_EXCEL_TO_DATABASE_COLUMNS).execute(app, d2eParams);

            final ScreenLog uiLog = new ScreenLog("Excel Data Importer");
            
            final ExcelRowProcessor rowImporter = this.getRowImporter(app, uiLog);

            final Set<Integer> failedImports = new LinkedHashSet();
            
            final Function<Sheet, Integer> importer = new ExcelSheetProcessor(
                    rowImporter, null, null, rowOffset + 1, Integer.MAX_VALUE, failedImports);

            try{
            
                uiLog.show(); 

                final Integer importCount = importer.apply(sheet);
                
                final int[] arrFailed = failedImports.stream().mapToInt((n) -> n +1).toArray();
                uiLog.log("Failed to import the following rows: " + Arrays.toString(arrFailed));
                
                
                uiLog.log("");
                uiLog.log("Verifying rows");
                
                final ExcelRowProcessor<Boolean> rowVerifier = this.getRowVerifier(app, uiLog);

                final Set<Integer> failedVerifications = new LinkedHashSet();

                final Function<Sheet, Integer> verifier = new ExcelSheetProcessor(
                        rowVerifier, null, null, rowOffset + 1, Integer.MAX_VALUE, failedVerifications);

                final Integer verificationCount = verifier.apply(sheet);
                final int [] arr = failedVerifications.stream().mapToInt((n) -> n +1).toArray();
                uiLog.log("Failed to verify the following rows: " + Arrays.toString(arr));
                
                uiLog.querySaveLogThenSave("import");
                
            }finally{
                
                uiLog.hideAndDispose();
            }
        }catch(IOException | BiffException e) {
            
            throw new TaskExecutionException(e);
        }
        
        return Boolean.TRUE;
    }
    
    public int promptSelectRowOffset(App app, Sheet sheet, int outputIfNone) 
            throws ParameterException, TaskExecutionException {
        
        final int offset = 0;
        final int limit = Math.min(sheet.getRows(), 10);
        final WorksheetTableModel tableModel = 
                new WorksheetTableModel(sheet, offset, limit);
        
        final JTable table = new JTable(tableModel);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        final JScrollPane scrolls = new JScrollPane(table);
        
        JOptionPane.showMessageDialog(app.getUIContext().getMainFrame(), scrolls, 
                "Select row number to start extracting from. Default is " + outputIfNone, 
                JOptionPane.PLAIN_MESSAGE);

        final int output = table.getSelectedRow() == -1 ? outputIfNone : table.getSelectedRow();

        return output;
    }
}
