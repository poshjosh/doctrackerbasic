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
package com.doctracker.basic.ui;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import com.doctracker.basic.DtbApp;
import com.doctracker.basic.ui.actions.DtbActionCommands;

/**
 *
 * @author Josh
 */
public class AppointmentPanel extends javax.swing.JPanel {

    /**
     * Creates new form AppointmentPanel
     */
    public AppointmentPanel() {
        this(null);
    }

    /**
     * Creates new form UnitPanel and initializes it from
     * @param app
     */
    public AppointmentPanel(DtbApp app) {
        initComponents();
        if(app != null) {
            this.parentAppointmentComboBox.setModel(new DefaultComboBoxModel(
                    app.getAppointmentValuesForComboBox()
            ));
            this.unitComboBox.setModel(new DefaultComboBoxModel(
                    app.getUnitValuesForComboBox()
            ));
            this.addAppointmentButton.setActionCommand(DtbActionCommands.ADD_APPOINTMENT);
            this.createNewUnitForAppointmentButton.setActionCommand(DtbActionCommands.DISPLAY_UNIT_UI);
            app.getUIContext().addActionListeners(AppointmentPanel.this, 
                    this.addAppointmentButton, this.createNewUnitForAppointmentButton);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        appointmentNameLabel = new javax.swing.JLabel();
        abbreviationLabel = new javax.swing.JLabel();
        parentAppointmentLabel = new javax.swing.JLabel();
        unitLabel = new javax.swing.JLabel();
        parentAppointmentExamplesLabel = new javax.swing.JLabel();
        parentAppointmentComboBox = new javax.swing.JComboBox<>();
        appointmentNameTextField = new javax.swing.JTextField();
        abbreviationTextField = new javax.swing.JTextField();
        unitComboBox = new javax.swing.JComboBox<>();
        addAppointmentButton = new javax.swing.JButton();
        createNewUnitForAppointmentButton = new javax.swing.JButton();

        appointmentNameLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        appointmentNameLabel.setText("Appointment Name");

        abbreviationLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        abbreviationLabel.setText("Abbreviation");

        parentAppointmentLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        parentAppointmentLabel.setText("Parent Appointment");

        unitLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        unitLabel.setText("Unit");

        parentAppointmentExamplesLabel.setText("CAS is parent appt to all Branch Chiefs,AOCs and DRUs. A branch chief is parent to all directorates under that Branch");

        parentAppointmentComboBox.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        parentAppointmentComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        appointmentNameTextField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        abbreviationTextField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        unitComboBox.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        unitComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        addAppointmentButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        addAppointmentButton.setText("Add Appointment");

        createNewUnitForAppointmentButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        createNewUnitForAppointmentButton.setText("Create New Unit");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(parentAppointmentLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(unitLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(abbreviationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(appointmentNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(27, 27, 27)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(appointmentNameTextField)
                                        .addComponent(parentAppointmentComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(abbreviationTextField)))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addGap(24, 24, 24)
                                    .addComponent(unitComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(createNewUnitForAppointmentButton))))
                        .addComponent(parentAppointmentExamplesLabel))
                    .addComponent(addAppointmentButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(appointmentNameLabel)
                    .addComponent(appointmentNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(abbreviationLabel)
                    .addComponent(abbreviationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(parentAppointmentLabel)
                    .addComponent(parentAppointmentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addComponent(parentAppointmentExamplesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(unitLabel)
                    .addComponent(unitComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(createNewUnitForAppointmentButton))
                .addGap(18, 18, 18)
                .addComponent(addAppointmentButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel abbreviationLabel;
    private javax.swing.JTextField abbreviationTextField;
    private javax.swing.JButton addAppointmentButton;
    private javax.swing.JLabel appointmentNameLabel;
    private javax.swing.JTextField appointmentNameTextField;
    private javax.swing.JButton createNewUnitForAppointmentButton;
    private javax.swing.JComboBox<String> parentAppointmentComboBox;
    private javax.swing.JLabel parentAppointmentExamplesLabel;
    private javax.swing.JLabel parentAppointmentLabel;
    private javax.swing.JComboBox<String> unitComboBox;
    private javax.swing.JLabel unitLabel;
    // End of variables declaration//GEN-END:variables

    public JLabel getAbbreviationLabel() {
        return abbreviationLabel;
    }

    public JTextField getAbbreviationTextField() {
        return abbreviationTextField;
    }

    public JButton getAddAppointmentButton() {
        return addAppointmentButton;
    }

    public JLabel getAppointmentNameLabel() {
        return appointmentNameLabel;
    }

    public JTextField getAppointmentNameTextField() {
        return appointmentNameTextField;
    }

    public JButton getCreateNewUnitForAppointmentButton() {
        return createNewUnitForAppointmentButton;
    }

    public JComboBox<String> getParentAppointmentComboBox() {
        return parentAppointmentComboBox;
    }

    public JLabel getParentAppointmentExamplesLabel() {
        return parentAppointmentExamplesLabel;
    }

    public JLabel getParentAppointmentLabel() {
        return parentAppointmentLabel;
    }

    public JComboBox<String> getUnitComboBox() {
        return unitComboBox;
    }

    public JLabel getUnitLabel() {
        return unitLabel;
    }
}
