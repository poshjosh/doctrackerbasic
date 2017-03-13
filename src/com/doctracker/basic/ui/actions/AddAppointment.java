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

import com.bc.jpa.dao.BuilderForSelect;
import com.doctracker.basic.parameter.ParameterNotFoundException;
import com.doctracker.basic.pu.entities.Appointment;
import com.doctracker.basic.pu.entities.Appointment_;
import com.doctracker.basic.pu.entities.Unit;
import com.doctracker.basic.pu.entities.Unit_;
import com.doctracker.basic.ui.AppointmentPanel;
import com.doctracker.basic.ui.SelectAppointmentOrCreateNewPanel;
import java.awt.Dimension;
import java.awt.Window;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 26, 2017 8:53:37 PM
 */
public class AddAppointment implements Action<Appointment> {
    
    private final static Logger logger = Logger.getLogger(AddAppointment.class.getName());

    @Override
    public Appointment execute(App app, Map<String, Object> params) throws TaskExecutionException {

        try{
            
            app.getAttributes().remove(AddAppointment.class.getName()+'#'+Appointment.class.getName());
            
            Appointment found = this.get(app, params, Appointment_.appointment.getName(), Appointment_.appointment.getName(), Appointment.class);

            final Appointment appt;
            
            if(found != null) {
                
                appt = found;
                
            }else {   

                final String apptStr = (String)params.get(Appointment_.appointment.getName());
                if(this.isNullOrEmpty(apptStr)) {
                    throw new ParameterNotFoundException(Appointment_.appointment.getName());
                }
                
                final String abbrevStr = (String)params.get(Appointment_.abbreviation.getName());
                if(this.isNullOrEmpty(abbrevStr)) {
                    throw new ParameterNotFoundException(Appointment_.abbreviation.getName());
                }
                
                final Appointment parent = this.get(app, params, Appointment_.parentappointment.getName(), Appointment_.appointment.getName(), Appointment.class);
                
                Unit unit = this.get(app, params, Unit_.unit.getName(), Unit_.unit.getName(),Unit.class);
                if(unit == null) {
                    final String name = AddUnit.class.getName()+'#'+Unit.class.getName();
                    unit = (Unit)app.getAttributes().get(name);
                    
                    logger.log(Level.FINE, "Unit from app attributes: {0}", unit);
                    
                    if(unit != null) {
                        app.getAttributes().remove(name);
                    }
                }
                
                appt = new Appointment();
                appt.setAbbreviation(abbrevStr);
                appt.setAppointment(apptStr);
                appt.setParentappointment(parent);
                appt.setUnit(unit);
                app.getDao().begin().persistAndClose(appt);
                app.getSlaveUpdates().addPersist(appt);
            }
            
            app.getAttributes().put(AddAppointment.class.getName()+'#'+Appointment.class.getName(), appt);

            final String name = SelectAppointmentOrCreateNewPanel.class.getName();
            final SelectAppointmentOrCreateNewPanel forUpdate = (SelectAppointmentOrCreateNewPanel)app.getAttributes().get(name);
            if(forUpdate != null) {
                DefaultListModel listModel = new DefaultListModel();
                for(String val : app.getAppointmentValuesForComboBox()) {
                    listModel.addElement(val);
                }
                final Dimension dim = forUpdate.getAppointmentsList().getPreferredSize();
                forUpdate.getAppointmentsList().setModel(listModel);
                forUpdate.getAppointmentsList().setPreferredSize(dim);
                forUpdate.getAppointmentsScrollPane().setPreferredSize(dim);
                forUpdate.getAppointmentsList().setSelectedValue(appt.getAppointment(), true);
                app.getAttributes().remove(name);
            }
            
            final AppointmentPanel forDisposal = (AppointmentPanel)params.get(AppointmentPanel.class.getName());
            if(forDisposal != null) {
                Window window = (Window)forDisposal.getTopLevelAncestor();
                if(window != null) {
                    window.setVisible(false);
                    window.dispose();
                }
            }
            
            return appt;
            
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
