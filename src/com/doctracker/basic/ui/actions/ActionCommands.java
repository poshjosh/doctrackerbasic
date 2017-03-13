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

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 8, 2017 10:12:10 PM
 */
public interface ActionCommands {
    
    String SEARCH = Search.class.getName();
    
    String SEARCH_AND_DISPLAY_RESULTS_UI = SearchAndDisplayResultsUI.class.getName();
    
    String BUILD_SEARCHUI_MESSAGE = BuildSearchUIMessage.class.getName();
    
    String EXIT = Exit.class.getName();

    String REFRESH_RESULTS = RefreshResults.class.getName();

    String DISPLAY_TASKEDITORPANE = DisplayTaskEditorPane.class.getName();

    String DISPLAY_ADD_TASK_UI = DisplayAddtaskUI.class.getName();
    String ADD_TASK = AddTask.class.getName();
    String ADD_TASK_AND_DOC = AddTaskAndDoc.class.getName();
    String ADD_TASK_TO_DOC = AddTaskToDoc.class.getName();
    String CLEAR_TASK = ClearTaskPanel.class.getName();
    
    String DISPLAY_ADD_RESPONSE_UI = DisplayAddResponseUI.class.getName();
    String ADD_TASKRESPONSE = AddTaskresponse.class.getName();
    
    String DISPLAY_ADD_REMARK_UI = DisplayAddRemarkUI.class.getName();
    
    String DISPLAY_APPOINTMENT_UI = DisplayAppointmentUI.class.getName();
    String ADD_APPOINTMENT = AddAppointment.class.getName();
    
    String DISPLAY_UNIT_UI = DisplayUnitUI.class.getName();
    String ADD_UNIT = AddUnit.class.getName();
    
    String SAVE_TABLE_AS = SaveTableAs.class.getName();
    
    String SAVE_TABLE_MODEL = SaveTableModel.class.getName();
    
    String SAVE_OUTPUT = SaveOutput.class.getName();
    
    String REFRESH_OUTPUT = RefreshOutput.class.getName();
    
    String PRINT = Print.class.getName();
    
    String IMPORT = ImportExcelData.class.getName();
    
    String CLOSE_TASK = CloseTask.class.getName();
    
    String DELETE_TASK = DeleteTask.class.getName();
    
    String ABOUT = About.class.getName();
    
    String EXECUTE_SELECT_QUERY = ExecuteSelectQuery.class.getName();
    String EXECUTE_UPDATE_QUERY = ExecuteUpdateQuery.class.getName();
    String EXECUTE_DELETE_QUERY = ExecuteDeleteQuery.class.getName();
    
    String NEXT_RESULT = NextResult.class.getName();
    String PREVIOUS_RESULT = PreviousResult.class.getName();
    
    String FIRST_RESULT = FirstResult.class.getName();
    String LAST_RESULT = LastResult.class.getName();
    
    String SEARCH_DEADLINE_TASKS = SearchDeadlineTasks.class.getName();
    
    String SET_LOOK_AND_FEEL = SetLookAndFeel.class.getName();
    
    String SCHEDULE_DEADLINE_TASKS_REMINDER = ScheduleDeadlineTasksReminder.class.getName();
    
    String REFRESH_OUTPUT_FROM_BACKUP = RefreshOutputFromBackup.class.getName();
    
    String CHANGE_FONT_SIZE = ChangeOutputFontSize.class.getName();
    
    String CHANGE_OUTPUT_DIR = ChangeOutputDir.class.getName();
    
    String SYNC_DATABASE = SyncDatabase.class.getName();
}
