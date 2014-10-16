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

import com.edifecs.epp.security.SessionId;

class StreamPacket implements StreamObject {
    private static final long serialVersionUID = 1L;

    private final int streamId;
    private final int packetId;
    private final byte[] data;
    private final SessionId userId;

    StreamPacket(int streamId, int packetId, byte[] data, SessionId userId) {
        this.streamId = streamId;
        this.packetId = packetId;
        this.data = data;
        this.userId = userId;
    }

    @Override
    public int getStreamId() {
        return streamId;
    }

    int getPacketId() {
        return packetId;
    }

    byte[] getData() {
        return data;
    }

    boolean isEndOfStream() {
        return data.length == 0;
    }

    @Override
    public SessionId getUserId() {
        return userId;
    }
}
