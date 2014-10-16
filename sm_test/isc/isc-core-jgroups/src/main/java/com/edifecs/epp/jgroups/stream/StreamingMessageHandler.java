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
package com.edifecs.epp.jgroups.stream;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;

import com.edifecs.epp.isc.CommandCommunicator;
import com.edifecs.epp.isc.communicator.ClusterConnection;
import com.edifecs.epp.isc.exception.StreamMessageException;
import com.edifecs.epp.security.SessionId;

/**
 * <p>
 * The core class of the JGroups streaming message framework. Can send and
 * receive {@link InputStream}s as series of packets sent over JGroups.
 * </p>
 * 
 * <p>
 * In order to use this class in a JGroups application, every member of the
 * JGroups cluster that intends to send or receive streams should have, in its
 * message handler method(s), a call to
 * {@link #tryToHandleStreamMessage(Address, Object)} which is passed the
 * contents (decoded message body) of each received message and which causes the
 * message handler to return immediately without processing the message further
 * if it returns {@code true}.
 * </p>
 * 
 * @author i-adamnels
 */

public class StreamingMessageHandler {

    public static final int DEFAULT_PACKET_SIZE = 1024 * 64; // 64k
    public static final int DEFAULT_BUFFER_SIZE = 1024 * 1024 * 200; // 200MB

    private final int packetSize;
    private final int bufferSize;
    private final Address source;
    private final JChannel channel;

    private final StreamKillThread streamKillThread;
    private final Map<Integer, SendingMessageStream> sendingStreams;
    private final Map<Integer, ReceivingMessageStream> receivingStreams;

    private StreamingMessageListener listener;

    public StreamingMessageHandler(JChannel channel) {
        this(channel, DEFAULT_PACKET_SIZE, DEFAULT_BUFFER_SIZE);
    }

    public StreamingMessageHandler(JChannel channel, int packetSize, int bufferSize) {
        this.channel = channel;
        this.packetSize = packetSize;
        this.bufferSize = bufferSize;

        source = channel.getAddress();
        sendingStreams = new ConcurrentHashMap<Integer, SendingMessageStream>();
        receivingStreams = new ConcurrentHashMap<Integer, ReceivingMessageStream>();

        streamKillThread = new StreamKillThread();
        streamKillThread.start();
    }

