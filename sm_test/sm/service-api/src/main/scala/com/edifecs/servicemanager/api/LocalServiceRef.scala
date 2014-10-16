package com.edifecs.servicemanager.api

import java.io.{File, Serializable}
import java.util.Properties

import scala.collection._
import scala.collection.JavaConversions._
import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.duration.Duration

import com.edifecs.core.configuration.helper.SystemVariables
import com.edifecs.epp.isc.{ICommandCommunicator, Isc, Address, CommandCommunicator}
import com.edifecs.epp.isc.core.{LogFile, ServiceStatus}
import com.edifecs.epp.isc.core.command.{CommandAnnotationProcessor, CommandRef}
import com.edifecs.servicemanager.annotations.{Service => ServiceAnnotation}
import com.edifecs.epp.isc.exception.InvalidCommandException
import com.edifecs.epp.isc.command.CommandMessage

class LocalServiceRef(
  serviceInstance: AbstractService,
  serviceAnnotation: ServiceAnnotation,
  id: String,
  properties: Properties,
  resources: mutable.Map[String, Properties],
  address: Address
) extends ServiceRef {
 
  private var serviceStatus: ServiceStatus = null
  private var message: String = ""
  private var commands: immutable.Map[String, CommandRef] = immutable.Map.empty

  override def receiveCommand(
    communicator: ICommandCommunicator,
    command: CommandMessage
  )(
    implicit context: ExecutionContext
  ): Future[Serializable] =
    if (commands.isEmpty) {
      Future(throw new IllegalStateException(
        s"The service '$id' does not have any command handlers, and cannot receive command" +
        " messages. Command handlers can be defined by annotating methods of the service" +
        " interface with @Handler."))
    } else {
      commands.get(command.name).map { c =>
        c.receiveCommand(communicator, command)
      }.getOrElse {
        Future(throw new InvalidCommandException(command,
          s"The service '$id' does not recognize the command '${command.name}'."))
      }
    }

  override def respondsTo(command: String): Boolean =
    commands contains command

  override def getTimeoutFor(command: String): Option[Duration] =
    commands.get(command).flatMap(_.getTimeoutFor(command))

  override def getId: String = id
  override def getReceiverName: String = serviceInstance.getClass.getSimpleName
  override def getAddress: Address = address
  override def getStatus: ServiceStatus = serviceStatus
  override def setStatus(status: ServiceStatus): Unit = this.serviceStatus = status
  override def getClassLoader: ClassLoader = serviceInstance.getClass.getClassLoader
  override def getProperties: Properties = properties
  override def getResources: mutable.Map[String, Properties] = resources
  override def getMessage: String = message
  override def setMessage(message: String): Unit = this.message = message
  override def getServiceAnnotation: ServiceAnnotation = serviceAnnotation

  override def getLogFiles: java.util.List[LogFile] = {
    // Check Service Log File Location
    var logfiles = mutable.ListBuffer[LogFile]()
    val file2 = new File(SystemVariables.LOG_PATH +
      System.getProperty(SystemVariables.NODE_NAME_KEY) + File.separator + id + ".log")
    val files = immutable.List[File](file2)
    files.filter(f => f.exists && f.isFile).foreach { f =>
      logfiles += new LogFile(f.getName, getAddress, f.getAbsolutePath, f.lastModified, f.length)
    }
    logfiles.toList
  }

  override def registerCommand(command: CommandRef): Boolean = this.synchronized {
    if (!(commands contains command.name)) {
      commands += (command.name -> command)
      true
    } else {
      false
    }
  }

  override def unregisterCommand(commandName: String): Option[CommandRef] = this.synchronized {
    commands.get(commandName) map { command =>
      commands -= commandName
      command
    }
  }

  @inline private def updateStatus(status: ServiceStatus)(implicit cc: CommandCommunicator) = {
    ServiceRegistry.updateServiceStatus(getId, status)
    cc.getAddressRegistry.updateLocalServiceInformation(
      getAddress, getId, ServiceRegistry.getServiceInformation(getId))
  }

  override def start() = {
    implicit val cc = CommandCommunicator.getInstance
    try {
      updateStatus(ServiceStatus.Starting)
      serviceInstance.packageScopeStart()
      updateStatus(ServiceStatus.Started)
      cc.requestServiceRegistryUpdate()
    } catch {
      case e: Exception =>
        try {
          ServiceRegistry.updateServiceStatus(getId, ServiceStatus.Error, e.getMessage)
          cc.getAddressRegistry.updateLocalServiceInformation(
            getAddress, getId, ServiceRegistry.getServiceInformation(getId))
        } finally ServiceRegistry.unregisterLocalService(id)
        throw e
    }
  }

  override def stop() = {
    implicit val cc = CommandCommunicator.getInstance
    try {
      updateStatus(ServiceStatus.Stopping)
      serviceInstance.packageScopeStop()
      updateStatus(ServiceStatus.Stopped)
    } finally {
      ServiceRegistry.unregisterLocalService(id)
      CommandCommunicator.getInstance.requestServiceRegistryUpdate()
    }
  }

  override protected def getTestPropertiesAndResources =
    serviceInstance.getTestPropertiesAndResources

  override def getSpecifications(rootUrl: Option[String] = None) =
    commands.filter(
      !_._1.startsWith(Isc.builtinCommandNamespace + Isc.commandSeparator)
    ).values.flatMap(_.getSpecifications(rootUrl)).toSeq

  CommandAnnotationProcessor.processAnnotatedCommandHandler(
    new BuiltinCommandHandler(this), None, getServiceAnnotation.name
  ) foreach registerCommand
}

