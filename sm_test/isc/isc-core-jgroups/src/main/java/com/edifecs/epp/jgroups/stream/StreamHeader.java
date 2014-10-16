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

import org.jgroups.Address;

import com.edifecs.epp.security.SessionId;

class StreamHeader implements StreamObject {
    private static final long serialVersionUID = 1L;

    private final int streamId;
    private final int packetSize;
    private final long totalSize;
    private final SessionId userId;

    StreamHeader(SendingMessageStream sstream, SessionId userId) {
        this.streamId = sstream.getStreamId();
        this.packetSize = sstream.getPacketSize();
        this.totalSize = sstream.getTotalSize();
        this.userId = userId;
    }

    @Override
    public int getStreamId() {
        return streamId;
    }

    int getPacketSize() {
        return packetSize;
    }

    long getTotalSize() {
        return totalSize;
    }

    ReceivingMessageStream createReceivingStream(Address source,
            StreamingMessageHandler handler) throws IOException {
        return new ReceivingMessageStream(source, this, handler);
    }

    @Override
    public SessionId getUserId() {
        return userId;
    }
}
