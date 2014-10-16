// -----------------------------------------------------------------------------
// Copyright (c) Edifecs Inc. All Rights Reserved.
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

package com.edifecs.epp.security.service.handler;

import com.edifecs.epp.flexfields.model.FlexGroup;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Created by willclem on 7/8/2014.
 */
public class AppStoreHandlerTest extends AbstractHandlerTest  {

    AppStoreHandler appStoreHandler;

    @Before
    public void before() throws Exception {
        appStoreHandler = new AppStoreHandler();
        appStoreHandler.initialize(commandCommunicator, commandCommunicator);


    }

    //TODO: Implement test cases
    @Test
    @Ignore
    public void testGetTenantAppConfigurations() throws Exception {
        Map<String, List<FlexGroup>> groups = appStoreHandler.getTenantAppConfigurations();
        Assert.assertNotNull(groups);
    }
}