    public int getPacketSize() {
        return packetSize;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public Address getSource() {
        return source;
    }

    public JChannel getChannel() {
        return channel;
    }
    
    public void disconnect() {
        streamKillThread.disconnect();
    }

    public void setStreamingMessageListener(StreamingMessageListener listener) {
        this.listener = listener;
    }

    public int sendStreamingMessage(Address dest, InputStream stream) throws IOException {
        return sendStreamingMessage(dest, stream, -1);
    }

    public int sendStreamingMessage(Address dest, InputStream stream, long streamLength) throws IOException {
        final SendingMessageStream sstream = new SendingMessageStream(this, dest, stream, streamLength);
        final StreamHeader header = new StreamHeader(sstream, getCurrentUserId());
        sendingStreams.put(sstream.getStreamId(), sstream);
        sendMessage(dest, header);
        return sstream.getStreamId();

    }

    public boolean tryToHandleStreamMessage(Address source, Object msg) throws StreamMessageException, IOException {
        if (msg instanceof StreamObject) {
            StreamObject so = (StreamObject) msg;
            CommandCommunicator.getInstance().getSecurityManager().getSessionManager().registerCurrentSession(so.getUserId());

            try {
                if (msg instanceof StreamHeader) {
                    openReceivingStream(source, (StreamHeader) msg);
                } else if (msg instanceof StreamPacket) {
                    handlePacket(source, (StreamPacket) msg);
                } else if (msg instanceof StreamAck) {
                    handleAck(source, (StreamAck) msg);
                } else if (msg instanceof StreamExceptionWrapper) {
                    ((StreamExceptionWrapper) msg).getException().printStackTrace();
                    handleRemoteException((StreamExceptionWrapper) msg);
                } else {
                    throw new StreamMessageException("Got unrecognized StreamObject subtype: "
                            + msg.getClass().getCanonicalName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    void sendMessage(Address dest, Serializable payload) throws IOException {
        Message msg = new Message();
        msg.setDest(dest);
        byte[] data = convertToByteArray(payload);
        msg.setBuffer(data);
        try {
            channel.send(msg);
        } catch (Exception ex) {
            throw new IOException("Error sending message: " + ex.getMessage(), ex);
        }
    }

    void tryToSendMessage(Address dest, Serializable payload) throws IOException {
        sendMessage(dest, payload);
    }

    private void openReceivingStream(Address source, StreamHeader header) throws IOException {
        try {
            if (listener == null) {
                throw new IllegalStateException("No listener is assigned to"
                        + " this StreamingMessageHandler. Cannot accept any" + " incoming streams.");
            }
            if (header.getPacketSize() > bufferSize) {
                throw new IllegalStateException(String.format("Received a"
                        + " stream with packet size %d, which is larger than this"
                        + " handler's buffer size (%d). Unable to read stream.", header.getPacketSize(), bufferSize));
            }
            final ReceivingMessageStream rstream = header.createReceivingStream(source, this);
            receivingStreams.put(rstream.getStreamId(), rstream);
            try {
                sendMessage(source, new StreamAck(rstream.getStreamId(), -1, getCurrentUserId()));
                listener.receiveStream(source, rstream.getStreamId(), rstream);
            } catch (Exception ex) {
                ex.printStackTrace();
                final StreamObject exw = handleLocalException(false, rstream.getStreamId(), ex);
                try {
                    tryToSendMessage(source, exw);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            final StreamObject exw = handleLocalException(false, header.getStreamId(), ex);
            tryToSendMessage(source, exw);
        }
    }

    private void handlePacket(Address source, StreamPacket packet) throws IOException {
        try {
            final ReceivingMessageStream rstream = receivingStreams.get(packet.getStreamId());
            if (rstream == null) {
                throw new IllegalStateException(String.format("No receiving stream exists with the id %x.",
                        packet.getStreamId()));
            }
            if (packet.isEndOfStream()) {
                receivingStreams.remove(packet.getStreamId());
            }
            rstream.acceptPacket(packet);
        } catch (Exception ex) {
            final StreamObject exw = handleLocalException(false, packet.getStreamId(), ex);
            tryToSendMessage(source, exw);
        }
    }

    private void handleAck(Address source, StreamAck ack) throws IOException {
        try {
            final SendingMessageStream stream = sendingStreams.get(ack.getStreamId());
            if (stream == null) {
                throw new IllegalStateException(String.format("No sending stream exists with the id %x.",
                        ack.getStreamId()));
            }
            boolean sent = stream.ack(ack.getPacketId());
            if (!sent) {
                sendingStreams.remove(ack.getStreamId());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            final StreamObject exw = handleLocalException(true, ack.getStreamId(), ex);
            tryToSendMessage(source, exw);
        }
    }

    private void handleRemoteException(StreamExceptionWrapper exw) {
        if (exw.isSendStream()) {
            sendingStreams.remove(exw.getStreamId());
        } else {
            if (receivingStreams.containsKey(exw.getStreamId())) {
                final IOException wrapped = new IOException("Sender threw exception: "
                        + exw.getException().getMessage(), exw.getException());
                receivingStreams.get(exw.getStreamId()).interruptWithError(wrapped);
                receivingStreams.remove(exw.getStreamId());
            }
        }
    }

    private StreamExceptionWrapper handleLocalException(boolean send, int streamId, Exception ex) {
        return new StreamExceptionWrapper(streamId, !send, ex, getCurrentUserId());
    }

    private byte[] convertToByteArray(final Serializable message) throws IOException {
        if (message != null) {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
            ObjectOutputStream objectOS = new ObjectOutputStream(new BufferedOutputStream(byteStream));
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

    public SessionId getCurrentUserId() {
        return CommandCommunicator.getInstance().getSecurityManager().getSessionManager().getCurrentSession();
    }

    class StreamKillThread extends Thread {

        boolean running = true;
        
        public StreamKillThread() {
        }

        public void disconnect() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Date currentDate = new Date();

                    for (Entry<Integer, SendingMessageStream> entry : sendingStreams.entrySet()) {
                        if (currentDate.getTime() - entry.getValue().getUpdatedDate().getTime() > ClusterConnection.DEFAULT_MESSAGE_TIMEOUT) {
                            sendingStreams.remove(entry.getKey()).kill();
                        }
                    }

                    for (Entry<Integer, ReceivingMessageStream> entry : receivingStreams.entrySet()) {
                        if (entry.getValue().isFinished()) {
                            receivingStreams.remove(entry.getKey());
                        } else {
                            if (currentDate.getTime() - entry.getValue().getUpdatedDate().getTime() > ClusterConnection.DEFAULT_MESSAGE_TIMEOUT) {
                                receivingStreams.remove(entry.getKey()).kill();
                            }
                        }
                    }

                    Thread.sleep(1000);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
