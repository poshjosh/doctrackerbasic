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

import com.bc.appcore.actions.TaskExecutionException;
import java.io.IOException;
import java.util.Map;
import jxl.read.biff.BiffException;
import com.doctracker.basic.pu.entities.Doc;
import com.bc.appbase.excel.ExcelDataImporterImpl;
import com.doctracker.basic.excel.VerifyExcelRow;
import com.bc.appbase.excel.ExcelRowHandler;
import com.doctracker.basic.excel.ExtractDocFromExcelRow;
import com.bc.appbase.ui.UILog;
import com.bc.appcore.actions.Action;
import java.io.File;
import java.util.Collections;
import javax.swing.JOptionPane;
import jxl.Workbook;
import com.doctracker.basic.DtbApp;
import com.bc.appbase.App;
import com.bc.appcore.parameter.ParameterException;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 17, 2017 10:41:15 AM
 */
public class ImportExcelData implements Action<App,Boolean> {

    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        try{
            
            final File file = (File)app.getAction(DtbActionCommands.PROMPT_SELECT_EXCEL_FILE).execute(app, params);

            if(file == null) {
                return Boolean.FALSE;
            }

            final Workbook workbook = Workbook.getWorkbook(file);

            final String sheetName = (String)app.getAction(DtbActionCommands.PROMPT_SELECT_SHEETNAME).execute(
                    app, Collections.singletonMap(Workbook.class.getName(), workbook));

            if(sheetName == null) {
                return Boolean.FALSE;
            }

            final String selection = JOptionPane.showInputDialog(app.getUIContext().getMainFrame(), 
                    "Enter row number to start extracting from. Default is 0", "Enter First Row-number", JOptionPane.PLAIN_MESSAGE);

            final int rowOffset = selection == null || selection.isEmpty() ? 0 :
                    Integer.parseInt(selection);

            final UILog uiLog = new UILog("Excel Data Importer");
            
            final ExcelRowHandler<Doc> rowImporter = new ExtractDocFromExcelRow((DtbApp)app, uiLog);

            final ExcelDataImporterImpl importer = new ExcelDataImporterImpl(
                    app, rowImporter, uiLog);

            try{
            
                uiLog.show();

                importer.execute(file, sheetName, rowOffset, Integer.MAX_VALUE);

                final ExcelRowHandler<Boolean> rowVerifier = new VerifyExcelRow((DtbApp)app);

                final ExcelDataImporterImpl verifier = new ExcelDataImporterImpl(
                        app, rowVerifier, uiLog);

                verifier.execute(file, sheetName, rowOffset, Integer.MAX_VALUE);

                ((DtbApp)app).updateReports(true);
                
                uiLog.querySaveLogThenSave();
                
            }finally{
                
                uiLog.dispose();
            }
        }catch(IOException | BiffException e) {
            
            throw new TaskExecutionException(e);
        }
        
        return Boolean.TRUE;
    }
}
