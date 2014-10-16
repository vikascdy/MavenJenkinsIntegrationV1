package com.edifecs.servicemanager.api

import java.io.IOException
import java.util.Properties

import scala.collection.Map
import scala.collection.JavaConversions._

import javax.xml.bind.JAXBException

import org.slf4j.{Logger, LoggerFactory}

import com.edifecs.core.configuration.helper.PropertiesException
import com.edifecs.epp.isc._
import com.edifecs.epp.isc.core.LogFile
import com.edifecs.epp.security.ISecurityManager
import com.edifecs.servicemanager.annotations.Service
import scala.Some
import com.edifecs.contentrepository.{IContentRepositoryService, IContentRepositoryHandler}

import scala.concurrent.ExecutionContext

/**
 * Base class for service implementations. Any implementation of an interface
 * annotated with `@`{@link Service} should extend this class; if it does not,
 * the service registry will not allow it to be used as a service.
 *
 * This abstract class provides utility getter methods which can be used by
 * service implementations to access the messaging and security systems.
 * 
 * @author willclem
 * @author c-adamnels
 */
abstract class AbstractService {
  protected val logger = LoggerFactory.getLogger(getClass)

  private var ref: Option[ServiceRef] = None
  private var commandCommunicator: Option[ICommandCommunicator] = None
  private var iscInstance: Option[Isc] = None
  private var contentRepository: Option[IContentRepositoryHandler] = None

  def initialize(ref: ServiceRef, commandCommunicator: CommandCommunicator, isc: Isc): Unit = {
    this.ref = Some(ref)
    this.commandCommunicator = Some(commandCommunicator)
    this.iscInstance = Some(isc)
    this.contentRepository = Some(commandCommunicator.getService(classOf[IContentRepositoryService]).getContentRepositoryHandler)
  }

  private lazy val uninitialized =
    new IllegalStateException(s"The service ${getClass.getCanonicalName} is not yet initialized.")
    
  def getLogger: Logger = logger

  def getId: String =
    ref map (_.getId) getOrElse {throw uninitialized}

  def getCommandCommunicator: ICommandCommunicator =
    commandCommunicator getOrElse {throw uninitialized}

  protected def isc: Isc =
    iscInstance getOrElse {throw uninitialized}

  protected implicit def executionContext: ExecutionContext =
    isc.getExecutionContext

  def getAddress: Address =
    ref map (_.getAddress) getOrElse {throw uninitialized}

  def getAddressRegistry: IAddressRegistry =
    commandCommunicator.get.getAddressRegistry

  def getContentRepository: IContentRepositoryHandler =
    contentRepository getOrElse {throw uninitialized}

  def getSecurityManager: ISecurityManager =
    commandCommunicator.get.getSecurityManager

  def getProperties: Properties =
    ref map (_.getProperties) getOrElse {throw uninitialized}

  def getResources: java.util.Map[String, Properties] =
    ref map (_.getResources) getOrElse {throw uninitialized}

  def getServiceAnnotation: Service =
    ref map (_.getServiceAnnotation) getOrElse {throw uninitialized}

  def getLogFiles: java.util.List[LogFile] =
    ref map (_.getLogFiles) getOrElse {throw uninitialized}

  def getMessage: String =
    ref map (_.getMessage) getOrElse {throw uninitialized}

  @throws(classOf[Exception])
  private[api] final def packageScopeStart() = start()

  @throws(classOf[Exception])
  private[api] final def packageScopeStop() = stop()

  @throws(classOf[Exception])
  private[api] final def getTestPropertiesAndResources: (Properties, Map[String, Properties]) =
    (getTestProperties, getTestResources)

  @throws(classOf[Exception])
  protected def start(): Unit

  @throws(classOf[Exception])
  protected def stop(): Unit

  @throws(classOf[Exception])
  protected def getTestProperties: Properties =
    new Properties()

  @throws(classOf[Exception])
  protected def getTestResources: java.util.Map[String, Properties] =
    java.util.Collections.emptyMap[String, Properties]
}

