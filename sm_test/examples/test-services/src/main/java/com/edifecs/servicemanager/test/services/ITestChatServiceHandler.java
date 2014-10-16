// -----------------------------------------------------------------------------
// Copyright (c) Edifecs Inc. All Rights Reserved.
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

package com.edifecs.servicemanager.test.services;

import com.edifecs.epp.isc.annotations.Akka;
import com.edifecs.epp.isc.annotations.Arg;
import com.edifecs.epp.isc.annotations.Command;
import com.edifecs.epp.isc.annotations.CommandHandler;
import com.edifecs.epp.isc.Address;

/**
 * Handles the chat messages.
 * Users write messages in the <code>ChatWindow</code> and it is multicasted to all running applications 
 * 
 * @author willclem
 */
@CommandHandler
@Akka(enabled=true)
public interface ITestChatServiceHandler {

    @Command(name = "addChatServiceClientCommand")
    public Boolean addChatServiceClientCommand(
            @Arg(name = "addr", required = true, description = "Client Address") Address addr
        ) throws Exception;
        
    @Command(name = "removeChatServiceClientCommand")
    public Boolean removeChatServiceClientCommand(
            @Arg(name = "addr", required = true, description = "Client Address"
        ) Address addr) throws Exception;

    @Command(name = "sendChatMessageCommand")
    public Boolean sendChatMessageCommand(
            @Arg(name = "message", required = true, description = "Message to be sent") String msg,
            @Arg(name = "client", required = true, description = "Application Name")    String name,
            @Arg(name = "addr", required = true, description = "Source Address")        Address src
        ) throws Exception;
}

