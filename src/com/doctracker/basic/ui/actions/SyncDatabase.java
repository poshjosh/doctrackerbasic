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
import com.doctracker.basic.jpa.JpaSync;
import com.doctracker.basic.pu.entities.Appointment;
import com.doctracker.basic.pu.entities.Doc;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Taskresponse;
import com.doctracker.basic.pu.entities.Unit;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 8, 2017 10:29:57 PM
 */
public class SyncDatabase implements Action<Boolean> {

    @Override
    public Boolean execute(App app, Map<String, Object> params) throws TaskExecutionException {
        
        final JpaSync jpaSync = app.getJpaSync();
        
        if(jpaSync.isRunning()) {
            
            app.getUI().showSuccessMessage("Sync already running");
            
            return Boolean.FALSE;
            
        }else{
            
            app.getUI().showSuccessMessage("Running sync in background. You will be notified on completion");
            
            new Thread(this.getClass().getName()+"_Thread") {
                @Override
                public void run() {
                    
                    try{
                        
                        app.getSlaveUpdates().pause();

                        if(!jpaSync.isRunning()) {

                            jpaSync.sync(new Class[]{Unit.class, Appointment.class, Doc.class, Task.class, Taskresponse.class});
//                            jpaSync.sync(new Class[]{Taskresponse.class, Task.class, Doc.class, Appointment.class, Unit.class});
//                            jpaSync.sync(new Class[]{Unit.class, Appointment.class, Taskresponse.class, Task.class, Doc.class});
//                            jpaSync.sync(new Class[]{Doc.class, Task.class, Taskresponse.class, Appointment.class, Unit.class});  

                            app.getUI().showSuccessMessage("Sync successful");
                            
                        }else{
                            
                            app.getUI().showSuccessMessage("Sync already running");
                        }
                    }catch(RuntimeException e) {
                        
                        Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Unexpected exception syncing", e);
                        
                        app.getUI().showErrorMessage(e, "Sync failed");
                        
                    }finally{
                    
                        app.getSlaveUpdates().resume();
                    }
                }
            }.start();

            return Boolean.TRUE;
        }
    }
}
