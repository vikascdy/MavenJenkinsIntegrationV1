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

package com.edifecs.servicemanager.test.services;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.edifecs.epp.isc.core.ServiceStatus;
import com.edifecs.servicemanager.annotations.Service;
import com.edifecs.servicemanager.api.AbstractService;

/**
 * Service that prints log messages every 10 minutes.
 * 
 * @author abhising
 */
@Service(
    name = "Test Log Service",
    version = "1.0",
    description = "Logs regularly after every 10 min.")
public class TestRegLogService extends AbstractService {

    Map<String, ServiceStatus> map;

    @Override
    public void start() throws Exception {
        getLogger().debug("{}: Service Successfully Started", this.getClass());

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override public void run() {
                getLogger().debug("Hi, logging from {}, after every 10 min.", getClass().getSimpleName());
            }
        }, 1, 10, TimeUnit.MINUTES); // run every 10 min.
    }

    @Override
    public void stop() throws Exception {
        getLogger().debug("{}: Service Successfully Stopped", this.getClass());
    }
}

