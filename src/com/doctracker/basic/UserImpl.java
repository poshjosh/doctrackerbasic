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
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 9, 2017 2:25:08 AM
 */
public final class UserImpl implements User, Serializable {

    private transient final App app;
    
    private final Appointment appointment;
    
    private final String name;
    
    private final boolean loggedIn;

    public UserImpl(App app, User user, boolean loggedIn) {
        this(app, user.getAppointment(), user.getName(), loggedIn);
    }
    
    public UserImpl(App app, Appointment appointment, String name, boolean loggedin) {
        this.app = app;
        this.appointment = appointment;
        this.name = name;
        this.loggedIn = loggedin;
    }

    @Override
    public Appointment getAppointment() {
        return appointment;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isLoggedIn() {
        return loggedIn;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.appointment);
        hash = 89 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserImpl other = (UserImpl) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.appointment, other.appointment)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "UserImpl{" + ", name=" + name + ", loggedIn=" + loggedIn + ", appointment=" + (appointment==null?null:appointment.getAbbreviation()) + '}';
    }
}
