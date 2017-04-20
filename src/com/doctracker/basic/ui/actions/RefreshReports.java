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
import com.doctracker.basic.pu.entities.Appointment;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bc.appcore.actions.Action;
import com.doctracker.basic.DtbApp;
import com.bc.appbase.App;
import com.bc.appcore.parameter.ParameterException;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 8, 2017 8:55:47 AM
 */
public class RefreshReports implements Action<App,Boolean>{

    @Override
    public Boolean execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        new Thread() {
            @Override
            public void run() {
                try{
                    
                    app.getAction(DtbActionCommands.SAVE_REPORTS).execute(app, Collections.singletonMap(Appointment.class.getName()+"List", ((DtbApp)app).getBranchChiefs()));
                    
                    app.getAction(DtbActionCommands.REFRESH_REPORTS_FROM_BACKUP).execute(app, Collections.EMPTY_MAP);
                    
                    app.getUIContext().showSuccessMessage("Output has been successfully refreshed");
                    
                }catch(ParameterException | TaskExecutionException | RuntimeException e) {
                    
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Unexpected exception", e);
                    
                    app.getUIContext().showErrorMessage(e, "Unexpected error while refreshing output");
                }
            }
        }.start();
        return Boolean.TRUE;
    }
}
