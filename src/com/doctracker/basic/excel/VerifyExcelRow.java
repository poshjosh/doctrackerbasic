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

package com.doctracker.basic.excel;

import com.bc.appbase.excel.ExcelRowHandler;
import com.doctracker.basic.pu.entities.Task;
import java.util.List;
import java.util.Set;
import jxl.Cell;
import jxl.Sheet;
import com.doctracker.basic.jpa.SelectDaoBuilder;
import java.util.Objects;
import com.doctracker.basic.DtbApp;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 1, 2017 6:33:29 PM
 */
public class VerifyExcelRow implements ExcelRowHandler<Boolean> {

    private final int refnumCol = 1;
    private final int taskDescriptionCol = 2;
    private final int apptCol = 3;
    private final int timeopenedCol = 4;
    private final int action0col = 5;
    private final int action1col = 6;
    private final int remarksCol = 7;
    
    private final DtbApp app;
    
    public VerifyExcelRow(DtbApp app) {
        this.app = Objects.requireNonNull(app);
    }

    @Override
    public Boolean handleRow(Boolean previousResult, Sheet sheet, Cell [] cells, int row, Set<Integer> failedRows) {
    
        final int [] cols = {taskDescriptionCol, action0col, action1col, remarksCol};
        
        boolean success = false;
        
        for(int col : cols) {
            
            final String str = cells[col].getContents();
            
            final SelectDaoBuilder<Task> sb = this.app.getSearchContext(Task.class).getSelectDaoBuilder();
            
            final List<Task> results = sb.textToFind(str).build().getResultsAndClose(0, 1);
        
            if(results == null || results.isEmpty()) {
                
                failedRows.add(row);

            }else{
                
                success = true;
                
                break;
            }
        }
        
        return success;
    }
}
