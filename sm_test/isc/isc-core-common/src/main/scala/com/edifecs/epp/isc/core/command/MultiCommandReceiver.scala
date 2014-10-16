package com.edifecs.epp.isc.core.command

import java.io.Serializable

import com.edifecs.epp.isc.ICommandCommunicator
import com.edifecs.epp.isc.command.CommandMessage
import com.edifecs.epp.isc.exception.InvalidCommandException

import scala.collection.Seq
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

/**
 * A wrapper around a group of {@link CommandReceiver}s, which delegates
 * command messages to the first `CommandReceiver` that responds.
 *
 * @author c-adamnels
 */
class MultiCommandReceiver(
  commands: Seq[_ <: CommandRef]
) extends CommandReceiver {
  
  @throws(classOf[Exception])
  override def receiveCommand(
    communicator: ICommandCommunicator,
    command: CommandMessage
  )(
    implicit context: ExecutionContext
  ): Future[Serializable] =
    commands.find(_.respondsTo(command.name)).getOrElse {
      throw new InvalidCommandException(command,
        s"Receiver does not recognize the command '${command.name}'.")
    }.receiveCommand(communicator, command)

  override def getReceiverName = "MultiCommandReceiver"

  override def respondsTo(command: String): Boolean =
    commands.exists(_.respondsTo(command))

  override def getTimeoutFor(command: String): Option[Duration] =
    commands.find(_.respondsTo(command)).flatMap(_.getTimeoutFor(command))

  override def getSpecifications(rootUrl: Option[String] = None) =
    commands.flatMap(_.getSpecifications(rootUrl))
}

