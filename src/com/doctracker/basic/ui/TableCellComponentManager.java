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

import java.awt.Component;
import javax.swing.JTable;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 4, 2017 3:40:25 PM
 */
public interface TableCellComponentManager {

    Component getComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column);

    Component getComponent();
}
