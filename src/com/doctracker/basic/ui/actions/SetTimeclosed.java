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

import com.bc.appbase.App;
import com.bc.appcore.actions.Action;
import com.bc.appcore.actions.TaskExecutionException;
import com.bc.jpa.dao.Dao;
import com.doctracker.basic.DtbApp;
import com.doctracker.basic.pu.entities.Appointment;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Task_;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 4, 2017 12:42:16 PM
 */
public class SetTimeclosed implements Action<App,Boolean> {
    
    public static final String PARAMETER_TIMECLOSED = "timeClosed";

    @Override
    public Boolean execute(App app, Map<String, Object> params) throws TaskExecutionException {
        
        final Object oval = params.get(PARAMETER_TIMECLOSED);
        final Date timeClosed;
        if(params.containsKey(PARAMETER_TIMECLOSED)) {
            timeClosed = oval == null ? null : (Date)oval;
        }else{
            timeClosed = oval == null ? new Date() : (Date)oval;
        }
        
        final List taskidList = (List)params.get(Task_.taskid.getName()+"List");
        Objects.requireNonNull(taskidList);

        final List<Appointment> apptList = new ArrayList<>();

        for(Object taskid : taskidList) {

            final Dao dao = app.getDao(Task.class);

            final Task managedEntity = dao.find(Task.class, taskid);

            managedEntity.setTimeclosed(timeClosed);

            dao.begin().mergeAndClose(managedEntity);
            app.getSlaveUpdates().addMerge(managedEntity);

            apptList.add(managedEntity.getReponsibility());
        }

//            ((DtbApp)app).updateReports(apptList, true);
        ((DtbApp)app).updateReports(true);

        return Boolean.TRUE;
    }
}
