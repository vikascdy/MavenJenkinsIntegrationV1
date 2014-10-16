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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.edifecs.epp.isc.annotations.*;
import com.edifecs.epp.isc.core.command.AbstractCommandHandler;

public class TestMessageHandler extends AbstractCommandHandler implements ITestMessageHandler {

    private boolean received = false;

    private int counter = 0;

    private SynchronousQueue<String> queue = new SynchronousQueue<String>();

    public boolean isReceived() {
        return received;
    }

    public int getCounter() {
        return counter;
    }

    public SynchronousQueue<String> getQueue() {
        return queue;
    }

    public boolean testCommand() {
        received = true;
        System.out.println("Command Successfully sent!");
        return true;
    }

    public int testIncrementCommand() {
        counter++;
        System.out.println("Command Successfully sent!");
        return counter;
    }

    public String streamCommand(@StreamArg(name = "stream") InputStream stream) throws IOException {
        System.out.println("Got a stream.");
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final byte[] buffer = new byte[1024];
        int c;
        while ((c = stream.read(buffer)) > -1) {
            baos.write(buffer, 0, c);
        }
        stream.close();
        baos.flush();
        final String result = new String(baos.toByteArray());
        System.out.println("Stream contents: " + result);
        return result;
    }

    public String waitForMessage() throws InterruptedException, TimeoutException {
        System.out.println("Waiting for deliverMessage command...");
        final String message = queue.poll(10, TimeUnit.SECONDS);
        if (message == null) {
            System.out.println("Timed out while waiting for deliverMessage command.");
            throw new TimeoutException("Times out waiting for message.");
        }
        return message;
    }

    public boolean deliverMessage(@Arg(name = "message") String message) {
        System.out.println("Delivering message '" + message + "'.");
        return queue.offer(message);
    }
}
