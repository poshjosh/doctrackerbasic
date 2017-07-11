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
import com.bc.appbase.ui.actions.ParamNames;
import com.bc.appcore.actions.Action;
import com.bc.appcore.actions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import com.doctracker.basic.ConfigNames;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 8, 2017 5:55:10 PM
 */
public class OpenReportsFolder implements Action<App,File> {
    
    @Override
    public File execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final ActionStatus actionStatus = ((SaveReports)app.getAction(DtbActionCommands.SAVE_REPORTS)).getLastStatus();
        
        final boolean proceed = new ActionStatusPrompt(app).promptUserProceed(actionStatus);
        
        final File output;
        
        if(!proceed) {
            
            output = null;
            
        }else{
            
            final Map<String, Object> map = new HashMap<>();
            map.put(ParamNames.TITLE, "Folder where reports will be automatically saved");
            map.put(ParamNames.CURRENT_DIR, app.getConfig().getString(ConfigNames.REPORT_FOLDER_PATH));

            output = (File)app.getAction(ActionCommands.DISPLAY_OPEN_DIALOG).execute(app, map);

            if(output != null && output.isFile()) {

                app.getAction(ActionCommands.OPEN_FILE).execute(
                        app, Collections.singletonMap(java.io.File.class.getName(), output));
            }
        }
        
        return output;
    }
}
