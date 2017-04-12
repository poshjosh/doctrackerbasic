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

import com.doctracker.basic.ui.TaskResponseFrame;
import javax.swing.JComboBox;
import com.doctracker.basic.DtbApp;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 11, 2017 2:31:48 PM
 */
public class DisplayAddRemarkUI extends DisplayAddResponseUI {

    @Override
    public void beforeDisplay(DtbApp app, final TaskResponseFrame frame) {
        frame.getTitleLabel().setText("Add Remark");
        final JComboBox cb = frame.getTaskResponsePanel().getAuthorCombobox();
        cb.setSelectedItem(
               app.getUser().getAppointment().getAppointment());
        cb.setEnabled(false);
    }
}
