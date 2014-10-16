package com.edifecs.servicemanager.api

import java.util.Properties

import scala.collection._

import com.edifecs.epp.isc.{Address, CommandCommunicator}
import com.edifecs.epp.isc.core.{LogFile, ServiceStatus}
import com.edifecs.epp.isc.core.command.{CommandAnnotationProcessor, CommandReceiver, CommandRef}
import com.edifecs.servicemanager.annotations.{Service => ServiceAnnotation}
import java.net.URLEncoder

trait ServiceRef extends CommandReceiver with IService {
  def getId: String
  def getAddress: Address
  def getStatus: ServiceStatus
  def setStatus(status: ServiceStatus): Unit
  def getClassLoader: ClassLoader
  def getProperties: Properties
  def getResources: mutable.Map[String, Properties]
  def getServiceAnnotation: ServiceAnnotation
  def getLogFiles: java.util.List[LogFile]
  def getMessage: String
  def setMessage(message: String): Unit
  def registerCommand(command: CommandRef): Boolean
  def unregisterCommand(commandName: String): Option[CommandRef]

  protected def getTestPropertiesAndResources: (Properties, Map[String, Properties])

  @throws(classOf[Exception])
  override def startTestMode(): Unit = {
    //val cc = CommandCommunicator.getInstance
    val (p, r) = getTestPropertiesAndResources
    //val serverAddress = new Address(
    //  cc.getAddress.getServerName,
    //  cc.getAddress.getNodeName)
    getProperties.putAll(p)
    getResources ++= r
    start()
  }

  def getServiceSpecification(rootUrl: Option[String] = None): ServiceSpecification =
    ServiceSpecification(
      address = getAddress,
      serviceType = getServiceAnnotation.name(),
      version = getServiceAnnotation.version(),
      description = getServiceAnnotation.description(),
      commands = getSpecifications(rootUrl.map(
        _ + URLEncoder.encode(getServiceAnnotation.name(), "UTF-8") + "/")))
}

object ServiceRef {
  final val httpServiceUrlProperty = "http.service.url"
  final val serviceTypeProperty = "service.type"
  final val securityServiceName = "security-service"
}
