package com.edifecs.epp.isc.command

import java.io.Serializable

import akka.actor.{PossiblyHarmful, ActorRef}
import com.edifecs.epp.isc.{AbstractMessage, Address}
import com.edifecs.epp.security.SessionId

import scala.collection.immutable.Map

case class CommandMessage (
  name: String,
  args: Map[String, _ <: Serializable] = Map.empty,
  source: CommandSource = CommandSource.AKKA,
  sender: Address = null,
  session: SessionId = null,
  timestamp: Long = System.currentTimeMillis,
  stack: Array[CommandStackFrame] = Array.empty
) extends AbstractMessage with PossiblyHarmful {
  def toStack(destination: Address): Array[CommandStackFrame] = {
    val array = Array.ofDim[CommandStackFrame](stack.length+1)
    array(0) = CommandStackFrame(sender, destination, name, timestamp)
    System.arraycopy(stack, 0, array, 1, stack.length)
    array
  }
}

case class RemoteCommandMessage(
  destination: Address,
  command: CommandMessage,
  sender: ActorRef = null
)

case class CommandStackFrame(
  sender: Address,
  destination: Address,
  command: String,
  timestamp: Long
) {
  override def toString =
    command + " (" + (Option(sender) getOrElse "?") + " -> " + (Option(destination) getOrElse "?") + ")"
}
