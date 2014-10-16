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

package com.edifecs.epp.security.service;

import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.isc.CommandCommunicator;
import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder;
import com.edifecs.epp.security.data.User;
import com.edifecs.epp.security.data.token.UsernamePasswordAuthenticationToken;
import com.edifecs.servicemanager.api.ServiceAnnotationProcessor;
import com.edifecs.servicemanager.api.ServiceRef;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by willclem on 3/11/14.
 */
public class TestSecurityServiceTestMode {

    @Test
    public void testSecurityServiceTestMode() throws Exception {

        // Initialize a commandCommunicator Instance
        CommandCommunicator commandCommunicator = new CommandCommunicatorBuilder().initializeTestMode();
        commandCommunicator.connect();

        // Initialize the esm-service
        SecurityService securityService = new SecurityService();
        ServiceRef ref = ServiceAnnotationProcessor.processAnnotatedService(
                securityService,
                "securityServiceTestMode",
                commandCommunicator);
        ref.startTestMode();

        commandCommunicator.getSecurityManager().getAuthenticationManager().loginToken(
                new UsernamePasswordAuthenticationToken(
                    SystemVariables.DEFAULT_TENANT_NAME,
                    SystemVariables.DEFAULT_ORG_NAME,
                    "admin", "admin".toCharArray()));

        User user = commandCommunicator.getService(ISecurityService.class).subjects().getUser();
        Assert.assertNotNull(user);

        commandCommunicator.disconnect();

    }

}
