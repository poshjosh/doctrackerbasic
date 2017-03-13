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

import com.bc.jpa.dao.Dao;
import com.doctracker.basic.pu.entities.Appointment;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Task_;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 13, 2017 12:09:41 PM
 */
public class CloseTask implements Action<Object> {

    @Override
    public Object execute(App app, Map<String, Object> params) throws TaskExecutionException {
        
        final int selection = JOptionPane.showConfirmDialog(app.getUI().getMainFrame(), 
                "Are you sure you want to close the selected task(s)?", "Confirm Close", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if(selection == JOptionPane.YES_OPTION) {
            
            final List taskidList = (List)params.get(Task_.taskid.getName()+"List");
            
            final List<Appointment> apptList = new ArrayList<>();

            final Date timeClosed = new Date();

            for(Object taskid : taskidList) {

                final Dao dao = app.getDao();

                final Task managedEntity = dao.find(Task.class, taskid);

                managedEntity.setTimeclosed(timeClosed);

                dao.begin().mergeAndClose(managedEntity);
                app.getSlaveUpdates().addMerge(managedEntity);
                
                apptList.add(managedEntity.getReponsibility());
            }
            
//            app.updateOutput(apptList);
            app.updateOutput();

            app.getUI().showSuccessMessage("Success");
        }
        
        return null;
    }
}
