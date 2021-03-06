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
import com.doctracker.basic.pu.entities.Task;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 4, 2017 12:38:15 PM
 */
public class OpenTask implements Action<App,Boolean> {

    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final int selection = JOptionPane.showConfirmDialog(app.getUIContext().getMainFrame(), 
                "Are you sure you want to OPEN the selected task(s)?", "Confirm Open", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if(selection == JOptionPane.YES_OPTION) {

            final Map stcParams = new HashMap(params);
            stcParams.put(SetTimeclosed.PARAMETER_TIMECLOSED, null);
            
            app.getAction(DtbActionCommands.SET_TIMECLOSED).execute(app, stcParams);

            app.getUIContext().showSuccessMessage("Success");

            try{
                Map<String, Object> map = new HashMap(params);
                map.put(ParamNames.ENTITY_TYPE, Task.class);
                app.getAction(ActionCommands.REFRESH_ALL_RESULTS).execute(app, map);
            }catch(ParameterException | TaskExecutionException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Unexpected exception", e);
            }
        }
        
        return Boolean.TRUE;
    }
}

