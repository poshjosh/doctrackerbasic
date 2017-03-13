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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 3, 2017 12:49:55 AM
 */
public class ExecutorServiceTest {

    private final int queueSize = 1;
    private final int corePoolSize = 1;
    private final int maxPoolSize = 1;
    
    private final BlockingQueue<Runnable> q = new ArrayBlockingQueue<>(queueSize);
    private final ExecutorService es = new ThreadPoolExecutor(
            corePoolSize, maxPoolSize, 0L, TimeUnit.MILLISECONDS, 
            q, Executors.defaultThreadFactory(), 
            new ThreadPoolExecutor.DiscardOldestPolicy()); 
        
//new LinkedBlockingQueue<Runnable>()    
    public void update(String name, int size) {
        
        final Runnable runnable = () -> {
            try{
                for(int i =0; i<size; i++) {
                    System.out.println(name+'_'+(i+1)+" of "+size);
                    try{
                        Thread.currentThread().sleep(1000);
                    }catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }catch(RuntimeException e) {
                e.printStackTrace();
            }
        };
        es.submit(runnable);
    }
    
    public void update() throws InterruptedException {

        this.update("A", 10);
        Thread.sleep(3000);
        this.update("B", 3);
        Thread.sleep(2000);
        this.update("C", 7);
        Thread.sleep(1000);
        this.update("D", 6);
        Thread.sleep(1000);
        this.update("E", 4);
        Thread.sleep(2000);
        this.update("F", 8);
        Thread.sleep(3000);
        this.update("G", 8);
        Thread.sleep(2000);
        this.update("H", 8);
        es.shutdown();
        es.awaitTermination(10, TimeUnit.SECONDS);
    }
    
    public static void main(String [] args) {
        try{
            ExecutorServiceTest at = new ExecutorServiceTest();
            at.update();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
