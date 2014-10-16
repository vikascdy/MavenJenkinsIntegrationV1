package com.edifecs.servicemanager.api

import java.io.IOException

import scala.collection.immutable._
import scala.collection.JavaConversions._

import com.edifecs.epp.isc.{Args, CommandCommunicator}
import com.edifecs.epp.isc.core.{LogFile, ServiceInformation, ServiceStatus}
import com.edifecs.epp.isc.exception.{CommandHandlerRegistrationException, ServiceException}

/**
 * A service registry that holds references to local services only.
 * 
 * @author willclem
 * @author c-adamnels
 */
object ServiceRegistry extends IServiceRegistry {

  /** Stores references to the local services. */
  private var localServicesMap: Map[String, ServiceRef] = Map.empty

  @throws(classOf[CommandHandlerRegistrationException])
  override def registerLocalService(
    service: ServiceRef
  ): Unit = this.synchronized {
    if (localServicesMap contains service.getId) {
      throw new IllegalStateException(
        s"A local service with the ID '${service.getId}' is already registered.")
    }
    localServicesMap += (service.getId -> service)
    CommandCommunicator.getInstance.registerCommandReceiver(service.getAddress, service)
    service.setStatus(ServiceStatus.Created)
  }

  override def unregisterLocalService(serviceName: String): Boolean = this.synchronized {
    localServicesMap.get(serviceName).flatMap { service =>
      service.setStatus(null)
      localServicesMap -= serviceName
      try {
        Option(CommandCommunicator.getInstance.unregisterAllCommandReceiversAt(
          service.getAddress))
      } catch {
        case ex: CommandHandlerRegistrationException => None
      }
    }.isDefined
  }

  override def isServiceLocal(serviceName: String): Boolean =
    localServicesMap contains serviceName

  override def getLocalServiceNames: java.util.List[String] =
    localServicesMap.keys.toList

  override def getLocalService(serviceName: String): ServiceRef =
    localServicesMap.get(serviceName).getOrElse(null)

  @throws(classOf[Exception])
  override def updateServiceInformation(
    serviceName: String,
    serviceInformation: ServiceInformation
  ): Unit = {
    localServicesMap.get(serviceName).map { service =>
      // Set the sevice status
      service.setStatus(serviceInformation.getServiceStatus)
      service.setMessage(serviceInformation.getMessage)
    }
    // Send broadcast status update message
    // FIXME: Should this only be sent when the service is present?
    CommandCommunicator.getInstance.sendBroadcastMessage(
      "updateServiceStatus",
      new Args(
        "address", CommandCommunicator.getInstance.getAddress,
        "serviceName", serviceName,
        "serviceInformation", serviceInformation
      ).asInstanceOf[java.util.Map[String, Serializable]])
  }

  override def getLocalServiceStatus(serviceName: String): ServiceStatus =
    localServicesMap.get(serviceName).map(_.getStatus).getOrElse(ServiceStatus.Error)

  @throws(classOf[ServiceException])
  override def getLocalServiceLogFiles(serviceName: String): java.util.List[LogFile] =
    localServicesMap.get(serviceName).map(_.getLogFiles).get

  @throws(classOf[IOException])
  @throws(classOf[ServiceException])
  override def getLocalServiceLogFile(serviceName: String, logFileName: String): String =
    (for(
      service <- List(localServicesMap.get(serviceName)).flatten;
      logFile <- service.getLogFiles if logFile.getName == logFileName
    ) yield io.Source.fromFile(logFile.getPath).mkString).headOption.get

  @throws(classOf[Exception])
  override def updateServiceStatus(serviceName: String, status: ServiceStatus): Unit = {
    val serviceInformation = getServiceInformation(serviceName)
    serviceInformation.setServiceStatus(status)
    updateServiceInformation(serviceName, serviceInformation)
  }

  @throws(classOf[Exception])
  override def updateServiceStatus(
    serviceName: String,
    status: ServiceStatus,
    message: String
  ): Unit =
    try {
      val serviceInformation = getServiceInformation(serviceName)
      serviceInformation.setServiceStatus(status)
      serviceInformation.setMessage(message)
      updateServiceInformation(serviceName, serviceInformation)
    } catch {
      case ex: ServiceException =>
        val serviceInformation = new ServiceInformation()
        serviceInformation.setServiceName(serviceName)
        serviceInformation.setServiceStatus(status)
        serviceInformation.setMessage(message)
        updateServiceInformation(serviceName, serviceInformation)
    }

  @throws(classOf[ServiceException])
  override def getServiceInformation(serviceName: String): ServiceInformation = {
    val service = Option(getLocalService(serviceName)) getOrElse {
      throw new ServiceException("Service not found: " + serviceName)
    }
    val serviceInformation = new ServiceInformation()

    serviceInformation.setServiceStatus(service.getStatus)
    serviceInformation.setMessage(service.getMessage)
    serviceInformation.setProperties(service.getProperties)
    serviceInformation.setServiceName(service.getId)

    if (service.getServiceAnnotation != null) {
      serviceInformation.setDescription(service.getServiceAnnotation.description)
      serviceInformation.setServiceType(service.getServiceAnnotation.name)
      serviceInformation.setVersion(service.getServiceAnnotation.version)
    }

    serviceInformation
  }

  def getLocalServiceAddresses: java.util.Set[String] =
    localServicesMap.keys.toSet[String]
}

