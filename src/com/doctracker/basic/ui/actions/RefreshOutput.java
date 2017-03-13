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

import com.doctracker.basic.App;
import com.doctracker.basic.pu.entities.Appointment;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 8, 2017 8:55:47 AM
 */
public class RefreshOutput implements Action<Boolean>{

    @Override
    public Boolean execute(App app, Map<String, Object> params) throws TaskExecutionException {
        new Thread() {
            @Override
            public void run() {
                try{
                    
                    app.getAction(ActionCommands.SAVE_OUTPUT).execute(
                            app, Collections.singletonMap(Appointment.class.getName()+"List", app.getBranchChiefs()));
                    
                    app.getAction(ActionCommands.REFRESH_OUTPUT_FROM_BACKUP).execute(app, Collections.EMPTY_MAP);
                    
                    app.getUI().showSuccessMessage("Output has been successfully refreshed");
                    
                }catch(TaskExecutionException | RuntimeException e) {
                    
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Unexpected exception", e);
                    
                    app.getUI().showErrorMessage(e, "Unexpected error while refreshing output");
                }
            }
        }.start();
        return Boolean.TRUE;
    }
}
