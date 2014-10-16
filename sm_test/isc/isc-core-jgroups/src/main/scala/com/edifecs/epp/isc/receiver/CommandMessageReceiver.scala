package com.edifecs.epp.isc.receiver

import java.io.{InputStream, Serializable}

import com.edifecs.epp.isc.command.CommandMessage
import com.edifecs.epp.isc.core.MessageInputStream
import com.edifecs.epp.isc.exception.InvalidCommandException
import com.edifecs.epp.isc.{Address, CommandCommunicator}

import scala.collection.JavaConversions._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * Abstract class that must be implemented if an application wants to be able to
 * receive messages from the JGroups cluster.
 * 
 * @author willclem
 * @author c-adamnels
 */
class CommandMessageReceiver(commandCommunicator: CommandCommunicator) {

  val defaultTimeout = 10 seconds

  @throws(classOf[Exception])
  protected def handleIncomingCommandMessage(
    message: CommandMessage,
    address: Address,
    stream: InputStream
  ): Serializable = {

    // Set user's session to the current thread
    if (message.session != null) {
        commandCommunicator.getSecurityManager.getSessionManager.registerCurrentSession(
          message.session)
    }

    commandCommunicator.getAllCommandReceiversAt(address).find(
      _.respondsTo(message.name)
    ).map { rcvr =>
      Await.result(
        rcvr.receiveCommand(
          commandCommunicator,
          message
        ),
        rcvr.getTimeoutFor(message.name) getOrElse defaultTimeout
      ) match {
        case stream: InputStream =>
          // Send the stream
          val streamId = commandCommunicator.getClusterConnection.sendStream(
            message.sender, stream, -1)
          return new MessageInputStream(streamId)
        case returnValue: Any =>
          returnValue.asInstanceOf[Serializable]
        case null =>
          null
      }
    } getOrElse {
      throw new InvalidCommandException(message,
        s"Command '${message.name}' could not be sent: no command receivers at address" +
        s" '$address' responded to the command.")
    }
  }

  @throws(classOf[Exception])
  def handleIncomingAsyncCommandMessage(message: CommandMessage, receiver: Address): Unit =
    handleIncomingCommandMessage(message, receiver, null)

  @throws(classOf[Exception])
  def handleIncomingAsyncCommandMessage(
    message: CommandMessage,
    receiver: Address,
    stream: InputStream
  ): Unit =
    handleIncomingCommandMessage(message, receiver, stream)

  @throws(classOf[Exception])
  def handleIncomingSyncCommandMessage(message: CommandMessage, receiver: Address): Serializable =
    handleIncomingCommandMessage(message, receiver, null)

  @throws(classOf[Exception])
  def handleIncomingSyncCommandMessage(
    message: CommandMessage,
    receiver: Address,
    stream: InputStream
  ): Serializable =
    handleIncomingCommandMessage(message, receiver, stream)
}

