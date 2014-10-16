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

import com.edifecs.epp.isc.AbstractMessage;
import com.edifecs.epp.isc.Address;

import java.util.Collection;

/**
 * A message wrapper with an attached stream; the stream is not included in the
 * message, but is sent separately. The message includes the stream's stream ID
 * so that, when the stream itself is received, it can be associated with this
 * message using the ID.
 * 
 * @author i-adamnels
 *
 */
public class CommandStreamMessage extends AbstractMessage {
    private static final long serialVersionUID = 1L;

    private final AbstractMessage abstractMessage;
    private final int streamId;

    public CommandStreamMessage(Address sender, Address receiver, AbstractMessage abstractMessage,
            int streamId) {
        super(sender, receiver);
        this.streamId = streamId;
        this.abstractMessage = abstractMessage;
    }

    public CommandStreamMessage(Address sender, Collection<Address> receivers,
            AbstractMessage abstractMessage, int streamId) {
        super(sender, receivers);
        this.streamId = streamId;
        this.abstractMessage = abstractMessage;
    }

    public AbstractMessage getAbstractMessage() {
        return abstractMessage;
    }

    public int getStreamId() {
        return streamId;
    }
}
