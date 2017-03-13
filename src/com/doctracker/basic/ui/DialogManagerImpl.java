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

package com.doctracker.basic.ui;

import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 2, 2017 8:23:55 AM
 */
public class DialogManagerImpl implements DialogManager {

    private final App app;
    
    private File lastSelectedFile;
    
    public DialogManagerImpl(App app) {
        this.app = Objects.requireNonNull(app);
    }
    
    @Override
    public void showErrorMessage(Throwable t, Object message) {
        JOptionPane.showMessageDialog(getUI().getMainFrame(), 
                message, "Error", JOptionPane.ERROR_MESSAGE, null);
    }

    @Override
    public void showSuccessMessage(Object message) {
        JOptionPane.showMessageDialog(getUI().getMainFrame(), 
                message, "Succes", JOptionPane.INFORMATION_MESSAGE, null);
    }

    @Override
    public PageSelection promptSelectPages(String msg) {
        
        final Object [] selectionValues = PageSelection.values();
        
        final Object selection = JOptionPane.showInputDialog(
                getUI().getMainFrame(), msg, "Select Pages", JOptionPane.QUESTION_MESSAGE, 
                null, selectionValues, selectionValues[1]);
        
        return selection == null ? null : (PageSelection)selection;
    }  
    
    @Override
    public File showDialog(int dialogType, String title, FileFilter fileFilter, int fileSelectionMode) {

        final File [] files = this.showDialog(dialogType, title, false, null, fileFilter, fileSelectionMode);
        
        return files.length == 0 ? null : files[0];
    }
    
    @Override
    public File [] showDialog(int dialogType, String title, boolean multiSelectionEnabled, 
            String approveButtonText, FileFilter fileFilter, int fileSelectionMode) {
        
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(this.getCurrentDirectory());
        fileChooser.setDialogType(dialogType);
        fileChooser.setDialogTitle(title);
        fileChooser.setMultiSelectionEnabled(multiSelectionEnabled);
        if(fileFilter != null) {
            fileChooser.setFileFilter(fileFilter);
        }
        if(fileSelectionMode != -1) {
            fileChooser.setFileSelectionMode(fileSelectionMode);
        }
        
        fileChooser.setVisible(true);
        
        final int selection = fileChooser.showDialog(app.getUI().getMainFrame(), approveButtonText);
        
        File [] output;
        if(selection == JFileChooser.APPROVE_OPTION) {
            if(multiSelectionEnabled) {
                output = fileChooser.getSelectedFiles();
            }else{
                output = new File[]{fileChooser.getSelectedFile()};
            }
        }else{
            output = new File[]{};
        }
        return output;
    }
    
    private File getCurrentDirectory() {
        File output;
        if(lastSelectedFile == null) {
            output = Paths.get(System.getProperty("user.home")).toFile();
        }else{
            if(lastSelectedFile.isFile()) {
                output = lastSelectedFile.getParentFile();
            }else{
                output = lastSelectedFile;
            }
        }
        return output;
    }
    
    private UI getUI() {
        return app.getUI();
    }
}
