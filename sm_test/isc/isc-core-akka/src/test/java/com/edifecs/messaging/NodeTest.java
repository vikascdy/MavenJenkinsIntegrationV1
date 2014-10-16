package com.edifecs.messaging;

import com.edifecs.epp.isc.CommandCommunicator;
import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder;
import com.edifecs.epp.isc.Address;

public class NodeTest {
    
    public static void main(String[] args) throws Exception {
        NodeTest node = new NodeTest();
        node.connectionStabilityTest();
    }
    
    public void connectionStabilityTest() throws Exception {
        CommandCommunicatorBuilder builder = new CommandCommunicatorBuilder();
        builder.setClusterName("XEServer");
        builder.setAddress(new Address("_WILLIAMS_TEST_CONNECTION_"));
        
        CommandCommunicator commandCommunicator = builder.initialize();
        commandCommunicator.connect();
        
        while(true) {
            System.out.println(commandCommunicator.getAddressRegistry().getRegisteredAddresses().toString());
            System.out.println(commandCommunicator.getAddressRegistry().getAllServiceInformation());
            Thread.sleep(5000);
        }
    }
    
}
