/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.bverify;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import bench.ThroughputBenchmark;

public class MPTThroughputBenchmark {
	
    @State(Scope.Thread)
    public static class BenchmarkState {
    	
    	public ExecutorService workers;
    	public ThroughputBenchmark bench;

        @Setup(Level.Iteration)
        public void doSetup() {
            System.out.println("...starting workers");
            this.workers = Executors.newCachedThreadPool();
            int n = 1000000;
            int updates = 100000;
            this.bench = new ThroughputBenchmark(n, updates);
        }

        @TearDown(Level.Iteration)
        public void doTearDown() {
            System.out.println("...shutting down workers");
    		try {
    		    if (!this.workers.awaitTermination(800, TimeUnit.MILLISECONDS)) {
    		        this.workers.shutdownNow();
    		    } 
    		} catch (InterruptedException e) {
    			throw new RuntimeException(e);
    		}
        }

    }

    @Benchmark
    public void testDoUpdates(BenchmarkState s) {
    	s.bench.performUpdates();
    }
    
    @Benchmark
    public void testCommitUpdatesSingleThreaded(BenchmarkState s) {
    	s.bench.commitUpdatesSingleThreaded();
    }
    
    @Benchmark
    public void testCommitUpdatesParallelized(BenchmarkState s) {
    	s.bench.commitUpdatesParallelized(s.workers);
    }

}
