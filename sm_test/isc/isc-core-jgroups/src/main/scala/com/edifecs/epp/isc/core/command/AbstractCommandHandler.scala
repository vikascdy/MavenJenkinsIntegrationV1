package com.edifecs.epp.isc.core.command

import org.slf4j.{Logger, LoggerFactory}

import com.edifecs.epp.isc.{CommandCommunicator, Isc}
import com.edifecs.epp.security.ISecurityManager
import com.edifecs.servicemanager.metric.api.IMetric
import com.edifecs.epp.security.remote.SecurityManager

abstract class AbstractCommandHandler {

  protected val logger = LoggerFactory.getLogger(getClass)

  private var commandCommunicator: Option[CommandCommunicator] = None
  private var iscInstance: Option[Isc] = None

  def initialize(communicator: CommandCommunicator, isc: Isc): Unit = {
    this.commandCommunicator = Some(communicator)
    this.iscInstance = Some(isc)
  }

  protected def getLogger: Logger = logger

  protected def getCommandCommunicator: CommandCommunicator =
    this.commandCommunicator getOrElse {
      throw new IllegalStateException(
        s"The message handler class '${getClass.getCanonicalName}' is not initialized.")
    }

  protected def isc: Isc =
    this.iscInstance getOrElse {
      throw new IllegalStateException(
        s"The message handler class '${getClass.getCanonicalName}' is not initialized.")
    }

  protected def getSecurityManager: SecurityManager =
    getCommandCommunicator.getSecurityManager

  protected def getMetricApi: IMetric =
    getCommandCommunicator.getMetric

  // Override in subclasses (such as various ESM command handlers) that must
  // use a different ISecurityManager when receiving commands. Unlike similar
  // getter methods (which are called once, then cached), this is called every
  // time a command is received.
  //
  // This is really only necessary for the ESM Service. Because the default
  // security manager sends commands to the ESM Service, infinite recursion
  // would occur if the ESM command handlers couldn't override it with a
  // different security manager.
  protected[command] def getReceivingSecurityManager: Option[ISecurityManager] =
    None
}

