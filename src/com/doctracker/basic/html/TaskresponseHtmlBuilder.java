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
import com.doctracker.basic.pu.entities.Taskresponse;
import com.doctracker.basic.App;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 11, 2017 5:43:58 PM
 */
public class TaskresponseHtmlBuilder implements HtmlBuilder<Taskresponse> {

    private final App app;
    
    private boolean buildAttempted;
    
    private Taskresponse taskresponse;

    public TaskresponseHtmlBuilder(App app) {
        this.app = app;
    }

    @Override
    public HtmlBuilder<Taskresponse> with(Taskresponse task) {
        this.taskresponse = task;
        return this;
    }

    @Override
    public String build() {
        
        this.checkBuildAttempted();
        
//        final String author = taskresponse.getAuthor().getAppointment();
        final String response = taskresponse.getResponse();
        final String deadlineStr = taskresponse.getDeadline() == null ? null : 
                app.getDateTimeFormat().format(taskresponse.getDeadline());
        
        final HtmlGen htmlGen = new HtmlGen();
        final StringBuilder builder = new StringBuilder(10000);
        
        if(deadlineStr != null) {
            htmlGen.enclosingTag("div", "<tt>" + deadlineStr + "</tt>", builder);
        }
        htmlGen.enclosingTag("div", response, builder);
        
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
