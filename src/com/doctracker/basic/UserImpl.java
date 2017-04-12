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

import com.bc.appcore.UserBaseImpl;
import com.doctracker.basic.pu.entities.Appointment;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 9, 2017 2:25:08 AM
 */
public class UserImpl extends UserBaseImpl implements User, Serializable {

    private final Appointment appointment;
    
    public UserImpl(User user, boolean loggedIn) {
        this(user.getAppointment(), user.getName(), loggedIn);
    }
    
    public UserImpl(Appointment appointment, String name, boolean loggedin) {
        super(name, loggedin);
        this.appointment = appointment;
    }

    @Override
    public Appointment getAppointment() {
        return appointment;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.appointment);
        hash = 89 * hash + super.hashCode();
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
        if (!Objects.equals(this.isLoggedIn(), other.isLoggedIn())) {
            return false;
        }
        if (!Objects.equals(this.getName(), other.getName())) {
            return false;
        }
        if (!Objects.equals(this.appointment, other.appointment)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '{' + "Name=" + this.getName() + ", loggedIn=" + this.isLoggedIn() + ", appointment=" + (appointment==null?null:appointment.getAbbreviation()) + '}';
    }
}
