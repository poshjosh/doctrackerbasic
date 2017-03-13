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

import java.util.Map;
import javax.swing.JOptionPane;
import com.doctracker.basic.App;
import com.doctracker.basic.jpa.JpaSync;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 20, 2017 7:33:54 PM
 */
public class Exit implements Action<Boolean> {

    @Override
    public Boolean execute(App app, Map<String, Object> params) throws TaskExecutionException {

        final int selection = JOptionPane.showConfirmDialog(
                app.getUI().getMainFrame(), "Are you sure you want to exit?", 
                "Exit?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if(selection == JOptionPane.YES_OPTION) {
                      
            final JpaSync jpaSync = app.getJpaSync();

            if(!jpaSync.isRunning()) {
              
                app.shutdown();
                
                System.exit(0);
                
                return Boolean.TRUE;
            }else{

                if(SwingUtilities.isEventDispatchThread()) {
                    
                    app.getUI().dispose();
                                                  
                    this.waitForJpaSyncThenExit(app, jpaSync);
                                                   
                }else{
                                                  
                    java.awt.EventQueue.invokeLater(() -> { 
                        try{
                                                            
                            app.getUI().dispose(); 
                                                          
                            this.waitForJpaSyncThenExit(app, jpaSync);
                                                        
                        }catch(RuntimeException e) {
                            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, 
                                    "Exception while disposing UI", e);
                        }
                    });
                }
                
                return Boolean.FALSE;
            }
        }else{
                                      
            return Boolean.FALSE;
        }
    }
    
    private void waitForJpaSyncThenExit(App app, JpaSync jpaSync) {
        
        final Thread waitForJpaSyncThread = new Thread("Wait_for_JpaSync_then_exit_Thread") {
            @Override
            public synchronized void run() {

                try{

                    while(jpaSync.isRunning()) {
                        this.wait(1000);
                    }

                }catch(RuntimeException | InterruptedException e) {

                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, 
                            "Exception while waiting for JpaSync to complete", e);
                }finally{

                    this.notifyAll();

                    app.shutdown();

                    System.exit(0);
                }
            }
        };

        waitForJpaSyncThread.start();
    }
}
