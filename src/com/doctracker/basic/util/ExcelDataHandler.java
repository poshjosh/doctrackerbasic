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

package com.doctracker.basic.util;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 1, 2017 6:59:19 PM
 */
public class ExcelDataHandler<T> {

    private final App app;
    
    private final UILog uiLogger;
    
    public ExcelDataHandler(App app) {
        this(app, "Excel Data Importer", 700, 700);
    }
    
    public ExcelDataHandler(App app, String title, int width, int height) {
        this.app = Objects.requireNonNull(app);
        this.uiLogger = new UILog(title, width, height);
    }
    
    public void execute() throws IOException, BiffException {
        try{
            this.showUIToSelectRequiredArgsThenExecute();
        }finally{
            try{
                this.uiLogger.querySaveLogThenSave();
            }finally{
                this.uiLogger.dispose();
            }
        }
    }
    
    protected void showUIToSelectRequiredArgsThenExecute() throws IOException, BiffException {
        
        final javax.swing.filechooser.FileFilter fileFilter = new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".xls");
            }
            @Override
            public String getDescription() {
                return "Location to Save";
            }
        };
        
        final File file = app.getUI().getDialogManager().showDialog(
                JFileChooser.OPEN_DIALOG, "Select Excel File To Import Data From", 
                fileFilter, JFileChooser.FILES_ONLY);
        
        if(file != null) {

            if(!file.exists()) {
                
                JOptionPane.showMessageDialog(null, 
                        "The file you selected does not exist: " + file, 
                        "File Not Found", JOptionPane.WARNING_MESSAGE);
                
                this.showUIToSelectRequiredArgsThenExecute();
                
                return;
            }

            final Workbook workbook = Workbook.getWorkbook(file);
            
            final String [] sheetNames = workbook.getSheetNames();
            
            if(sheetNames == null || sheetNames.length == 0) {
                JOptionPane.showMessageDialog(null, 
                        "The workbook you selected contains No worksheets", 
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            final Object oval = JOptionPane.showInputDialog(null, "Select the worksheet to import data from", "Select Worksheet", JOptionPane.PLAIN_MESSAGE, null, sheetNames, sheetNames[0]);
            
            final String sheetName = oval == null ? null : oval.toString();

            if(sheetName == null || sheetName.isEmpty()) {
                
                JOptionPane.showMessageDialog(null, 
                        "You did not select any sheet name to import data from",
                        "Nothing Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            this.execute(file, sheetName);
        }
    }

    public void execute(File file, String sheetName) {
        
        try{
            
            final Workbook workbook = Workbook.getWorkbook(file);
            final Sheet sheet = workbook.getSheet(sheetName);
            
            final Set<Integer> failedRows = new TreeSet();
    
            T previousResult = null;
            for(int row = 0; row< sheet.getRows(); row++) {
                
                if(row == 0) {
                    continue;
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
                    
                    final T result = this.handleRow(previousResult, sheet, cells, row, failedRows);
                    
                    if(result != null) {
                        
                        previousResult = result;
                    }
                    
                }catch(Throwable t) {
                    this.log(t);
                    failedRows.add(row);
                }
            }
            
            final Set<Integer> toDisplay = new TreeSet<>();
            for(Integer row : failedRows) {
                toDisplay.add(row + 1);
            }
            
            final String msg = "The following rows were unsuccessful:\n"+toDisplay;
            JTextArea textArea = new JTextArea();
            textArea.setText(msg);
            Dimension dim = new Dimension(600, 150); 
            textArea.setPreferredSize(dim);
            JScrollPane scrolls = new JScrollPane(textArea);
            scrolls.setPreferredSize(dim);
            JFrame frame = new JFrame();
            frame.setPreferredSize(dim);
            frame.getContentPane().add(scrolls);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
            
            log(msg);
            
        }catch(IOException | BiffException e) {
            log(e);
        }
    }
    
    protected T handleRow(T previousDoc, Sheet sheet, Cell [] cells, int row, Set<Integer> failedRows) {
        return null;
    }

    public void log(Throwable t) {
        uiLogger.log(t);
    }
    
    public void log(String msg) {
        uiLogger.log(msg);
    }

    public final App getApp() {
        return app;
    }

    public final UILog getUiLogger() {
        return uiLogger;
    }
}
