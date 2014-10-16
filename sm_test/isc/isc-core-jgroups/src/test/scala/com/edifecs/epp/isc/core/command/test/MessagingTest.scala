package com.edifecs.epp.isc.core.command.test

import java.io.{InputStream, ByteArrayInputStream}
import java.util.concurrent.{Executors, SynchronousQueue, TimeUnit, TimeoutException}

import scala.collection.immutable._
import scala.collection.mutable.{ArrayBuffer, SynchronizedBuffer}
import scala.collection.JavaConversions._

import org.slf4j.LoggerFactory

import org.specs2.mutable._

import com.edifecs.epp.isc.{Address, CommandCommunicator, ICommandCommunicator, Args}
import com.edifecs.epp.isc.annotations._
import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder
import com.edifecs.epp.isc.exception._
import com.edifecs.epp.isc.core.command._
import com.edifecs.test.BeforeAfterSpecification

class MessagingTest extends BeforeAfterSpecification {

  private val logger = LoggerFactory.getLogger(getClass)

  final val server1Name = "server1"
  final val server2Name = "server2"
  final val node1Name = "node1"
  final val node2Name = "node2"

  final val clusterName = "testCluster"

  final val streamText = "This is a really really long message that's going to be sent as a" +
    " stream. The rain in Spain stays mainly on the plain."

  var communicator1: CommandCommunicator = null
  var communicator2: CommandCommunicator = null

  var node1: Address = null
  var node2: Address = null

  var receiver: Address = null
  val receivers = new ArrayBuffer[Address]()

  protected override def beforeAll(): Unit = {
    node1 = new Address(server1Name, node1Name)
    node2 = new Address(server2Name, node2Name)

    receiver = node2

    receivers.clear()
    receivers += receiver

    val builder1 = new CommandCommunicatorBuilder()
    builder1.setClusterName(clusterName)
    builder1.setAddress(node1)
    communicator1 = builder1.initialize
    communicator1.connect()
    communicator1.registerCommandHandler(node1, new TestMessageHandler())

    val builder2 = new CommandCommunicatorBuilder()
    builder2.setClusterName(clusterName)
    builder2.setAddress(node2)
    communicator2 = builder2.initialize
    communicator2.connect()
    communicator2.registerCommandHandler(node2, new TestMessageHandler())
  }

  protected override def afterAll(): Unit = {
    communicator1.disconnect()
    communicator2.disconnect()
  }

  "The messaging system" should {
    "send synchronous messages" in {
      val response = communicator1.sendSyncMessage(receivers, "testCommand")
      mapAsScalaMap(response.getResponseMap) must haveValue(true)
    }
    "send streaming messages" in {
      val stream = new ByteArrayInputStream(streamText.getBytes)
      val response = communicator1.sendSyncMessage(
        receiver, "streamCommand", "stream", stream,
        new Args().asInstanceOf[java.util.Map[String, Serializable]])
      response mustEqual streamText
    }
    "send multiple streaming messages concurrently" in {
      val threadSync = new Object()
      val responses = new ArrayBuffer[String] with SynchronizedBuffer[String]
      val executor = Executors.newCachedThreadPool
      (0 until 50) foreach { _ =>
        executor.execute(new Runnable() {
          override def run(): Unit = {
            try {
              val stream = new ByteArrayInputStream(streamText.getBytes)
              val response = communicator1.sendSyncMessage(
                receiver, "streamCommand", "stream", stream,
                new Args().asInstanceOf[java.util.Map[String, Serializable]])
              responses += response.asInstanceOf[String]
            } catch {
              case ex: Exception =>
                ex.printStackTrace()
                responses += "error"
            }
          }
        })
      }
      executor.shutdown()
      executor.awaitTermination(10, TimeUnit.MINUTES)
      responses.mkString("\n") mustEqual List.fill(50)(streamText).mkString("\n")
    }
  }
}

