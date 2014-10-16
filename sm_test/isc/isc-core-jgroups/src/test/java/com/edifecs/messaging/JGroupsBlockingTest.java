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

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.junit.Before;
import org.junit.Test;

public class JGroupsBlockingTest {

    private static final String CLUSTER_NAME = "JGroupsBlockingTest";

    private SynchronousQueue<String> queue1;
    private SynchronousQueue<String> queue2;

    private JChannel channel1;
    private JChannel channel2;

    private Address addr1;
    private Address addr2;

    @Before
    public void setUp() throws Exception {
        System.setProperty("java.net.preferIPv4Stack", "true");
        queue1 = new SynchronousQueue<String>();
        queue2 = new SynchronousQueue<String>();
        channel1 = new JChannel();
        channel2 = new JChannel();
        channel1.setReceiver(new Receiver1());
        channel2.setReceiver(new Receiver2());
        channel1.connect(CLUSTER_NAME);
        channel2.connect(CLUSTER_NAME);
        addr1 = channel1.getAddress();
        addr2 = channel2.getAddress();
    }

    @Test
    public void testBlockingMessage() throws Exception {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    channel1.send(addr2, "Hi there!".getBytes());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        .start();
        channel1.send(new Message(addr2));
        assertEquals("Hi there!", queue1.poll(10, TimeUnit.SECONDS));
    }

    private abstract class AbstractReceiver implements Receiver {
        @Override
        public void getState(OutputStream arg0) throws Exception {
        }

        @Override
        public void setState(InputStream arg0) throws Exception {
        }

        @Override
        public void block() {
        }

        @Override
        public void suspect(Address arg0) {
        }

        @Override
        public void unblock() {
        }

        @Override
        public void viewAccepted(View arg0) {
        }
    }

    private class Receiver1 extends AbstractReceiver {
        @Override
        public void receive(Message msg) {
            final String str = new String(msg.getBuffer());
            System.out.println("Received reply with string '" + str + "'.");
            queue1.offer(str);
        }
    }

    private class Receiver2 extends AbstractReceiver {
        @Override
        public void receive(Message msg) {
            if (msg.getBuffer() == null || msg.getBuffer().length == 0) {
                try {
                    System.out.println("Waiting for message.");
                    final String otherMsg = queue2.poll(10, TimeUnit.SECONDS);
                    if (otherMsg == null) {
                        System.out.println("Timed out waiting for second message.");
                    } else {
                        System.out.println("Got '" + otherMsg + "' from queue; sending as reply.");
                        channel2.send(addr1, otherMsg.getBytes());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                final String str = new String(msg.getBuffer());
                System.out.println("Received message with string '" + str + "'. Pushing to queue.");
                queue2.offer(str);
            }
        }
    }
}
