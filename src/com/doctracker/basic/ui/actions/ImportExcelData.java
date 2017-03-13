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

import com.doctracker.basic.util.ExcelDataImporter;
import java.io.IOException;
import java.util.Map;
import jxl.read.biff.BiffException;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 17, 2017 10:41:15 AM
 */
public class ImportExcelData implements Action<Object> {

    @Override
    public Object execute(App app, Map<String, Object> params) throws TaskExecutionException {
        
        try{
            
            new ExcelDataImporter(app).execute();
            
            app.updateOutput();
            
        }catch(IOException | BiffException e) {
            
            throw new TaskExecutionException(e);
        }
        
        return null;
    }
}
