package com.edifecs.epp.isc

import java.io.{File, InputStream, Serializable}
import java.net.InetAddress
import java.util.Collections
import java.util.concurrent.{TimeoutException, TimeUnit}

import akka.actor
import com.edifecs.epp.isc.cluster.messages.{Registered, NotifyWhenRegistered}
import com.edifecs.epp.security.remote.SecurityManager
import com.edifecs.epp.isc.stream.StreamRootActor.{IncomingStream, ExpectIncomingStream, RegisterOutgoingStream}
import org.jboss.netty.channel.ChannelException

import scala.collection.JavaConversions._
import scala.collection.immutable
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

import akka.actor.{ActorSystem, ActorRef, ActorSelection, RootActorPath}
import akka.pattern.{AskTimeoutException, ask}

import com.typesafe.config.{ConfigValueFactory, Config}

import org.apache.shiro.mgt.DefaultSecurityManager
import org.apache.shiro.session.InvalidSessionException
import org.slf4j.LoggerFactory

import com.edifecs.core.configuration.helper.{SystemVariables, TypesafeConfigKeys => Keys}
import com.edifecs.epp.isc.api.{RemoteSessionManager, RemoteSubjectFactory, SecurityServiceRealm}
import com.edifecs.epp.isc.async.{StatePropagatingExecutor, MessageFuture}
import com.edifecs.epp.isc.async.Implicits._
import com.edifecs.epp.isc.cluster.{ClusterJoinerActor, ClusterRootActor}
import com.edifecs.epp.isc.communicator._
import com.edifecs.epp.isc.core.ServiceStatus
import com.edifecs.epp.isc.core.command.{CommandRootActor, AbstractCommandHandler, CommandReceiver}
import com.edifecs.epp.isc.core.command.CommandAnnotationProcessor._
import com.edifecs.epp.isc.core.command.CommandReceiverActor.{RegisterCommandReceiver,
  UnregisterAllCommandReceivers, UnregisterCommandReceiver}
import com.edifecs.epp.isc.stream.{StreamRootActor, MessageStream}
import com.edifecs.epp.security.SessionId
import com.edifecs.epp.security.exception.SecurityManagerException
import com.edifecs.servicemanager.metric.api.IMetric
import com.edifecs.servicemanager.metric.api.exception.MetricException
import com.edifecs.servicemanager.metric.api.impl.CodahaleMetricImpl
import com.edifecs.servicemanager.metric.api.reporter._
import com.edifecs.epp.isc.exception._
import com.edifecs.epp.isc.command.{CommandSpecification, RemoteCommandMessage, CommandSource, CommandMessage}

/**
 * Message class used to send out any command messages to anyone in the
 * cluster. It knows if it's a local service or remote service, and will
 * properly route the messages to where they need to go.
 *
 * @author willclem
 * @author c-adamnels
 */
class CommandCommunicator(var config: Config, address: Address) extends ICommandCommunicator with Isc {

  import CommandCommunicator._

  private type CommandMap = java.util.Map[Address, java.util.List[CommandSpecification]]

  private final val logger = LoggerFactory.getLogger(getClass)

  private implicit lazy val asyncTimeout: akka.util.Timeout =
    config.getDuration(Keys.ASYNC_MESSAGE_TIMEOUT, MILLISECONDS)
  private lazy val syncTimeout: Int =
    config.getDuration(Keys.SYNC_MESSAGE_TIMEOUT, MILLISECONDS).toInt

  private val clusterName = config.getString(Keys.CLUSTER_NAME)
  if (clusterName==null){
    "Heeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeelllllllllllllllllllllllllllllllllllllllooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
  }
  private val clusterJoinerClass: Class[_ <: ClusterJoinerActor] = {
    val clsName = config.getString(Keys.CLUSTER_JOINER_CLASS)
    val cls = Class.forName(clsName)
    if (classOf[ClusterJoinerActor].isAssignableFrom(cls)) {
      cls.asInstanceOf[Class[_ <: ClusterJoinerActor]]
    } else throw new RuntimeException(
        s"The cluster joiner class $clsName is not a subclass of ClusterJoinerActor.")
  }
  private var state: Option[State] = None
  private def getState: State = state.getOrElse(throw new IllegalStateException(
    "CommandCommunicator is not connected."))

