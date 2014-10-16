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

import com.edifecs.core.configuration.helper.TypesafeConfigKeys;
import com.edifecs.epp.isc.*;
import com.edifecs.epp.isc.Address;
import com.edifecs.epp.isc.core.CommandStreamMessage;
import com.edifecs.epp.isc.exception.ConnectionException;
import com.edifecs.epp.isc.exception.MessageException;
import com.edifecs.epp.isc.exception.StreamMessageException;
import com.edifecs.epp.isc.receiver.JGroupsMessageReceiver;
import com.edifecs.epp.jgroups.stream.StreamingMessageHandler;
import com.edifecs.epp.jgroups.stream.StreamingMessageListener;
import org.jgroups.*;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.stack.IpAddress;
import org.jgroups.util.DefaultSocketFactory;
import org.jgroups.util.Rsp;
import org.jgroups.util.RspList;

import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Only handles outbound communication through JGroups.
 */
public class ClusterConnection implements StreamingMessageListener {

    private static final int BYTE_ARRAY_SPLIT_ARRAY_LENGTH = 5000;

    private JChannel channel;

    private CommandCommunicator commandCommunicator;

    /**
     * Default Timeout for JGroups communications.
     */
    // TODO: Move this default configuration into a configuration file, it can
    // be set on a per command basis already
    public static long DEFAULT_MESSAGE_TIMEOUT;

    private JGroupsMessageReceiver messageReceiver;

    private MessageDispatcher messageDispatcher;

    private StreamingMessageHandler streamHandler;

    private Map<Integer, InputStream> waitingStreams = new ConcurrentHashMap<Integer, InputStream>();

    private void setSystemProperties() {
        System.setProperty(Global.USE_JDK_LOGGER, "false");
    }

    /**
     * Establish connection to the cluster joining with the given ID
     * information.
     * 
     * @param commandCommunicator
     * 
     * @param clusterName
     *            Name of the Cluster to connect too
     * @param address
     *            The ID Information for the Node
     * @throws ConnectionException
     *             Thrown is there is a problem connecting to the cluster
     */
    public ClusterConnection(CommandCommunicator commandCommunicator, JChannel channel, String clusterName,
            Address address) throws ConnectionException {
        if (clusterName == null) {
            throw new ConnectionException("Cannot connect to a null cluster");
        }
        
        this.channel = channel;
        
        DefaultSocketFactory socketFactory = new DefaultSocketFactory();

        messageReceiver = new JGroupsMessageReceiver(commandCommunicator);
        this.commandCommunicator = commandCommunicator;

        DEFAULT_MESSAGE_TIMEOUT = commandCommunicator.config().getDuration(TypesafeConfigKeys.SYNC_MESSAGE_TIMEOUT, TimeUnit.MILLISECONDS);

        try {
            setSystemProperties();

            channel.setSocketFactory(socketFactory);
            channel.setName(address.toString());

            messageDispatcher = new MessageDispatcher(channel, messageReceiver, messageReceiver,
                    messageReceiver);

            streamHandler = new StreamingMessageHandler(channel);
            streamHandler.setStreamingMessageListener(this);
        } catch (Exception e) {
            throw new ConnectionException("Unable to establish a connection to the cluster.", e);
        }
    }
    
    public void connect(String clusterName) throws ConnectionException {
        try {
            channel.connect(clusterName);
        } catch (Exception e) {
            throw new ConnectionException("Unable to establish a connection to the cluster.", e);
        }
    }

    public boolean isConnected() {
        return channel.isConnected();
    }

    public void disconnect() {
        channel.disconnect();
        channel.close();
        streamHandler.disconnect();

        waitingStreams.clear();
    }

    /*
     * 
     * Broadcast Message Definitions
     */

    public void sendBroadcast(final AbstractMessage message) throws MessageException {
        sendBroadcast(message, new MessageOption());
    }

    public void sendBroadcast(final AbstractMessage message, final MessageOption options)
            throws MessageException {
        
        if (message != null && message.getSender() == null) {
            message.setSender(commandCommunicator.getAddress());
        }

        if (channel != null && channel.isConnected()) {
            try {
                Message msg = new Message();
                byte[] data = convertToByteArray(message);

                msg.setBuffer(data);
                msg.setDest(null);
                channel.send(msg);
            } catch (IOException e) {
                throw new MessageException("Invalid Message Format.", e);
            } catch (Exception e) {
                throw new MessageException("Unable to send Message.", e);
            }
        }
    }

    /*
     * 
     * Send ASYNC Message Definitions
     */

