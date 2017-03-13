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

package com.doctracker.basic.html;

import com.bc.html.HtmlGen;
import com.doctracker.basic.pu.entities.Appointment;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Taskresponse;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 11, 2017 2:20:04 AM
 */
public class TaskHtmlBuilder implements HtmlBuilder<Task> {

    private final App app;
    
    private boolean buildAttempted;
    
    private Task task;

    public TaskHtmlBuilder(App app) {
        this.app = app;
    }

    @Override
    public HtmlBuilder<Task> with(Task task) {
        this.task = task;
        return this;
    }

    @Override
    public String build() {
        
        this.checkBuildAttempted();
        
        final String from = task.getAuthor().getAppointment();
        final String to = task.getReponsibility().getAppointment();
        final String desc = task.getDescription();
        
        final String timeStr = task.getTimeopened() == null ? null : 
                app.getDateTimeFormat().format(task.getTimeopened());
        
        final HtmlGen htmlGen = new HtmlGen();
        htmlGen.setUseNewLine(true);
        final StringBuilder builder = new StringBuilder(10000);
        
        htmlGen.tagStart("div", "style", "width:100%; font-size:16px;", builder);
        
        final String timePart = timeStr == null ? "" : "<tt>" + timeStr + "</tt><br/>";
        htmlGen.enclosingTag("div", timePart + "<i>"+from+"</i>&emsp;<b>&gt;&nbsp;&gt;&nbsp;&gt;</b>&emsp;<i>" + to + "</i>", builder);
        htmlGen.enclosingTag("p", "style", "font-weight:900;", desc, builder);
        builder.append("<br/>");
        
        final List<Taskresponse> responseList = task.getTaskresponseList();
        
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, 
                "Task has {0} responses", responseList == null ? null :responseList.size());
        
        if(responseList != null && !responseList.isEmpty()) {
            
            builder.append("<table style=\"width:100%\">");
            
            final Appointment currAppt = app.getUser().getAppointment();
            
            for(Taskresponse response : responseList) {
                
                final boolean author = response.getAuthor().equals(currAppt);
                
                final String responseBody = this.builder(Taskresponse.class).with(response).build();
                
                builder.append("<tr>");
                if(!author) {
                    builder.append("<td></td>");
                }
                htmlGen.enclosingTag("td", "style", "border:1px solid gray;", responseBody, builder);
                if(author) {
                    builder.append("<td></td>");
                }
                builder.append("</tr>");
            }
            
            builder.append("</table>");
            
        }else{
            
            htmlGen.enclosingTag("p", "style", "width:100%", "There are no deadlines or reponses to this task", builder);
        }
        
        htmlGen.tagEnd("div", builder);

//System.out.println("-------------------------------------------------------");        
//System.out.println(builder);
//System.out.println("-------------------------------------------------------");        
        return builder.toString();
    }

    @Override
    public <B> HtmlBuilder<B> builder(Class<B> builderType) {
        return app.getHtmlBuilder(builderType);
    }
    
    private void checkBuildAttempted() {
        if(buildAttempted) {
            throw new IllegalStateException("build method may be called only once");
        }
        buildAttempted = true;
    }
}
