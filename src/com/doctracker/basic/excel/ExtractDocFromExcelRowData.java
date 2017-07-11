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

import com.bc.appbase.ui.ScreenLog;
import com.bc.appbase.ui.UIContext;
import com.bc.appbase.ui.actions.ActionCommands;
import com.bc.appbase.ui.actions.ParamNames;
import com.bc.jpa.JpaContext;
import com.bc.jpa.dao.Dao;
import com.doctracker.basic.pu.entities.Appointment;
import com.doctracker.basic.pu.entities.Appointment_;
import com.doctracker.basic.pu.entities.Doc;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Taskresponse;
import com.doctracker.basic.jpa.DocDao;
import com.doctracker.basic.jpa.TaskDao;
import com.doctracker.basic.ui.SelectAppointmentOrCreateNewPanel;
import com.doctracker.basic.ui.actions.AddAppointment;
import com.bc.appcore.actions.TaskExecutionException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.appcore.util.TextHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JTable;
import jxl.Cell;
import jxl.Sheet;
import java.util.Objects;
import com.doctracker.basic.DtbApp;
import java.awt.Window;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 13, 2017 2:27:04 PM
 */
public class ExtractDocFromExcelRowData implements ExcelRowProcessor<Doc> {
    
//            final String [] cols = {"Serial", "Reference", "Task", "Branch to take action",
//                    "Date Tracking Commenced", "Action Taken", "Additional Followup",
//                    "Remarks"
//            };
    
    private final int subjCol = 1;//-1;
    private final int refnumCol = 2;//1;
    private final int dateSignedCol = 3; //-1;
    private final int taskDescriptionCol = 5;//2;
    private final int apptCol = 4;// 3;
    private final int timeopenedCol = 6; //4;
    private final int action0col = 7; //5;
    private final int action1col = 8; //6;
    private final int remarksCol = 9;//7;
    
    private final String [] expectedAbbreviations0 =
            {"CAS", "COPP", "CTOP", "CACE", "CLOG", "COA", "COSE", "CMS", "AIR SEC", "CAB", "PASO", "AA", "DPROC"};
    private final String [] expectedAbbreviations1 =
            {"CAS", "Pol", "Trg", "AcE", "Log", "Admin", "COSE", "CMS", "AIRSEC", "CAB", "PASO", "AA", "DPROC"};
    private final String [] expectedAbbreviations2 =
            {"CAS", "Plans", "Ops", "Engr", "Comms", "DOPRI", "COSE", "CMS", "AIR-SEC", "CAB", "PASO", "AA"};
    private final String [][] expectedAbbrevArrs = {expectedAbbreviations0, expectedAbbreviations1, expectedAbbreviations2};
    
    // If null then the row will be skipped if 'date tracking commenced' can't be resolved to a date
    //
    private final Date defaultTrackStart = new Date();
    
    private final List<Appointment> appointments;
    
    private final Appointment CAS;
    
    private final TextHandler textHandler;
    
    private final DtbApp app;
    
    private final ScreenLog uiLogger;
    
    public ExtractDocFromExcelRowData(DtbApp app, ScreenLog uiLogger) {
        this.app = Objects.requireNonNull(app);
        this.appointments = app.getJpaContext().getBuilderForSelect(Appointment.class).getResultsAndClose(0, Integer.MAX_VALUE);
        this.CAS = this.appointments.get(0);
        this.textHandler = app.getOrException(TextHandler.class);
        this.uiLogger = uiLogger;
    }
    
