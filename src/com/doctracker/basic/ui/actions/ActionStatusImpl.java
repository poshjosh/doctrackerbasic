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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 19, 2017 2:54:42 PM
 */
public class ActionStatusImpl implements ActionStatus {

    private final long startTime;
    
    private final AtomicInteger busyCount;
    
    private final AtomicInteger completedAttempts;
    
    private final AtomicInteger totalPeriod;

    public ActionStatusImpl(
            long startTime, AtomicInteger busyCount, 
            AtomicInteger completedAttempts, AtomicInteger totalPeriod) {
        this.startTime = startTime;
        this.busyCount = busyCount;
        this.completedAttempts = completedAttempts;
        this.totalPeriod = totalPeriod;
    }
    
    @Override
    public long getEstimatedTimeLeft(TimeUnit timeUnit) {
        final long att = this.getAverageTimeTaken(timeUnit);
        final long ts = this.getTimeSpent(timeUnit);
        if(att < 1 || ts < 1) {
            return 0;
        }else{
            return att - ts;
        }
    }
    
    @Override
    public long getAverageTimeTaken(TimeUnit timeUnit) {
        final int nTotal = totalPeriod.get();
        final int nCompleted = completedAttempts.get();
        if(nTotal < 1 || nCompleted < 1) {
            return 0;
        }else{
            return timeUnit.convert(nTotal / nCompleted, TimeUnit.MILLISECONDS);
        }
    }
    
    @Override
    public long getTimeSpent(TimeUnit timeUnit) {
        if(startTime < 1) {
            return 0;
        }else{
            
            return timeUnit.convert(System.currentTimeMillis() - getStartTime(), TimeUnit.MILLISECONDS);
        }
    }
    
    @Override
    public long getStartTime() {
        return startTime;
    }
    
    @Override
    public boolean isAnyBusy() {
        return getBusyCount() > 0;
    }
    
    @Override
    public int getBusyCount() {
        return busyCount.get();
    }
}
