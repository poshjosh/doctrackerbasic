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
import com.doctracker.basic.ConfigNames;
import java.io.File;
import java.util.Collections;
import java.util.Map;
import com.bc.appcore.actions.Action;
import com.bc.appbase.App;
import com.bc.appcore.parameter.ParameterException;
import java.io.IOException;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 3, 2017 2:43:48 PM
 */
public class ChangeFolderForReports implements Action<App,File> {

    @Override
    public File execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final File selectedDir = (File)app.getAction(DtbActionCommands.OPEN_FOLDER_FOR_REPORTS).execute(
                app, Collections.EMPTY_MAP);
        
        if(selectedDir != null) {
            
            final String previousDirname = app.getConfig().getString(ConfigNames.REPORT_FOLDER_PATH);
            
            final String selectedDirname = selectedDir.getPath();
            
            if(!selectedDirname.equals(previousDirname)) {
                
                app.getConfig().setString(ConfigNames.REPORT_FOLDER_PATH, selectedDirname);
                
                try{
                    app.getConfigService().store();
                }catch(IOException e) {
                    throw new TaskExecutionException(e);
                }
                
                app.getAction(DtbActionCommands.REFRESH_REPORTS_FROM_BACKUP).execute(app, Collections.EMPTY_MAP);
            }
        }
        
        return selectedDir;
    }
}
