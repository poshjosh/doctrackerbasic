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

import com.bc.jpa.JpaContext;
import com.bc.jpa.JpaContextImpl;
import com.bc.jpa.dao.BuilderForDelete;
import com.bc.jpa.dao.BuilderForSelect;
import com.bc.jpa.paging.PaginatedList;
import com.bc.jpa.search.BaseSearchResults;
import com.bc.jpa.search.SearchResults;
import com.doctracker.basic.pu.entities.Doc_;
import com.doctracker.basic.pu.remote.entities.Doc;
import com.doctracker.basic.pu.remote.entities.Task;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 17, 2017 9:41:25 PM
 */
public class DeleteDuplicatesInsertedOnSpecifiedDate_NoSlaveUpdatesHereForNow {
    
    private final Calendar calendar;
    
    private final int dayOfMonth;
    
    private final boolean useCache;
    
    private final int pageSize;
    
    private final JpaContext jpaContext;
    
    private DeleteDuplicatesInsertedOnSpecifiedDate_NoSlaveUpdatesHereForNow(Calendar calendar) throws URISyntaxException {
        this(
                new JpaContextImpl(Thread.currentThread().getContextClassLoader().getResource("META-INF/persistence.xml").toURI(), null),
                20, false,
                calendar, 17);
    }
    
    private DeleteDuplicatesInsertedOnSpecifiedDate_NoSlaveUpdatesHereForNow(
            JpaContext jpaContext, int pageSize, boolean useCache,
            Calendar calendar, int dayOfMonth) {
        this.jpaContext = Objects.requireNonNull(jpaContext);
        this.pageSize = pageSize;
        this.useCache = useCache;
        this.calendar = Objects.requireNonNull(calendar);
        if(dayOfMonth < 0 || dayOfMonth > 31) {
            throw new IllegalArgumentException();
        }
        this.dayOfMonth = dayOfMonth;
    }

    public void run() {
        this.call();
    }
    
    public Integer call() {
        
        int deleteCount = 0;
        
        try{

            final Set<Integer> deleteList = new HashSet();

            try(final BuilderForSelect<Doc> dao = jpaContext.getBuilderForSelect(Doc.class)) {
                
                final SearchResults<Doc> searchResults = new BaseSearchResults(dao, pageSize, useCache);
                
                final PaginatedList<Doc> docList = searchResults.getPages();
                
                for(Doc doc : docList) {
                    
                    final Date date = doc.getDatesigned();
                    final String ref = doc.getReferencenumber();
                    final String subj = doc.getSubject();

                    if(date == null || ref == null || subj == null) {
                        continue;
                    }

                    if(ref.isEmpty()) {
                        continue;
                    }

                    final List<Doc> foundDocs = jpaContext.getBuilderForSelect(Doc.class)
                            .where(Doc_.datesigned.getName(), date)
                            .and().where(Doc_.referencenumber.getName(), ref)
                            .and().where(Doc_.subject.getName(), subj)
                            .getResultsAndClose();
                    
                    final int SIZE = foundDocs.size();
                    
                    if(SIZE == 0) {
                        this.logWarning("WARN. For Doc with id: "+doc.getDocid()+" expected > 0 matching records but found "+SIZE);                                
                    }else if(SIZE == 1) {
                        this.log("INFO. Doc with id: "+foundDocs.get(0).getDocid()+" has no duplicates ");                                
                    }else if(SIZE == 2) {
                        final Doc found0 = foundDocs.get(0);
                        final Doc found1 = foundDocs.get(1);
                        final Doc toRetain;
                        final Doc toDelete;
                        if(this.getDayOfMonth(found0.getTimecreated()) == this.getDayOfMonth() &&
                                this.taskListMatches(found0, found1)) {
                            toRetain = found1;
                            toDelete = found0;
                        }else if(this.getDayOfMonth(found1.getTimecreated()) == this.getDayOfMonth() &&
                                this.taskListMatches(found0, found1)) {
                            toDelete = found1;
                            toRetain = found0;
                        }else{
                            toDelete = null;
                            toRetain = null;
                            this.logWarning("WARN. Between Docs with ids: "+found0.getDocid()+" and "+found1.getDocid()+", could not detemine which to delete");
                        }
                        if(toDelete != null) {
                            deleteList.add(toDelete.getDocid());
                            this.log("INFO. Doc with id: "+toDelete.getDocid()+" is a duplicate of "+toRetain.getDocid());                                
                        }
                    }else if(SIZE > 2) {
                        this.logWarning("WARN. Doc with id: "+doc.getDocid()+", has "+SIZE+" (more than 2) duplicates.");
                    }
                }
            }
            
            this.log("INFO. Deleting "+deleteList.size()+" duplicates");
            
            for(Integer id : deleteList) {
                    
                try(final BuilderForDelete<Doc> dao = jpaContext.getBuilderForDelete(Doc.class)) {
                
                    dao.begin();
                    
                    final Integer updateCount = dao.where(
                            Doc.class, Doc_.docid.getName(), id).createQuery().executeUpdate();
                    
                    deleteCount += updateCount;
                    
                    if(updateCount == 1) {
                        
                        this.log("INFO. "+(deleteCount)+" Deleted doc with id: "+id);                                
                    }else{
                        this.logWarning("WARN. "+(deleteCount)+" Failed to delete doc with id: "+id);                                
                    }
                
                    dao.commit();
                }
            }
                
            this.log("Successfully deleted: " + deleteCount +" Docs.");
            
        }catch(RuntimeException e) {
            
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Unexpected exception", e);
        }  
        
        return deleteCount;
    }
    
    private boolean taskListMatches(Doc d0, Doc d1) {
        List<Task> taskList0 = d0.getTaskList();
        List<Task> taskList1 = d1.getTaskList();
        if(taskList0.size() != taskList1.size()) {
            return false;
        }
        for(int i=0; i<taskList0.size(); i++) {
            final Task task0 = taskList0.get(i);
            final Task task1 = taskList1.get(i);
            if(!task0.getAuthor().equals(task1.getAuthor())) {
                return false;
            }
            if(!task0.getReponsibility().equals(task1.getReponsibility())) {
                return false;
            }
            if(!task0.getDescription().equals(task1.getDescription())) {
                return false;
            }
        }
        return true;
    }
    
    private void log(String msg) {
        System.out.println(msg);        
    }

    private void logWarning(String msg) {
        System.err.println(msg);        
    }
    
    private int getDayOfMonth(Date date) {
        final Date actualTime = calendar.getTime();
        try{
            calendar.setTime(date);
            return calendar.get(Calendar.DATE);
        }finally{
            calendar.setTime(actualTime);
        }
    }
    
    public int getDayOfMonth() {
        return this.dayOfMonth;
    }
}
