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
package com.edifecs.epp.isc.communicator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import com.edifecs.epp.isc.CommandCommunicator;
import com.edifecs.epp.isc.Address;
import com.edifecs.epp.isc.exception.MessageException;
import com.edifecs.epp.isc.MessageResponse;
import com.edifecs.epp.isc.command.CommandMessage;
import com.edifecs.epp.isc.receiver.CommandMessageReceiver;

/**
 * Message class to use to send out any command messages to anyone in the
 * cluster. It knows if it's a local service or remote service, and will
 * properly route the messages to where they need to go.
 * 
 * @author willclem
 */
public class Communicator implements ICommunicator {

    private final ClusterConnection clusterConnection;

    private final CommandMessageReceiver commandMessageReceiver;

    public Communicator(CommandCommunicator commandCommunicator) {
        this.clusterConnection = commandCommunicator.getClusterConnection();
        this.commandMessageReceiver = new CommandMessageReceiver(commandCommunicator);
    }

    @Override
    public MessageResponse sendSyncMessage(CommandMessage message) throws Exception {
        MessageResponse messageResponse = new MessageResponse();
        Collection<Address> receivers = new ArrayList<Address>(message.getReceivers());
        Collection<Address> origionalReceivers = new ArrayList<Address>(message.getReceivers());
        
        // Reroute any internal messages to bypass JGroups
        for (Address receiver : origionalReceivers) {
            try {
                if (clusterConnection.receiverBelongsToNode(receiver)) {
                    receivers.remove(receiver);
                    
                    message.clearReceivers();
                    message.addReceiver(receiver);
                    
                    // Handle message internally
                    messageResponse.addResponse(receiver,
                            commandMessageReceiver.handleIncomingSyncCommandMessage(message,
                                    receiver));
                }
            } catch (MessageException e) {
                messageResponse.addException(receiver, e);
            } catch (Exception e) {
                messageResponse.addException(receiver, new MessageException(e));
            }
        }
        
        // If there are any messages left to send, sent them
        if (receivers.size() > 0) {
            message.clearReceivers();
            message.addReceivers(receivers);
            messageResponse.merge(clusterConnection.sendSyncMessage(message));
        }
        
        return messageResponse;
    }

    @Override
    public MessageResponse sendSyncMessage(CommandMessage message, InputStream stream) throws Exception {

        if (message.getReceivers().size() != 1) {
            throw new MessageException("A stream message can only have one recipient.");
        }

        MessageResponse messageResponse = new MessageResponse();
        for (Address receiver : message.getReceivers()) {
            try {
                if (clusterConnection.receiverBelongsToNode(receiver)) {
                    // Handle message internally
                    messageResponse.addResponse(receiver,
                            commandMessageReceiver.handleIncomingSyncCommandMessage(message,
                                    receiver, stream));
                } else {
                    // Send Message
                    messageResponse.addResponse(receiver,
                            clusterConnection.sendSyncMessage(message,
                                    stream));
                }
            } catch (MessageException e) {
                messageResponse.addException(receiver, e);
            } catch (Exception e) {
                messageResponse.addException(receiver, new MessageException(e));
            }
        }

        return messageResponse;
    }

    @Override
    public void sendAsyncMessage(CommandMessage message) throws Exception {
        for (Address receiver : message.getReceivers()) {
            try {
                if (clusterConnection.receiverBelongsToNode(receiver)) {
                    // Handle message internally
                    commandMessageReceiver.handleIncomingSyncCommandMessage(message, receiver);
                } else {
                    // Send Message
                    clusterConnection.sendSyncMessage(message);
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    @Override
    public void sendAsyncMessage(CommandMessage message, InputStream stream) throws Exception {
        if (message.getReceivers().size() != 1) {
            throw new MessageException("A stream message can only have one recipient.");
        }
        for (Address receiver : message.getReceivers()) {
            try {
                if (clusterConnection.receiverBelongsToNode(receiver)) {
                    // Handle message internally
                    commandMessageReceiver.handleIncomingSyncCommandMessage(message, receiver,
                            stream);
                } else {
                    // Send Message
                    clusterConnection.sendSyncMessage(message, stream);
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    @Override
    public void sendBroadcastMessage(CommandMessage msg) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendBroadcastMessage(CommandMessage msg, InputStream stream) throws Exception {
        throw new UnsupportedOperationException();
    }

}
