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
package com.edifecs.core.memtracer.test;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edifecs.core.memtracer.ServerMemoryTracer;

import java.lang.reflect.Field;

public class MemTracerTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test public void testServerCpuAndMemoryData() throws Exception {

        System.setProperty( "java.library.path", this.getClass().getResource("/native").getPath() );

        Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
        fieldSysPath.setAccessible( true );
        fieldSysPath.set( null, null );


        logger.info("\nCreated console logger for test.\n");
        ServerMemoryTracer tracer = new ServerMemoryTracer();
        Thread t = new Thread(tracer);
        t.start();
        Thread.sleep(100);
        if (!t.isAlive()) {
            fail("The thread died; there was probably an exception thrown.");
        }
        logger.info("\nWaiting 11 seconds to collect data...\n");
        Thread.sleep(11000);
        logger.info("\nCollecting data...\n");
        double[] cpuTraces = tracer.getCpuTraces();
        double[] memTraces = tracer.getMemTraces();
        tracer.stop();
        t.join();
        logger.info("Last 10 CPU traces:\n");
        for (int i = 0; i < 10; i++) {
            logger.info("- " + cpuTraces[49 - i] + "\n");
        }
        logger.info("\nLast 10 memory traces:");
        for (int i = 0; i < 10; i++) {
            logger.info("- " + memTraces[49 - i] + "\n");
        }
        for (int i = 0; i < 10; i++) {
            if (memTraces[49 - i] == 0.0) {
                fail("At least one memory trace was 0; memory is not being properly tracked.");
            }
        }
        boolean allCpuZero = true;
        for (int i = 0; i < 10; i++) {
            if (cpuTraces[49 - i] > 0.0) {
                allCpuZero = false;
            }
        }
        if (allCpuZero) {
            fail("All CPU traces were 0; CPU is not being properly tracked.");
        }
    }

}
