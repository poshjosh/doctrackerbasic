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

import com.bc.appbase.ui.SearchResultsPanel;
import com.bc.appbase.ui.SearchResultsPanelMouseRightClickListener;
import com.bc.appbase.ui.UIContexImpl;
import com.bc.table.cellui.ColumnWidths;
import com.bc.appcore.jpa.model.ResultModel;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.util.Objects;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import com.doctracker.basic.DtbApp;
import com.doctracker.basic.ui.actions.DtbActionCommands;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 10, 2017 4:51:32 PM
 */
public class DtbUIContextImpl extends UIContexImpl implements DtbUIContext {
    
    private transient final Logger logger = Logger.getLogger(DtbUIContextImpl.class.getName());

    private final DtbApp app;
    
    private final TaskFrame newTaskFrame;
    
    public DtbUIContextImpl(DtbApp app, ImageIcon icon, DtbMainFrame mainFrame, TaskFrame newTaskFrame) {
        super(app, icon, mainFrame);
        this.app = Objects.requireNonNull(app);
        this.newTaskFrame = Objects.requireNonNull(newTaskFrame);
    }
    
    @Override
    public void dispose() {
        super.dispose();
        if(this.newTaskFrame.isVisible()) {
            this.newTaskFrame.setVisible(false);
        }
        this.newTaskFrame.dispose();
    }

    @Override
    public DtbMainFrame getMainFrame() {
        return (DtbMainFrame)super.getMainFrame();
    }

    @Override
    public ColumnWidths getColumnWidths(ResultModel resultModel) {
        return new TaskColumnWidths(resultModel);
    }

    @Override
    public MouseListener getMouseListener(Container container) {
        
        if(container instanceof SearchResultsPanel) {
            
            final SearchResultsPanelMouseRightClickListener listener = 
                    new SearchResultsPanelMouseRightClickListener(app, (SearchResultsPanel)container);
            
            listener.addMenuItem("View Task", DtbActionCommands.DISPLAY_TASKEDITORPANE);
            listener.addMenuItem("Add Response", DtbActionCommands.DISPLAY_ADD_RESPONSE_UI);
            listener.addMenuItem("Add Remark", DtbActionCommands.DISPLAY_ADD_REMARK_UI);
            listener.addMenuItem("Close Task", DtbActionCommands.CLOSE_TASK);
            listener.addMenuItem("Open Task", DtbActionCommands.OPEN_TASK);
            listener.addMenuItem("Delete Task", DtbActionCommands.DELETE_TASK);
            return listener;
            
        }else{
            return new MouseAdapter() {};
        }
    }
    
    @Override
    public AppointmentPanel createAppointmentPanel() {
        return new AppointmentPanel(app);
    }

    @Override
    public UnitPanel createUnitPanel() {
        return new UnitPanel(app);
    }

    @Override
    public TaskResponseFrame createTaskResponseFrame() {
        TaskResponseFrame frame = new TaskResponseFrame();
        frame.getTaskResponsePanel().init(app);
        return frame;
    }

    @Override
    public EditorPaneFrame createEditorPaneFrame(SearchResultsPanel resultsPanel) {
        EditorPaneFrame frame = new EditorPaneFrame();
        if(this.getImageIcon() != null) {
            frame.setIconImage(this.getImageIcon().getImage());
        }
        frame.getAddResponseButton().setActionCommand(DtbActionCommands.DISPLAY_ADD_RESPONSE_UI);
        frame.getAddRemarkButton().setActionCommand(DtbActionCommands.DISPLAY_ADD_REMARK_UI);
        frame.getCloseTaskButton().setActionCommand(DtbActionCommands.CLOSE_TASK);
        frame.getDeleteTaskButton().setActionCommand(DtbActionCommands.DELETE_TASK);
        this.addActionListeners(resultsPanel, 
                frame.getAddResponseButton(),
                frame.getAddRemarkButton(),
                frame.getCloseTaskButton(),
                frame.getDeleteTaskButton());
        return frame;
    }

    @Override
    public TaskFrame getTaskFrame() {
        return newTaskFrame;
    }
}
