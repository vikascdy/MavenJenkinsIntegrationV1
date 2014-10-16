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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.edifecs.epp.isc.Address;
import com.edifecs.servicemanager.annotations.Service;
import com.edifecs.servicemanager.api.AbstractService;

/**
 * Service designed to run Chat Application in SM. 
 * The chat messages are handled through <code>TestChatServiceHandler</code>.
 * 
 * @author willclem
 */
public class TestChatService extends AbstractService implements ITestChatService {

    private static final String TEST_CHAT_RECEIVER_SERVICE = TestChatService.class.getAnnotation(Service.class).name();    
    private static final String CLIENT                     = "client_name";

    private TestChatServiceHandler handler;

    @Override public void start() throws Exception {  
        getLogger().debug("{}: {} Service Started", getServiceAnnotation().name(), getProperties().getProperty(CLIENT)) ;                                         

        // Obtaining Chat Window Properties
        final int MAX_WIDTH    = (int) Long.parseLong(getProperties().getProperty("win_width", "200"));
        final int MAX_HEIGHT   = (int) Long.parseLong(getProperties().getProperty("win_height", "200"));
        final int START_WIDTH  = (int) Long.parseLong(getProperties().getProperty("start_width", "40"));
        final int START_HEIGHT = (int) Long.parseLong(getProperties().getProperty("start_height", "18"));

        List<Address> chatAddresses = getChatAddresses();

        handler = new TestChatServiceHandler(getAddress(), chatAddresses, getProperties().getProperty(CLIENT));
        handler.initializeChatWindow(MAX_WIDTH, MAX_HEIGHT, START_WIDTH, START_HEIGHT);

        getLogger().debug("{}: {} Service Handler Successfully Started", getServiceAnnotation().name(), getProperties().getProperty(CLIENT));

        // Inform other clients
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put("addr", getAddress());

        for (Address addr: chatAddresses) {
            Boolean rsp = (Boolean) getCommandCommunicator().sendSyncMessage(addr, "addChatServiceClientCommand", properties);              
            getLogger().debug(String.format("%s: %s => %s ? %s", getServiceAnnotation().name(), getProperties().getProperty(CLIENT), addr, rsp));
        }

        getLogger().debug("{}: {} Service Successfully Started", getServiceAnnotation().name(), getProperties().getProperty(CLIENT));
    }

    @Override public void stop() throws Exception {
        // Inform other clients
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put("addr", getAddress());

        for (Address addr: getChatAddresses()) {
            Boolean rsp = (Boolean) getCommandCommunicator().sendSyncMessage(addr, "removeChatServiceClientCommand", properties);           
            getLogger().debug(String.format("%s: %s => %s ? %s", getServiceAnnotation().name(), getProperties().getProperty(CLIENT), addr, rsp));
        }

        getLogger().debug("{}: {} Service Successfully Stopped", getServiceAnnotation().name(), getProperties().getProperty(CLIENT));
    }       

    private List<Address> getChatAddresses() {
        // pick the chat receiver service instances to send command
        List<Address> addresses = getAddressRegistry().getAddressesForServiceTypeName(TEST_CHAT_RECEIVER_SERVICE);
        if (addresses == null || addresses.isEmpty()) {
            getLogger().error("No instances of {} are active", TEST_CHAT_RECEIVER_SERVICE);
        }

        return addresses;
    }

    @Override
    public TestChatServiceHandler getTestChatServiceHandler() {
        return handler;
    }
}

