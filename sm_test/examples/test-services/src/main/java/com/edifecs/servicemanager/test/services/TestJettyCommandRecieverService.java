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

import com.edifecs.servicemanager.annotations.Service;
import com.edifecs.servicemanager.api.AbstractService;

/**
 * TM REST Service
 * 
 * @author abhising
 */
@Service(
    name = "TM REST Service", 
    version = "1.0", 
    description = "TM REST Service")
public class TestJettyCommandRecieverService extends AbstractService implements ITestJettyCommandRecieverService {

    @Override
    public void start() throws Exception {
        getLogger().debug("{}: Service Successfully Started", this.getClass());
    }

    @Override
    public void stop() throws Exception {
        getLogger().debug("{}: Service Successfully Stopped", this.getClass());
    }

    @Override
    public ITestJettyCommandRecieverServiceHandler getTestJettyCommandRecieverServiceHandler() {
        getLogger().debug("{}: Handler Successfully added",
                TestJettyCommandRecieverServiceHandler.class);

        return new TestJettyCommandRecieverServiceHandler();
    }
}

