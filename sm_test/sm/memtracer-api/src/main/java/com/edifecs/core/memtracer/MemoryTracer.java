// -----------------------------------------------------------------------------
//  Copyright (c) Edifecs Inc. All Rights Reserved.
//
// This software is the confidential and proprietary information of Edifecs Inc.
// ("Confidential Information").  You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the license
// agreement you entered into with Edifecs.
//
// EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
// SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
// NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
// LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
// ITS DERIVATIVES.
// -----------------------------------------------------------------------------
package com.edifecs.core.memtracer;

/**
 * <p>
 * Tracks the memory and CPU usage of some entity (such as a server or node).
 * Keeps a list of the most recent 51 CPU and memory traces, the amount
 * required by the graphs in the UI.
 * </p>
 * <p>
 * In order to begin tracking CPU and memory usage, the tracer's {@link #run()}
 * method must be called, ideally in a separate thread. If the tracer is not
 * running, the {@link #getCpuTraces()} and {@link #getMemTraces()} methods
 * will throw {@link IllegalStateException}s.
 * </p>
 * 
 * @author i-adamnels
 */
public abstract class MemoryTracer implements Runnable {
    protected static final int MAX_TRACES  = 51;
    protected static final int INTERVAL_MS = 1000;

    private final double[] cpuTraces = new double[MAX_TRACES];
    private final double[] memTraces = new double[MAX_TRACES];
    
    private int traceIndex = 0;
    
    /**
     * <p>
     * Begins tracking CPU and memory usage, and will loop endlessly until an
     * exception occurs or {@link #stop()} is called.
     * </p>
     */
    @Override public abstract void run();
    
    public abstract boolean isRunning();
    
    protected final void registerTrace(double cpu, double mem) {
        cpuTraces[traceIndex] = cpu;
        memTraces[traceIndex] = mem;
        traceIndex = (traceIndex + 1) % MAX_TRACES;
    }
    
    /**
     * <p>
     * Returns an array of the 51 most recent CPU usage percentage traces, as
     * <code>double</code> values between <code>0.0</code> and
     * <code>100.0</code>.
     * </p>
     * 
     * @return An array of the 51 most recent CPU usage percentage traces.
     */
    public final double[] getCpuTraces() {
        if (!isRunning()) {
            throw new IllegalStateException("The memory tracer is not running; cannot get CPU traces.");
        }
        final double[] traces = new double[MAX_TRACES];
        for (int i = 0; i < MAX_TRACES; i++) {
            traces[i] = cpuTraces[(traceIndex + i) % MAX_TRACES];
        }
        return traces;
    }

    /**
     * <p>
     * Returns an array of the 51 most recent memory usage percentage traces,
     * as <code>double</code> values between <code>0.0</code> and
     * <code>100.0</code>.
     * </p>
     * 
     * @return An array of the 51 most recent memory usage percentage traces.
     */
    public final double[] getMemTraces() {
        if (!isRunning()) {
            throw new IllegalStateException("The memory tracer is not running; cannot get memory traces.");
        }
        final double[] traces = new double[MAX_TRACES];
        for (int i = 0; i < MAX_TRACES; i++) {
            traces[i] = memTraces[(traceIndex + i) % MAX_TRACES];
        }
        return traces;
    }
    
    /**
     * <p>
     * Stops the tracer if it is currently running. (In other words, terminates
     * the loop started by {@link #run()}).
     * </p>
     */
    public abstract void stop();
}
