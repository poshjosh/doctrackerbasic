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

import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 15, 2017 11:10:49 PM
 */
public class SelectTaskresponseBuilder<T> 
        extends SelectDaoBuilderImpl<T> 
        implements SelectDaoBuilder<T>{

    public SelectTaskresponseBuilder() { }

    @Override
    public void searchTask(CriteriaBuilder cb, Root task, List likes, String toFind) { }
    
    @Override
    public void searchDoc(CriteriaBuilder cb, Join taskDoc, List likes, String toFind) { }
}
