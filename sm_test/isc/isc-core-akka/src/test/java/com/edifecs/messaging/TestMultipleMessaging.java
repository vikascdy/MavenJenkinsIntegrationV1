/*// -----------------------------------------------------------------------------
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
package com.edifecs.messaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder;
import com.edifecs.epp.isc.core.Address;

public class TestMultipleMessaging {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int MESSAGING_QUANTITY = 1000;
    private static final int MESSAGING_TIMEOUT = 1000;

    private static final String CLUSTER_NAME = "testCluster";

    @Before
    public void beforeTest() {
    }

    @Test
    public void testAsyncQuantityMessaging() throws Exception {
        Address node1 = new Address("server1", "node10");
        Address node2 = new Address("server2", "node20");

        Address receiver = node2;
        List<Address> receivers = new ArrayList<Address>();
        receivers.add(receiver);

        CommandCommunicatorBuilder builder = new CommandCommunicatorBuilder();
        builder.setClusterName(CLUSTER_NAME);
        builder.setAddress(node1);
        
        ICommandCommunicator commandCommunicator1 = builder.initialize();
        commandCommunicator1.connect();
        
        builder = new CommandCommunicatorBuilder();
        builder.setClusterName(CLUSTER_NAME);
        builder.setAddress(node2);
        
        ICommandCommunicator commandCommunicator2 = builder.initialize();
        commandCommunicator2.connect();

        TestMessageHandler messageHandler1 = new TestMessageHandler();
        commandCommunicator1.registerCommandHandler(node1, messageHandler1);

        TestMessageHandler messageHandler2 = new TestMessageHandler();
        commandCommunicator2.registerCommandHandler(node2, messageHandler2);

        for (int i = 0; i < MESSAGING_QUANTITY; i++) {
            commandCommunicator1.sendSyncMessage(node2, "testIncrementCommand");
        }

        Thread.sleep(MESSAGING_TIMEOUT);
        assertTrue(messageHandler2.getCounter() > 0);
        assertEquals(MESSAGING_QUANTITY, messageHandler2.getCounter());
        
        Thread.sleep(1000);
        
        commandCommunicator1.disconnect();
        commandCommunicator2.disconnect();
    }

}
*/