    public void sendAsyncMessage(final AbstractMessage message) throws MessageException {
        sendAsyncMessage(message, new MessageOption());
    }

    public void sendAsyncMessage(final AbstractMessage message, InputStream inputStream)
            throws MessageException {
        throw new UnsupportedOperationException(
                "ASync sending of messages with InputStreams not yet supported");
    }

    public void sendAsyncMessage(final AbstractMessage message, final MessageOption options)
            throws MessageException {
        if (message.getSender() == null) {
            message.setSender(commandCommunicator.getAddress());
        }

        for (Address address : message.getReceivers()) {
            if (address != null) {
                try {
                    Address id = new Address(address.getServerName(), address.getNodeName());
                    Message msg = new Message();
                    byte[] data = convertToByteArray(message);
                    msg.setBuffer(data);
                    msg.setDest(commandCommunicator.getAddressRegistry().getAddressForNode(id));
                    channel.send(msg);
                } catch (IOException e) {
                    throw new MessageException("Invalid Message Format.", e);
                } catch (Exception e) {
                    throw new MessageException("Unable to send Message.", e);
                }
            }
        }
    }

    /*
     * Send Sync Broadcast Message Definitions
     */

    // TODO: Implement Sync Broadcast Messages.

    /*
     * 
     * Send Sync Message Definitions
     */

    public MessageResponse sendSyncMessage(final AbstractMessage message) throws MessageException {
        return sendSyncMessage(message, new MessageOption());
    }

    public MessageResponse sendSyncMessage(final AbstractMessage message, final int timeout)
            throws MessageException {
        MessageOption options = new MessageOption();
        options.setTimeout(timeout);

        return sendSyncMessage(message, options);
    }

    public MessageResponse sendSyncMessage(final AbstractMessage message,
            final MessageOption options) throws MessageException {

        // Set Sender ID if it doesn't exist
        if (message.getSender() == null) {
            message.setSender(commandCommunicator.getAddress());
        }

        // Set Default Request Options
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.setMode(ResponseMode.GET_ALL).setAnycasting(true);

        if (options.getTimeout() <= 0) {
            requestOptions.setTimeout(DEFAULT_MESSAGE_TIMEOUT);
        } else {
            requestOptions.setTimeout(options.getTimeout());
        }

        Collection<org.jgroups.Address> receivers = getReceiverAddresses(message);

        if (receivers == null) {
            throw new MessageException("Invalid Addresses, Unable to send message to: "
                    + message.getReceivers());
        }

        org.jgroups.Message msg = new org.jgroups.Message();

        RspList<?> rspList = null;
        try {
            byte[] data = convertToByteArray(message);
            msg.setBuffer(data);

            rspList = messageDispatcher.castMessage(receivers, msg, requestOptions);
        } catch (IOException e) {
            throw new MessageException("Invalid Message Format.", e);
        } catch (Exception e) {
            throw new MessageException("Unable to send Message.", e);
        }

        MessageResponse response = createResponse(rspList);

        // Throw a MessageException if there is no response to the message
        // request.
        if (response.getResponseMap().size() == 0 && response.getExceptionMap().size() == 0) {
            throw new MessageException("No response from any message receivers.");
        }

        return response;
    }

    public MessageResponse sendSyncMessage(AbstractMessage message, final InputStream inputStream)
            throws Exception {
        return sendSyncMessage(message, inputStream, new MessageOption());
    }

    public MessageResponse sendSyncMessage(final AbstractMessage message,
            final InputStream inputStream, final MessageOption options) throws MessageException {
        if (message.getReceivers().size() != 1) {
            throw new MessageException("A stream message can only have one recipient.");
        }
        try {
            MessageOption messageOption = new MessageOption();
            messageOption.setTimeout(0);
            for (Address address : message.getReceivers()) {
                final int streamId = sendStream(address, inputStream, -1);
                return sendSyncMessage(
                        new CommandStreamMessage(message.getSender(), message.getReceivers(),
                                message, streamId), messageOption);
            }
            throw new MessageException("A stream message can only have one recipient.");
        } catch (IOException ex) {
            throw new MessageException(ex);
        }
    }

    /*
     * Streaming Methods
     */

