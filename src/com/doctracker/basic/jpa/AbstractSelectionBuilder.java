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

package com.doctracker.basic.jpa;

import com.bc.jpa.dao.BuilderForSelect;
import com.bc.jpa.dao.Criteria;
import com.doctracker.basic.App;
import com.doctracker.basic.pu.entities.Appointment;
import com.doctracker.basic.pu.entities.Appointment_;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 10, 2017 3:57:32 PM
 */
public abstract class AbstractSelectionBuilder<T> implements SelectionBuilder<T> {

    private App app;
    private String query;
    private Date from;
    private Date to;
    private Boolean opened;
    private Boolean closed;
    private Appointment appointment;
    private Date deadlineFrom;
    private Date deadlineTo;
    
    protected AbstractSelectionBuilder() {  }
    
    @Override
    public SelectionBuilder<T> app(App app) {
        this.app = app;
        return this;
    }

    @Override
    public SelectionBuilder<T> query(String query) {
        this.query = query;
        return this;
    }

    @Override
    public SelectionBuilder<T> from(Date from) {
        this.from = from;
        return this;
    }

    @Override
    public SelectionBuilder<T> to(Date to) {
        this.to = to;
        return this;
    }

    @Override
    public SelectionBuilder<T> opened(boolean opened) {
        this.opened = opened;
        return this;
    }

    @Override
    public SelectionBuilder<T> closed(boolean closed) {
        this.closed = closed;
        return this;
    }

    @Override
    public SelectionBuilder<T> who(String who) {
        if(who != null && !who.isEmpty()) {
            try(final BuilderForSelect<Appointment> bfs = app.getJpaContext().getBuilderForSelect(Appointment.class)) {
                this.appointment = bfs.where(Appointment_.appointment.getName(), Criteria.ComparisonOperator.EQUALS, who).createQuery().getSingleResult();
            }catch(Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Unexpected error", e);
            }
        }
        return this;
    }

    @Override
    public SelectionBuilder<T> who(Appointment who) {
        this.appointment = who;
        return this;
    }

    @Override
    public SelectionBuilder<T> deadlineFrom(Date deadlineFrom) {
        this.deadlineFrom = deadlineFrom;
        return this;
    }

    @Override
    public SelectionBuilder<T> deadlineTo(Date deadlineTo) {
        this.deadlineTo = deadlineTo;
        return this;
    }

    public App getApp() {
        return app;
    }

    public String getQuery() {
        return query;
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

    public Boolean getOpened() {
        return opened;
    }

    public Boolean getClosed() {
        return closed;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public Date getDeadlineFrom() {
        return deadlineFrom;
    }

    public Date getDeadlineTo() {
        return deadlineTo;
    }
}
