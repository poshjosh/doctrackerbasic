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

import com.doctracker.basic.ui.UnitPanel;
import java.util.Map;
import javax.swing.JFrame;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 26, 2017 9:05:05 PM
 */
public class DisplayUnitUI implements Action<UnitPanel> {
    
    @Override
    public UnitPanel execute(final App app, final Map<String, Object> params) throws TaskExecutionException {
        
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        UnitPanel unitPanel = app.getUI().createUnitPanel();
        frame.getContentPane().add(unitPanel);
        
        frame.pack();
        frame.setVisible(true);
        
        final String name = UnitPanel.class.getName();
        app.getAttributes().put(name, unitPanel);
        
        return unitPanel;
    }
}
