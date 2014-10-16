package com.edifecs.servicemanager.api

import java.util.{Collections, ArrayList}

import com.edifecs.epp.isc.command.CommandSpecification

import scala.collection.JavaConversions._

import com.edifecs.epp.isc.core.command.AbstractCommandHandler
import com.edifecs.epp.isc.annotations.{NullSessionAllowed, Rest, SyncCommand, CommandHandler}
import com.edifecs.epp.isc.Isc

@CommandHandler(namespace=Isc.builtinCommandNamespace)
@Rest(enabled = false)
@NullSessionAllowed
trait IBuiltinCommandHandler {
  @SyncCommand(name=Isc.commandListCommandWithoutNamespace)
  def listCommands: java.util.List[CommandSpecification]
}

class BuiltinCommandHandler(
  serviceRef: ServiceRef
) extends AbstractCommandHandler with IBuiltinCommandHandler {
  override def listCommands = Collections.unmodifiableList(
    new ArrayList(serviceRef.getSpecifications(None)))
}
