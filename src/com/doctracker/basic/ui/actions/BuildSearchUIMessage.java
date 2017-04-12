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

import com.bc.appcore.actions.TaskExecutionException;
import com.doctracker.basic.parameter.SearchParametersBuilder;
import com.doctracker.basic.pu.entities.Task_;
import java.util.Date;
import java.util.Map;
import com.bc.appcore.actions.Action;
import com.bc.appbase.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 6, 2017 10:58:07 PM
 */
public class BuildSearchUIMessage implements Action<App,String> {

    @Override
    public String execute(App app, Map<String, Object> params) throws TaskExecutionException {
     
        final String s = (String)params.get("query");
        final String query = s == null || s.isEmpty() ? null : s;
        
        final Date from = (Date)params.get("from");
        final Date to = (Date)params.get("to");
        final Boolean b = (Boolean)params.get(SearchParametersBuilder.CLOSED_TASKS);
        final boolean closed = b == null ? false : b;
        final String who = (String)params.get(Task_.reponsibility.getName());
        final Date deadlineFrom = (Date)params.get(SearchParametersBuilder.DEADLINE_FROM);
        final Date deadlineTo = (Date)params.get(SearchParametersBuilder.DEADLINE_TO);
        
        StringBuilder msg = new StringBuilder();
        msg.append("<html>");
        if(query != null) {
            msg.append("<tt>You searched for</tt>: ").append(query);
        }

        msg.append("<br/>Closed: ").append(closed?"<tt>Yes</tt>":"<tt>No</tt>");

        if(from != null) {
            msg.append("<br/>");
            msg.append("Track-start From:&emsp;<tt>").append(app.getDateTimeFormat().format(from)).append("</tt>");
        }
        if(to != null) {
            final String spacer = from == null ? "<br/>" : "&emsp;";
            msg.append(spacer);
            msg.append("Track-start To:&emsp;<tt>").append(app.getDateTimeFormat().format(to)).append("</tt>");
        }
        if(!this.isNullOrEmpty(who)) {
            msg.append("<br/>Who:&emsp;<tt>").append(who).append("</tt>");
        }
        
        if(deadlineFrom != null) {
            msg.append("<br/>");
            msg.append("Deadline From:&emsp;<tt>").append(app.getDateTimeFormat().format(deadlineFrom)).append("</tt>");
        }
        if(deadlineTo != null) {
            final String spacer = deadlineFrom == null ? "<br/>" : "&emsp;";
            msg.append(spacer);
            msg.append("Deadline To:&emsp;<tt>").append(app.getDateTimeFormat().format(deadlineTo)).append("</tt>");
        }
        
        msg.append("</html>");
        
        return msg.toString();
    }

    private boolean isNullOrEmpty(Object obj) {
        return obj == null || "".equals(obj);
    }
}
