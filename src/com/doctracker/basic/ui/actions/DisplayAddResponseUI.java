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
import com.doctracker.basic.ui.TaskResponseFrame;
import com.doctracker.basic.ui.TaskResponsePanel;
import java.util.List;
import java.util.Map;
import com.bc.appcore.actions.Action;
import com.doctracker.basic.DtbApp;
import com.bc.appbase.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 11, 2017 2:41:04 PM
 */
public class DisplayAddResponseUI implements Action<App,Object> {
    
    @Override
    public Object execute(final App app, final Map<String, Object> params) throws TaskExecutionException {
        
        final List taskidList = (List)params.get(Task_.taskid.getName() + "List");
        try(Dao dao = app.getDao(Task.class)) {
            for(Object taskid : taskidList) {
                final Task task = dao.find(Task.class, taskid);
                this.execute(((DtbApp)app), task);
            }
        }
        return Boolean.TRUE;
    }

    public TaskResponseFrame execute(DtbApp app, final Task task) {
        
        final TaskResponseFrame frame = app.getUIContext().createTaskResponseFrame();

        final TaskResponsePanel panel = frame.getTaskResponsePanel();

        panel.getTaskidLabel().setText(String.valueOf(task.getTaskid()));

        StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("Subject: ").append(task.getDoc().getSubject()).append("<br/>");
        html.append("Task: ").append(task.getDescription()).append("<br/>");
        html.append("</html>");
        frame.getMessageLabel().setText(html.toString());

        this.beforeDisplay(app, frame);

        frame.pack();

        frame.setVisible(true);
        
        return frame;
    }
    
    public void beforeDisplay(DtbApp app, final TaskResponseFrame frame) {
        frame.getTitleLabel().setText("Add Response to Task");
        frame.getTaskResponsePanel().getAuthorCombobox().setSelectedItem(null);
    }
}
