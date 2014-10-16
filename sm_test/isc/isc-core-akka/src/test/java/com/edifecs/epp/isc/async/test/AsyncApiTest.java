package com.edifecs.epp.isc.async.test;

import static org.junit.Assert.*;

import com.edifecs.epp.isc.async.*;
import com.edifecs.epp.isc.exception.MessageException;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class AsyncApiTest {
  
  SynchronousQueue<String> q;
  AsyncActorSystem system;

  @Before
  public void setUp() {
    q = new SynchronousQueue<>();
    system = new AsyncActorSystem();
  }

  @Test
  public void testSimpleChain() throws InterruptedException {
    final AsyncActor actor1 = system.spawn("actor1");
    actor1.sendDelayedMessage("foo", 1).then(new ChainCallback<String, String>() {
      public MessageFuture<String> call(String arg) {
        return actor1.sendDelayedMessage(arg, 1);
      }
    }).thenDo(new Callback<String>() {
      public void call(String arg) {
        q.offer(arg);
      }
    });
    Thread.sleep(1000);
    assertEquals("foo from actor1 from actor1", q.poll(5, TimeUnit.SECONDS));
  }

  @Test
  public void testMultipleChains() throws InterruptedException {
    final AsyncActor actor1 = system.spawn("actor1");
    final AsyncActor actor2 = system.spawn("actor2");
    actor2.sendDelayedMessage("bar", 2).then(new ChainCallback<String, String>() {
      public MessageFuture<String> call(String arg) {
        return actor2.sendDelayedMessage(arg, 1);
      }
    }).then(new ChainCallback<String, String>() {
      public MessageFuture<String> call(String arg) {
        return actor2.sendDelayedMessage(arg, 1);
      }
    }).thenDo(new Callback<String>() {
      public void call(String arg) {
        q.offer(arg);
      }
    });
    actor1.sendDelayedMessage("foo", 1).then(new ChainCallback<String, String>() {
      public MessageFuture<String> call(String arg) {
        return actor1.sendDelayedMessage(arg, 1);
      }
    }).thenDo(new Callback<String>() {
      public void call(String arg) {
        q.offer(arg);
      }
    });
    Thread.sleep(1000);
    assertEquals("foo from actor1 from actor1", q.poll(5, TimeUnit.SECONDS));
    assertEquals("bar from actor2 from actor2 from actor2", q.poll(5, TimeUnit.SECONDS));
  }

  @Test
  public void testBreakChainWithRuntimeException() throws InterruptedException {
    AsyncActor actor1 = system.spawn("actor1");
    actor1.sendDelayedMessage("foo", 1).then(new ChainCallback<String, String>() {
      public MessageFuture<String> call(String arg) {
        throw new RuntimeException("BOOM!");
      }
    }).thenDo(new Callback<String>() {
      public void call(String arg) {
        q.offer(arg);
      }
    });
    assertNull(q.poll(3, TimeUnit.SECONDS));
  }

  @Test
  public void testBreakChainWithMessageException() throws InterruptedException {
    AsyncActor actor1 = system.spawn("actor1");
    actor1.sendDelayedMessage("foo", 1).then(new ChainCallback<String, String>() {
      public MessageFuture<String> call(String arg) throws MessageException {
        throw new MessageException("BOOM!");
      }
    }).thenDo(new Callback<String>() {
      public void call(String arg) {
        q.offer(arg);
      }
    });
    assertNull(q.poll(3, TimeUnit.SECONDS));
  }
  @Test
  public void testCatchRuntimeException() throws InterruptedException {
    AsyncActor actor1 = system.spawn("actor1");
    actor1.sendDelayedMessage("foo", 1).then(new ChainCallback<String, String>() {
      public MessageFuture<String> call(String arg) {
        throw new RuntimeException(arg);
      }
    }).orCatch(new Callback<MessageException>() {
      public void call(MessageException ex) {
        q.offer(ex.getMessage());
      }
    });
    assertEquals("<java.lang.RuntimeException> foo from actor1", q.poll(5, TimeUnit.SECONDS));
  }

  @Test
  public void testCatchMessageException() throws InterruptedException {
    AsyncActor actor1 = system.spawn("actor1");
    actor1.sendDelayedMessage("foo", 1).then(new ChainCallback<String, String>() {
      public MessageFuture<String> call(String arg) throws MessageException {
        throw new MessageException(arg);
      }
    }).orCatch(new Callback<MessageException>() {
      public void call(MessageException ex) {
        q.offer("MessageException: " + ex.getOriginalException().getMessage());
      }
    });
    assertEquals("MessageException: foo from actor1", q.poll(5, TimeUnit.SECONDS));
  }

  @Test
  public void testCatchRuntimeExceptionAfterThenDo() throws InterruptedException {
    AsyncActor actor1 = system.spawn("actor1");
    actor1.sendDelayedMessage("foo", 1).then(new ChainCallback<String, String>() {
      public MessageFuture<String> call(String arg) {
        throw new RuntimeException(arg);
      }
    }).thenDo(new Callback<String>() {
      public void call(String arg) {
        q.offer(arg);
      }
    }).orCatch(new Callback<MessageException>() {
      public void call(MessageException ex) {
        q.offer(ex.getMessage());
      }
    });
    assertEquals("<java.lang.RuntimeException> foo from actor1", q.poll(5, TimeUnit.SECONDS));
  }

  @Test
  public void testCatchMessageExceptionAfterThenDo() throws InterruptedException {
    AsyncActor actor1 = system.spawn("actor1");
    actor1.sendDelayedMessage("foo", 1).then(new ChainCallback<String, String>() {
      public MessageFuture<String> call(String arg) throws MessageException {
        throw new MessageException(arg);
      }
    }).thenDo(new Callback<String>() {
      public void call(String arg) {
        q.offer(arg);
      }
    }).orCatch(new Callback<MessageException>() {
      public void call(MessageException ex) {
        q.offer("MessageException: " + ex.getOriginalException().getMessage());
      }
    });
    assertEquals("MessageException: foo from actor1", q.poll(5, TimeUnit.SECONDS));
  }

  @Test
  public void testReturnFromExceptionHandler() throws InterruptedException {
    final AsyncActor actor1 = system.spawn("actor1");
    actor1.sendDelayedMessage("foo", 1).then(new ChainCallback<String, String>() {
      public MessageFuture<String> call(String arg) throws MessageException {
        throw new MessageException(arg);
      }
    }).then(new ChainCallback<String, String>() {
      public MessageFuture<String> call(String arg) {
        return actor1.sendDelayedMessage(arg, 1);
      }
    }).orCatchThen(new ChainCallback<MessageException, String>() {
      public MessageFuture<String> call(MessageException ex) {
        return actor1.sendDelayedMessage(ex.getMessage() + " from exception", 1);
      }
    }).then(new ChainCallback<String, String>() {
      public MessageFuture<String> call(String arg) {
        return actor1.sendDelayedMessage(arg, 1);
      }
    }).thenDo(new Callback<String>() {
      public void call(String arg) {
        q.offer(arg);
      }
    });
    assertEquals("foo from actor1 from exception from actor1 from actor1", q.poll(5, TimeUnit.SECONDS));
  }
}

