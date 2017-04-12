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
import com.doctracker.basic.ConfigNames;
import com.doctracker.basic.FileNames;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bc.appcore.actions.Action;
import com.bc.appbase.App;
import com.bc.appcore.util.Util;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 3, 2017 1:33:32 PM
 */
public class RefreshOutputFromBackup implements Action<App,List<File>> {
    
    private transient static final Logger logger = Logger.getLogger(RefreshOutputFromBackup.class.getName());

    @Override
    public List<File> execute(App app, Map<String, Object> params) throws TaskExecutionException {
        
        final List<File> output = new ArrayList();
        
        final String targetDir = app.getConfig().getString(ConfigNames.REPORT_FOLDER_PATH);
        
        logger.log(Level.FINE, "Output dir: {0}", targetDir);
        
        if(!this.isNullOrEmpty(targetDir)) {
            
            final Path backupDirPath = Paths.get(app.getWorkingDir().toString(), FileNames.REPORT_BACKUP_DIR);
            final File backupDir = backupDirPath.toFile();
            final String suffix = "." + FileNames.REPORT_BACKUP_FILE_EXT;
            final String [] backupFnames = backupDir.list((File dir, String name) -> name.endsWith(suffix));
            
            for(String backupFname : backupFnames) {
                
                final Path backupPath = Paths.get(backupDirPath.toString(), backupFname);
                
                final String targetFname = Util.convertToExtension(backupFname, FileNames.REPORT_FILE_EXT);
                
                if(backupPath.toFile().exists()) {
                    
                    final Path targetPath = Paths.get(targetDir, targetFname);
                    
                    if(logger.isLoggable(Level.FINE)) {
                        logger.log(Level.FINE, "Copying from: {0} to: {1}", 
                                new Object[]{backupPath, targetPath});
                    }

                    try{

                        final File targetFile = Util.createFiles(targetPath.toString())[0];

                        Files.copy(backupPath, targetPath, StandardCopyOption.REPLACE_EXISTING);

                        output.add(targetFile);

                    }catch(IOException e) {
                        
                        if(e instanceof java.nio.file.FileSystemException && 
                                e.getMessage().toLowerCase().contains("cannot access")) {
                            
                            app.getUIContext().showErrorMessage(e, 
                                    "Please close this file and retry: " + targetPath);
                        }
                        
                        logger.log(Level.WARNING, "Copy failed from: " + backupPath + ", to: " + targetPath, e);
                    }
                }
            }
        }
       
        return output;
    }
    
    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
