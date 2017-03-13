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
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.ui.DialogManager;
import com.doctracker.basic.ui.SearchResultsFrame;
import java.awt.Window;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 17, 2017 10:41:10 AM
 */
public class Print implements Action<Boolean> {

    @Override
    public Boolean execute(App app, Map<String, Object> params) throws TaskExecutionException {
        
        final DialogManager.PageSelection pageSelection = 
                app.getUI().getDialogManager().promptSelectPages("Which pages do you want to print?");
        
        final JTable table = (JTable)params.get(JTable.class.getName());
        
        final Boolean success;
        
        switch(pageSelection) {
            case CurrentPage:
                success = this.print(app, table); break;
            case AllPages:
                final SearchResults searchResults = this.getSearchResults(app, table);
                this.print(app, searchResults, 0, searchResults.getPageCount());
                success = true; break;
            case FirstPage:
                this.print(app, this.getSearchResults(app, table), 0, 1);
                success = true; break;
            default: 
                app.getUI().showErrorMessage(null, "Only printing of 'Current Page' or 'All Pages' or 'First Page' is supported for now");
                success = false;
        }
        
        return success;
    }
    
    public Boolean print(App app, JTable table) {
        
        try{

            PrintRequestAttributeSet set = new HashPrintRequestAttributeSet();
	    set.add(OrientationRequested.LANDSCAPE);
            
            MessageFormat header = null; //new MessageFormat("RESTRICTED"); 
            MessageFormat footer = new MessageFormat("{0}"); 
            
            if(table.print(JTable.PrintMode.FIT_WIDTH, header, footer, true, set, true)) {
            
                app.getUI().showSuccessMessage("Print Successful");
                
                return Boolean.TRUE;
            }
        }catch(PrinterException e) {
            
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Error printing table", e);
            
            app.getUI().showErrorMessage(e, "Print Failed");
        }
        
        return Boolean.FALSE;
    }
    
    public void print(App app, SearchResults<Task> searchResults, int pageNum, int numberOfPages) {
        
        if(SwingUtilities.isEventDispatchThread()) {
            this.doPrint(app, searchResults, pageNum, numberOfPages);
        }else{
            java.awt.EventQueue.invokeLater(() -> {
                try{
                    doPrint(app, searchResults, pageNum, numberOfPages);
                }catch(RuntimeException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Unexpected exception", e);
                }
            });
        }
    }
    
    private Boolean doPrint(App app, SearchResults<Task> searchResults, int pageNum, int numberOfPages) {
        final SearchResultsFrame allResultsFrame = app.getUI().createSearchResultsFrame(
                searchResults, null, pageNum, numberOfPages, Task.class, searchResults.getSize()+" results for printing", true);
        try{
            app.getUI().positionFullScreen(allResultsFrame);
            allResultsFrame.pack();
            allResultsFrame.setVisible(true);
            final JTable allResultsTable = allResultsFrame.getSearchResultsPanel().getSearchResultsTable();
            Boolean success = this.print(app, allResultsTable);
            return success;
        }finally{
            allResultsFrame.setVisible(false);
            allResultsFrame.dispose();
        }
    }
    
    public SearchResults getSearchResults(App app, JTable table) {
        final Window window = (Window)table.getTopLevelAncestor();
        final String KEY = window.getName();
        return (SearchResults)app.getAttributes().get(KEY);
    }
}
