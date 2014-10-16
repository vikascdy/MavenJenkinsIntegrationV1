package com.edifecs.epp.isc.core.command.test

import java.io.InputStream
import java.util.concurrent.{SynchronousQueue, TimeUnit, TimeoutException}

import com.edifecs.epp.isc.annotations._
import com.edifecs.epp.isc.async.MessageFuture
import com.edifecs.epp.isc.core.command._
import com.edifecs.epp.isc.exception.MessageException
import com.edifecs.epp.isc.stream.{MessageStreamBuilder, MessageStream}

@CommandHandler
@NullSessionAllowed
class TestMessageHandler extends AbstractCommandHandler {

  private var received = false
  private var counter = 0
  private val queue = new SynchronousQueue[String]()

  def isReceived: Boolean = received

  def getCounter: Int = counter

  def getQueue: SynchronousQueue[String] = queue

  // TODO: Make some of these commands async.

  @AsyncCommand(name = "testCommand")
  def testCommand = MessageFuture {
    received = true
    logger.debug("testCommand successfully sent!")
    true
  }

  @AsyncCommand(name = "asyncExceptionCommand")
  def asyncExceptionCommand(): MessageFuture[Unit] = MessageFuture {
    throw new MessageException("ASYNC EXCEPTION")
  }

  @SyncCommand(name = "syncExceptionCommand")
  def syncExceptionCommand(): Unit =
    throw new MessageException("SYNC EXCEPTION")

  @SyncCommand(name = "testIncrementCommand")
  def testIncrementCommand: Int = {
    counter += 1
    logger.debug("testIncrementCommand successfully sent!")
    counter
  }

  @SyncCommand(name = "streamCommand")
  def streamCommand(
    @StreamArg(name = "stream") stream: InputStream
  ): String = {
    logger.debug("Got a stream.")
    try {
      val result = io.Source.fromInputStream(stream).mkString("")
      logger.debug("Stream contents: " + result)
      result
    } finally {
      stream.close()
    }
  }

  @AsyncCommand(name = "concatStreams")
  def concatStreams(
    @Arg(name = "stream1", required = true) stream1: MessageStream,
    @Arg(name = "stream2", required = true) stream2: MessageStream,
    @Arg(name = "stream3", required = true) stream3: MessageStream
  ): MessageFuture[MessageStream] = MessageFuture {
    val builder = new MessageStreamBuilder
    stream1 forEachChunk(builder.chunk(_)) andThenDo {
      System.out.println("************* Stream 1")
      stream2 forEachChunk (builder.chunk(_))
    } andThenDo {
      System.out.println("************* Stream 2")
      stream3 forEachChunk (builder.chunk(_))
    } andThenDo {
      System.out.println("************* Stream 3")
      builder.end()
    }
    builder.toStream
  }

  @AsyncCommand(name = "htmlCommand")
  def htmlCommand(
    @Arg(name = "content", required = true) content: String
  ): MessageFuture[MessageStream] = MessageFuture {
    MessageStream.fromXml(
      <html>
        <head><title>HTML Sample</title></head>
        <body>
          <h1>This is an HTML page.</h1>
          <p>{content}</p>
        </body>
      </html>,
    httpContentType = "text/html", xmlDecl = false)
  }

  @SyncCommand(name = "waitForMessage")
  def waitForMessage: String = {
    logger.debug("Waiting for deliverMessage command...")
    Option(queue.poll(10, TimeUnit.SECONDS)) getOrElse {
      logger.warn("Timed out while waiting for deliverMessage command.")
      throw new TimeoutException("Timed out waiting for message.")
    }
  }

  @SyncCommand(name = "deliverMessage")
  def deliverMessage(
    @Arg(name = "message") message: String
  ): Boolean = {
    logger.debug(s"Delivering message '$message'.");
    queue.offer(message)
  }
}
