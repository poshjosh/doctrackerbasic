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

package com.doctracker.basic;

import com.doctracker.basic.pu.entities.Appointment;
import java.util.List;
import com.bc.appbase.App;
import com.doctracker.basic.jpa.DtbSearchContext;
import com.doctracker.basic.ui.DtbUIContext;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 7, 2017 11:10:58 PM
 */
public interface DtbApp extends App {

    @Override
    User getUser();
    
    @Override
    <T> DtbSearchContext<T> getSearchContext(Class<T> resultType);

    @Override
    public DtbUIContext getUIContext();
    
    void updateOutput();
    
    void updateOutput(List<Appointment> appointmentList);
    
    String [] getAppointmentValuesForComboBox();
    
    String [] getUnitValuesForComboBox();
    
    List<Appointment> getBranchChiefs();
}
