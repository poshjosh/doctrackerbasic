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

import com.bc.appcore.jpa.predicates.MasterPersistenceUnitTest;
import com.bc.jpa.JpaContext;
import com.bc.jpa.JpaContextImpl;
import com.bc.jpa.JpaMetaData;
import com.bc.jpa.JpaUtil;
import com.bc.jpa.util.PrintRelationships;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 23, 2017 8:49:44 PM
 */
public class JpaTests {

    public static void main(String [] args) {
        try{
            final URI peristenceURI = Thread.currentThread().getContextClassLoader().getResource("META-INF/persistence.xml").toURI();
            final JpaContext jpa = new JpaContextImpl(peristenceURI, null);
//            final Unit unit = jpa.getDao(Unit.class).find(Unit.class, 2);
//            JpaTests.removeManyToOnes(jpa, unit);
            final JpaMetaData metaData = jpa.getMetaData();
            final String [] puNames = metaData.getPersistenceUnitNames();
            final Predicate<String> masterPuTest = new MasterPersistenceUnitTest();
            
            final PrintRelationships pr = new PrintRelationships(jpa);
            
            for(String puName : puNames) {
                if(!masterPuTest.test(puName)) {
                    continue;
                }
                final Class [] puClasses = metaData.getEntityClasses(puName);
                for(Class puClass : puClasses) {
                    
                    final Object entity = jpa.getBuilderForSelect(puClass).getResultsAndClose(0, 1).get(0);
                    
                    pr.execute(entity);
                }
            }
        }catch(Exception e) {
            
            e.printStackTrace();
        }    
    }
    
    private static void removeManyToOnes(JpaContext jpa, Object local) {
        final Class refClass = local.getClass();
        final Class [] refingClasses = jpa.getMetaData().getReferencingClasses(refClass);
System.out.println("Referencing types: "+Arrays.asList(refingClasses));        
        if(refingClasses != null && refingClasses.length != 0) {
            for(Class refingClass : refingClasses) {
                final Method setter = JpaUtil.getMethod(true, refClass, refingClass);
System.out.println("Referencing type: "+refingClass.getName()+", setter: "+(setter==null?null:setter.getName()));                
                if(setter == null) {
                    continue;
                }
                try{
                    final Object returnValue = setter.invoke(local, new Object[]{null}); 
System.out.println("Return value: "+returnValue);                    
                }catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
//                    logger.log(Level.WARNING, "Error invoking "+setter.getName()+" with argument: null", e);
                }
            }
        }
    }
}
