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
import com.bc.jpa.dao.Dao;
import com.bc.appcore.parameter.InvalidParameterException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.parameter.ParameterNotFoundException;
import com.doctracker.basic.pu.entities.Appointment;
import com.doctracker.basic.pu.entities.Appointment_;
import com.doctracker.basic.pu.entities.Doc;
import com.doctracker.basic.pu.entities.Doc_;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Task_;
import com.doctracker.basic.jpa.DocDao;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bc.appcore.actions.Action;
import com.doctracker.basic.DtbApp;
import com.bc.appbase.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 12, 2017 4:07:23 PM
 */
public class AddTask implements Action<App,Task> {

    @Override
    public Task execute(App appBase, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {

        final DtbApp app = (DtbApp)appBase;
        
        app.getUIContext().getTaskFrame().getMessageLabel().setText(null);
        
        final Logger logger = Logger.getLogger(AddTask.class.getName());
        
        final Task task;
        
        try(Dao dao = app.getDao(Task.class)){
            
            dao.begin();
            
            final Doc doc;
            final Object docid = params.get(Doc_.docid.getName());
            
            logger.log(Level.FINE, "docid: {0}", docid);
            
            final String refnum = (String)params.get(Doc_.referencenumber.getName());
            final String subj = (String)params.get(Doc_.subject.getName());
            final Date datesigned = (Date)params.get(Doc_.datesigned.getName());
            
            if(docid != null) {
                doc = app.getDao(Doc.class).findAndClose(Doc.class, docid);
                logger.log(Level.FINER, "Doc: {0}", doc);
                if(doc == null) {
                    throw new InvalidParameterException(Doc_.docid.getName() + " = " + docid);
                }
            }else{
                doc = new DocDao(app.getJpaContext()).findOrCreateIfNone(datesigned, refnum, subj);
                if(doc.getDocid() == null) {
                    logger.log(Level.FINER, "Persisting: {0}", doc);
                    dao.persist(doc);
                }
            }
            
            final String resCol = Task_.reponsibility.getName();
            final String resVal = (String)params.get(resCol);
            if(resVal == null) {
                throw new ParameterNotFoundException(resCol);
            }
            
            final Appointment responsibility = dao.builderForSelect(Appointment.class)
                    .where(Appointment.class, Appointment_.appointment.getName(), resVal)
                    .createQuery()
                    .getSingleResult();
            
            if(responsibility == null) {
                throw new InvalidParameterException(resCol + " = " + resVal);
            }
            
            task = new Task();
            task.setDescription((String)params.get(Task_.description.getName()));
            task.setDoc(doc);
            task.setAuthor(app.getUser().getAppointment());
            task.setReponsibility(responsibility);
            task.setTimeopened((Date)params.get(Task_.timeopened.getName()));
            
            dao.persist(task);
            
            dao.commit();
            
            app.getSlaveUpdates().addPersist(doc);
            app.getSlaveUpdates().addPersist(task);
            
//            app.updateReports(Collections.singletonList(responsibility), true);
            app.updateReports(true);
            
            logger.log(Level.FINER, "After commit docid: {0}", doc.getDocid());
            logger.log(Level.FINER, "After commit taskid: {0}", task.getTaskid());
            
            app.getUIContext().getTaskFrame().getMessageLabel().setText("Success");
        }
        
        return task;
    }
}    