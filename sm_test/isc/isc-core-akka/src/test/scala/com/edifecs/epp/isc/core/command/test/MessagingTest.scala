package com.edifecs.epp.isc.core.command.test

import java.io.ByteArrayInputStream
import java.util.UUID

import com.edifecs.epp.isc.async.MessageFuture
import com.edifecs.epp.isc.exception.MessageException
import com.edifecs.epp.isc.stream.MessageStream

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Await
import scala.concurrent.duration._

import com.edifecs.epp.isc.{Address, CommandCommunicator, Args}
import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder
import com.edifecs.test.BeforeAfterSpecification

import org.slf4j.LoggerFactory

class MessagingTest extends BeforeAfterSpecification {

  val uuid: String = UUID.randomUUID.toString

  final val server1Name = "mt-server1-" + uuid
  final val server2Name = "mt-server2-" + uuid
  final val node1Name = "mt-node1-" + uuid
  final val node2Name = "mt-node2-" + uuid

  final val clusterName = "mt-testCluster-" + uuid

  final val streamText = "This is a really really long message that's going to be sent as a" +
    " stream. The rain in Spain stays mainly on the plain."

  var communicator1: CommandCommunicator = null
  var communicator2: CommandCommunicator = null

  var node1: Address = null
  var node2: Address = null

  val startupTimeout = DurationInt(1).minutes
  val timeout = DurationInt(2).minutes

  sequential

  protected override def beforeAll(): Unit = {
    node1 = new Address(server1Name, node1Name)
    node2 = new Address(server2Name, node2Name)

    val builder1 = new CommandCommunicatorBuilder()
      .setClusterName(clusterName).setAddress(node1)
    communicator1 = builder1.initialize
    communicator1.connect()
    communicator1.registerCommandHandler(node1, new TestMessageHandler())

    val builder2 = new CommandCommunicatorBuilder()
      .setClusterName(clusterName).setAddress(node2)
    communicator2 = builder2.initialize
    communicator2.connect()
    communicator2.registerCommandHandler(node2, new TestMessageHandler())

    println("Waiting for node1 to detect node2...")
    communicator1.waitUntilNodeJoins(node2, startupTimeout)
    println("Waiting for node2 to detect node1...")
    communicator2.waitUntilNodeJoins(node1, startupTimeout)
  }

  protected override def afterAll(): Unit = {
    communicator1.disconnect()
    communicator2.disconnect()
  }

