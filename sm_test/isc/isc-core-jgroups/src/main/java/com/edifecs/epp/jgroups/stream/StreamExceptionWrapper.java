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

class StreamExceptionWrapper implements StreamObject {
    private static final long serialVersionUID = 1L;

    private final int streamId;
    private final boolean send;
    private final Exception exception;
    private final SessionId userId;

    StreamExceptionWrapper(int streamId, boolean send, Exception exception, SessionId userId) {
        this.streamId = streamId;
        this.send = send;
        this.exception = exception;
        this.userId = userId;
    }

    @Override
    public int getStreamId() {
        return streamId;
    }

    boolean isSendStream() {
        return send;
    }

    Exception getException() {
        return exception;
    }

    @Override
    public SessionId getUserId() {
        return userId;
    }
}
