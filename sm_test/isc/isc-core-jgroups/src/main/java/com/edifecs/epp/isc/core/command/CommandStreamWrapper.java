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
package com.edifecs.epp.isc.core.command;

import java.io.InputStream;
import java.io.Serializable;

/**
 * A sender-side wrapper for sending {@link InputStream}s as command arguments.
 * Whenever a command expects an {@code InputStream} as an argument, the stream
 * object should be wrapped in a {@code CommandStreamWrapper} before being used
 * as an argument, in order to ensure that it is properly serialized.
 * 
 * @author i-adamnels
 */
public class CommandStreamWrapper implements Serializable {
    private static final long serialVersionUID = 1L;

    private final transient InputStream stream;
    private int streamId;

    public CommandStreamWrapper(InputStream stream) {
        this.stream = stream;
        this.streamId = -1;
    }

    public int getStreamId() {
        return streamId;
    }

    public boolean containsActualStream() {
        return stream != null;
    }

    public InputStream getStream() {
        return stream;
    }

    void setStreamId(int id) {
        streamId = id;
    }
}
