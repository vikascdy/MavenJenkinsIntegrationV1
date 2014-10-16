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

import java.util.regex.Pattern;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.ptql.ProcessQuery;
import org.hyperic.sigar.ptql.ProcessQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Tracks the CPU and memory activity of a specific JVM process, by its PID.
 * </p>
 * 
 * @author i-adamnels
 */
public class NodeMemoryTracer extends MemoryTracer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final int  TRIES          = 5;
    private final long RETRY_INTERVAL = 100;
    
    private final Sigar sigar;
    private final long pid;
    private final String nodeName;
    
    private long maxMem;
    
    private volatile boolean running = false;

    public NodeMemoryTracer(String nodeName, long pid) {
        this.pid      = pid;
        this.nodeName = nodeName;
        this.sigar    = new Sigar();
    }
    
    public NodeMemoryTracer(String nodeName) throws NodeNotFoundException {
        this.nodeName = nodeName;
        this.sigar    = new Sigar();
        
        // Try to locate the running node process.
        long pid = -1;
        for (int i=0; i<TRIES; i++) {
            try {
                ProcessQuery query = new ProcessQueryFactory().getQuery("State.Name.eq=java,"
                		+ "Args.*.re=com\\.edifecs\\.agent\\.launcher\\.NodeStarter,"
                        + "Args.*.re=-nodeName=" + Pattern.quote(nodeName));
                pid = query.findProcess(sigar);
                logger.debug("Found process for node '" + nodeName + "': pid " + pid);
                break;
            } catch (SigarException ex) {
                if (i < TRIES-1) {
                    logger.debug("Could not find process for node '{}'; retrying...",
                            nodeName);
                    try {
                        Thread.sleep(RETRY_INTERVAL);
                        continue;
                    }
                    catch (InterruptedException ex2) {}
                }
                throw new NodeNotFoundException("Could not find process for node '"
                        + nodeName + "'. Sigar error occurred: " + ex.getMessage(), ex);
            }
        }
        this.pid = pid;
    }
    
    @Override public void run() {
        running = true;
        logger.info("Starting node '" + nodeName + "' (pid " + pid
                + ") CPU/MEM trace.");
        try {
            this.maxMem = sigar.getMem().getRam() * 1000000; // getRam() is in MB.
        } catch (SigarException ex) {
            running = false;
            logger.error(ex.getMessage(), ex);
        }
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
        logger.info("Stopping node '" + nodeName + "' (pid " + pid
                + ") CPU/MEM trace.");
    }
    
    private double getCpuTrace() throws SigarException {
        return sigar.getProcCpu(pid).getPercent() * 100.0;
    }
    
    private double getMemTrace() throws SigarException {
        return ((double) sigar.getProcMem(pid).getSize() / (double) maxMem) * 100.0;
    }
    
    public String getNodeName() {
        return nodeName;
    }
    
    public long getPid() {
        return pid;
    }

    @Override public boolean isRunning() {
        return running;
    }
    
    @Override public void stop() {
        running = false;
    }
}
