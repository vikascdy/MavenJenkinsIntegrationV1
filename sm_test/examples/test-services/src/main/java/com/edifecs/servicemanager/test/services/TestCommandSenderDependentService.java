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

import com.edifecs.epp.isc.Address;
import com.edifecs.servicemanager.annotations.Service;
import com.edifecs.servicemanager.annotations.ServiceDependency;
import com.edifecs.servicemanager.api.AbstractService;

@Service(
    name = "Test Command Sender Dependent",
    version = "1.0",
    description = "Sends commands to Test Command Sender",
    services = {@ServiceDependency (name = "Test Command Sender", typeName = "Test Command Sender", version = "1.0", unique = false)},
    properties = {},
    unmovable = true
)
public class TestCommandSenderDependentService extends AbstractService {

    private static final String TEST_COMMAND_SENDER_SERVICE = TestCommandSenderService.class.getAnnotation(Service.class).name();
    private static final int    THREAD_WAIT_DURATION        = 3000; // 3 seconds

    @Override public void start() throws Exception {          
        getLogger().debug("{}: Service Successfully Started", getServiceAnnotation().name());

        // give the command receiver a chance to start
        Thread.sleep(THREAD_WAIT_DURATION);

        // pick the command receiver service instance to send command
        Address receiver = getAddressRegistry().getAddressForServiceTypeName(TEST_COMMAND_SENDER_SERVICE);                

        sendSyncMessage(receiver, "testSenderCommand");       
    }   

    @Override public void stop() throws Exception {           
        getLogger().debug("{}: Service Successfully Stopped", getServiceAnnotation().name());
    }

    private void sendSyncMessage(Address receiver, String command) throws Exception {
        getLogger().debug("Sending command {} to {}", command, TEST_COMMAND_SENDER_SERVICE);

        Boolean sent = (Boolean) getCommandCommunicator().sendSyncMessage(receiver, command);
        getLogger().debug(String.format("Command %s sent to %s ? %s", command, TEST_COMMAND_SENDER_SERVICE, sent));        
    }
}

