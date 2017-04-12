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
import com.bc.util.concurrent.NamedThreadFactory;
import com.doctracker.basic.ConfigNames;
import com.doctracker.basic.pu.entities.Taskresponse_;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ExecutorService;
import com.bc.appcore.actions.Action;
import com.bc.appbase.App;
import com.bc.appcore.parameter.ParameterException;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 19, 2017 8:45:06 PM
 */
public class ScheduleDeadlineTasksReminder implements Action<App,ExecutorService> {

    @Override
    public ExecutorService execute(App app, Map<String, Object> params) 
            throws ParameterException, TaskExecutionException {
        
        final Logger logger = Logger.getLogger(this.getClass().getName());
        
        final Integer deadlineHours = (Integer)params.get(ConfigNames.DEADLINE_HOURS);
        
        final Integer deadlineReminderIntervalHours = (Integer)params.get(ConfigNames.DEADLINE_REMINDER_INTERVAL_HOURS);
        final ScheduledExecutorService svc = Executors.newSingleThreadScheduledExecutor(
                new NamedThreadFactory(this.getClass().getName()+"_ThreadFactory"));
        
        svc.scheduleAtFixedRate(new Runnable(){
            @Override
            public void run() {
                try{
                    
                    final Date date = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(deadlineHours));
                    
                    if(logger.isLoggable(Level.FINE)) {
                        logger.log(Level.FINE, "Deadline hours: {0}, deadline: {1}", new Object[]{deadlineHours, date});
                    }

                    final Map<String, Object> params = Collections.singletonMap(
                            Taskresponse_.deadline.getName(), date);
                    
                    app.getAction(DtbActionCommands.SEARCH_DEADLINE_TASKS).execute(app, params);
                    
                }catch(ParameterException | TaskExecutionException | RuntimeException e) {
                    Logger.getLogger(this.getClass().getName()).log(
                            Level.WARNING, "Unexpected error", e);
                }
            }
        }, deadlineReminderIntervalHours, deadlineReminderIntervalHours, TimeUnit.HOURS);

        return svc;
    }
}