    @Override
    public Doc process(Doc previousDoc, Sheet sheet, Cell [] cells, int row, Set<Integer> failedRows) {
        
        final String apptStr = cells[apptCol].getContents();

        // Task
        //
        String taskDesc = cells[this.taskDescriptionCol].getContents();
        if((this.textHandler.isNullOrEmpty(apptStr) || this.textHandler.isNullOrEmpty(taskDesc)) 
                && previousDoc != null && !previousDoc.getTaskList().isEmpty()) {

            List<Appointment> apptList;
            if(this.textHandler.isNullOrEmpty(apptStr)) {
                apptList = this.promptSelectAppointmentOrCreateNew(sheet, row, apptCol);
            }else{
                apptList = Collections.EMPTY_LIST;
            }
            if(apptList.isEmpty()) {

                log("ERRO ["+row+':'+apptCol+"]\tUnable to resolve any apointment. Row: "+this.getCellContents(cells));

                failedRows.add(row);

                return null;
            }

            final List<Task> previousTaskList = previousDoc.getTaskList();

            Date timeopened = null;

            if(this.textHandler.isNullOrEmpty(taskDesc)) {

                final Task lastTask = previousTaskList.get(previousTaskList.size() - 1);

                taskDesc = lastTask.getDescription();

                timeopened = lastTask.getTimeopened();

                log("INFO ["+row+":]\tJoining row to previous row with task description: " + taskDesc);
            } 

            List<String> responseList = this.getResponseList(cells, row);
            if(responseList.isEmpty()) {
                responseList = this.toResponseList(previousTaskList);
            }

            if(timeopened == null) {
                timeopened = this.getTimeopened(cells, row, this.timeopenedCol, responseList, defaultTrackStart);
            }

            log("INFO ["+row+":]\tFound adjoining row. Appointments: " + this.toAppointmentAbbrevList(apptList) + ", responses: "+responseList);

            final Boolean success = this.insertRow(previousDoc, apptList, responseList, taskDesc, timeopened, cells, row);

            if(!success) {

                log("ERRO ["+row+":]\tSKIPPING ROW. Insufficient content. Row" + this.getCellContents(cells));

                failedRows.add(row);
            }
        }else{

            final Doc doc = this.insertRow(sheet, cells, row);

            if(doc == null) {
                failedRows.add(row);
            }

            return doc;
        }
        return null;
    }
    
    private Doc insertRow(Sheet sheet, Cell [] cells, int row) {
        
        // Ref num
        //
        final String refnumRaw = cells[refnumCol].getContents();
        String refnum = null;
        final String DATED = "dated";
        final int indexOfDated;
        if(!this.textHandler.isNullOrEmpty(refnumRaw)) {
            if((indexOfDated = refnumRaw.indexOf(DATED)) != -1) {
                refnum = refnumRaw.substring(0, indexOfDated).trim();
            }else{
                refnum = refnumRaw;
            }
        }else{
            indexOfDated = -1;
        }

        String datesignedStr;
        if(dateSignedCol != -1) {
            datesignedStr = cells[this.dateSignedCol].getContents();
        }else{
            datesignedStr = null;
        }
        if(this.textHandler.isNullOrEmpty(datesignedStr)) {
            if(indexOfDated != -1) {
                datesignedStr = refnumRaw.substring(indexOfDated + DATED.length()).trim();
            }else{
                datesignedStr = refnumRaw;
            }
            datesignedStr = this.textHandler.getLastDateStr(datesignedStr);
        }
        final Date datesigned;
        if(!this.textHandler.isNullOrEmpty(datesignedStr)) {
            datesigned = this.textHandler.getDate(datesignedStr);
        }else{
            datesigned = null;
        }
        
        if(datesigned == null) {
            log("WARN ["+row+':'+1+"]\tUnable to resolve datesigned from Reference number: "+refnumRaw);
        }

        final String subj;
        if(subjCol == -1) {
            subj = "";
        }else{
            final String subjCellContents = cells[subjCol].getContents();
            subj = subjCellContents == null ? "" : subjCellContents;
        }
        Objects.requireNonNull(subj);
        
        final DocDao docDao = new DocDao(app.getJpaContext());
        final Doc doc;
        if(this.textHandler.isNullOrEmpty(refnum)) {
            log("WARN ["+row+':'+1+"]\tUnable to resolve reference number from raw value: "+refnumRaw);
            doc = docDao.create(datesigned, null, "");
        }else{
            final List<Doc> docList = docDao.find(datesigned, refnum, subj, 0, 2);
            if(docList == null || docList.isEmpty()) {
                doc = docDao.create(datesigned, refnum, subj);
            }else if(docList.size() == 1) {
                doc = docList.get(0);
            }else{
                doc = docList.get(docList.size() - 1);
                log("WARN ["+row+':'+1+"]\tFound > 1 entry for Doc with ref: "+refnum+", datesigned: "+datesigned);
            }
        }

        final List<Appointment> apptList = this.getAppointments(sheet, cells, row, apptCol);
        
        if(apptList.isEmpty()) {
            log("ERRO ["+row+':'+apptCol+"]\tUnable to resolve any apointment from 'Branch to take action'. Row: "+this.getCellContents(cells));
            return null;
        }
        
        final List<String> responseList = this.getResponseList(cells, row);

        final String taskDesc = this.getTaskDescription(cells, row, this.taskDescriptionCol, null);
        
        final Date timeopened = this.getTimeopened(cells, row, this.timeopenedCol, responseList, this.defaultTrackStart);
        
        final Boolean success = this.insertRow(doc, apptList, responseList, taskDesc, timeopened, cells, row);
        
        return success ? doc : null;
    }
    
