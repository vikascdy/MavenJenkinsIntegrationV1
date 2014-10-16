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
import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

import org.jgroups.Address;

class SendingMessageStream {
    private static final Random RND = new SecureRandom();

    private final InputStream stream;
    private final StreamingMessageHandler handler;
    private final Address destination;
    private final int streamId;
    private final long totalSize;
    private boolean finished = false;

    private Date createdDate;
    private Date updatedDate;

    SendingMessageStream(StreamingMessageHandler handler, Address destination,
            InputStream stream, long totalSize) {
        if (stream == null) {
            throw new NullPointerException("stream cannot be null.");
        }
        this.stream = stream;
        if (handler == null) {
            throw new NullPointerException("handler cannot be null.");
        }
        this.handler = handler;
        if (destination == null) {
            throw new NullPointerException("destination cannot be null.");
        }
        this.destination = destination;
        this.streamId = RND.nextInt();
        this.totalSize = totalSize;

        createdDate = new Date();
        updatedDate = new Date();
    }

    Address getSource() {
        return handler.getSource();
    }

    Address getDestination() {
        return destination;
    }

    int getStreamId() {
        return streamId;
    }

    int getPacketSize() {
        return handler.getPacketSize();
    }

    long getTotalSize() {
        return totalSize;
    }

    boolean ack(int packetId) throws IOException {
        updatedDate = new Date();
        if (finished) {
            return false;
        }
        final StreamPacket packet = buildPacket(packetId);
        if (packet.getData().length > 0) {
            handler.sendMessage(destination, packet);
            return true;
        }
        return false;
    }

    private StreamPacket buildPacket(int packetId) throws IOException {
        byte[] data = new byte[handler.getPacketSize()];

        try {
            final int bytesRead = stream.read(data);

            if (bytesRead == -1) {
                data = new byte[0];
                stream.close();
                finished = true;
            } else if (bytesRead < handler.getPacketSize()) {
                final byte[] padded = data;
                data = new byte[bytesRead];
                System.arraycopy(padded, 0, data, 0, bytesRead);
                stream.close();
                finished = true;
            }
        } catch (IOException e) {
            data = new byte[0];
            stream.close();
            finished = true;
        }

        return new StreamPacket(streamId, packetId + 1, data, handler.getCurrentUserId());
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void kill() throws IOException {
        stream.close();
    }
}
