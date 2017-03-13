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

import java.awt.Dimension;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 26, 2017 7:45:37 PM
 */
public abstract class AbstractExecuteQuery implements Action<Integer> {

    protected abstract Integer execute(App app, String sql);
    
    @Override
    public Integer execute(App app, Map<String, Object> params) throws TaskExecutionException {
        
        final Dimension dim = new Dimension(700, 300);
        
        final JTextArea textArea = new JTextArea();
        textArea.setPreferredSize(dim);
        
        final JScrollPane scrolls = new JScrollPane(textArea);
        scrolls.setPreferredSize(dim);
        
        final int selection = JOptionPane.showConfirmDialog(app.getUI().getMainFrame(), scrolls, "Enter SELECT Query to Execute", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if(selection == JOptionPane.OK_OPTION) {
        
            final String sql = textArea.getText() == null ? null : textArea.getText().trim();
            
            if(sql == null || sql.isEmpty()) {
                
                app.getUI().showErrorMessage(null, "You did not enter any query");
                
                return -1;
            }
            
            final Integer updateCount = this.execute(app, sql);
            
            app.updateOutput();
           
            return updateCount;
        }
        
        return -1;
    }
}