    /**
     * Delegate to
     * {@link StreamingMessageHandler#tryToHandleStreamMessage(org.jgroups.Address, Object)}
     * .
     * 
     * @param source
     *            The JGroups address from which the given message body object
     *            originated.
     * @param msg
     *            The message body object attached to a received message.
     * @return {@code true} if {@code obj} was a stream packet/header and was
     *         handled as such; {@code false} if {@code obj} was not handled and
     *         should be handled by another method.
     * @throws IOException
     * @throws StreamMessageException
     */
    public boolean tryToHandleStreamMessage(org.jgroups.Address source, Object msg)
            throws StreamMessageException, IOException {
        return streamHandler.tryToHandleStreamMessage(source, msg);
    }

    /**
     * Sends an {@link InputStream} over JGroups to another node, and returns a
     * unique ID number that can be used to retrieve the stream on the other
     * node.
     * 
     * @param dest
     *            The address of the service or node to send the stream to. Only
     *            the node part of the address will be used; to guarantee that a
     *            specific service receives the stream, use the unique ID number
     *            returned by this method.
     * @param inputStream
     *            The stream to send. Should not be {@code null} or closed.
     * @param streamLength
     *            Optional; the total length of the stream, in bytes. If the
     *            stream length is unknown, this should be -1.
     * @return The unique identifier of the stream. This number can be used to
     *         retrieve the stream from the other node's
     *         {@code ClusterConnection} using
     *         {@link #receiveStream(org.jgroups.Address, int, InputStream)}.
     * @throws IOException
     */
    public int sendStream(Address dest, InputStream inputStream, long streamLength)
            throws IOException {
        final org.jgroups.Address jaddr = commandCommunicator.getAddressRegistry()
                .getAddressForNode(dest);
        return streamHandler.sendStreamingMessage(jaddr, inputStream, streamLength);
    }

    /**
     * Listener method called when this node receives an {@link InputStream}.
     * 
     * @param source
     *            The JGroups address from which the stream originated.
     * @param streamId
     *            A unique identifier for the stream.
     * @param stream
     *            The stream itself. A wrapper object around the stream data
     *            being sent over JGroups.
     * @throws IOException
     *             If an error occurs while receiving the stream, if the stream
     *             is corrupted in any way, or if the stream's ID number is a
     *             duplicate of an existing stream.
     */
    @Override
    public void receiveStream(org.jgroups.Address source, int streamId, InputStream stream)
            throws IOException {
        if (waitingStreams.containsKey(streamId)) {
            throw new IOException(String.format("Another stream with the ID %x is currently"
                    + " cached by this receiver.", streamId));
        }
        waitingStreams.put(streamId, stream);
    }

    /**
     * Retrieves a stream that has been sent to this node by its unique ID
     * number, blocking if necessary until the stream is received or the given
     * timeout expires. The stream in question is removed from storage when this
     * method is called; if it is called twice in a row with the same ID number,
     * the second call will most likely time out.
     * 
     * @param streamId
     *            The unique ID number of the stream to retrieve.
     * @param timeout
     *            The time to wait, in milliseconds, before throwing a
     *            {@link TimeoutException}.
     * @return The most recently received stream with the given ID number.
     * @throws InterruptedException
     *             If the calling thread is interrupted while waiting for the
     *             stream to arrive.
     * @throws TimeoutException
     *             If no stream with the given ID is received within the
     *             specified timeout period.
     */
    public InputStream getReceivedStream(int streamId, long timeout) throws InterruptedException,
            TimeoutException {
        int timer = 0;
        while (!waitingStreams.containsKey(streamId)) {
            Thread.sleep(1);
            if (timer >= timeout) {
                throw new TimeoutException(String.format("Timed out after %dms waiting for"
                        + " stream #%x to arrive.", timeout, streamId));
            }
            timer++;
        }
        return waitingStreams.remove(streamId);
    }

    /*
     * 
     * Other Methods
     */

    /*
     * 
     * Private Methods
     */
    private byte[] convertToByteArray(final AbstractMessage message) throws IOException {
        if (message != null) {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(
                    BYTE_ARRAY_SPLIT_ARRAY_LENGTH);
            ObjectOutputStream objectOS = new ObjectOutputStream(new BufferedOutputStream(
                    byteStream));
            objectOS.flush();
            objectOS.writeObject(message);
            objectOS.flush();
            byte[] data = byteStream.toByteArray();
            objectOS.close();
            return data;
        } else {
            return null;
        }
    }

    private Collection<org.jgroups.Address> getReceiverAddresses(final AbstractMessage message) {
        Collection<org.jgroups.Address> addressList = new ArrayList<org.jgroups.Address>();
        for (Address receiver : message.getReceivers()) {
            addressList.add(commandCommunicator.getAddressRegistry().getAddressForNode(
                    new Address(receiver.getServerName(), receiver.getNodeName())));
        }
        return addressList;
    }

