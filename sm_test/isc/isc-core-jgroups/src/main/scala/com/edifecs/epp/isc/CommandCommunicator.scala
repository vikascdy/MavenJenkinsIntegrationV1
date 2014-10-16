package com.edifecs.epp.isc

import java.io.{File, InputStream, Serializable}
import java.util.Collections
import java.util.concurrent.{TimeoutException, TimeUnit}

import akka.actor
import akka.actor.{ActorSystem, ActorSelection}
import com.edifecs.epp.isc.api.{RemoteSessionManager, RemoteSubjectFactory, SecurityServiceRealm}
import com.edifecs.core.configuration.helper.{TypesafeConfigKeys => Keys}
import com.edifecs.epp.isc.async.Implicits._
import com.edifecs.epp.isc.async.{StatePropagatingExecutor, MessageFuture}
import com.edifecs.epp.isc.command.{CommandSource, CommandMessage, CommandSpecification}
import com.edifecs.epp.isc.communicator.{AddressRegistry, ClusterConnection, Communicator, ICommunicator, ServerServiceRegistry, ServiceRegistryUpdater}
import com.edifecs.epp.isc.core.command.CommandAnnotationProcessor._
import com.edifecs.epp.isc.core.command.{AbstractCommandHandler, CommandReceiver}
import com.edifecs.epp.isc.core.{MessageInputStream, ServiceStatus}
import com.edifecs.epp.isc.exception._
import com.edifecs.epp.isc.stream.MessageStream
import com.edifecs.epp.security.SessionId
import com.edifecs.epp.security.exception.SecurityManagerException
import com.edifecs.epp.security.remote.SecurityManager
import com.edifecs.servicemanager.metric.api.IMetric
import com.edifecs.servicemanager.metric.api.exception.MetricException
import com.edifecs.servicemanager.metric.api.impl.CodahaleMetricImpl
import com.edifecs.servicemanager.metric.api.reporter._
import com.typesafe.config.{ConfigValueFactory, Config}
import org.apache.shiro.mgt.DefaultSecurityManager
import org.apache.shiro.session.InvalidSessionException
import org.jgroups.JChannel
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
import scala.collection.immutable
import scala.collection.immutable.{Set => ImmutableSet}
import scala.collection.mutable.{HashMap, MultiMap, Set}
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Await, Future}

/**
 * Message class used to send out any command messages to anyone in the
 * cluster. It knows if it's a local service or remote service, and will
 * properly route the messages to where they need to go.
 *
 * @author willclem
 * @author c-adamnels
 */
