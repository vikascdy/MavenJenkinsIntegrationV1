package com.edifecs.servicemanager.api

import java.io.IOException
import java.util.Properties
import java.lang.annotation.Annotation
import java.lang.reflect.InvocationTargetException

import javax.xml.bind.JAXBException;

import scala.collection._

import com.edifecs.core.configuration.helper.PropertiesException;
import com.edifecs.epp.isc.{CommandCommunicator, Address, Isc}
import com.edifecs.epp.isc.core.command.{AbstractCommandHandler, CommandAnnotationProcessor}
import com.edifecs.servicemanager.{annotations => a}
import com.edifecs.epp.isc.exception.HandlerConfigurationException
import java.util
import scala.Some
import org.slf4j.LoggerFactory

object ServiceAnnotationProcessor {

  private lazy val logger = LoggerFactory.getLogger(getClass)

  @throws(classOf[IOException])
  @throws(classOf[PropertiesException])
  @throws(classOf[JAXBException])
  @throws(classOf[HandlerConfigurationException])
  def processAnnotatedService(
      service: AbstractService,
      serviceName: String,
      commandCommunicator: CommandCommunicator with Isc
      ): ServiceRef = {
    processAnnotatedService(
      service,
      serviceName,
      commandCommunicator,
      new Properties(),
      new util.HashMap[String, Properties]()
    )
  }

  @throws(classOf[IOException])
  @throws(classOf[PropertiesException])
  @throws(classOf[JAXBException])
  @throws(classOf[HandlerConfigurationException])
  def processAnnotatedService(
    service: AbstractService,
    serviceName: String,
    commandCommunicator: CommandCommunicator with Isc,
    properties: Properties = new Properties(),
    resources: java.util.Map[String, Properties] = new util.HashMap[String, Properties]()
  ): ServiceRef = {
    val cls = getInterfaceWithAnnotation(service, classOf[a.Service]) getOrElse {
      throw new IllegalArgumentException(
        s"Cannot register an instance of ${service.getClass.getCanonicalName} as a service:" +
         " neither this class nor any of its interfaces is annotated with" +
        s" ${classOf[a.Service].getCanonicalName}.")
    }
    val annotation = cls.getAnnotation(classOf[a.Service])
    logger.debug(s"Processing service '{}', of type '{}', from annotations on class {}.",
      serviceName, annotation.name, cls.getName)
    val address = new Address(
      commandCommunicator.getAddress.getServerName,
      commandCommunicator.getAddress.getNodeName,
      serviceName)
    val map: mutable.Map[String, Properties] = JavaConversions.mapAsScalaMap(resources)
    val ref = new LocalServiceRef(
      serviceInstance = service,
      serviceAnnotation = annotation,
      id = serviceName,
      properties = properties,
      resources = map.map(identity)(breakOut),
      address = address)
    service.initialize(ref, commandCommunicator, commandCommunicator)
    cls.getDeclaredMethods.filter(_.isAnnotationPresent(classOf[a.Handler])) foreach { method =>
      if (method.getParameterTypes.length > 0) {
        throw new HandlerConfigurationException(
        s"The handler getter method '${cls.getCanonicalName}.${method.getName}' has more than" +
        s" 0 arguments. Methods annotated with @${classOf[a.Handler].getSimpleName} must not" +
        " take any arguments.")
      }
      try {
        logger.debug("Calling handler getter method {}.{}.", Seq(cls.getSimpleName, method.getName): _*)
        method.invoke(service) match {
          case handler: AbstractCommandHandler =>
            CommandAnnotationProcessor.processAnnotatedCommandHandler(
                handler, Option(commandCommunicator), annotation.name
            ) foreach ref.registerCommand
          case other: Any =>
            throw new HandlerConfigurationException(
            s"The handler getter method '${service.getClass.getCanonicalName}." +
            s"${method.getName} returned an object of type ${other.getClass.getCanonicalName}," +
            s" which is not a subclass of ${classOf[AbstractCommandHandler].getCanonicalName}." +
            " All command handler implementations must extend this abstract class.")
          case _ =>
            throw new HandlerConfigurationException(
              s"The handler getter method '${service.getClass.getCanonicalName}." +
              s"${method.getName} returned a null value," +
              s" which is not a subclass of ${classOf[AbstractCommandHandler].getCanonicalName}." +
              " All command handler implementations must extend this abstract class. Please make sure" +
              " that the initialization of the CommandHandler is correct.")
        }
      } catch {
        case ex: InvocationTargetException =>
          throw new HandlerConfigurationException(
          "Exception occurred in the command handler initialization method" +
          s" '${cls.getCanonicalName}.${method.getName}'.", ex.getTargetException)
      }
    }
    // Register the service with the service registry
    ServiceRegistry.registerLocalService(ref)
    ref
  }

  private def getInterfaceWithAnnotation[A <: Annotation](
    owner: Object,
    annClass: Class[A]
  ): Option[Class[_]] = {
    val cls = owner.getClass
    if (cls.isAnnotationPresent(annClass)) {
      Some(cls)
    } else {
      cls.getInterfaces.find(_.isAnnotationPresent(annClass))
    }
  }
}

