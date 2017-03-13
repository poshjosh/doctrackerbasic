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

package com.doctracker.basic.jpa;

import com.bc.jpa.JpaContext;
import com.doctracker.basic.pu.entities.Doc;
import com.doctracker.basic.pu.entities.Doc_;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 16, 2017 3:13:14 PM
 */
public class DocDao {

    private final JpaContext jpaContext;

    public DocDao(JpaContext jpaContext) {
        this.jpaContext = jpaContext;
    }
    
    public Doc findOrCreateIfNone(Date datesigned, String refnum, String subj) {
        final List<Doc> found = this.find(datesigned, refnum, subj, 0, 2);
        final Doc doc;
        if(found != null && found.size() > 1) {
            throw new javax.persistence.NonUniqueResultException();
        }else if(found != null && found.size() == 1) {
            doc = found.get(0);
        }else {
            doc = this.create(datesigned, refnum, subj);
        }
        return doc;
    }
    
    public Doc create(Date datesigned, String refnum, String subj) {
        final Doc doc;
        doc = new Doc();
        doc.setReferencenumber(refnum);
        doc.setSubject(subj);
        doc.setDatesigned(datesigned);
        return doc;
    }
    
    public List<Doc> find(Date datesigned, String refnum, String subj) {
        return this.find(datesigned, refnum, subj, 0, Integer.MAX_VALUE);
    }
    
    public List<Doc> find(Date datesigned, String refnum, 
            String subj, int firstResult, int maxResults) {
        Objects.requireNonNull(subj);
        final List<Doc> found = jpaContext.getBuilderForSelect(Doc.class)
                .where(Doc.class, Doc_.referencenumber.getName(), refnum)
                .and().where(Doc.class, Doc_.subject.getName(), subj)
                .and().where(Doc.class, Doc_.datesigned.getName(), datesigned)
                .getResultsAndClose(firstResult, maxResults);
        return found;
    }
}