class CommandCommunicator(
  channel: JChannel,
  var config: Config,
  address: Address
) extends ICommandCommunicator with Isc {

  import CommandCommunicator._

  private type CommandMap = java.util.Map[Address, java.util.List[CommandSpecification]]

  private final val logger = LoggerFactory.getLogger(getClass)

  private implicit lazy val asyncTimeout: akka.util.Timeout =
    config.getDuration(Keys.ASYNC_MESSAGE_TIMEOUT, MILLISECONDS)
  private lazy val syncTimeout: Int =
    config.getDuration(Keys.SYNC_MESSAGE_TIMEOUT, MILLISECONDS).toInt

  private var securityBackend: Option[SecurityManager] = None
  private var serviceUpdaterThread: Option[ServiceRegistryUpdater] = None

  private val clusterName = config.getString(Keys.CLUSTER_NAME)
  private val addressRegistry = new AddressRegistry()
  private val clusterConnection = new ClusterConnection(this, channel, clusterName, address)
  private val communicator: ICommunicator = new Communicator(this)
  private val commandReceivers: MultiMap[Address, CommandReceiver] =
    new HashMap[Address, Set[CommandReceiver]] with MultiMap[Address, CommandReceiver]

  implicit val system = {
    var s: Option[ActorSystem] = None
    var tries = 0
    //TODO: Review this logic and if needed set the connection retry level
    while (s.isEmpty && tries < 10) {
      try s = Some(ActorSystem("isc", config, classOf[ActorSystem].getClassLoader))
      catch {
        case ex: Exception =>
          tries += 1
          logger.warn("Failed to connect; {}", if (tries<5) "trying again..." else ex.getMessage)
          config = config.withValue(
            Keys.AKKA_PORT,
            ConfigValueFactory.fromAnyRef(config.getInt(Keys.AKKA_PORT) + 1))
          Thread.sleep(1000L)
      }
    }
    s getOrElse {throw new ConnectionException("Could not create ActorSystem.")}
  }

  if (instance.isDefined) logger.warn(
    "Another CommandCommunicator instance already exists! This may create problems.")
  else {
    instance = Some(this)
    Isc.instance = Some(this)
  }

  private val executionContext = new StatePropagatingExecutor(system.dispatcher, this);

  private val availableCommandsLock = new Object
  private var availableCommandsValid = false
  private var availableCommands: CommandMap = Collections.emptyMap()
  private var availableServices: ImmutableSet[(Address, String, ServiceStatus)] =
    ImmutableSet.empty

  private val metric = setupMetric()
  private var reporting = true

  private def setupMetric(): IMetric = {
    // init metric api
    // TODO: registry for each node? maintain metric registy like address registry?
    // TODO: add multiple reporters from config.properties?
    // allow metric reporter configuration in one line comma separated values

    val metric = new CodahaleMetricImpl()
    config.getString(Keys.METRIC_REPORTER_TYPE) match {
      case null =>
        reporting = false
        logger.warn("No metric reporter has been configured")
      case mrtype: String =>
        try {
          mrtype.toLowerCase match {
            case "console" =>
              val reporter = new ConsoleMetricReporter(
                config.getDuration(Keys.METRIC_REPORTER_PERIOD, TimeUnit.MILLISECONDS),
                TimeUnit.MILLISECONDS)
              logger.info("Console reporter configured successfully")
              metric.configureReporter(reporter)
            case "csv" =>
              val directory = config.getString(Keys.METRIC_REPORTER_DATA_DIRECTORY)
              val reporter = new CSVMetricReporter(
                config.getDuration(Keys.METRIC_REPORTER_PERIOD, TimeUnit.MILLISECONDS),
                TimeUnit.MILLISECONDS,
                directory)
              logger.info("CSV reporter files generated at: {}",
                new File(directory).getAbsolutePath)
              metric.configureReporter(reporter)
            case "zabbix" =>
              val zabbixHost = config.getString(Keys.METRIC_REPORTER_ZABBIX_HOST)
              val zabbixPort = config.getInt(Keys.METRIC_REPORTER_ZABBIX_PORT)
              val zabbixHostGroup = config.getString(Keys.METRIC_REPORTER_ZABBIX_HOST_GRP)
              val reporter = new ZabbixMetricReporter(zabbixHost, zabbixPort, zabbixHostGroup)
              metric.configureReporter(reporter)
              logger.info(s"zabbix reporter configured successfully @ $zabbixHost $zabbixPort ")
            case "jmx" =>
              val reporter = new JmxMetricReporter
              metric.configureReporter(reporter)
              logger.info("JMX reporter configured successfully")
          }
        } catch {
          case e: MetricException =>
            logger.error("error configuring metric reporter", e)
        }
    }
    metric
  }

  private def getTimeUnit(metricReporterPeriodUnit: String): TimeUnit =
    metricReporterPeriodUnit.toLowerCase match {
      case "seconds" => TimeUnit.SECONDS
      case "minutes" => TimeUnit.MINUTES
      case "hours" => TimeUnit.HOURS
      case _ => null
    }

  private def getCurrentSession: SessionId =
    securityBackend.map(_.getSessionManager.getCurrentSession).get

  private def parseMessageResponse(response: MessageResponse, source: Address): Future[Serializable] = {
    if (response.isErrorResponse) {
      response.getExceptionMap.get(source) match {
        case null =>
          throw new MessageException("Unknown error. Dump of MessageResponse: " + response)
        case e: MessageException => throw e.getOriginalException
        case e: Exception => throw e
      }
    } else {
      response.getResponseMap.get(source) match {
        case stream: MessageInputStream =>
          stream.initStream(clusterConnection)
          Future(stream)
        case s: Serializable =>
          Future(s)
        case null =>
          null
      }
    }
  }

  private def unwrapExceptions(ex: MessageException): Exception = {
    // This is called when a MessageException is thrown, and unwraps any exception from a MessageException if possible.
    try {
      ex.getOriginalException
    } catch {
      case e: ClassNotFoundException => ex
    }
  }

  private def processMessage(message: CommandMessage): MessageResponse =
    communicator.sendSyncMessage(message)

  private def processMessage(message: CommandMessage, is: InputStream): MessageResponse =
    communicator.sendSyncMessage(message, is)


  private def updateNodeRegistry(): Unit = {
    logger.debug("Starting Service Monitor")

    // Update the Address view for the cluster
    val view = clusterConnection.getView
    if (view != null) updateRegistry(view.getMembers)
    logger.debug("Address Registry Updated")

    // Request Status information for all other services in teh cluster
    requestServiceRegistryUpdate()
    logger.debug("Initial Service Status Updated")

    // Start the thread which queries all the nodes in the cluster for
    // available services and their statuses.
    serviceUpdaterThread = Some(new ServiceRegistryUpdater(this))
    serviceUpdaterThread.get.start()
    logger.debug("Started Status Thread")
  }

  @throws(classOf[RegistryUpdateException])
  def requestServiceRegistryUpdate(): Unit = {
    val registry = getAddressRegistry

    // Send Request for the status information
    try {
      val addresses = registry.getRegisteredNodeAddresses
      addresses.remove(getAddress) // Nodes shouldn't ping themselves
      val response = sendSyncMessage(addresses, "getServicesInformation")

      // Output log errors for thrown exceptions
      response.getExceptionMap foreach {
        case (_, ex: InvalidSessionException) =>
          throw ex
        case (a: Address, ex: Throwable) =>
          logger.error(s"Error occurred while getting service data from $a.", ex)
      }

      response.getResponseMap foreach { e =>
        val (address, value) = e
        if (value != null) {
          val statusGroup = value.asInstanceOf[ServerServiceRegistry]
          registry.updateAllServiceInformation(address, statusGroup.getServiceInformation)
        }
      }

      val services = ImmutableSet(asScalaSet(registry.getAllServiceInformation.entrySet).toSeq map
        {e => (e.getKey, e.getValue.getServiceName, e.getValue.getServiceStatus)}: _*)

      availableCommandsLock.synchronized {
        if (availableServices != services) {
          logger.debug("Invalidating command specification cache.")
          availableServices = services
          availableCommandsValid = false
        }
      }
    } catch {
      case ex: InvalidSessionException =>
        // This exception may be thrown if the SecurityService has been stopped
        // and a SessionId is still bound to the current thread.
        getSecurityManager.getSessionManager.unregisterCurrentSession()
        requestServiceRegistryUpdate() // Retry; hopefully the error is fixed now...
      case ex: Exception =>
        throw new RegistryUpdateException(
          "Unable to update service registry information.", ex)
    }
  }

  def getClusterConnection: ClusterConnection = clusterConnection

  // --------------------------------------------------------------------------
  // Message-sending methods
  // --------------------------------------------------------------------------

  private[isc] def sendMessage(
    message: CommandMessage,
    destination: Address
  ): Future[MessageResponse] =
    // FIXME: This should be truly async. Creating a new Future spawns an unnecessary extra thread.
    Future(sendMessageSync(message, Seq(destination), syncTimeout))

  private[isc] def sendMessage(
    message: CommandMessage,
    destinations: Seq[Address]
  ): Future[MessageResponse] =
  // FIXME: This should be truly async. Creating a new Future spawns an unnecessary extra thread.
    Future(sendMessageSync(message, destinations, syncTimeout))


  private[isc] def sendMessageSync(
    message: CommandMessage,
    destination: Address
  ): MessageResponse =
    sendMessageSync(message, Seq(destination), syncTimeout)

  private[isc] def sendMessageSync(
    message: CommandMessage,
    destinations: Seq[Address]
  ): MessageResponse =
    sendMessageSync(message, destinations, syncTimeout)

  private[isc] def sendMessageSync(
    message: CommandMessage,
    destinations: Seq[Address],
    timeout: Int
  ): MessageResponse =
    try Await.result(sendMessage(message, destinations), timeout.milliseconds)
    catch {
      case ex: MessageException =>
        val mr = new MessageResponse
        destinations.foreach(mr.addException(_, ex))
        mr
      case ex: Exception =>
        val mr = new MessageResponse
        destinations.foreach { d =>
          mr.addException(d, new MessageException(ex, message.toStack(d)))
        }
        mr
    }

  // --------------------------------------------------------------------------
  // Public interface methods for Isc
  // --------------------------------------------------------------------------

  override def send(
    destination: Address,
    command: String,
    arguments: scala.collection.Map[String, _ <: Serializable],
    source: CommandSource
  ): MessageFuture[Serializable] =
    sendMessage(CommandMessage(
      command,
      toImmutable(arguments),
      source,
      sender = Isc.address.orNull,
      session = getCurrentSession,
      stack = Isc.stack),
      destination).flatMap(parseMessageResponse(_, destination))

  @throws(classOf[MessageException])
  @throws(classOf[SecurityManagerException])
  @throws(classOf[TimeoutException])
  override def sendSync(
      destination: Address,
      command: String,
      arguments: scala.collection.Map[String, _ <: Serializable],
      source: CommandSource
      ): Serializable =
    Await.result(parseMessageResponse(sendMessageSync(CommandMessage(
      command,
      toImmutable(arguments),
      source,
      sender = Isc.address.orNull,
      session = getCurrentSession,
      stack = Isc.stack),
      destination), destination), syncTimeout milliseconds)

  override def getAvailableCommands = availableCommandsLock.synchronized {
    if (!availableCommandsValid) {
      val map = new java.util.HashMap[Address, java.util.List[CommandSpecification]]
      for (
        entry <- getAddressRegistry.getAllServiceInformation.entrySet
              if entry.getValue.getServiceStatus == ServiceStatus.Started
      ) {
        val addr = entry.getKey
        map.put(addr, sendSync(addr, Isc.commandListCommand).asInstanceOf[
          java.util.List[CommandSpecification]])
      }
      availableCommands = Collections.unmodifiableMap(map)
      availableCommandsValid = true
    }
    availableCommands
  }

  // --------------------------------------------------------------------------
  // Public interface methods for ICommandCommunicator
  // --------------------------------------------------------------------------

  @throws(classOf[ConnectionException])
  @throws(classOf[CommandHandlerRegistrationException])
  @throws(classOf[HandlerConfigurationException])
  @throws(classOf[RegistryUpdateException])
  def connect(): Unit = {
    registerCommandReceivers(
      address,
      processAnnotatedCommandHandler(new BaseMessageHandler, Some(this)))

    clusterConnection.connect(clusterName)

    val securityManager = new DefaultSecurityManager()
    securityManager.setSessionManager(new RemoteSessionManager(this))
    securityManager.setSubjectFactory(new RemoteSubjectFactory(securityManager, this))
    securityManager.setRealm(new SecurityServiceRealm(this))
    this.securityBackend = Some(new SecurityManager(securityManager, this))

    // TODO: Also display the IP and port used by JGroups
    logger.info("Joined cluster: {} using address: {}", Seq(clusterName, address): _*)

    logger.info("JGroups Version: {}", clusterConnection.getVersion)
    logger.debug("Successfully connected to cluster")

    // Initialize the Service Status Update Thread
    updateNodeRegistry()

    // logger.info("Servers found in Cluster: {}",
    // clusterConnection.getCommandCommunicator.getAddressRegistry.getRegisteredAddresses)
    logger.info("Agents found in Cluster: {}", clusterConnection
      .getCommandCommunicator.getAddressRegistry.getRegisteredAgentAddresses)
    logger.info("Nodes found in Cluster: {}", clusterConnection
      .getCommandCommunicator.getAddressRegistry.getRegisteredNodeAddresses)

    CommandCommunicator.instance = Some(this)
  }

  override def disconnect: Unit = {
    // Shutdown status Update Thread
    serviceUpdaterThread.map { thread =>
      thread.setRunning(false)

      // Join the thread so that it waits for completion before continuing
      try thread.join()
      catch {
        case ex: InterruptedException => logger.error(ex.getMessage, ex)
      }
    }

    addressRegistry.unregisterNode(getAddress)
    this.synchronized {
      commandReceivers.clear()
    }

    clusterConnection.disconnect()
    CommandCommunicator.instance = None
  }

  // --------------------------------------------------------------------------
  // Public interface methods for ICommandCommunicator
  // --------------------------------------------------------------------------

  @throws(classOf[MessageException])
  @throws(classOf[SecurityManagerException])
  override def sendSyncMessage(destination: Address, command: String): Serializable =
    sendSync(destination, command)

  @throws(classOf[MessageException])
  @throws(classOf[SecurityManagerException])
  override def sendSyncMessage(
    destination: Address,
    command: String,
    args: java.util.Map[String, _ <: Serializable]
  ): Serializable =
    sendSync(destination, command, args.asInstanceOf[java.util.Map[String, Serializable]])

  @deprecated("The varargs versions of sendSyncMessage create code that is difficult to read and" +
    " potentially ambiguous. Use the versions that take a Map instead.",
    since="1.6.0.0-SNAPSHOT")
  @throws(classOf[MessageException])
  @throws(classOf[SecurityManagerException])
  override def sendSyncMessage(
    destination: Address,
    command: String,
    args: Serializable*
  ): Serializable =
    sendSync(destination, command, varargsToMap(args))

  @deprecated("Instead of attaching an InputStream, use sendSync and attach a MessageStream as a" +
    " normal argument.", since="akka")
  @throws(classOf[MessageException])
  @throws(classOf[SecurityManagerException])
  override def sendSyncMessage(
    destination: Address,
    command: String,
    streamArgName: String,
    stream: InputStream,
    args: java.util.Map[String, _ <: Serializable]
  ): Serializable =
    sendSync(destination, command,
      mapAsScalaMap(args) + (streamArgName -> MessageStream.fromInputStream(stream)))

  @deprecated("The varargs versions of sendSyncMessage create code that is difficult to read and" +
    " potentially ambiguous. Use the versions that take a Map instead.", since="1.6.0.0-SNAPSHOT")
  @throws(classOf[MessageException])
  @throws(classOf[SecurityManagerException])
  override def sendSyncMessage(
    destination: Address,
    command: String,
    streamArgName: String,
    stream: InputStream,
    args: Serializable*
  ): Serializable =
    sendSync(destination, command,
      varargsToMap(args) + (streamArgName -> MessageStream.fromInputStream(stream)))

  @throws(classOf[MessageException])
  @throws(classOf[SecurityManagerException])
  override def sendSyncMessage(
    destinations: java.util.Collection[Address],
    command: String
  ): MessageResponse =
    sendSyncMessage(destinations, command, Collections.emptyMap[String, Serializable]())

  @throws(classOf[MessageException])
  @throws(classOf[SecurityManagerException])
  override def sendSyncMessage(
    destinations: java.util.Collection[Address],
    command: String,
    args: java.util.Map[String, _ <: Serializable]
  ): MessageResponse =
    sendMessageSync(CommandMessage(
      command,
      toImmutable(mapAsScalaMap(args)),
      CommandSource.AKKA,
      sender = Isc.address.orNull,
      session = getCurrentSession,
      stack = Isc.stack
    ), destinations.toSeq)

  @deprecated("The varargs versions of sendSyncMessage create code that is difficult to read and" +
    " potentially ambiguous. Use the versions that take a Map instead.",
    since="1.6.0.0-SNAPSHOT")
  @throws(classOf[MessageException])
  @throws(classOf[SecurityManagerException])
  override def sendSyncMessage(
    destinations: java.util.Collection[Address],
    command: String,
    args: Serializable*
  ): MessageResponse =
    sendMessageSync(CommandMessage(
      command,
      varargsToMap(args),
      CommandSource.AKKA,
      sender = Isc.address.orNull,
      session = getCurrentSession,
      stack = Isc.stack
    ), destinations.toSeq)

  @throws(classOf[Exception])
  override def sendBroadcastMessage(command: String): MessageResponse =
    sendSyncMessage(getAddressRegistry.getRegisteredAddresses, command)

  @throws(classOf[Exception])
  override def sendBroadcastMessage(
    command: String,
    args: java.util.Map[String, _ <: Serializable]
  ): MessageResponse =
    sendSyncMessage(getAddressRegistry.getRegisteredAddresses, command, args)

  @deprecated("The varargs version of sendBroadcastMessage creates code that is difficult to" +
    " read and potentially ambiguous. Use the version that takes a Map instead.",
    since="1.5.0.0-SNAPSHOT")
  @throws(classOf[Exception])
  override def sendBroadcastMessage(command: String, args: Serializable*): MessageResponse =
    sendSyncMessage(getAddressRegistry.getRegisteredAddresses, command, args: _*)

  override def getAddress: Address = address

  @throws(classOf[ConnectionException])
  def getIpForAddress(nodeAddress: Address): String =
    clusterConnection.getIpForAddress(addressRegistry.getAddressForNode(
      new Address(nodeAddress.getServerName, nodeAddress.getNodeName)))

  def updateRegistry(viewMembers: java.util.List[org.jgroups.Address]): Unit =
    addressRegistry.updateRegistry(viewMembers)

  override def getAddressRegistry: AddressRegistry = addressRegistry

  @throws(classOf[HandlerConfigurationException])
  @throws(classOf[CommandHandlerRegistrationException])
  def registerCommandHandler(address: Address, handler: AbstractCommandHandler): Unit =
    registerCommandReceivers(address, processAnnotatedCommandHandler(handler, Some(this)))

  @throws(classOf[CommandHandlerRegistrationException])
  def registerCommandReceiver(address: Address, receiver: CommandReceiver): Unit = {
    // Check to make sure that the command receiver is register to the local
    // agent, node, or service
    if (!address.isLocalAddress(getAddress)) {
      throw new CommandHandlerRegistrationException(
        "Unable to register command receiver. Not a local address: " + address)
    }
    this.synchronized {
      // Check to see if the same receiver is already registered at this address.
      if (commandReceivers.entryExists(address, _ == receiver)) {
        throw new CommandHandlerRegistrationException(
          "Unable to register command receiver. Same receiver has already been registered for" +
            " address: " + address);
      }
      // Add the receiver to the map.
      commandReceivers.addBinding(address, receiver)
    }
  }

  @throws(classOf[CommandHandlerRegistrationException])
  def registerCommandReceivers(
    address: Address,
    receivers: java.util.Collection[_ <: CommandReceiver]
  ): Unit =
    receivers foreach (registerCommandReceiver(address, _))

  def unregisterCommandReceiver(address: Address, receiver: CommandReceiver): Boolean =
    this.synchronized {
      val result = commandReceivers.entryExists(address, _ == receiver)
      commandReceivers.removeBinding(address, receiver)
      result
    }

  def unregisterAllCommandReceiversAt(address: Address): java.util.Set[CommandReceiver] =
    this.synchronized {
      commandReceivers.remove(address).map(setAsJavaSet(_)).getOrElse(
        Collections.emptySet[CommandReceiver])
    }

  def getAllCommandReceiversAt(address: Address): java.util.Set[CommandReceiver] =
    commandReceivers.get(address).map(setAsJavaSet(_)).getOrElse(
      Collections.emptySet[CommandReceiver])

  override def isConnected: Boolean = clusterConnection.isConnected

  def getClusterName: String = clusterConnection.getClusterName

  def getSecurityManager: SecurityManager = securityBackend.get

  def getMetric: IMetric = metric

  def reportingEnabled: Boolean = reporting

  private def varargsToMap(varargs: Seq[Serializable]) = {
    if (varargs.size % 2 != 0)
      throw new IllegalArgumentException("An even number of varargs parameters is required;" +
        " the parameters are interpreted as key-value pairs.")
    immutable.Map[String, Serializable]((0 until varargs.size by 2) map { n =>
      varargs(n).toString -> varargs(n+1)
    }: _*)
  }

  private def toImmutable(m: scala.collection.Map[String, _ <: Serializable]):
  immutable.Map[String, Serializable] = m match {
    case m: immutable.Map[_, _] => m.asInstanceOf[immutable.Map[String, Serializable]]
    case m: scala.collection.Map[_, _] => immutable.Map[String, Serializable](m.toList: _*)
  }

  override def getConfig = config

  override def getActorSystem: ActorSystem = system

  override def actorFromIscAddress(address: Address): ActorSelection = ???

  override def getIncomingStream(stream: MessageStream, from: actor.Address): Future[MessageStream] = ???

  override def registerOutgoingStream(stream: MessageStream): Unit = ???

  override implicit def getExecutionContext: ExecutionContext = executionContext
}

object CommandCommunicator {
  private[isc] var instance: Option[CommandCommunicator] = None

  def getInstance: CommandCommunicator =
    instance getOrElse {
      throw new IllegalStateException("No CommandCommunicator instance is available.")
    }
}