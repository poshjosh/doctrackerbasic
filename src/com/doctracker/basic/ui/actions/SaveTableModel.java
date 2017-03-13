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

import com.doctracker.basic.util.WriteExcel;
import com.doctracker.basic.ConfigNames;
import com.doctracker.basic.ParamNames;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.ui.model.ColumnWidth;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.swing.table.TableModel;
import jxl.write.WritableFont;
import jxl.write.WriteException;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 2, 2017 2:06:21 PM
 */
public class SaveTableModel implements Action<File> {
    
    @Override
    public File execute(App app, Map<String, Object> params) throws TaskExecutionException {

        final Map<String, TableModel> data = (Map<String, TableModel>)params.get(ParamNames.DATA);
        
        if(data == null || data.isEmpty()) {
            
            return null;
        }

        final File file = (File)params.get(java.io.File.class.getName());
        
        try{
            
            final File parent = file.getParentFile();
            if(!parent.exists()) {
                parent.mkdirs();
            }
            if(!file.exists()) {
                file.createNewFile();
            }
            
            final Integer fontSize = app.getConfig().getInt(ConfigNames.OUTPUT_FONT_SIZE, 12);
            
            final Boolean append = params.get(ParamNames.APPEND) == null ? Boolean.FALSE : (Boolean)params.get(ParamNames.APPEND);
            
            final WriteExcel writeExcel = new WriteExcel(
                    app.getDateTimeFormat(), WritableFont.ARIAL, fontSize);
            
            final ColumnWidth columnWidths = app.getUI().getColumnWidths(Task.class, null);
            
            writeExcel.write(file, data, columnWidths, append);

        }catch(IOException | WriteException e) {

            throw new TaskExecutionException(e);
        }
        
        return file;
    }
}
