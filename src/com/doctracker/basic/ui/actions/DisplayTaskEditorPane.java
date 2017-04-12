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
import com.bc.jpa.dao.Dao;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Task_;
import com.doctracker.basic.ui.EditorPaneFrame;
import com.bc.appbase.ui.SearchResultsPanel;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bc.appcore.actions.Action;
import com.doctracker.basic.DtbApp;
import com.bc.appbase.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 11, 2017 3:24:17 PM
 */
public class DisplayTaskEditorPane implements Action<App,Boolean> {
    
    @Override
    public Boolean execute(final App app, final Map<String, Object> params) throws TaskExecutionException {
        
        final List taskidList = (List)params.get(Task_.taskid.getName()+"List");
        
        final SearchResultsPanel resultsPanel = (SearchResultsPanel)params.get(SearchResultsPanel.class.getName());
        
        try(final Dao dao = app.getDao()) {
            
            for(Object taskid : taskidList) {
                
                this.execute(app, dao.find(Task.class, taskid), resultsPanel);
            }        
        }
        
        return Boolean.TRUE;
    }
        
    public Object execute(final App app, Task task, SearchResultsPanel resultsPanel) throws TaskExecutionException {
        
        final String html = app.getHtmlBuilder(Task.class).with(task).build();

        final EditorPaneFrame frame = ((DtbApp)app).getUIContext().createEditorPaneFrame(resultsPanel);

        java.awt.EventQueue.invokeLater(new Runnable(){
            @Override
            public void run() {

                try{
                    
                    app.getUIContext().positionHalfScreenRight(frame);
                    
                    frame.getEditorPane().setContentType("text/html");
                    frame.getEditorPane().setText(html);

                    frame.pack();
                    frame.setVisible(true);
                    
                }catch(RuntimeException e) {
                    
                    final String msg = "Error displaying task details";
                    
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, msg, e);
                    
                    app.getUIContext().showErrorMessage(e, msg);
                }
            }
        });
        
        return null;
    }
}
