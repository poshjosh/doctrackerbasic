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

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 8, 2017 7:57:31 PM
 */
public class PauseAndResumeContinousProcessTest {
    
    private volatile boolean paused;
    
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public PauseAndResumeContinousProcessTest() {
        final Thread thread = new Thread(this.getClass().getName()+"_LooperThread") {
            
            @Override
            public void run() {
                int count = -1;
                while(true) {
                    if(paused) {
                        continue;
                    }
                    try{
                        ++count;
                        try{
                            lock.readLock().lock();
System.out.println("peek");                            
                        }finally{
                            lock.readLock().unlock();
                        }

                        try{

                            lock.writeLock().lock();
System.out.print(""+count+"- - -");                            
for(int i=0; i<1000; i++){
    System.out.print(i+", ");
}
System.out.println();

                        }catch(Exception e) {
System.err.println(e);                            
                        }finally{
                            lock.writeLock().unlock();
                        }
                    }catch(RuntimeException e) {
                        
System.err.println(e);
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        Runtime.getRuntime().addShutdownHook(new Thread(this.getClass().getName()+"_ShutdownHook"){
            @Override
            public void run() {
System.out.println("Shutting down");                
            }
        });
    }
    
    public synchronized void pause() throws InterruptedException {
System.out.println(" PAUSE ----------------------------------------------");        
        paused = true;
    }
    
    public synchronized void resume() {
System.out.println("RESUME xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");        
        paused = false;
    }

    public static void main(String [] args) {
        PauseAndResumeContinousProcessTest test = new PauseAndResumeContinousProcessTest();
        try{
            Thread.sleep(2000);
            test.pause();
            Thread.sleep(2000);
            test.resume();
            Thread.sleep(2000);
            test.pause();
            Thread.sleep(2000);
            test.resume();
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
