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

import com.doctracker.basic.ConfigNames;
import com.doctracker.basic.parameter.InvalidParameterException;
import com.doctracker.basic.parameter.ParameterException;
import java.util.Map;
import javax.swing.JOptionPane;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 3, 2017 2:31:07 PM
 */
public class ChangeOutputFontSize implements Action<Integer> {

    @Override
    public Integer execute(App app, Map<String, Object> params) throws TaskExecutionException {
        try{
            
            final String input = JOptionPane.showInputDialog(
                    app.getUI().getMainFrame(), "Enter new font size for reports", "Enter Font Size", JOptionPane.PLAIN_MESSAGE);
            
            final Integer outputFontSize;
            
            if(input == null) {
                
                outputFontSize = app.getConfig().getInt(ConfigNames.OUTPUT_FONT_SIZE);
                
            }else{
                
                try{
                    outputFontSize = Integer.parseInt(input);
                }catch(NumberFormatException e) {
                    throw new InvalidParameterException(ConfigNames.OUTPUT_FONT_SIZE+'='+input, e);
                }
                
                app.getConfig().setInt(ConfigNames.OUTPUT_FONT_SIZE, outputFontSize);

                app.updateOutput();
            }

            return outputFontSize;
            
        }catch(ParameterException e) {
            
            throw new TaskExecutionException(e);
        }
    }
}
