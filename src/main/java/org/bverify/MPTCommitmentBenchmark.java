package org.bverify;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import bench.CommitBenchmark;

public class MPTCommitmentBenchmark {
	
    @State(Scope.Thread)
    public static class BenchmarkState {
    	
    	public CommitBenchmark bench;

        @Setup(Level.Iteration)
        public void doSetup() {
            int n = 1000000;
            int nBatchSize = 1000;
            this.bench = new CommitBenchmark(n, nBatchSize);
        }
    }
    
    @Benchmark
    public void testUpdate(BenchmarkState s, Blackhole bh) {
    	bh.consume(s.bench.performCommit());
    }
}
