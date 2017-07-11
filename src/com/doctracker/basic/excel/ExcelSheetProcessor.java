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

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import jxl.Cell;
import jxl.Sheet;

/**
 * @author Chinomso Bassey Ikwuagwu on May 4, 2017 3:53:37 PM
 */
public class ExcelSheetProcessor<T> implements Function<Sheet, Integer> {

    private final int rowOffset;
    
    private final int rowLimit;
    
    private final ExcelRowProcessor<T> excelRowProcessor;
    
    private final Consumer<T> resultHandler;
    
    private final Consumer<Throwable> errorHandler;
    
    private final Set<Integer> failedRowsBuffer;
    
    public ExcelSheetProcessor(ExcelRowProcessor<T> excelRowProcessor, 
            Consumer<T> resultHandler, Consumer<Throwable> errorHandler, Set<Integer> failedRowsBuffer) {
        this(excelRowProcessor, resultHandler, errorHandler, 0, Integer.MAX_VALUE, failedRowsBuffer);
    }
    
    public ExcelSheetProcessor(ExcelRowProcessor<T> excelRowProcessor, 
            Consumer<T> resultHandler, Consumer<Throwable> errorHandler, 
            int rowOffset, int rowLimit, Set<Integer> failedRowsBuffer) {
        this.excelRowProcessor = Objects.requireNonNull(excelRowProcessor);
        this.resultHandler = Objects.requireNonNull(resultHandler);
        this.errorHandler = Objects.requireNonNull(errorHandler);
        this.rowOffset = rowOffset;
        this.rowLimit = rowLimit;
        this.failedRowsBuffer = Objects.requireNonNull(failedRowsBuffer);
    }

    @Override
    public Integer apply(Sheet sheet) {
        
        int updateCount = 0;
        
        T previous = null;
        
        for(int row = 0, executed = 0; row < sheet.getRows(); row++, executed++) {

            if(row < rowOffset) {
                continue;
            }

            if(executed >= rowLimit) {
                break;
            }

            final Cell [] cells = sheet.getRow(row);                

            if(cells == null || cells.length < 1) {
                break;
            }

//                if(cells.length < minCols) {

//                    log("ERRO ["+row+":]\tSKIPPING ROW. Insufficient cells");

//                    failedRows.add(row);

//                    continue;
//                }

            try{

                final T result = excelRowProcessor.process(previous, sheet, cells, row, failedRowsBuffer);
                
                if(result != null) {
                    previous = result;
                }
                
                this.resultHandler.accept(result);

                ++updateCount;
                
            }catch(Throwable t) {
             
                this.errorHandler.accept(t);
            }
        }
        
        return updateCount;
    }
    
    public final ExcelRowProcessor<T> getExcelRowProcessor() {
        return excelRowProcessor;
    }
}
