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
import com.bc.jpa.dao.SelectDao;
import com.doctracker.basic.pu.entities.Doc;
import com.doctracker.basic.pu.entities.Task;
import com.doctracker.basic.pu.entities.Taskresponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 17, 2017 2:33:03 PM
 */
public class SelectDaoBuilderTest {

    public static void main(String [] args) {
        
        try{
            
            final URI persistenceURI = Thread.currentThread().getContextClassLoader()
                    .getResource("META-INF/persistence.xml").toURI();

            final JpaContext jpaContext = new JpaContextImpl(persistenceURI, null);
            
            final String toFind = "construction";
            final Comparator<Task> comparator = (Task t1, Task t2) -> { return t1.getTaskid().compareTo(t2.getTaskid()); };
            final SelectDao<Task> dao1 = new com.doctracker.basic.jpa.SelectDaoBuilderImpl()
                    .jpaContext(jpaContext).resultType(Task.class).textToFind(toFind).build();
            final List<Task> results1 = dao1.getResultsAndClose();
            Collections.sort(results1, comparator);
System.out.println(results1.size() + " results");            
System.out.println(results1);          
            final SelectDao<Task> dao2 = new com.bc.jpa.util.SelectDaoBuilderImpl()
                    .typesToSearch(new HashSet(Arrays.asList(String.class, Doc.class, Task.class, Taskresponse.class)))
                    .jpaContext(jpaContext).resultType(Task.class).textToFind(toFind).build();
            final List<Task> results2 = dao2.getResultsAndClose();
            Collections.sort(results2, comparator);
System.out.println(results2.size() + " results");             
System.out.println(results2);  
            
        }catch(URISyntaxException e) {
            
            e.printStackTrace();
        }
    }
}
