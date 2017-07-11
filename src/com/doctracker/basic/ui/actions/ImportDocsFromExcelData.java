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
import java.util.Map;
import com.doctracker.basic.excel.VerifyExcelRow;
import com.doctracker.basic.excel.ExtractDocFromExcelRowData;
import com.bc.appbase.ui.ScreenLog;
import com.doctracker.basic.DtbApp;
import com.bc.appbase.App;
import com.bc.appcore.parameter.ParameterException;
import com.doctracker.basic.excel.ExcelRowProcessor;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 17, 2017 10:41:15 AM
 */
public class ImportDocsFromExcelData extends ImportExcelData {

    @Override
    public ExcelRowProcessor getRowImporter(App app, ScreenLog uiLog) {
        return new ExtractDocFromExcelRowData((DtbApp)app, uiLog);
    }

    @Override
    public ExcelRowProcessor getRowVerifier(App app, ScreenLog uiLog) {
        return new VerifyExcelRow((DtbApp)app);
    }

    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final Boolean output = super.execute(app, params);
        
        ((DtbApp)app).updateReports(true);
                
        return output;
    }
}
