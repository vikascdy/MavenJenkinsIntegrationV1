package com.edifecs.epp.isc.core.command.test

import java.io.InputStream
import java.util.concurrent.{SynchronousQueue, TimeUnit, TimeoutException}

import com.edifecs.epp.isc.annotations._
import com.edifecs.epp.isc.core.command._

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

  @SyncCommand(name = "testCommand")
  def testCommand: Boolean = {
    received = true
    logger.debug("testCommand successfully sent!");
    true
  }

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
      return result
    } finally {
      stream.close()
    }
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