    private Boolean insertRow(Doc doc, List<Appointment> apptList, List<String> responseList, 
            String taskDesc, Date timeopened, Cell [] cells, int row) {
        
        final List<Object> slaveUpdates = new ArrayList();
        
        try(Dao dao = app.getDao(Doc.class)) {

            if(cells.length < 4) {
                log("ERRO ["+row+":]\tSKIPPING ROW. Insufficient cells");
                return Boolean.FALSE;
            }

            dao.begin();

            boolean createdDoc = false;

            if(doc.getDocid() == null) {
                
                dao.persist(doc);
                slaveUpdates.add(doc);
                
                createdDoc = true;
            }

            if(taskDesc == null) {
                log("ERRO ["+row+":]\tSKIPPING ROW. Task description = null. Row: "+this.getCellContents(cells));
                return Boolean.FALSE;
            }
            
            final TaskDao taskDao = new TaskDao(app.getJpaContext());
            
            for(Appointment appt: apptList) {
                
                final Task task;
                if(createdDoc) {
                    task = taskDao.create(CAS, taskDesc, appt, timeopened);
                }else{
                    final List<Task> taskList = taskDao.find(CAS, taskDesc, appt);
                    if(taskList == null || taskList.isEmpty()) {
                        task = taskDao.create(CAS, taskDesc, appt, timeopened);
                    }else if(taskList.size() == 1) {
                        task = taskList.get(0);
                    }else{
                        task = taskList.get(taskList.size() - 1);
                        log("WARN ["+row+"]\tFound > 1 record with branch to take action: "+appt.getAppointment()+", for row: "+this.getCellContents(cells));
                    }
                }
                boolean createdTask = false;
                if(task.getTaskid() == null) {
                    List<Task> docTaskList = doc.getTaskList();
                    if(docTaskList == null) {
                        docTaskList = new ArrayList();
                        doc.setTaskList(docTaskList);
                    }
                    docTaskList.add(task);
                    task.setDoc(doc);
                    
                    dao.persist(task);
                    slaveUpdates.add(task);
                    
                    createdTask = true;
                }

                for(String responseStr : responseList) {

                    if(this.textHandler.isNullOrEmpty(responseStr)) {
                        continue;
                    }

                    Taskresponse response = new Taskresponse();
                    response.setAuthor(appt);
                    final String lastDateStr = this.textHandler.getLastDateStr(responseStr);
                    final Date deadline = this.textHandler.getDate(lastDateStr);
//System.out.println("-----------------------------------------------------------");                    
//System.out.println("Response string: "+responseStr+", last date string: "+lastDateStr+", deadline: "+deadline);                    
                    response.setDeadline(deadline);
                    response.setResponse(responseStr);
                    
                    List<Taskresponse> trl = task.getTaskresponseList();
                    if(trl == null) {
                        trl = new ArrayList();
                        task.setTaskresponseList(trl);
                    }
                    trl.add(response);
                    response.setTask(task);

                    dao.persist(response);
                    slaveUpdates.add(response);
                }
            }

            dao.commit(); 

            for(Object entity : slaveUpdates) {
                app.getSlaveUpdates().addPersist(entity);
            }
            
            log("SUCCESS. Row: "+row+ ", Doc ID: "+doc.getDocid()+ ". "+this.getCellContents(cells));

            return Boolean.TRUE;
        }
    }
    
    private List<String> getResponseList(Cell [] cells, int row) {
        
        String actionTaken = getContents(cells, row, action0col);
        String followup =  getContents(cells, row, action1col);
        String remarks =   getContents(cells, row, this.remarksCol);
        final String [] responses = {actionTaken, followup, remarks};
        
        final List<String> responseList = new ArrayList<>();

        for(String responseStr : responses) {
            if(this.textHandler.isNullOrEmpty(responseStr)) {
                continue;
            }
            responseList.add(responseStr);
        }
        return responseList.isEmpty() ? Collections.EMPTY_LIST : Collections.unmodifiableList(responseList);
    }
    
    private String getTaskDescription(Cell [] cells, int row, int col, String outputIfNone) {
        final String taskDesc = cells[col].getContents();
        if(this.textHandler.isNullOrEmpty(taskDesc)) {
            return outputIfNone;
        }else{
            return taskDesc;
        }
    }
    
