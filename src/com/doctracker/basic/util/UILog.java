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

package com.doctracker.basic.util;

import com.doctracker.basic.Doctrackerbasic;
import com.doctracker.basic.ui.InstallationLogFrame;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 17, 2017 11:10:45 AM
 */
public class UILog {
    
    private StringBuilder log;
    
    private InstallationLogFrame logFrame;
    
    private final String lineSeparator = System.getProperty("line.separator");
    
    public UILog(String title, int width, int height) {
        log = new StringBuilder();
        java.awt.EventQueue.invokeLater(() -> {
            logFrame = new InstallationLogFrame(title, width, height);
            logFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            logFrame.pack();
            logFrame.setVisible(true);
        });
    }
    
    public void querySaveLogThenSave() {
        if(log != null && log.length() > 0) {
            final int option = JOptionPane.showConfirmDialog(null, "Do you want to save installation log?", "Save Log?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(option == JOptionPane.YES_OPTION) {
                final File file = Paths.get(System.getProperty("user.home"), "Tasktracker_installationlog_"+System.currentTimeMillis()+".txt").toFile();
                try{
                    if(!file.exists()) {
                        file.createNewFile();
                    }
                    try(Writer out = new FileWriter(file)){
                        out.write(log.toString());
                    }
                    JOptionPane.showMessageDialog(logFrame, "Installation log saved to: "+file, "Saved Log to File", JOptionPane.INFORMATION_MESSAGE);
                }catch(IOException e) {
                    JOptionPane.showMessageDialog(logFrame, "Error saving installation log: "+e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }    
        
    public void dispose() {   
        log = null;
        if(logFrame != null) {
            logFrame.setVisible(false);
            logFrame.dispose();
        }
        logFrame = null;
    }
    
    public void log(Throwable t) {
        log(t.getLocalizedMessage());
    }    
    
    private long ld;
    public void log(Object msg) {
        if(msg == null) {
            msg = "null";
        }
        if(log != null) {
            log.append(msg).append(this.lineSeparator);
            if((System.currentTimeMillis() - ld) > TimeUnit.SECONDS.toMillis(1)) {
                ld = System.currentTimeMillis();
                if(SwingUtilities.isEventDispatchThread()) {
                    this.log();
                }else{
                    java.awt.EventQueue.invokeLater(() -> {
                        try{
                            log();
                        }catch(RuntimeException e) {
                            Logger.getLogger(Doctrackerbasic.class.getName()).log(Level.WARNING, "Error logging to installation log", e);
                        }
                    });
                }
            }
        }
    }
    
    private void log() {
        if(logFrame != null && log != null) {
            logFrame.getMessageTextArea().setText(log.toString());
        }
    }
}
