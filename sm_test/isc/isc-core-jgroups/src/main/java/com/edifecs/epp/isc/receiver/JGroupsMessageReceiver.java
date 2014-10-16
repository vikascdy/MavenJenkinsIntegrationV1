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
package com.edifecs.epp.isc.receiver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.jgroups.MembershipListener;
import org.jgroups.Message;
import org.jgroups.MessageListener;
import org.jgroups.View;
import org.jgroups.blocks.RequestCorrelator.Header;
import org.jgroups.blocks.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edifecs.epp.isc.CommandCommunicator;
import com.edifecs.epp.isc.AbstractMessage;
import com.edifecs.epp.isc.Address;
import com.edifecs.epp.isc.exception.MessageException;
import com.edifecs.epp.isc.MessageResponse;
import com.edifecs.epp.isc.core.CommandStreamMessage;
import com.edifecs.epp.isc.command.CommandMessage;
import com.edifecs.epp.isc.exception.MessageTypeNotSupportedException;

/**
 * Abstract class that must be implemented if an application wants to be able to
 * receive messages from the JGroups cluster.
 * 
 * @author willclem
 */
public class JGroupsMessageReceiver implements MessageListener, MembershipListener, RequestHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final CommandCommunicator commandCommunicator;

    private final CommandMessageReceiver commandMessageReceiver;

    private static final short DISPATCH_HEADER_ID = 200;

    public JGroupsMessageReceiver(CommandCommunicator commandCommunicator) {
        this.commandCommunicator = commandCommunicator;

        commandMessageReceiver = new CommandMessageReceiver(commandCommunicator);
    }

    /**
     * Called when there is a incoming ASynchronous message request.
     * 
     * @param message
     *            IMessage
     * @throws Exception
     *             Exception
     */
    public void receiveAsync(AbstractMessage message) throws Exception {
        processAsyncMessageRequest(message);
    }

    /**
     * Called when there is a incoming Synchronous message request.
     * 
     * @param message
     *            IMessage
     * @return Data to return to the message sender
     * @throws Exception
     *             Exception
     */
    public Serializable receiveSync(AbstractMessage message) throws Exception {
        return processSyncMessageRequest(message);
    }

    /**
     * Takes the incoming message, and splits it to execute either the Async or
     * Sync methods based on its message header.
     * 
     * @param msg
     *            Message received
     */
    @Override
    public final void receive(final Message msg) {
        try {
            Object object = convertToMessage(msg);

            // If this is a message do this
            if (object instanceof AbstractMessage) {
                handle(msg);
            } else if (commandCommunicator != null) {
                commandCommunicator.getClusterConnection().tryToHandleStreamMessage(msg.getSrc(),
                        object);
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Takes the incoming message, and splits it to execute either the Async or
     * Sync methods based on if its message header.
     * 
     * @param msg
     *            Message received
     * @throws Exception
     *             Thrown if there is a problem handling the message
     * @return The Response to the message if one is required
     */
    @Override
    public final Object handle(final Message msg) throws Exception {
    	try {
	        Object object = convertToMessage(msg);
	
	        if (object instanceof AbstractMessage) {
	            AbstractMessage message = (AbstractMessage) object;
	
	            if (msg.getHeader(DISPATCH_HEADER_ID) != null
	                    && ((Header) msg.getHeader(DISPATCH_HEADER_ID)).rsp_expected) {
	                Serializable response = null;
	                try {
	                    response = receiveSync(message);
	                } catch (Exception e) {
	                    // If an Exception is thrown populating the return
	                    // message, return the Exception instead.
	                    response = new MessageException(e);
	                }
	                return response;
	            } else {
	                receiveAsync(message);
	            }
	        } else if (commandCommunicator != null) {
	            try {
	                commandCommunicator.getClusterConnection().tryToHandleStreamMessage(msg.getSrc(),
	                        object);
	            } catch (Exception e) {
	                // If an Exception is thrown populating the return
	                // message, return the Exception instead.
	                logger.debug("Handled message exception: {}", msg.toString());
	                return new MessageException(e);
	            }
	        }
	        logger.error("Handled message ignored: {}", msg.toString());
	        return null;
    	} catch (Throwable e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Updates the Clusters Registry of nodes for quick access.
     * Called within viewAccepted method. Any update related to the cluster view
     * in the class which extends from AbstractMessageReceiver can be done in
     * this method.
     * 
     * @param view
     *            The JGroups Cluster View
     */
    @Override
    public final void viewAccepted(final View view) {
        try {
        	logger.info(view.toString());
            commandCommunicator.updateRegistry(view.getMembers());
            logger.info("Connections found in cluster: {}", view.toString());
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

    private Object convertToMessage(final Message msg) throws MessageException {

        byte[] data = msg.getBuffer();

        if (data != null) {
            try {
                ByteArrayInputStream byteArrayIS = new ByteArrayInputStream(data);
                ObjectInputStream objectIS = new ObjectInputStream(byteArrayIS);

                Object message = objectIS.readObject();

                return message;
            } catch (IOException e) {
                throw new MessageException(e);
            } catch (Exception e) {
                throw new MessageException(e);
            }
        } else {
            return null;
        }
    }

    @Override
    public void suspect(final org.jgroups.Address suspectedMbr) {
        // Intentionally Left Blank
    }

    @Override
    public void block() {
        // Intentionally Left Blank

    }

    @Override
    public void unblock() {
        // Intentionally Left Blank

    }

    @Override
    public void getState(final OutputStream output) throws Exception {
        // Intentionally Left Blank

    }

    @Override
    public void setState(final InputStream input) throws Exception {
        // Intentionally Left Blank
    }

    /**
     * Handles processing of ASync Requests from both internal and external
     * sources.
     * 
     * @param message
     */
    private void processAsyncMessageRequest(final AbstractMessage message) throws Exception {
        if (message instanceof CommandMessage) {
            for (Address receiver : message.getReceivers()) {
                if (commandCommunicator.getClusterConnection().receiverBelongsToNode(receiver)) {
                    try {
                        commandMessageReceiver.handleIncomingAsyncCommandMessage(
                                (CommandMessage) message, receiver);
                    } catch (Exception e) {
                        // Log all exceptions thrown by ASync Messages
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }

        throw new MessageTypeNotSupportedException("Message type not supported, message ignored: "
                + message.getClass());
    }

    /**
     * Handles processing of Sync Requests from both internal and external
     * sources.
     * 
     * @param message
     */
    private Serializable processSyncMessageRequest(final AbstractMessage message) throws Exception {
        if (message instanceof CommandMessage) {
            CommandMessage commandMessage = (CommandMessage) message;
            MessageResponse responses = new MessageResponse();

            for (Address receiver : message.getReceivers()) {
                try {
                    if (commandCommunicator.getClusterConnection().receiverBelongsToNode(receiver)) {
                        try {
                            final Serializable result = commandMessageReceiver.handleIncomingSyncCommandMessage(
                                    commandMessage, receiver);
                            responses.addResponse(receiver, result);
                            logger.debug("Handled command " + commandMessage.name() +
                                    " from sender " + commandMessage.getSender().toString() +
                                    " for usersession " + commandMessage.session());
                        } catch (Exception e) {
                            // Convert all exceptions to MessageExceptions so
                            // they
                            // can always be sent through message API
                            responses.addException(receiver, new MessageException(e));
                        }
                    }
                } catch (NullPointerException e) {
                    logger.error(e.getMessage(), e);
                }
            }

            if (responses.isEmpty()) {
                logger.error("Message {} sent to {} was ignored since it was not meant for this connection.",
                        message, message.getReceivers());
            }

            return responses;
        } else if (message instanceof CommandStreamMessage) {
            final CommandStreamMessage streamMessage = (CommandStreamMessage) message;
            final InputStream stream = commandCommunicator.getClusterConnection()
                    .getReceivedStream(streamMessage.getStreamId(), 10000);
            final CommandMessage wrapped = (CommandMessage) streamMessage.getAbstractMessage();
            final MessageResponse responses = new MessageResponse();

            for (Address receiver : message.getReceivers()) {
                if (commandCommunicator.getClusterConnection().receiverBelongsToNode(receiver)) {
                    try {
                        final Serializable result = commandMessageReceiver
                                .handleIncomingSyncCommandMessage(wrapped, receiver, stream);
                        responses.addResponse(receiver, result);
                        logger.debug("Handled stream id {} from sender {}", streamMessage.getStreamId(), streamMessage.getSender());
                    } catch (Exception e) {
                        // Convert all exceptions to MessageExceptions so they
                        // can always be sent through message API
                        responses.addException(receiver, new MessageException(e));
                    }
                }
            }

            if (responses.isEmpty()) {
                logger.error("Message {} sent to {} was ignored since it was not meant for this"
                        + " connection.", message, message.getReceivers());
            }

            return responses;
        }

        throw new MessageTypeNotSupportedException("Message type not supported, message ignored: "
                + message.getClass());
    }

}
