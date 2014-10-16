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
package com.edifecs.epp.isc.communicator;

import java.io.InputStream;

import com.edifecs.epp.isc.MessageResponse;
import com.edifecs.epp.isc.command.CommandMessage;

public interface ICommunicator {

    MessageResponse sendSyncMessage(CommandMessage msg, InputStream stream) throws Exception;

    MessageResponse sendSyncMessage(CommandMessage msg) throws Exception;

    void sendAsyncMessage(CommandMessage msg, InputStream stream) throws Exception;

    void sendAsyncMessage(CommandMessage msg) throws Exception;

    void sendBroadcastMessage(CommandMessage msg) throws Exception;

    void sendBroadcastMessage(CommandMessage msg, InputStream stream) throws Exception;

}
