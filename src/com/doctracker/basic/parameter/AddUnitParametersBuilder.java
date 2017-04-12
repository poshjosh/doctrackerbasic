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

package com.doctracker.basic.parameter;

import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.parameter.ParameterNotFoundException;
import com.bc.appcore.parameter.ParametersBuilder;
import com.doctracker.basic.pu.entities.Unit_;
import com.doctracker.basic.ui.UnitPanel;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import com.bc.appcore.AppCore;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 26, 2017 11:01:02 PM
 */
public class AddUnitParametersBuilder implements ParametersBuilder<UnitPanel> {

    private AppCore app;
    
    private UnitPanel ui;
    
    @Override
    public ParametersBuilder<UnitPanel> context(AppCore app) {
        this.app = app;
        return this;
    }

    @Override
    public ParametersBuilder<UnitPanel> with(UnitPanel ui) {
        this.ui = ui;
        return this;
    }
    
    @Override
    public Map<String, Object> build() throws ParameterException {
        
        final Map<String, Object> params = new HashMap();
        
        params.put(UnitPanel.class.getName(), ui);
        
        this.addFromTextField(params, Unit_.abbreviation.getName(), ui.getAbbreviationTextField());
        
        this.addFromComboBox(params, Unit_.parentunit.getName(), ui.getParentUnitComboBox());
        
        this.addFromTextField(params, Unit_.unit.getName(), ui.getUnitNameTextField());
        
        return params;
    }
    
    private void addFromTextField(Map<String, Object> params, String name, JTextField tf) throws ParameterException {
        final String text = tf.getText();
        if(!this.isNullOrEmpty(text)) {
            params.put(name, text);
        }else{
            throw new ParameterNotFoundException(name);
        }
    }
    
    private void addFromComboBox(Map<String, Object> params, String name, JComboBox cb) throws ParameterException {
        final Object selected = cb.getSelectedItem();
        if(!this.isNullOrEmpty(selected)) {
            params.put(name, selected);
        }else{
            throw new ParameterNotFoundException(name);
        }
    }
    
    private boolean isNullOrEmpty(Object obj) {
        return obj == null || "".equals(obj);
    }
}