  private val availableCommandsLock = new Object
  private var availableCommandsValid = false
  private var availableCommands: CommandMap = Collections.emptyMap()
  private var availableServices: immutable.Set[(Address, String, ServiceStatus)] =
    immutable.Set.empty

  private var reporting = true

  def connect(): Unit = globalLock synchronized {
    // Search for an open port.
    config = PortFinder.findAndSetConfigPort(config).withValue(
      Keys.AKKA_HOSTNAME,
      ConfigValueFactory.fromAnyRef(InetAddress.getLocalHost.getHostAddress))

    implicit val system = {
      var s: Option[ActorSystem] = None
      var tries = 0
      //TODO: Review this logic and if needed set the connection retry level
      while (s.isEmpty && tries < 10) {
        try s = Some(ActorSystem("isc", config, classOf[ActorSystem].getClassLoader))
        catch {
          case ex: ChannelException =>
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
    val addressRegistry = new AddressRegistry()
    if (instance.isDefined) logger.warn(
      "Another CommandCommunicator instance already exists! This may create problems.")
    else {
      instance = Some(this)
      Isc.instance = Some(this)
    }

    try {
      state = Some(State(
        addressRegistry = addressRegistry,
        metric = setupMetric(),
        system = system,
        executionContext = new StatePropagatingExecutor(system.dispatcher, this),
        streamRootRef = StreamRootActor.createInstance,
        commandRootRef = CommandRootActor.createInstance(this),
        clusterRootRef = ClusterRootActor.createInstance(
          clusterName, address, addressRegistry, clusterJoinerClass),
        securityBackend = {
          // Register a command receiver in an unorthodox way, without calling
          // registerCommandReceiver, in order to avoid a dependency cycle.
          val actor = system.actorSelection("user/" + CommandRootActor.name)
          processAnnotatedCommandHandler(new BaseMessageHandler, Some(this)) foreach { r =>
            actor ! RegisterCommandReceiver(address, r)
          }
          val securityManager = new DefaultSecurityManager()
          securityManager.setSessionManager(new RemoteSessionManager(this))
          securityManager.setSubjectFactory(new RemoteSubjectFactory(securityManager, this))
          securityManager.setRealm(new SecurityServiceRealm(this))
          new SecurityManager(securityManager, this)
        },
        serviceRegistryUpdater = updateNodeRegistry()
      ))
      requestServiceRegistryUpdate()

      // TODO: Also display the IP and port used by Akka
      logger.info("Joined cluster: {} using address: {}", Seq(clusterName, address): _*)

      // logger.info("Servers found in Cluster: {}", getAddressRegistry.getRegisteredAddresses)
      logger.debug("Agents found in Cluster: {}", getAddressRegistry.getRegisteredAgentAddresses)
      logger.debug("Nodes found in Cluster: {}", getAddressRegistry.getRegisteredNodeAddresses)
    } catch {
      case t: Throwable =>
        state = None
        system.shutdown()
        if (instance.orNull == this) instance = None
        if (Isc.instance.orNull == this) Isc.instance = None
        system.awaitTermination(
          config.getDuration(Keys.SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS).milliseconds)
        throw t
    }
  }

  def connectExclusively(): Unit = globalLock synchronized {
    while (instance.isDefined) {
      instance map {_.disconnect()}
      instance = None
      Isc.instance = None
    }
    connect()
  }

  override def disconnect(): Unit = globalLock synchronized {
    // Shutdown status Update Thread
    state map { s =>
      s.serviceRegistryUpdater.setRunning(false)

      // Join the thread so that it waits for completion before continuing
      try s.serviceRegistryUpdater.join()
      catch {
        case ex: InterruptedException => logger.error(ex.getMessage, ex)
      }

      s.system.shutdown()
      s.system.awaitTermination(
        config.getDuration(Keys.SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS).milliseconds)
      s.addressRegistry.unregisterNode(getAddress)
      if (instance.orNull == this) instance = None
      if (Isc.instance.orNull == this) Isc.instance = None
    }
    state = None
  }

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
              val zabbixUsername=config.getString(Keys.METRIC_REPORTER_ZABBIX_USERNAME)
              val zabbixPassword=config.getString(Keys.METRIC_REPORTER_ZABBIX_PASSWORD)
              val reporter = new ZabbixMetricReporter(zabbixHost, zabbixPort, zabbixHostGroup,zabbixUsername, zabbixPassword)

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

  private def getCurrentSession: SessionId =
    getState.securityBackend.getSessionManager.getCurrentSession

  private def parseMessageResponse(response: MessageResponse, source: Address): Future[Serializable] = {
    if (response.isErrorResponse) Future {
      response.getExceptionMap.get(source) match {
        case null =>
          throw new MessageException("Unknown error. Dump of MessageResponse: " + response, Isc.stack)
        case mx: MessageException =>
          throw unwrapExceptions(mx)
        case ex: Exception => throw ex
      }
    } else {
      response.getResponseMap.get(source) match {
        case stream: MessageStream if !source.isLocalAddress(address) =>
          val nodeAddress = new Address(source.getServerName, source.getNodeName)
          getIncomingStream(stream, getAddressRegistry.getAddressForNode(nodeAddress))
        case s: Serializable => Future(s)
        case null => Future(null)
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

  private def updateNodeRegistry(): ServiceRegistryUpdater = {
    logger.debug("Starting Service Monitor")

    // Request Status information for all other services in the cluster
    //requestServiceRegistryUpdate()
    //logger.debug("Initial Service Status Updated")

    // Start the thread which queries all the nodes in the cluster for
    // available services and their statuses.
    val serviceUpdaterThread = new ServiceRegistryUpdater(this)
    serviceUpdaterThread.start()
    logger.debug("Started Status Thread")
    serviceUpdaterThread
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
        case (a: Address, ex: MessageException) =>
          if (ex.getOriginalExceptionClassName == classOf[InvalidSessionException].getCanonicalName)
            throw ex.getOriginalException.asInstanceOf[InvalidSessionException]
          else
            logger.warn(s"Error occurred while getting service data from $a.", ex)
      }

      response.getResponseMap foreach { e =>
        val (address, value) = e
        if (value != null) {
          val statusGroup = value.asInstanceOf[ServerServiceRegistry]
          registry.updateAllServiceInformation(address, statusGroup.getServiceInformation)
        }
      }

      val services = immutable.Set(asScalaSet(registry.getAllServiceInformation.entrySet).toSeq map
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

  @throws[TimeoutException]
  def waitUntilNodeJoins(address: Address, timeout: FiniteDuration): Unit = {
    if (!Await.result(
          getState.clusterRootRef.?(NotifyWhenRegistered(address, timeout))(timeout),
          timeout
        ).isInstanceOf[Registered]) {
      throw new IllegalStateException("Unexpected response to NotifyWhenRegistered.")
    }
  }

  // --------------------------------------------------------------------------
  // Message-sending methods
  // --------------------------------------------------------------------------

  private[isc] def sendMessage(
    message: CommandMessage,
    destination: Address
  ): Future[MessageResponse] = {
    val nodeAddress = new Address(destination.getServerName, destination.getNodeName)
    val (actorPath, remote) =
      if (nodeAddress == address) {
        if (destination.isService) (getState.commandRootRef.path / destination.getService, false)
        else (getState.commandRootRef.path, false)
      } else {
        val akkaAddress = getAddressRegistry.getAddressForNode(nodeAddress)
        if (akkaAddress == null) return Future {
          throw new InvalidCommandException(message,
            s"Failed to send command to $destination -- Cannot find node $nodeAddress in cluster.")
        }
        (RootActorPath(akkaAddress) / "user" / SystemVariables.AKKA_COMMAND_ROOT, true)
      }
    val actor = getActorSystem.actorSelection(actorPath)
    try {
      if (remote) {
        message.args.values.collect { case ms: MessageStream => registerOutgoingStream(ms)}
        (actor ? RemoteCommandMessage(destination, message)).mapTo[MessageResponse]
      } else (actor ? message).mapTo[MessageResponse]
    } catch {
      case ex: AskTimeoutException => Future {
        val msg = s"Async command '${message.name}' sent to $destination timed out after ${asyncTimeout.duration.toMillis}ms"
        logger.warn(msg + message.stack.map("\n\tfrom command " + _).mkString(""))
        val mr = new MessageResponse
        mr.addException(destination, new MessageException(
          new TimeoutException(msg), message.toStack(destination)))
        mr
      }
    }
  }

  private[isc] def sendMessage(
    message: CommandMessage,
    destinations: Seq[Address]
  ): Future[MessageResponse] = {
    destinations.filter(_ != null).foldLeft(Future {
      val mr = new MessageResponse
      mr.setCompleteResponse(false)
      mr
    }(getExecutionContext)) { (future, address) =>
      future.flatMap { mr1 =>
        sendMessage(message, address).map { mr2 =>
          mr1.addResponse(address, mr2); mr1
        }
      }
    }.map { mr =>
      mr.setCompleteResponse(true); mr
    }
  }

  private[isc] def sendMessageSync(
    message: CommandMessage,
    destination: Address
  ): MessageResponse =
    sendMessageSync(message, destination, syncTimeout)

  private[isc] def sendMessageSync(
    message: CommandMessage,
    destination: Address,
    timeout: Int
  ): MessageResponse =
    try Await.result(sendMessage(message, destination), timeout.milliseconds)
    catch {
      case ex: MessageException =>
        val mr = new MessageResponse
        mr.addException(destination, ex)
        mr
      case ex: TimeoutException =>
        val msg = s"Sync command '${message.name}' sent to $destination timed out after ${timeout}ms"
        logger.warn(msg + message.stack.map("\n\tfrom command " + _).mkString(""))
        val mr = new MessageResponse
        mr.addException(destination, new MessageException(
          new TimeoutException(msg), message.toStack(destination)))
        mr
      case ex: Exception =>
        val mr = new MessageResponse
        mr.addException(destination, new MessageException(
          ex, message.toStack(destination)))
        mr
    }

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

  override def actorFromIscAddress(address: Address): ActorSelection =
    getActorSystem.actorSelection(
      if (getAddress.isLocalAddress(address)) {
        if (address.isNode) getState.commandRootRef.path
        else if (address.isService) getState.commandRootRef.path / address.getService
        else throw new IllegalArgumentException(
          s"Cannot get actor reference for address '$address'.")
      } else {
        val nodeAddress = new Address(address.getServerName, address.getNodeName)
        val root = Option(getAddressRegistry.getAddressForNode(address))
          .map(RootActorPath(_) / "user") getOrElse (
          throw new IllegalArgumentException(s"Cannot find node '$nodeAddress' in cluster."))
        if (address.isNode) root / CommandRootActor.name
        else if (address.isService) root / CommandRootActor.name / address.getService
        else throw new IllegalArgumentException(
          s"Cannot get actor reference for address '$address'.")
      })

  override def getActorSystem = getState.system

  override implicit def getExecutionContext = getState.executionContext

  override def getConfig = config

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

  @throws(classOf[MessageException])
  @throws(classOf[SecurityManagerException])
  override def sendBroadcastMessage(command: String): MessageResponse =
    sendSyncMessage(getAddressRegistry.getRegisteredAddresses, command)

  @throws(classOf[MessageException])
  @throws(classOf[SecurityManagerException])
  override def sendBroadcastMessage(
                                     command: String,
                                     args: java.util.Map[String, _ <: Serializable]
                                     ): MessageResponse =
    sendSyncMessage(getAddressRegistry.getRegisteredAddresses, command, args)

  @deprecated("The varargs version of sendBroadcastMessage creates code that is difficult to" +
    " read and potentially ambiguous. Use the version that takes a Map instead.",
    since="1.6.0.0-SNAPSHOT")
  @throws(classOf[MessageException])
  @throws(classOf[SecurityManagerException])
  override def sendBroadcastMessage(command: String, args: Serializable*): MessageResponse =
    sendSyncMessage(getAddressRegistry.getRegisteredAddresses, command, args: _*)

  override def getAddress: Address = address

  @throws(classOf[ConnectionException])
  def getIpForAddress(nodeAddress: Address): String =
    getAddressRegistry.getAddressForNode(
      new Address(nodeAddress.getServerName, nodeAddress.getNodeName)
    ).host.orNull

  override def getAddressRegistry: AddressRegistry = getState.addressRegistry

  override def registerOutgoingStream(stream: MessageStream) =
    state.map(_.streamRootRef ! RegisterOutgoingStream(stream))

  override def getIncomingStream(stream: MessageStream, from: actor.Address) =
    state.map { s =>
      (s.streamRootRef ? ExpectIncomingStream(stream, from, Isc.stack))
      .mapTo[IncomingStream].map(_.stream)
    } getOrElse Future(stream)

  @throws(classOf[HandlerConfigurationException])
  @throws(classOf[CommandHandlerRegistrationException])
  def registerCommandHandler(address: Address, handler: AbstractCommandHandler): Unit =
    registerCommandReceivers(address, processAnnotatedCommandHandler(handler, Some(this)))

  @throws(classOf[CommandHandlerRegistrationException])
  def registerCommandReceiver(address: Address, receiver: CommandReceiver): Unit = {
    // Check to make sure that the command receiver is register to the local
    // agent, node, or service
    if (!getAddress.isLocalAddress(address)) {
      throw new CommandHandlerRegistrationException(
        "Unable to register command receiver. Not a local address: " + address + " (node address: " + getAddress + ")")
    }
    getState.commandRootRef ! RegisterCommandReceiver(address, receiver)
  }

  @throws(classOf[CommandHandlerRegistrationException])
  def registerCommandReceivers(
    address: Address,
    receivers: java.util.Collection[_ <: CommandReceiver]
  ): Unit =
    receivers foreach (registerCommandReceiver(address, _))

  def unregisterCommandReceiver(address: Address, receiver: CommandReceiver): Unit =
    actorFromIscAddress(address) ! UnregisterCommandReceiver(address, receiver)

  def unregisterAllCommandReceiversAt(address: Address): Unit =
    actorFromIscAddress(address) ! UnregisterAllCommandReceivers(address)

  override def isConnected: Boolean = state.isDefined

  def getClusterName: String = clusterName

  def getSecurityManager: SecurityManager = getState.securityBackend

  def getMetric: IMetric = getState.metric

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
}

object CommandCommunicator {
  final val DEFAULT_MESSAGE_TIMEOUT: Int = 60000
  private[isc] val globalLock = new Object
  private[isc] var instance: Option[CommandCommunicator] = None

  def getInstance: CommandCommunicator = instance getOrElse {
    throw new IllegalStateException("No CommandCommunicator instance is available.")
  }

  private[isc] case class State(
    addressRegistry: AddressRegistry,
    metric: IMetric,
    system: ActorSystem,
    executionContext: ExecutionContext,
    streamRootRef: ActorRef,
    commandRootRef: ActorRef,
    clusterRootRef: ActorRef,
    securityBackend: SecurityManager,
    serviceRegistryUpdater: ServiceRegistryUpdater
  )
}