  "The messaging system" should {
    "send asynchronous messages to the same node" in {
      val future = communicator1.send(node1, "testCommand")
      Await.result(future.asScalaFuture, timeout) mustEqual true
    }
    "send asynchronous messages to other nodes" in {
      val future = communicator1.send(node2, "testCommand")
      Await.result(future.asScalaFuture, timeout) mustEqual true
    }
    "receive asynchronous exceptions from the same node" in {
      val future = communicator1.send(node1, "asyncExceptionCommand")
      (try Await.result(future.asScalaFuture, timeout)
       catch {case mx: MessageException => mx}) must beLike {
        case mx: MessageException => mx.getOriginalMessage mustEqual "ASYNC EXCEPTION"
      }
    }
    "receive asynchronous exceptions from other nodes" in {
      val future = communicator1.send(node2, "asyncExceptionCommand")
      (try Await.result(future.asScalaFuture, timeout)
       catch {case mx: MessageException => mx}) must beLike {
        case mx: MessageException => mx.getOriginalMessage mustEqual "ASYNC EXCEPTION"
      }
    }
    "send synchronous messages to the same node" in {
      val response = communicator1.sendSync(node1, "testCommand")
      response mustEqual true
    }
    "send synchronous messages to other nodes" in {
      val response = communicator1.sendSync(node2, "testCommand")
      response mustEqual true
    }
    "receive synchronous exceptions from the same node" in {
      (try communicator1.sendSync(node1, "syncExceptionCommand")
       catch {case mx: MessageException => mx}) must beLike {
        case mx: MessageException => mx.getOriginalMessage mustEqual "SYNC EXCEPTION"
      }
    }
    "receive synchronous exceptions from other nodes" in {
      (try communicator1.sendSync(node2, "syncExceptionCommand")
       catch {case mx: MessageException => mx}) must beLike {
        case mx: MessageException => mx.getOriginalMessage mustEqual "SYNC EXCEPTION"
      }
    }
    "send streaming messages to the same node, using the old API" in {
      val stream = new ByteArrayInputStream(streamText.getBytes)
      val response = communicator1.sendSyncMessage(
        node1, "streamCommand", "stream", stream,
        new Args().asInstanceOf[java.util.Map[String, Serializable]])
      response mustEqual streamText
    }
    "send streaming messages to other nodes, using the old API" in {
      val stream = new ByteArrayInputStream(streamText.getBytes)
      val response = communicator1.sendSyncMessage(
        node2, "streamCommand", "stream", stream,
        new Args().asInstanceOf[java.util.Map[String, Serializable]])
      response mustEqual streamText
    }
    "send streaming messages to the same node, using the new API" in {
      val stream = MessageStream.fromInputStream(new ByteArrayInputStream(streamText.getBytes))
      val future = communicator1.send(node1, "streamCommand", Map("stream" -> stream))
      Await.result(future.asScalaFuture, timeout) mustEqual streamText
    }
    "send streaming messages to other nodes, using the new API" in {
      val stream = MessageStream.fromInputStream(new ByteArrayInputStream(streamText.getBytes))
      val future = communicator1.send(node2, "streamCommand", Map("stream" -> stream))
      Await.result(future.asScalaFuture, timeout) mustEqual streamText
    }
    "receive streaming messages from the same node" in {
      val future = communicator1.send(node1, "htmlCommand", Map("content" -> "foo"))
        .as(classOf[MessageStream])
      val result = Await.result(future.then(stream =>
        stream.fold("")((s, chunk) => MessageFuture(s + chunk.decodeString("utf-8")))
      ).asScalaFuture, timeout)
      result must startWith("<html>") and contain("<p>foo</p>")
    }
    "receive streaming messages from other nodes" in {
      val future = communicator1.send(node2, "htmlCommand", Map("content" -> "foo"))
        .as(classOf[MessageStream])
      val result = Await.result(future.then(stream =>
        stream.fold("")((s, chunk) => MessageFuture(s + chunk.decodeString("utf-8")))
      ).asScalaFuture, timeout)
      result must startWith("<html>") and contain("<p>foo</p>")
    }
    //FIXME: This test is unstable and needs to be fixed
//    "send streaming messages with multiple streams" in {
//      val text1 = streamText + " (1)\n"
//      val text2 = streamText + " (2)\n"
//      val text3 = streamText + " (3)\n"
//      val fullText = text1 + text2 + text3
//      val future = communicator1.send(node2, "concatStreams", Map(
//        "stream1" -> MessageStream.fromInputStream(new ByteArrayInputStream(text1.getBytes)),
//        "stream2" -> MessageStream.fromInputStream(new ByteArrayInputStream(text2.getBytes)),
//        "stream3" -> MessageStream.fromInputStream(new ByteArrayInputStream(text3.getBytes))
//      )).as(classOf[MessageStream])
//      Await.result(future.then(stream =>
//        stream.fold("")((s, chunk) => MessageFuture({
//          val temp = s + chunk.decodeString("utf-8")
//          println("***********" + temp)
//          temp
//        }))
//      ).asScalaFuture, DurationInt(5).minutes) mustEqual fullText
//    }
    "send multiple streaming messages concurrently" in {
      val threadSync = new Object()
      val responses = new ArrayBuffer[String] with mutable.SynchronizedBuffer[String]
      (0 until 50).par foreach { _ =>
        try {
          val stream = MessageStream.fromInputStream(new ByteArrayInputStream(streamText.getBytes))
          val response = communicator1.sendSync(node2, "streamCommand", Map("stream" -> stream))
          responses += response.asInstanceOf[String]
        } catch {
          case ex: Exception =>
            ex.printStackTrace()
            responses += "error"
        }
      }
      responses.mkString("\n") mustEqual List.fill(50)(streamText).mkString("\n")
    }
  }
}
