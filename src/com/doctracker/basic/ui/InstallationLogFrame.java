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

import java.awt.Dimension;
import java.awt.HeadlessException;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 14, 2017 6:44:20 PM
 */
public class InstallationLogFrame extends JFrame {
    
    private final JScrollPane scrollPane;
    
    private final JTextArea messageTextArea;

    public InstallationLogFrame() throws HeadlessException {
        this("Installation Log", 700, 700);
    }

    public InstallationLogFrame(String title, int width, int height) throws HeadlessException {
        super(title);
        Dimension dim = new Dimension(width, height);
        this.setPreferredSize(dim);
        this.setSize(dim);
        this.messageTextArea = new JTextArea();
        this.messageTextArea.setPreferredSize(dim);
        this.messageTextArea.setSize(dim);
        this.scrollPane = new JScrollPane(messageTextArea);
        this.scrollPane.setPreferredSize(dim);
        this.scrollPane.setSize(dim);
        this.scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.getContentPane().add(this.scrollPane);
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public JTextArea getMessageTextArea() {
        return messageTextArea;
    }
}
