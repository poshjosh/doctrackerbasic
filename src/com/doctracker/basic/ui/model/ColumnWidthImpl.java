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

package com.doctracker.basic.ui.model;

import com.doctracker.basic.pu.entities.Task_;
import java.util.Date;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 3, 2017 10:33:14 AM
 */
public class ColumnWidthImpl implements ColumnWidth {
    
    private final ResultModel resultModel;

    public ColumnWidthImpl(ResultModel resultModel) {
        this.resultModel = resultModel;
    }
    
    @Override
    public int [] getColumnPreferredWidthInChars() {
        final int [] output = new int[resultModel.getColumnNames().size()];
        for(int i=0; i<output.length; i++) {
            output[i] = this.getColumnPreferredWidthInChars(i);
        }
        return output;
    }

    @Override
    public int getColumnPreferredWidthInChars(int columnIndex) {
        
        final Class aClass = resultModel.getColumnClass(columnIndex);

        final int widthInChars;
        if(aClass == Long.class || aClass == Integer.class || aClass == Short.class) {
            widthInChars = 4;
        }else if(aClass == Date.class) {
            widthInChars = 14;
        }else{
            final String colName = resultModel.getColumnName(columnIndex);
            if(Task_.reponsibility.getName().equals(colName)) {
                widthInChars = 14;
            }else if("Remarks".equals(colName)) {
                widthInChars = 11;
            }else{
                widthInChars = 30;
            }
        }

        return widthInChars;
    }
}
