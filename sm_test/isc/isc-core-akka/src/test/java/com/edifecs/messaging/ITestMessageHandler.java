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
package com.edifecs.messaging;

import com.edifecs.epp.isc.annotations.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeoutException;

@CommandHandler
@NullSessionAllowed
public interface ITestMessageHandler {

    // TODO: Make some of these async.

    @SyncCommand(name = "testCommand")
    public boolean testCommand();

    @SyncCommand(name = "testIncrementCommand")
    public int testIncrementCommand();

    @SyncCommand(name = "streamCommand")
    public String streamCommand(@StreamArg(name = "stream") InputStream stream) throws IOException;

    @SyncCommand(name = "waitForMessage")
    public String waitForMessage() throws InterruptedException, TimeoutException;

    @SyncCommand(name = "deliverMessage")
    public boolean deliverMessage(@Arg(name = "message") String message);
}
