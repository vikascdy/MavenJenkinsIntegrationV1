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

import com.edifecs.epp.isc.core.ServiceStatus;
import com.edifecs.servicemanager.annotations.Service;
import com.edifecs.servicemanager.api.AbstractService;

import java.util.Map;

/**
 * Service designed to throw exception on startup
 * 
 * @author abhising
 */
@Service(
    name = "Test Startup Exception", 
    version = "1.0",
    description = "throws exception on startup."
)
public class TestStartupExceptionService extends AbstractService {

    Map<String, ServiceStatus> map;

    @Override
    public void start() throws Exception {
        getLogger().debug("{}: Service Successfully Started", this.getClass());

        throw new Exception("Exception : testing for exception on service startup " + this.getClass());
    }

    @Override
    public void stop() throws Exception {
        getLogger().debug("{}: Service Successfully Stopped", this.getClass());
    }
}

