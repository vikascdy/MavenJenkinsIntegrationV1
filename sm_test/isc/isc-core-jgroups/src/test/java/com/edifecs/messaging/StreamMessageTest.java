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

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.edifecs.epp.jgroups.stream.StreamingMessageHandler;
import com.edifecs.epp.jgroups.stream.StreamingMessageListener;

/**
 * Tests the Streaming Message Framework on a variety of message sizes by
 * sending streams of generated random bytes and comparing them to the output of
 * another random number generator with the same seed. This allows for
 * arbitrarily large streams, even larger than the JVM's memory capacity, as the
 * stream is never held in memory all at once.
 * 
 * @author i-adamnels
 */
@RunWith(Parameterized.class)
public class StreamMessageTest implements StreamingMessageListener {

    /**
     * Provides the parameters used by the {@link Parameterized} JUnit runner.
     * Each parameter is an array containing a single {@code int}, the length of
     * the stream to send. All of the tests in this class will be re-run for
     * each parameter returned.
     */
    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][] {
                {100}, // 100B
//                {100000}, // 100kB
//                {1000000}, // 1MB
//                {10000000}, // 10MB
//                {100000000} // 1GB
        });
    }

    @SuppressWarnings("serial")
    private static class StreamComparisonException extends Exception {
        StreamComparisonException(String message) {
            super(message);
        }
    }

    /**
     * A stream that generates a fixed amount of random bytes, generated from a
     * {@link Random} with a specific seed. Two {@code RandomStream}s with the
     * same seed and length will generate the exact same stream data.
     */
    private static class RandomStream extends InputStream {
        private final Random rnd;
        private final int length;
        private int pos;

        RandomStream(long seed, int length) {
            this.rnd = new Random(seed);
            this.length = length;
        }

        @Override
        public int read() throws IOException {
            if (pos < length) {
                ++pos;
                return rnd.nextInt(256);
            }
            return -1;
        }

        @Override
        public int available() {
            return length - pos;
        }
    }

    /**
     * A {@link StreamingMessageHandler} that provides its own JGroups
     * {@link Receiver}. For testing purposes only; you don't want to do this
     * with an actual {@code StreamingMessageHandler} unless you want to only be
     * able to send streams.
     */
    private class InterceptingStreamingMessageHandler extends StreamingMessageHandler
            implements Receiver {

        public InterceptingStreamingMessageHandler(JChannel channel) {
            super(channel);
            channel.setReceiver(this);
        }

        private Object deserializeMessage(final Message msg) throws Exception {
            byte[] data = msg.getBuffer();

            if (data != null) {
                ByteArrayInputStream byteArrayIS = new ByteArrayInputStream(data);
                ObjectInputStream objectIS = new ObjectInputStream(byteArrayIS);
                Object message = objectIS.readObject();
                return message;
            } else {
                return null;
            }
        }

        @Override
        public void receive(Message msg) {
            try {
                final Object payload = deserializeMessage(msg);
                tryToHandleStreamMessage(msg.getSrc(), payload);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void getState(OutputStream os) throws Exception {
        }

        @Override
        public void setState(InputStream is) throws Exception {
        }

        @Override
        public void block() {
        }

        @Override
        public void suspect(Address addr) {
        }

        @Override
        public void unblock() {
        }

        @Override
        public void viewAccepted(View v) {
        }
    }

    /**
     * Compares two streams byte-for-byte, throwing an exception if they don't
     * match.
     */
    private static void compareStreams(InputStream stream1, InputStream stream2)
            throws StreamComparisonException, IOException {
        // http://stackoverflow.com/a/4245962/548027

        ReadableByteChannel ch1 = Channels.newChannel(stream1);
        ReadableByteChannel ch2 = Channels.newChannel(stream2);

        ByteBuffer buf1 = ByteBuffer.allocateDirect(1024);
        ByteBuffer buf2 = ByteBuffer.allocateDirect(1024);

        try {
            while (true) {
                int n1 = ch1.read(buf1);
                int n2 = ch2.read(buf2);

                if (n1 == -1 || n2 == -1) {
                    if (n1 == n2) {
                        return;
                    } else {
                        throw new StreamComparisonException("Stream sizes differ.");
                    }
                }

                buf1.flip();
                buf2.flip();

                for (int i = 0; i < Math.min(n1, n2); i++) {
                    if (buf1.get() != buf2.get()) {
                        throw new StreamComparisonException("Stream content does not match.");
                    }
                }

                buf1.compact();
                buf2.compact();
            }

        } finally {
            if (stream1 != null) {
                stream1.close();
            }
            if (stream2 != null) {
                stream2.close();
            }
        }
    }

    private static final String CLUSTER_NAME = "MessagingTest";

    private final int streamLength;
    private JChannel sendChannel;
    private JChannel recvChannel;
    private StreamingMessageHandler sendHandler;
    private StreamingMessageHandler recvHandler;
    private Map<Integer, InputStream> receivedStreams;
    private Map<Integer, CountDownLatch> msgLocks;

    // This constructor is called by Parameterized. A new object of this class
    // is created for each parameter array returned by getParameters().
    public StreamMessageTest(int streamLength) {
        this.streamLength = streamLength;
    }

    @Before
    public void setUp() throws Exception {
        System.setProperty("java.net.preferIPv4Stack", "true");
        sendChannel = new JChannel(this.getClass().getResourceAsStream("/udp.xml"));
        sendChannel.connect(CLUSTER_NAME);
        recvChannel = new JChannel(this.getClass().getResourceAsStream("/udp.xml"));
        recvChannel.connect(CLUSTER_NAME);
        sendHandler = new InterceptingStreamingMessageHandler(sendChannel);
        recvHandler = new InterceptingStreamingMessageHandler(recvChannel);
        recvHandler.setStreamingMessageListener(this);
        receivedStreams = new HashMap<Integer, InputStream>();
        msgLocks = new HashMap<Integer, CountDownLatch>();
    }

    @Override
    public void receiveStream(Address source, int streamId, InputStream stream) throws IOException {
        System.err.println("Got stream!");
        synchronized (msgLocks) {
            receivedStreams.put(streamId, stream);
            final CountDownLatch latch = msgLocks.remove(streamId);
            if (latch != null) {
                latch.countDown();
            }
        }
    }

    private InputStream getReceivedStream(int streamId) throws InterruptedException,
            TimeoutException {
        synchronized (msgLocks) {
            if (receivedStreams.containsKey(streamId)) {
                return receivedStreams.remove(streamId);
            } else if (!msgLocks.containsKey(streamId)) {
                msgLocks.put(streamId, new CountDownLatch(1));
            }
        }
        CountDownLatch latch = msgLocks.get(streamId);
        if (latch!=null && !latch.await(100000, TimeUnit.MILLISECONDS)) {
            throw new TimeoutException("Timed out while waiting for stream.");
        }
        synchronized (msgLocks) {
            return receivedStreams.remove(streamId);
        }
    }

    /**
     * Tests sending and receiving a single randomly-generated stream. Compares
     * the received stream byte-for-byte with a copy of the stream generated
     * using the same random seed.
     * @throws StreamComparisonException 
     */
    @Test
    @Ignore
    public void testSingleStream() throws IOException, InterruptedException, TimeoutException, StreamComparisonException {
        final InputStream sendStream = new RandomStream(91, streamLength);
        final InputStream cmpStream = new RandomStream(91, streamLength);
        System.err.printf("Sending stream of length %d...%n", streamLength);
        final int id = sendHandler.sendStreamingMessage(recvChannel.getAddress(), sendStream,
                streamLength);
        System.err.printf("Waiting for received stream #%x...%n", id);
        final InputStream recvStream = getReceivedStream(id);
        System.err.println("Comparing streams...");
        compareStreams(cmpStream, recvStream);
    }

    /**
     * Tests sending and receiving a stream that does not match the stream used
     * for comparison, to verify that the test will throw an exception when the
     * streams do not match.
     */
    @Test(expected = StreamComparisonException.class)
    @Ignore
    public void testInvalidStream() throws Exception {
        final InputStream sendStream = new RandomStream(8281, streamLength); // Different
                                                                             // seed!
        final InputStream cmpStream = new RandomStream(91, streamLength);
        System.err.printf("Sending (invalid) stream of length %d...%n", streamLength);
        final int id = sendHandler.sendStreamingMessage(recvChannel.getAddress(), sendStream,
                streamLength);
        System.err.printf("Waiting for received stream #%x...%n", id);
        final InputStream recvStream = getReceivedStream(id);
        System.err.println("Comparing streams...");
        compareStreams(cmpStream, recvStream);
    }

    /**
     * Tests that the streaming message framework can handle caching 100+
     * streams without running out of memory. Skipped if the stream length is
     * prohibitively large (>=10MB).
     */
    @Test
    @Ignore
    public void test100SimultaneousStreams() throws IOException, InterruptedException,
            TimeoutException {
        if (streamLength >= 10000000) {
            System.err.println("test100Messages does not test streams of size 10MB or larger.");
            return;
        }
        System.err.printf("Sending 100 simultaneous streams of length %d...%n", streamLength);
        final int[] ids = new int[100];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = sendHandler.sendStreamingMessage(recvChannel.getAddress(),
                    new RandomStream(91, streamLength), streamLength);
        }
        for (int i = 0; i < ids.length; i++) {
            final int id = ids[i];
            System.err.printf("Waiting for received stream #%x...%n", id);
            final InputStream recvStream = getReceivedStream(id);
            System.err.println("Comparing streams...");
            try {
                compareStreams(new RandomStream(91, streamLength), recvStream);
            } catch (StreamComparisonException ex) {
                fail("For stream #" + (i + 1) + " of length " + streamLength + ": "
                        + ex.getMessage());
            }
        }
    }

    /**
     * Tests that the streaming message framework can send many streams
     * consecutively without errors or memory leaks.
     */
    @Test
    @Ignore
    public void test100SequentialStreams() throws IOException, InterruptedException,
            TimeoutException {
        System.err.printf("Sending 100 sequential streams of length %d...%n", streamLength);
        for (int i = 0; i < 100; i++) {
            final int id = sendHandler.sendStreamingMessage(recvChannel.getAddress(),
                    new RandomStream(i, streamLength), streamLength);
            System.err.printf("Waiting for received stream #%x...%n", id);
            final InputStream recvStream = getReceivedStream(id);
            System.err.println("Comparing streams...");
            try {
                compareStreams(new RandomStream(i, streamLength), recvStream);
            } catch (StreamComparisonException ex) {
                fail("For stream #" + (i + 1) + " of length " + streamLength + ": "
                        + ex.getMessage());
            }
        }
    }

    @After
    public void tearDown() {
        sendChannel.disconnect();
        recvChannel.disconnect();
    }
}
