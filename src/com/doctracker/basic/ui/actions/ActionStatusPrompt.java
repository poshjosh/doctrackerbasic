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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 19, 2017 3:25:50 PM
 */
public class ActionStatusPrompt {
    
    private transient static final Logger logger = Logger.getLogger(ActionStatusPrompt.class.getName());
    
    private final float fontSizeEm = 1.2f;
    
    private final App app;

    public ActionStatusPrompt(App app) {
        this.app = app;
    }
    
    public boolean promptUserProceed(ActionStatus actionStatus) {
        
        boolean proceed = true;
        
        if(actionStatus.isAnyBusy()) {
            
            String timeName = "seconds";
            
            long estTimeLeft = actionStatus.getEstimatedTimeLeft(TimeUnit.SECONDS);
            
            if(estTimeLeft > 2) {
                
                if(estTimeLeft > 120) {
                    timeName = "minutes";
                    estTimeLeft = actionStatus.getEstimatedTimeLeft(TimeUnit.MINUTES);
                }
                
                if(logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "Estimated time left: {0} {1}", new Object[]{estTimeLeft, timeName});
                }
            }
            
            final StringBuilder messageHtml = new StringBuilder(200);
            
            messageHtml.append("<html><p style=\"font-size:"+fontSizeEm+"em;\">");
            messageHtml.append("... ... ...Please wait.");
            messageHtml.append("<br/>Reports are currently being saved.");
            if(estTimeLeft > 0) {
                messageHtml.append("<br/>Estimated time left: ").append(estTimeLeft).append(' ').append(timeName);
            }        
            messageHtml.append("<br/>Click OK to view reports anyway."); 
            messageHtml.append("<br/>However, results may be inconsistent!</p></html>"); 
            
            final JLabel message = new JLabel(messageHtml.toString());

            final int option = JOptionPane.showConfirmDialog(app.getUIContext().getMainFrame(), 
                    message, "Busy Saving Reports", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

            proceed = option == JOptionPane.OK_OPTION;
        }
        
        return proceed;
    }
}
