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

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Tracks the CPU and memory activity of the machine that the tracer is running
 * on.
 * </p>
 * 
 * @author i-adamnels
 */
public class ServerMemoryTracer extends MemoryTracer {
    private static final Logger logger = LoggerFactory.getLogger(ServerMemoryTracer.class);
    
    private final Sigar sigar;
    
    private volatile boolean running = false;
    
    public ServerMemoryTracer() {
        this.sigar  = new Sigar();
    }
    
    /**
     * <p>
     * Begins tracking the server's CPU and memory usage, and will loop
     * endlessly until an exception occurs or {@link #stop()} is called.
     * </p>
     */
    @Override public void run() {
        running = true;
        logger.info("Starting server CPU/MEM trace.");
        while (running) {
            try {
                final long mark = System.currentTimeMillis();
                registerTrace(getCpuTrace(), getMemTrace());
                final long timeTaken = System.currentTimeMillis() - mark;
                final long timeLeft = INTERVAL_MS - timeTaken;
                if (timeLeft > 0) {
                    Thread.sleep(timeLeft);
                }
            } catch (InterruptedException ex) {
                running = false;
                logger.warn(ex.getMessage(), ex);
                Thread.currentThread().interrupt();
            } catch (SigarException ex) {
                running = false;
                logger.error(ex.getMessage(), ex);
            }
        }
        logger.info("Stopping server CPU/MEM trace.");
    }
    
    private double getCpuTrace() throws SigarException {
        return sigar.getCpuPerc().getCombined() * 100.0;
    }
    
    private double getMemTrace() throws SigarException {
        return sigar.getMem().getUsedPercent();
    }

    @Override public boolean isRunning() {
        return running;
    }
    
    @Override public void stop() {
        running = false;
    }
}
