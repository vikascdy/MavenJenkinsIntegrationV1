package com.edifecs.epp.isc

import java.io.Serializable
import java.lang.reflect.InvocationTargetException

import akka.actor.{ActorSelection, ActorSystem}
import com.edifecs.epp.isc.async.MessageFuture
import com.edifecs.epp.isc.command.{CommandSpecification, CommandSource, CommandStackFrame}
import com.edifecs.epp.isc.exception.ServiceTypeNotFoundException
import com.edifecs.epp.isc.stream.MessageStream
import com.typesafe.config.Config

import scala.concurrent.{ExecutionContext, Future}

trait Isc {

  @throws(classOf[ServiceTypeNotFoundException])
  def getService[T](serviceClass: Class[T]): T = {
    val originalClassName = serviceClass.getName
    val lastDotIndex = originalClassName.lastIndexOf(".") + 1
    val proxyClassName = originalClassName.substring(0, lastDotIndex) + Isc.proxyClassPrefix +
      originalClassName.substring(lastDotIndex)
    try {
      val proxyClass = getClass.getClassLoader.loadClass(proxyClassName)
      if (!serviceClass.isAssignableFrom(proxyClass)) {
        throw new IllegalStateException(
          s"The proxy class $proxyClassName is not an instance of the interface" +
          s" $originalClassName, and therefore cannot be used as a proxy for this interface." +
           " This is most likely the result of a code-generation error.")
      }
      proxyClass.getConstructor(classOf[Isc]).newInstance(this).asInstanceOf[T]
    } catch {
      case ex: ClassNotFoundException =>
        throw new IllegalArgumentException(
          s"No generated proxy class exists for the service interface $originalClassName.", ex)
      case ex: NoSuchMethodException =>
        throw new IllegalStateException(
          s"The proxy class $proxyClassName does not have a two-argument constructor." +
           " This is most likely the result of a code-generation error.", ex)
      case ex: InvocationTargetException =>
        throw new RuntimeException(ex.getTargetException)
    }
  }

  final def send(
    destination: Address,
    command: String
  ): MessageFuture[Serializable] =
    send(destination, command, Map[String, Serializable]())

  final def send(
    destination: Address,
    command: String,
    arguments: java.util.Map[String, _ <: Serializable]
  ): MessageFuture[Serializable] =
    send(destination, command, scala.collection.JavaConversions.mapAsScalaMap(arguments))

  final def send(
    destination: Address,
    command: String,
    arguments: scala.collection.Map[String, _ <: Serializable]
  ): MessageFuture[Serializable] =
    send(destination, command, arguments, CommandSource.AKKA)

  final def send(
    destination: Address,
    command: String,
    arguments: java.util.Map[String, _ <: Serializable],
    source: CommandSource
  ): MessageFuture[Serializable] =
    send(destination, command, scala.collection.JavaConversions.mapAsScalaMap(arguments), source)

  def send(
    destination: Address,
    command: String,
    arguments: scala.collection.Map[String, _ <: Serializable],
    source: CommandSource
  ): MessageFuture[Serializable]
  
  @throws(classOf[Exception])
  final def sendSync(
    destination: Address,
    command: String
  ): Serializable =
    sendSync(destination, command, Map[String, Serializable]())

  @throws(classOf[Exception])
  final def sendSync(
    destination: Address,
    command: String,
    arguments: java.util.Map[String, _ <: Serializable]
  ): Serializable =
    sendSync(destination, command, scala.collection.JavaConversions.mapAsScalaMap(arguments))

  @throws(classOf[Exception])
  final def sendSync(
    destination: Address,
    command: String,
    arguments: scala.collection.Map[String, _ <: Serializable]
  ): Serializable =
    sendSync(destination, command, arguments, CommandSource.AKKA)

  @throws(classOf[Exception])
  final def sendSync(
    destination: Address,
    command: String,
    arguments: java.util.Map[String, _ <: Serializable],
    source: CommandSource
  ): Serializable =
    sendSync(destination, command, scala.collection.JavaConversions.mapAsScalaMap(arguments), source)

  @throws(classOf[Exception])
  def sendSync(
    destination: Address,
    command: String,
    arguments: scala.collection.Map[String, _ <: Serializable],
    source: CommandSource
  ): Serializable

  def getAddress: Address

  def getAddressRegistry: IAddressRegistry

  def getAvailableCommands: java.util.Map[Address, java.util.List[CommandSpecification]]

  def getConfig: Config

  def getActorSystem: ActorSystem

  implicit def getExecutionContext: ExecutionContext

  def actorFromIscAddress(address: Address): ActorSelection

  def registerOutgoingStream(stream: MessageStream): Unit

  def getIncomingStream(stream: MessageStream, from: akka.actor.Address): Future[MessageStream]
}

object Isc {

  /**
   * Automatically-generated proxy classes for service interfaces or command
   * handler interfaces are named by attaching this prefix to the beginning of
   * the original interface's name.
   */
  final val proxyClassPrefix = "__GeneratedProxy__"

  /**
   * Arguments whose names begin with this prefix will not cause warnings if
   * they are not defined by the command that receives them.
   */
  final val extraArgPrefix = "-x-"

  /**
   * The presence of this argument (which can be set to anything, but is
   * usually set to `true`) indicates that a message's response should be
   * converted to a JSON string.
   */
  final val jsonArg = extraArgPrefix + "json"

  /**
   * All REST commands include the HTTP method (GET, POST, etc.) as a String
   * argument with this name.
   */
  final val restMethodArg  = extraArgPrefix + "rest-method"

  /**
   * If a REST command has a URL suffix (following a slash after the command
   * name), it will be included in the command as a String argument with this
   * name.
   */
  final val urlSuffixArg   = extraArgPrefix + "url-suffix"

  /**
   * If a REST command sent using POST or PUT has a request body of type
   * `application/json`, it will be included in the command as a JSON argument
   * (which may be deserialized as any type) with this name.
   */
  final val requestBodyArg = extraArgPrefix + "request-body"

  /**
   * Builtin commands (commands available for all services) have this
   * namespace, in order to avoid name collisions with defined commands.
   */
  final val builtinCommandNamespace = "__builtins__"

  /**
   * The default (blank) namespace of commands with no namespace.
   */
  final val defaultNamespace = ""

  /**
   * The character that separates a command's namespace from its name.
   */
  final val commandSeparator = "."

  final val commandListCommandWithoutNamespace = "list-commands"

  /**
   * The name of a builtin command, available on all services, which returns
   * a list of {@link CommandSpecification}s, one for each command provided by
   * the service.
   */
  final val commandListCommand =
    builtinCommandNamespace + commandSeparator + commandListCommandWithoutNamespace

  private val threadState = new ThreadLocal[ThreadState]
  private[isc] var instance: Option[Isc] = None

  def get: Isc = Option(threadState.get()) map(_.isc) getOrElse(instance getOrElse {
    throw new IllegalStateException("No Isc instance is available.")
  })
  def address = Option(threadState.get()) map(_.address)
  def stack = Option(threadState.get()) map(_.stack) getOrElse Array.empty[CommandStackFrame]
  def state = threadState.get()
  def setState(state: ThreadState) = threadState.set(state)
  def clearState() = threadState.remove()

  case class ThreadState(
    isc: Isc,
    address: Address,
    stack: Array[CommandStackFrame]
  )
}