    /**
     * Converts the JGroups message response list into proper message API
     * message responses.
     * 
     * @param rspList
     *            The JGroups Response
     * @return MessageReponse
     */
    private MessageResponse createResponse(final RspList<?> rspList) {
        MessageResponse messageResponse = new MessageResponse();

        if (rspList == null || rspList.isEmpty()) {
            return messageResponse;
        }

        Set<org.jgroups.Address> keys = rspList.keySet();

        for (org.jgroups.Address key : keys) {
            Rsp<?> response = rspList.get(key);
            if (response == null || response.wasSuspected() || !response.wasReceived()) {
                messageResponse.setCompleteResponse(false);
            } else {
                Address nodeID = Address.fromString(key.toString());
                Address address = new Address(nodeID.getServerName(), nodeID.getNodeName());
                // Catch Unintentional Exceptions
                if (response.getException() != null) {
                    // Convert Message Exceptions to native Exceptions where
                    // possible

                    if (response.getException() instanceof MessageException) {
                        // If its a MessageException, try to recreate the
                        // original Exception if possible.
                        MessageException messageException = (MessageException) response
                                .getException();
                        messageResponse.addException(address, messageException);
                    } else {
                        // If Exception is not a MessageException simply add it
                        // too the list
                        messageResponse.addException(address, new MessageException(response.getException()));
                    }
                }
                // Catch Valid Exceptions
                if (response.getValue() instanceof Throwable) {
                    // Convert Message Exceptions to native Exceptions where
                    // possible

                    if (response.getValue() instanceof MessageException) {
                        // If its a MessageException, try to recreate the
                        // original Exception if possible.
                        MessageException messageException = (MessageException) response.getValue();
                        messageResponse.addException(address, messageException);
                    } else {
                        // If Exception is not a MessageException, simply add
                        // it to the list
                        messageResponse.addException(address, new MessageException((Throwable) response.getValue()));
                    }
                } else {
                    messageResponse.addResponse(address, (Serializable) response.getValue());
                }
            }
        }

        return messageResponse;
    }

    public View getView() {
        return channel.getView();
    }

    public String getIpForAddress(org.jgroups.Address address) throws ConnectionException {

        PhysicalAddress physicalAddr = (PhysicalAddress) channel.down(new Event(
                Event.GET_PHYSICAL_ADDRESS, address));

        if (physicalAddr instanceof IpAddress) {
            IpAddress ipAddr = (IpAddress) physicalAddr;
            InetAddress inetAddr = ipAddr.getIpAddress();
            return inetAddr.getHostAddress();
        }

        throw new ConnectionException("Invalid Node Address.");
    }

    // Code added for hostname
    public String getHostNameForAddress(org.jgroups.Address address) throws ConnectionException {

        PhysicalAddress physicalAddr = (PhysicalAddress) channel.down(new Event(
                Event.GET_PHYSICAL_ADDRESS, address));

        if (physicalAddr instanceof IpAddress) {
            IpAddress ipAddr = (IpAddress) physicalAddr;
            InetAddress inetAddr = ipAddr.getIpAddress();
            return inetAddr.getHostName();
        }

        throw new ConnectionException("Invalid Node Address.");
    }

    public boolean receiverBelongsToNode(final Address address) {
        // If meant for Agent
        if (address.isAgent() && commandCommunicator.getAddress().isAgent()) {
            return address.getServerName().equals(commandCommunicator.getAddress().getServerName());
            // If meant for Node
        } else if (address.isNode() && commandCommunicator.getAddress().isNode()) {
            return address.getServerName().equals(commandCommunicator.getAddress().getServerName())
                    && address.getNodeName().equals(commandCommunicator.getAddress().getNodeName());
            // If meant for Service
        } else if (address.isService() && commandCommunicator.getAddress().isNode()) {
            return address.getServerName().equals(commandCommunicator.getAddress().getServerName())
                    && address.getNodeName().equals(commandCommunicator.getAddress().getNodeName());
            // If meant for Resource
        } else if (address.isResource() && commandCommunicator.getAddress().isResource()) {
            return address.getServerName().equals(commandCommunicator.getAddress().getServerName());
        }
        return false;
    }

    public String getClusterName() {
        return channel.getClusterName();
    }

    public CommandCommunicator getCommandCommunicator() {
        return commandCommunicator;
    }
    
    public String getVersion() {
        return JChannel.getVersion();
    }

}
