package com.edifecs.epp.isc.core.command

import akka.actor._
import com.edifecs.core.configuration.helper.SystemVariables
import com.edifecs.epp.isc.command.RemoteCommandMessage
import com.edifecs.epp.isc.exception.{MessageException, InvalidCommandException}
import com.edifecs.epp.isc.{MessageResponse, CommandCommunicator}

class CommandRootActor(
  communicator: CommandCommunicator
) extends CommandReceiverActor(communicator, communicator.getAddress) {
  import com.edifecs.epp.isc.core.command.CommandReceiverActor._
  import com.edifecs.epp.isc.core.command.CommandRootActor._

  val address = communicator.getAddress

  override def receive = {
    case msg: RegisterCommandReceiver
      if address.isLocalAddress(msg.address) && msg.address.isService =>
      context.child(msg.address.getService).getOrElse {
        log.debug("Creating new actor at /user/{}/{}", name, msg.address.getService)
        context.actorOf(
          Props(classOf[CommandReceiverActor], communicator, msg.address),
          msg.address.getService)
      } ! msg
    case msg: UnregisterCommandReceiver
      if address.isLocalAddress(msg.address) && msg.address.isService =>
      context.child(msg.address.getService).map(_ ! msg)
    case msg: UnregisterAllCommandReceivers
      if address.isLocalAddress(msg.address) && msg.address.isService =>
      context.child(msg.address.getService).map(_ ! msg)
    case msg: RemoteCommandMessage =>
      if (msg.destination.isService) {
        val child = context.child(msg.destination.getService)
        if (child.isDefined) child.get ! msg.copy(sender = sender())
        else {
          val response = new MessageResponse
          response.addException(msg.destination, new MessageException(
            "No such service: " + msg.destination, msg.command.toStack(address)))
          sender() ! response
        }
      } else super.receive(msg)
    case msg: Any =>
      super.receive(msg)
  }
}

object CommandRootActor {
  final val name = SystemVariables.AKKA_COMMAND_ROOT

  def createInstance(communicator: CommandCommunicator)(implicit system: ActorSystem): ActorRef =
    system.actorOf(Props(classOf[CommandRootActor], communicator), name)
}
