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
import com.bc.appbase.ui.actions.ActionCommands;
import com.bc.appcore.actions.Action;
import com.bc.appcore.actions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import com.doctracker.basic.ConfigNames;
import com.doctracker.basic.FileNames;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 18, 2017 12:13:53 PM
 */
public class DisplayQueryTodayReport implements Action<App, File> {

    @Override
    public File execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final ActionStatus actionStatus = ((SaveReports)app.getAction(DtbActionCommands.SAVE_REPORTS)).getLastStatus();
        
        final boolean proceed = new ActionStatusPrompt(app).promptUserProceed(actionStatus);
        
        final File output;
        
        if(!proceed) {
            
            output = null;
            
        }else{
            
            final String targetDir = app.getConfig().getString(ConfigNames.REPORT_FOLDER_PATH);

            final Path path = Paths.get(targetDir, 
                    FileNames.REPORT_QUERY_TODAY_FILE_ID+'.'+FileNames.REPORT_FILE_EXT);

            final File file = path.toFile();

            final Boolean success = (Boolean)app.getAction(ActionCommands.OPEN_FILE).execute(
                    app, Collections.singletonMap(java.io.File.class.getName(), file));

            if(success) {
                output = file;
            }else{
                return null;
            }
        }
        
        return output;
    }
}
