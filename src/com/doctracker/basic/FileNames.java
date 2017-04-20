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

package com.doctracker.basic;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 28, 2017 12:23:43 PM
 */
public interface FileNames {
    
    String ROOT = "doctrackerbasic";
    
    String LOGS = "logs";
    
    String CONFIGS = "configs";
    
    String SLAVE_UPDATES_DIR = "slave_updates";
    
    String REPORT_FILE_EXT = "xls";
    
    String REPORT_BACKUP_DIR = "backup";
    
    String REPORT_OUTPUT_DIR = "output";
    
    String REPORT_BACKUP_FILE_ID = "tasks";
    
    String REPORT_BACKUP_FILE_EXT = "backup";
    
    String REPORT_BACKUP_FILENAME = REPORT_BACKUP_FILE_ID + '.' + REPORT_BACKUP_FILE_EXT;
    
    String REPORT_NODEADLINE_FILE_ID = "nodeadline";
    
    String REPORT_QUERY_TODAY_FILE_ID = "query_today";
    
    String REPORT_TRACK_START_TODAY_FILE_ID = "track_start_today";
    
    String REPORT_TODAY_FILE_ID = "today";
    
    String REPORT_ALL_OUTSTANDING_FILE_ID = "all_outstanding";
}
