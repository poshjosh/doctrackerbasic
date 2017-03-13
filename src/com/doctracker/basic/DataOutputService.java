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

import com.bc.util.Util;
import com.bc.util.concurrent.NamedThreadFactory;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 3, 2017 8:39:37 AM
 */
public class DataOutputService {
    
    private final String id;

    private final int queueSize = 1;
    
    private final int corePoolSize = 1;
    
    private final int maxPoolSize = 1;
    
    private final BlockingQueue<Runnable> taskQueue;
    
    private final ThreadFactory threadFactory;
    
    private final ExecutorService taskExecutor;

    public DataOutputService(String id) {
        this.id = Objects.requireNonNull(id);
        taskQueue = new ArrayBlockingQueue<>(queueSize);
        final String name = this.getClass().getName()+"_"+id;
        threadFactory = new NamedThreadFactory(this.getClass().getName()+"_"+id);
        taskExecutor = new ThreadPoolExecutor(
                corePoolSize, maxPoolSize, 0L, TimeUnit.MILLISECONDS, taskQueue, 
                threadFactory, new ThreadPoolExecutor.DiscardOldestPolicy()); 
        Runtime.getRuntime().addShutdownHook(new Thread(name + "_ShutdownHook"){
            @Override
            public void run() {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Shutting down @{0}", this.getName());
                Util.shutdownAndAwaitTermination(taskExecutor, 1, TimeUnit.SECONDS);
            }
        });
    }
        
    public Future<?> submit(Runnable runnable) {
        return taskExecutor.submit(runnable);
    }

    public <T> Future<T> submit(Callable<T> callable) {
        return taskExecutor.submit(callable);
    }

    public <T> Future<T> submit(Runnable runnable, T result) {
        return taskExecutor.submit(runnable, result);
    }
}
