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

import com.bc.jpa.search.SearchResults;
import com.doctracker.basic.ParamNames;
import com.doctracker.basic.util.Util;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.ui.DialogManager;
import com.doctracker.basic.ui.DialogManager.PageSelection;
import com.doctracker.basic.ui.model.SearchResultsTableModel;
import java.awt.Window;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 13, 2017 9:05:28 AM
 */
public class SaveTableAs implements Action<File> {

    @Override
    public File execute(App app, Map<String, Object> params) throws TaskExecutionException {

        final JTable table = Objects.requireNonNull((JTable)params.get(JTable.class.getName()));
        
        final DialogManager dialogManager = app.getUI().getDialogManager();
        
        final javax.swing.filechooser.FileFilter fileFilter = new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }
            @Override
            public String getDescription() {
                return "Location to Save";
            }
        };
        
        File file = dialogManager.showDialog(
                JFileChooser.SAVE_DIALOG, "Specify Location to Save", 
                fileFilter, JFileChooser.DIRECTORIES_ONLY);
        
        if(file == null) {
            
            return null;
        }
        
        file = new File(Util.convertToExtension(file.getPath(), "xls"));

        try{

            final PageSelection pageSelection = 
                    dialogManager.promptSelectPages("Which pages do you want to save?");
            
            final TableModel tableModel = this.getTableModel(app, table, pageSelection);

            final Map<String, Object> saveTableParams = new HashMap<>();
            saveTableParams.put(java.io.File.class.getName(), file);
            saveTableParams.put(ParamNames.DATA, Collections.singletonMap("Sheet 1", tableModel));
            saveTableParams.put(ParamNames.APPEND, Boolean.FALSE);
            
            app.getAction(ActionCommands.SAVE_TABLE_MODEL).execute(app, saveTableParams);

            app.getUI().showSuccessMessage("Table saved to: "+file);

        }catch(TaskExecutionException e) {

            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Error saving table to file: "+file, e);

            app.getUI().showErrorMessage(e, "Error saving table to file: "+file);
        }
        
        return file;
    }
    
    private TableModel getTableModel(App app, JTable table, PageSelection pageSelection) {
        final TableModel tableModel;
        switch(pageSelection) {
            case CurrentPage: tableModel = table.getModel(); 
                break;
            case AllPages: 
                tableModel = new SearchResultsTableModel(app, 
                        this.getSearchResults(app, table), 
                        app.getResultModel(Task.class, null)); 
                break;
            case FirstPage:
                tableModel = new SearchResultsTableModel(app, 
                        this.getSearchResults(app, table), 
                        app.getResultModel(Task.class, null), 0, 1); 
                break;
            default:
                throw new UnsupportedOperationException("Unexpected "+PageSelection.class.getName()+", found: "+pageSelection+", expected any of: " + Arrays.toString(PageSelection.values()));

        }
        return tableModel;
    }
    
    private SearchResults getSearchResults(App app, JTable table) {
        final Window window = (Window)table.getTopLevelAncestor();
        return (SearchResults)app.getAttributes().get(window.getName());
    }
}
