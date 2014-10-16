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

import org.jgroups.Address;

/**
 * Listens for streaming messages received by a {@link StreamingMessageHandler}
 * and calls a handler method when streaming messages are received.
 * 
 * @author i-adamnels
 */
public interface StreamingMessageListener {

    /**
     * Listener method called when a {@link StreamingMessageHandler} receives a
     * header for an incoming streaming message.
     * 
     * @param source
     *            The JGroups address of the stream's sender.
     * @param streamId
     *            A number that uniquely identifies the stream on both sides of
     *            the connection.
     * @param stream
     *            The stream itself; a wrapper object that receives stream
     *            packets over the JGroups channel.
     * @throws IOException
     *             If the stream is closed prematurely, the JGroups connection
     *             is broken, or any error occurs in reading the stream. If an
     *             {@code IOException} is thrown, it will be relayed back to the
     *             stream's sender, which will throw the same exception.
     */
    void receiveStream(Address source, int streamId, InputStream stream)
            throws IOException;
}
