/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
 * @author Chinomso Bassey Ikwuagwu on Feb 8, 2017 11:48:25 PM
 */
public class TaskExecutionException extends Exception {

    /**
     * Creates a new instance of <code>TaskExecutionException</code> without detail message.
     */
    public TaskExecutionException() {
    }


    /**
     * Constructs an instance of <code>TaskExecutionException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public TaskExecutionException(String msg) {
        super(msg);
    }

    public TaskExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskExecutionException(Throwable cause) {
        super(cause);
    }
}
