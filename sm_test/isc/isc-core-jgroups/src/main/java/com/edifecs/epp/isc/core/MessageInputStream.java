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
package com.edifecs.epp.isc.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.concurrent.TimeoutException;

import com.edifecs.epp.isc.communicator.ClusterConnection;

/**
 * Serializable wrapper class for streams returned by synchronous messages. If
 * the stored {@link InputStream} is lost in serialization, it will be retrieved
 * from the current node's {@link ClusterConnection}, assuming the stream was
 * sent to the node that received the {@code StreamResponse} via JGroups.
 * 
 * @author i-adamnels
 */
public class MessageInputStream extends InputStream implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final long DEFAULT_TIMEOUT = 10000L;

    private transient InputStream stream;
    private int streamId;

    public MessageInputStream(int streamId) {
        this.stream = null;
        this.streamId = streamId;
    }

    public MessageInputStream(InputStream stream) {
        this.stream = stream;
        this.streamId = -1;
    }

    public void initStream(ClusterConnection connection)
            throws InterruptedException, TimeoutException {
        if (stream == null) {
            stream = connection.getReceivedStream(streamId, DEFAULT_TIMEOUT);
        }
    }

    @Override
    public int read() throws IOException {
        return stream.read();
    }

    @Override
    public void close() throws IOException {
        if (stream != null) {
            stream.close();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
    }

}
