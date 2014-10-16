package com.edifecs.epp.isc.core.command

import java.io.Serializable

import com.edifecs.epp.isc.{Isc, MessageResponse, CommandCommunicator, Address}
import com.edifecs.epp.isc.command.{RemoteCommandMessage, CommandMessage}
import com.edifecs.epp.isc.exception.{InvalidCommandException, MessageException}
import com.edifecs.epp.isc.stream.MessageStream

import scala.collection.immutable._

import akka.actor.{PossiblyHarmful, ActorLogging, Actor}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

class CommandReceiverActor(
  communicator: CommandCommunicator,
  address: Address
) extends Actor with ActorLogging {

  import CommandReceiverActor._
  import communicator.getExecutionContext

  private var receivers: Seq[CommandReceiver] = Nil
  private implicit val timeout: akka.util.Timeout = 10 seconds

  override def receive: Receive = {
    case RegisterCommandReceiver(a, receiver) if a == address =>
      log.debug("Registering new command receiver {} at {}.", receiver.getReceiverName, address)
      receivers :+= receiver
    case UnregisterCommandReceiver(a, receiver) if a == address =>
      log.debug("Unregistering command receiver {} at {}.", receiver.getReceiverName, address)
      receivers = receivers.filter(_ != receiver)
    case UnregisterAllCommandReceivers(a) if a == address =>
      log.debug("Unregistering all command receivers at {}.", address)
      receivers = Nil
    case RemoteCommandMessage(_, msg, remoteSender) =>
      log.debug("Received remote command {}, with args {}.", msg.name, msg.args)
      val s = Option(remoteSender) getOrElse sender()
      handleCommand(msg, s.path.address, isRemote = true).onComplete {
        case Success(response) => s ! response
        case f: Failure[_] => s ! wrapResponse(f)
      }
    case msg: CommandMessage =>
      log.debug("Received local command {}, with args {}.", msg.name, msg.args)
      val s = sender()
      handleCommand(msg, s.path.address, isRemote = false).onComplete {
        case Success(response) => s ! response
        case f: Failure[_] => s ! wrapResponse(f)
      }
  }

  def handleCommand(
    command: CommandMessage,
    sender: akka.actor.Address,
    isRemote: Boolean
  ): Future[MessageResponse] = {
    try receivers find (_.respondsTo(command.name)) map {
      // The extra Future guarantees that even sync commands are run in another
      // thread, so that the message-receiving thread isn't blocked.
      Future(_).flatMap{r =>
        Isc.setState(Isc.ThreadState(communicator, address, command.toStack(address)))
        if (isRemote && command.args.exists(_._2.isInstanceOf[MessageStream])) {
          command.args.toList.collect {
            case (s: String, ms: MessageStream) => (s, ms)
          }.foldLeft(Future(command)) { (f, t) =>
            f.flatMap { command =>
              communicator.getIncomingStream(t._2, sender).map { stream =>
                command.copy(args = command.args ++ Map(t._1 -> stream))
              }
            }
          }.flatMap(r.receiveCommand(communicator, _))
        } else r.receiveCommand(communicator, command)
      } map {
        case stream: MessageStream =>
          if (isRemote) communicator.registerOutgoingStream(stream)
          Success(stream)
        case returnValue: Serializable => Success(returnValue)
        case null => Success(null)
      } recover {
        case ex: Exception => Failure(ex)
      }
    } getOrElse {
      throw new InvalidCommandException(command, s"Command '${command.name}' could not be sent:" +
        s" no command receivers at $address responded.")
    } map wrapResponse catch {
      case ex: Exception => Future(wrapResponse(Failure(ex)))
    } finally Isc.clearState()
  }

  private def wrapResponse(response: Try[Serializable]) = {
    val mr = new MessageResponse
    response match {
      case Success(value) =>
        mr.addResponse(address, value)
      case Failure(mx: MessageException) =>
        mr.addException(address, mx)
      case Failure(ex: Throwable) =>
        mr.addException(address, new MessageException(ex, Isc.stack))
    }
    mr
  }
}

object CommandReceiverActor {

  case class RegisterCommandReceiver(
    address: com.edifecs.epp.isc.Address,
    receiver: CommandReceiver
  ) extends PossiblyHarmful

  case class UnregisterCommandReceiver(
    address: com.edifecs.epp.isc.Address,
    receiver: CommandReceiver
  ) extends PossiblyHarmful

  case class UnregisterAllCommandReceivers(
    address: com.edifecs.epp.isc.Address
  ) extends PossiblyHarmful
}
