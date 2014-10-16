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

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.jgroups.Address;

import com.edifecs.epp.isc.communicator.ClusterConnection;

public class ReceivingMessageStream extends InputStream {
    private final StreamingMessageHandler handler;
    private final Address source;
    private final int streamId;
    private final long totalSize;

    private long receivedBytes;
    private boolean finished = false;

    private final BlockingQueue<StreamPacket> queue;
    private byte[] currentPacket;
    private int pos;
    private int nextPacketId;

    private Thread recvThread;
    private IOException error;

    private Date createdDate;
    private Date updatedDate;

    ReceivingMessageStream(Address source, StreamHeader header,
            StreamingMessageHandler handler) throws IOException {
        this.handler = handler;
        this.source = source;
        this.streamId = header.getStreamId();
        this.totalSize = header.getTotalSize();
        this.queue = new ArrayBlockingQueue<StreamPacket>(2);

        createdDate = new Date();
        updatedDate = new Date();
    }

    @Override
    public int read() throws IOException {
        updatedDate = new Date();

        pos++;
        if (currentPacket == null || pos >= currentPacket.length) {
            if (finished && queue.size() == 0) {
                return -1;
            }

            pos = 0;
            try {
                StreamPacket p;

                do {
                    p = queue.poll(ClusterConnection.DEFAULT_MESSAGE_TIMEOUT / 5,
                            TimeUnit.MILLISECONDS);
                } while ((p == null) && !finished);

                if (finished) {
                    return -1;
                }

                if (p == null) {
                    throw new IOException(String.format("Receiving stream %x is closed.", streamId));
                }

                if (p.getPacketId() == -1) {
                    finished = true;
                    throw error;
                }
                currentPacket = p.getData();
            } catch (InterruptedException ex) {
                currentPacket = new byte[0];
                Thread.currentThread().interrupt();
            }
            if (currentPacket.length < StreamingMessageHandler.DEFAULT_PACKET_SIZE) {
                finished = true;
                if (error != null) {
                    throw error;
                }
            }
        }
        return currentPacket[pos] & 0xff;
    }

    @Override
    public int available() {
        if (totalSize < 0) {
            return -1;
        } else {
            final long diff = totalSize - receivedBytes;
            if (diff < 0) {
                return 0;
            } else if (diff > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            } else {
                return (int) diff;
            }
        }
    }

    public int getStreamId() {
        return streamId;
    }

    boolean acceptPacket(StreamPacket packet) {
        updatedDate = new Date();
        if (finished) {
            throw new IllegalStateException(String.format(
                    "Receiving stream %x is closed.", streamId));
        }
        if (packet == null) {
            throw new NullPointerException("Cannot accept a null packet.");
        }
        if (packet.getPacketId() != nextPacketId) {
            throw new IllegalStateException(String.format(
                    "Received packet #%d out of order. Expected packet #%d.",
                    packet.getPacketId(), nextPacketId));
        }
        nextPacketId++;

        try {
            while (!finished
                    && !queue.offer(packet, ClusterConnection.DEFAULT_MESSAGE_TIMEOUT / 5,
                    TimeUnit.MILLISECONDS)) {
            	// Retry putting packet into Queue
            	Thread.sleep(100);
            }
            receivedBytes += packet.getData().length;
            if (source == null) {
                throw new IllegalStateException("Source cannot be null.");
            }
            handler.sendMessage(source, new StreamAck(streamId, packet.getPacketId(), handler.getCurrentUserId()));

        } catch (IOException ex) {
            ex.printStackTrace();
            error = ex;
            queue.offer(new StreamPacket(streamId, -1, new byte[0], handler.getCurrentUserId()));
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            queue.offer(new StreamPacket(streamId, -1, new byte[0], handler.getCurrentUserId()));
            Thread.currentThread().interrupt();
        }

        return false;
    }

    void interruptWithError(IOException ex) {
        error = ex;
        if (!finished && recvThread != null) {
            recvThread.interrupt();
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void kill() throws IOException, InterruptedException {
        error = new IOException(String.format(
                "Receiving stream %s was force closed.", streamId));
        finished = true;
        queue.offer(new StreamPacket(-1, -1, new byte[0], handler.getCurrentUserId()));
    }
}
