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
import com.bc.jpa.dao.BuilderForSelect;
import com.bc.appcore.parameter.ParameterNotFoundException;
import com.doctracker.basic.pu.entities.Unit;
import com.doctracker.basic.pu.entities.Unit_;
import com.doctracker.basic.ui.AppointmentPanel;
import com.doctracker.basic.ui.UnitPanel;
import java.awt.Window;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import com.bc.appcore.actions.Action;
import com.doctracker.basic.DtbApp;
import com.bc.appbase.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 26, 2017 8:53:28 PM
 */
public class AddUnit implements Action<App,Unit> {

    private static final Logger logger = Logger.getLogger(AddUnit.class.getName());

    @Override
    public Unit execute(App app, Map<String, Object> params) throws TaskExecutionException {
        
        try{
            
            app.getAttributes().remove(AddUnit.class.getName()+'#'+Unit.class.getName());
            
            Unit found = this.get(app, params, Unit_.unit.getName(), Unit_.unit.getName(),Unit.class);

            final Unit unit;
            
            if(found != null) {
                
                unit = found;
                
            }else {   

                final String unitStr = (String)params.get(Unit_.unit.getName());
                if(this.isNullOrEmpty(unitStr)) {
                    throw new ParameterNotFoundException(Unit_.unit.getName());
                }
                
                final String abbrevStr = (String)params.get(Unit_.abbreviation.getName());
                if(this.isNullOrEmpty(abbrevStr)) {
                    throw new ParameterNotFoundException(Unit_.abbreviation.getName());
                }
                
                final Unit parent = this.get(app, params, Unit_.parentunit.getName(), Unit_.unit.getName(), Unit.class);
                
                unit = new Unit();
                unit.setAbbreviation(abbrevStr);
                unit.setParentunit(parent);
                unit.setUnit(unitStr);
                app.getDao().begin().persistAndClose(unit);
                app.getSlaveUpdates().addPersist(unit);
            }
            
            app.getAttributes().put(AddUnit.class.getName()+'#'+Unit.class.getName(), unit);
            
            final String name = AppointmentPanel.class.getName();
            final AppointmentPanel forUpdate = (AppointmentPanel)app.getAttributes().get(name);
            if(forUpdate != null) {
                forUpdate.getUnitComboBox().setModel(new DefaultComboBoxModel(
                        ((DtbApp)app).getUnitValuesForComboBox()
                ));
                forUpdate.getUnitComboBox().setSelectedItem(unit.getUnit());
                app.getAttributes().remove(name);
            }
            
            final UnitPanel forDisposal = (UnitPanel)params.get(UnitPanel.class.getName());
            if(forDisposal != null) {
                Window window = (Window)forDisposal.getTopLevelAncestor();
                if(window != null) {
                    window.setVisible(false);
                    window.dispose();
                }
            }

            return unit;
            
        }catch(ParameterNotFoundException e) {
            throw new TaskExecutionException(e);
        }
    }
    
    private <T> T get(App app, Map<String, Object> params, String key, String name, Class<T> entityType) throws ParameterNotFoundException {
        final String value = (String)params.get(key);
        if(this.isNullOrEmpty(value)) {
            throw new ParameterNotFoundException(key);
        }
        try{
            return this.getDao(app, entityType)
                    .where(entityType, name, value)
                    .getSingleResultAndClose();
        }catch(javax.persistence.NoResultException ignored) {
            return null;
        } 
    }
    
    private <T> BuilderForSelect<T> getDao(App app, Class<T> entityType) {
        final BuilderForSelect<T> dao = app.getJpaContext().getBuilderForSelect(entityType);
        return dao;
    }
    
    private boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
