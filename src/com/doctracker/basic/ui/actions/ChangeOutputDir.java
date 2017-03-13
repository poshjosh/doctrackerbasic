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

import com.doctracker.basic.ConfigNames;
import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import javax.swing.JFileChooser;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 3, 2017 2:43:48 PM
 */
public class ChangeOutputDir implements Action<File> {

    @Override
    public File execute(App app, Map<String, Object> params) throws TaskExecutionException {
        javax.swing.filechooser.FileFilter fileFilter = new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }
            @Override
            public String getDescription() {
                return "Select Folder";
            }
        };
        
        final File selection = app.getUI().getDialogManager().showDialog(JFileChooser.OPEN_DIALOG, 
                "Select folder where reports will be automatically saved", 
                fileFilter, JFileChooser.DIRECTORIES_ONLY);
        
        File selectedDir = selection == null ? null : selection.isFile() ? 
                selection.getParentFile() : selection;
        
        if(selectedDir != null) {
            
            final String previousDirname = app.getConfig().getString(ConfigNames.REPORT_FOLDER_PATH);
            
            final String selectedDirname = selectedDir.getPath();
            
            if(!selectedDirname.equals(previousDirname)) {
                
                app.getConfig().setString(ConfigNames.REPORT_FOLDER_PATH, selectedDirname);
                
                app.getAction(ActionCommands.REFRESH_OUTPUT_FROM_BACKUP).execute(app, Collections.EMPTY_MAP);
            }
        }
        
        return selectedDir;
    }
}
