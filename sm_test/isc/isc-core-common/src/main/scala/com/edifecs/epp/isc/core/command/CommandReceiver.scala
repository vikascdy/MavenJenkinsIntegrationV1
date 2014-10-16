package com.edifecs.epp.isc.core.command

import java.io.Serializable

import com.edifecs.epp.isc.ICommandCommunicator
import com.edifecs.epp.isc.command.{CommandMessage, CommandSpecification}

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

/**
 * Anything that can receive commands; usually a service. This trait is
 * intended for **internal** use; to implement your own command handler, see
 * {@link AbstractCommandHandler}.
 *
 * @author c-adamnels
 */
trait CommandReceiver {

  def receiveCommand(
    communicator: ICommandCommunicator,
    message: CommandMessage
  )(
    implicit context: ExecutionContext
  ): Future[Serializable]

  def getReceiverName: String

  def respondsTo(command: String): Boolean

  def getTimeoutFor(command: String): Option[Duration]

  def getSpecifications(rootUrl: Option[String] = None): Seq[CommandSpecification]
}
