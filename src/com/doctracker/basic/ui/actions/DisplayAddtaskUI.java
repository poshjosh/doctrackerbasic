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

import com.doctracker.basic.ui.TaskFrame;
import java.util.Map;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 10, 2017 6:39:14 PM
 */
public class DisplayAddtaskUI implements Action<Object> {
    
    @Override
    public Object execute(final App app, final Map<String, Object> params) throws TaskExecutionException {
        
        final TaskFrame frame = app.getUI().getTaskFrame();
        
        frame.pack();
        
        frame.setVisible(true);
        
        return null;
    }
}