    private List<Appointment> getAppointments(Sheet sheet, Cell [] cells, int row, int col) {
        
        final String resp = cells[col].getContents();
        
        if(this.textHandler.isNullOrEmpty(resp)) {
            return Collections.EMPTY_LIST;
        }

        final List<Appointment> output = new ArrayList<>();
        
        final String [] parts = resp.split(",");
        for(String part : parts) {

            part = part.trim();

            if(part.isEmpty()) {
                continue;
            }
// DOPRI, Comms, LOG & COMMS, AFIT Comdt
            Appointment appt = this.getAppointment(part);
            if(appt == null) {
                try{
                    List<Appointment> list = this.promptConfirmApptOrCreateNew(app, sheet, part, row, col);
                    output.addAll(list);
                }catch(TaskExecutionException e) {
                    log("WARN ["+row+':'+col+"]\t" + e + "Creating apointment from 'Branch to take action' PART: " + part+". Row: "+this.getCellContents(cells));
                }
            }else {
                output.add(appt);
            }
        }
        
        return output;
    }
    
    private List<Appointment> promptConfirmApptOrCreateNew(DtbApp app, Sheet sheet, String apptStr, int row, int col) throws TaskExecutionException {

        final List<Appointment> output = new ArrayList<>();
        
        final JpaContext jpaContext = app.getJpaContext();
        
        Appointment appt;
        try{
            appt = jpaContext.getBuilderForSelect(Appointment.class)
                    .where(Appointment.class, Appointment_.appointment.getName(), apptStr)
                    .getSingleResultAndClose();
        }catch(javax.persistence.NoResultException ignored) {
            appt = null;
        }
        
        if(appt != null) {
            output.add(appt);
        }else {    
            output.addAll(this.promptSelectAppointmentOrCreateNew(sheet, row, col));
        }
        
        return output;
    }
    
    private List<Appointment> promptSelectAppointmentOrCreateNew(Sheet sheet, int row, int col) {
        final List<Appointment> output = new ArrayList();
        final SelectAppointmentOrCreateNewPanel container = 
                this.createAndShowSelectAppointmentUI(sheet, row, col);

        final List<String> selected = container.getAppointmentsList().getSelectedValuesList();
        if(selected != null && !selected.isEmpty()) {
            for(String selectedApptStr : selected) {
                Appointment selectedAppt = app.getJpaContext().getBuilderForSelect(Appointment.class)
                        .where(Appointment.class, Appointment_.appointment.getName(), selectedApptStr)
                        .getSingleResultAndClose();
                output.add(selectedAppt);
            }
        }else {
            final String name = AddAppointment.class.getName()+'#'+Appointment.class.getName();
            final Appointment appt = (Appointment)app.getAttributes().get(name);

            log("INFO ["+row+':'+col+"]\tAppointment from app attributes: " + appt);

            if(appt != null) {
                output.add(appt);
                app.getAttributes().remove(name);
            }
        }
        return output;
    }
    
    private SelectAppointmentOrCreateNewPanel createAndShowSelectAppointmentUI(Sheet sheet, int row, int col) {

        final UIContext uiContext = app.getUIContext();
        
        final SelectAppointmentOrCreateNewPanel container = new SelectAppointmentOrCreateNewPanel(app);
        final JTable table = container.getTable();
        
        final int midway = 3;
        
        final Map<String, Object> params = new HashMap(8, 0.75f);
        params.put(ParamNames.SHEET, sheet);
        params.put(JTable.class.getName(), table);
        params.put(ParamNames.OFFSET, row-midway);
        params.put(ParamNames.LIMIT, (midway * 2 + 1));
        params.put(ParamNames.TITLE, "Select Appointment or Create New");
        
        final JFrame frame;
        try{
            frame = (JFrame)app.getAction(ActionCommands.CREATE_WORKSHEET_FRAME).execute(app, params);
        }catch(ParameterException | TaskExecutionException e) {
            log("ERRO ["+row+':'+col+"]\t " + e);
            return container;
        }
        
        uiContext.positionFullScreen(frame);
        
        frame.pack();
        
        uiContext.updateTableUI(table, Task.class, -1);
        
        frame.setVisible(true);
        
        uiContext.scrollTo(table, midway, midway);
        
        params.clear();
        params.put(Window.class.getName(), frame);
        params.put(JButton.class.getName(), container.getDoneButton());
        try{
            app.getAction(ActionCommands.BLOCK_WINDOW_TILL_BUTTON_CLICK).execute(app, params);
        }catch(ParameterException | TaskExecutionException e) {
            log("ERRO ["+row+':'+col+"]\t " + e);
        }
        
        return container;
    }
    
