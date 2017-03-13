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

import java.io.IOException;
import java.util.Map;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 11, 2017 4:54:19 AM
 */
public class About implements Action<Object> {

    @Override
    public Object execute(App app, Map<String, Object> params) throws TaskExecutionException {
        
        try{

            final JFrame frame = new JFrame();
            
            frame.setIconImage(app.getUI().getImageIcon().getImage()); 
            
            JEditorPane editor = new JEditorPane();
            editor.setContentType("text/html");
            editor.setPage(App.class.getResource("about.html"));
            frame.getContentPane().add(editor);
            
            app.getUI().positionHalfScreenRight(frame);
            frame.pack();
            frame.setVisible(true);
            
        }catch(IOException e) {
            throw new TaskExecutionException(e);
        }
        
        return null;
    }
}

