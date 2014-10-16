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
package com.edifecs.jgroup;

import java.io.File;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JgroupSecurityTest {

    private JChannel sender;
    private JChannel receiver;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testEncryption() throws Exception {
        File config = new File(this.getClass().getResource("/udpE.xml")
                .getFile());
        sender = new JChannel(config);
        receiver = new JChannel(config);
        receiver.setReceiver(new ReceiverAdapter() {
            @Override
            public void receive(Message msg) {
                Long t2 = System.currentTimeMillis();
                System.out.println((double) t2 / 1000);
                System.out.println(msg.printHeaders());
                System.out.println("received Encrypted msg from channel " + msg.getSrc() + ": "
                        + msg.getObject());
            }
        });
        sender.connect("MyCluster");
        receiver.connect("MyCluster");
        sender.send(new Message(null, null, "hello world "));
        Thread.sleep(100);
        sender.close();
        receiver.close();
    }

    @Test
    public void testCompression() throws Exception {
        File config = new File(this.getClass().getResource("/udpC.xml")
                .getFile());
        sender = new JChannel(config);
        receiver = new JChannel(config);
        receiver.setReceiver(new ReceiverAdapter() {
            @Override
            public void receive(Message msg) {
                Long t2 = System.currentTimeMillis();
                System.out.println(msg.printHeaders());
                System.out.println((double) t2 / 1000);
                System.out.println("received Compressed msg from channel " + msg.getSrc() + ": "
                        + msg.getObject());
            }
        });
        sender.connect("MyCluster");
        receiver.connect("MyCluster");
        sender.send(new Message(null, null, "hello world "));
        Thread.sleep(100);
        sender.close();
        receiver.close();
    }

    @Test
    public void testAuthSameKeyStore() throws Exception {
        File config = new File(this.getClass().getResource("/udpA.xml")
                .getFile());
        sender = new JChannel(config);
        receiver = new JChannel(config);
        receiver.setReceiver(new ReceiverAdapter() {
            @Override
            public void receive(Message msg) {
                Long t2 = System.currentTimeMillis();
                System.out.println((double) t2 / 1000);
                System.out.println(msg.printHeaders());
                System.out.println("received msg from channel " + msg.getSrc() + ": "
                        + msg.getObject());
            }
        });
        sender.connect("MyCluster");
        receiver.connect("MyCluster");
        sender.send(new Message(null, null, "hello world "));
        Thread.sleep(100);
        sender.close();
        receiver.close();
    }

    @Test
    public void testAllProperties() throws Exception {
        File config = new File(this.getClass().getResource("/udp.xml")
                .getFile());
        sender = new JChannel(config);
        receiver = new JChannel(config);
        receiver.setReceiver(new ReceiverAdapter() {
            @Override
            public void receive(Message msg) {
                Long t2 = System.currentTimeMillis();
                System.out.println((double) t2 / 1000);
                System.out.println(msg.printHeaders());
                System.out.println("received msg from channel " + msg.getSrc() + ": "
                        + msg.getObject());
            }
        });
        sender.connect("MyCluster");
        receiver.connect("MyCluster");
        sender.send(new Message(null, null, "hello world "));
        Thread.sleep(100);
        sender.close();
        receiver.close();
    }
}