    private Appointment getAppointment(String abbrev) {
        final int index = this.getAppointmentIndex(abbrev);
        return index == -1 ? null : appointments.get(index);
    }
    
    public int getAppointmentIndex(String abbrev) {
        for(String [] abbrevArr : this.expectedAbbrevArrs) {
            final int i = this.textHandler.indexOfMatchingText(abbrevArr, abbrev);
            if(i != -1) {
                return i;
            }
        }
        return -1;
    }
    
    
    private Date getTimeopened(Cell [] cells, int row, int col, List<String> responseList, Date outputIfNone) {
       
        Date timeopened = this.getTimeopened(cells, row, col, null);
        
        if(timeopened == null) {
            
            Date earliest = null;
            
            for(String response : responseList) {
                
                List<Date> dateList = this.textHandler.getDateList(response);
                for(Date date : dateList) {
                    if(earliest == null) {
                        earliest = date;
                    }else{
                        earliest = date.before(earliest) ? date : earliest;
                    }
                }
            }
//System.out.println("------------------ Row: "+row+", earliest date: "+earliest);            
            if(earliest != null) {
                timeopened = new Date(earliest.getTime() - TimeUnit.HOURS.toMillis(24));
            }
        }
        
        if(timeopened == null) {
            final String msg = "WARN ["+row+':'+col+"]\tINVALID Date tracking commenced = " + cells[col].getContents();
            if(outputIfNone == null) {
                log(msg);
            }else{
                log(msg + ", using default: " + outputIfNone);
            }
            timeopened = outputIfNone;
        }
        
        return timeopened == null ? outputIfNone : timeopened;
    }

    private Date getTimeopened(Cell [] cells, int row, int col, Date valueIfNone) {
        // Eg:  22 Dec 16 (RPB NLT 23 Dec 16)
        //
        final String cellContents = cells[col].getContents();
        final String trackStartStr = this.textHandler.isNullOrEmpty(cellContents) ? null : this.textHandler.getLastDateStr(cellContents);
        Date timeopened = this.textHandler.getDate(trackStartStr);
        return timeopened == null ? valueIfNone : timeopened;
    }
    
    private List<Appointment> toAppointmentList(List<Task> taskList) {
        final List<Appointment> output = new ArrayList<>(taskList.size());
        for(Task task : taskList) {
            output.add(task.getReponsibility());
        }
        return output;
    }
    
    private List<String> toResponseList(List<Task> taskList) {
        final List<String> output = new ArrayList<>();
        for(Task task : taskList) {
            List<Taskresponse> taskresponseList = task.getTaskresponseList();
            for(Taskresponse taskresponse : taskresponseList) {
                output.add(taskresponse.getResponse());
            }
        }
        return output;
    }
    
    private List<String> toAppointmentAbbrevList(List<Appointment> apptList) {
        final List<String> output = new ArrayList<>(apptList.size());
        for(Appointment appt : apptList) {
            output.add(appt.getAbbreviation());
        }
        return output;
    }
    
    private List<String> getCellContents(Cell [] cells) {
        final List<String> output = new ArrayList<>(cells.length);
        for(Cell cell : cells) {
            output.add(cell.getContents());
        }
        return output;
    }
    
    private String getContents(Cell [] cells, int row, int col) {
        try {
            return cells[col].getContents();
        }catch(RuntimeException e) {
            log("ERRO ["+row+':'+col+"]\t " + e);
            throw e;
        }
    }
    
    public void log(Throwable t) {
        if(uiLogger != null) uiLogger.log(t);
    }
    
    public void log(String msg) {
        if(uiLogger != null) uiLogger.log(msg);
    }

    public int getRefnumCol() {
        return refnumCol;
    }

    public int getTaskDescriptionCol() {
        return taskDescriptionCol;
    }

    public int getApptCol() {
        return apptCol;
    }

    public int getTimeopenedCol() {
        return timeopenedCol;
    }

    public int getAction0col() {
        return action0col;
    }

    public int getAction1col() {
        return action1col;
    }

    public int getRemarksCol() {
        return remarksCol;
    }

    public Date getDefaultTrackStart() {
        return defaultTrackStart;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public Appointment getCAS() {
        return CAS;
    }

    public TextHandler getTextHandler() {
        return textHandler;
    }
}
