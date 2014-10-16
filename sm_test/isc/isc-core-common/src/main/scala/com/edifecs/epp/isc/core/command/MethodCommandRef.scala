package com.edifecs.epp.isc.core.command

import java.io.{InputStream, Serializable}
import java.lang.reflect.{InvocationTargetException, Method, Modifier}
import java.net.URLEncoder
import java.util.EnumSet

import com.edifecs.epp.isc.{ICommandCommunicator, Address}
import com.edifecs.epp.isc.async.MessageFuture
import com.edifecs.epp.isc.command.{ArgumentSpecification, CommandSource, CommandSpecification}
import com.edifecs.epp.isc.exception.HandlerConfigurationException
import com.edifecs.epp.isc.json.{JsonArg, JsonConverter, Schema}
import com.edifecs.epp.security.exception._
import com.edifecs.epp.security.remote.SessionManager
import com.edifecs.epp.security.{ISecurityManager, SessionId}
import com.edifecs.servicemanager.metric.api.SupportedMetricOperations
import org.slf4j.LoggerFactory

import scala.collection.immutable._
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
 * A handler for a single command. A wrapper around a method that parses
 * incoming messages and converts them to method calls.
 *
 * @author c-adamnels
 */
@throws(classOf[HandlerConfigurationException])
class MethodCommandRef(
  val name: String,
  val method: Method,
  val methodOwner: Object,
  val arguments: Seq[CommandRef.Argument],
  val description: String = "No description available.",
  val async: Boolean = false,
  val timeout: Option[Duration] = None,
  val allowedSources: EnumSet[CommandSource] = EnumSet.noneOf(classOf[CommandSource]),
  securityManager: => Option[ISecurityManager] = None,
  val jsonConverter: JsonConverter = new JsonConverter(null),
  val nullSessionAllowed: Boolean = false,
  val serviceTypeName: String = null
) extends CommandRef {

  import com.edifecs.epp.isc.core.command.CommandRef._

  @inline private def fullMethodName: String =
    s"${method.getDeclaringClass.getCanonicalName}.${method.getName}()"

  private val methodTypes = method.getParameterTypes

  override protected lazy val logger = LoggerFactory.getLogger(methodOwner.getClass)

  // Verify that the method is public.
  if (!Modifier.isPublic(method.getModifiers)) {
    throw new HandlerConfigurationException(
      s"The command method '$fullMethodName' is not public. All command methods must be public.")
  } else if (!Modifier.isPublic(method.getDeclaringClass.getModifiers)) {
    throw new HandlerConfigurationException(
      s"The declaring class of command method '$fullMethodName' is not public. All command" +
        " methods must be public.")
  }

  if (arguments.size != methodTypes.length) {
    throw new HandlerConfigurationException(
      s"Invalid number of arguments for command method '$fullMethodName': Command constructor" +
        s" was given ${arguments.size} argument(s), but the method takes ${methodTypes.length}.")
  }

  if (arguments.count(_.isInstanceOf[StreamArgument]) > 1) {
    throw new HandlerConfigurationException(
      s"The command method '$fullMethodName' has more than one stream argument." +
        " Command methods may only have one stream argument.")
  } else if (arguments.count(_ == SenderArgument) > 1) {
    throw new HandlerConfigurationException(
      s"The command method '$fullMethodName' has more than one sender argument." +
        " Command methods may only have one sender argument.")
  }

  val streamArgIndex = arguments.indexWhere(_.isInstanceOf[StreamArgument])
  val senderArgIndex = arguments.indexWhere(_ == SenderArgument)

  if (streamArgIndex > -1 && methodTypes(streamArgIndex) != classOf[InputStream]) {
    throw new HandlerConfigurationException(
      s"A stream argument must be of type ${classOf[InputStream].getCanonicalName}. Argument" +
        s" #${streamArgIndex + 1} of command method '$fullMethodName' is of type" +
        s" ${methodTypes(streamArgIndex).getCanonicalName} instead.")
  } else if (senderArgIndex > -1 && methodTypes(senderArgIndex) != classOf[Address]) {
    throw new HandlerConfigurationException(
      s"A sender argument must be of type ${classOf[Address].getCanonicalName}. Argument" +
        s" #${senderArgIndex + 1} of command method '$fullMethodName' is of type" +
        s" ${methodTypes(streamArgIndex).getCanonicalName} instead.")
  }

  private val argumentMap: Map[String, BasicArgument] = Map(arguments.flatMap {
    case a: BasicArgument => Seq(a.name -> a)
    case _ => Seq()
  }: _*)

  protected override def executeCommand(
    cc: ICommandCommunicator,
    arguments: Array[Object],
    userSession: Option[SessionId]
  )(
    implicit context: ExecutionContext
  ): Future[Serializable] = {
    if (getReceiverName == "authorization.checkPermission") {
      println("authorization.checkPermission")
    }
    Try(try {
      startMetering(cc)
      if (nullSessionAllowed && userSession.isEmpty) {
        method.invoke(methodOwner, arguments: _*)
      } else {
        if (userSession.isEmpty) throw new NullSessionCommandException(name)
        (securityManager getOrElse cc.getSecurityManager).getSessionManager.asInstanceOf[SessionManager]
          .callMethodAsUser(userSession.get, methodOwner, method, arguments)
      }
    } catch {
      case ex: InvocationTargetException =>
        if (ex.getTargetException.isInstanceOf[Exception]) {
          throw ex.getTargetException
        } else {
          throw ex
        }
    } finally {
      stopMetering(cc)
    }) match {
      case Success(result) =>
        if (async) result.asInstanceOf[MessageFuture[Serializable]].asScalaFuture
        else Future(result.asInstanceOf[Serializable])
      case Failure(ex) => Future(throw ex)
    }
  }

  override def getReceiverName = name

  override def getTimeoutFor(command: String): Option[Duration] =
    if (command == name) timeout
    else None

  private def startMetering(cc: ICommandCommunicator): Unit =
    try {
      // set meter and timer metrics
      cc.getMetric.performOperation(name + "-Meter",
        SupportedMetricOperations.MARK)
      cc.getMetric.performOperation(name + "-Timer",
        SupportedMetricOperations.TIMER_START)
    } catch {
      case ex: Exception =>
        // TODO: handle exception
        logger.error(s"metric error while executing command : $name", ex)
    }

  private def stopMetering(cc: ICommandCommunicator): Unit =
    try {
      // stop timer - same name as above
      cc.getMetric.performOperation(method.getName + "-Timer",
        SupportedMetricOperations.TIMER_STOP)
    } catch {
      case ex: Exception =>
        // TODO: handle exception
        logger.error(s"metric error while executing command : $name", ex)
    }

  override def getSpecifications(rootUrl: Option[String] = None) =
    new CommandSpecification(
      name = name,
      description = description,
      url = if (allowedSources contains CommandSource.REST) {
        rootUrl.map(_ + URLEncoder.encode(name, "UTF-8"))
      } else None,
      accessibleBy = allowedSources.clone,
      arguments = arguments.flatMap {
        case a: BasicArgument =>
          new ArgumentSpecification(
            name = a.name,
            description = a.description,
            required = a.required,
            schema = if (a.`class` != classOf[JsonArg] &&
                         allowedSources.contains(CommandSource.REST)) {
              Some(jsonConverter.getSchemaFor(a.`type`).getOrElse(Schema.fromType(a.`type`)))
            } else None,
            typeName = a.`class`.getName
          ) :: Nil
        case a: StreamArgument =>
          new ArgumentSpecification(
            name = a.name,
            description = a.description,
            required = true,
            stream = true
          ) :: Nil
        case _ =>
          Nil
      },
      requestBodySchema = methodOwner match {
        case rch: RestCommandHandler[_] =>
          Some(rch.typeAdapter.getSchema(rch.typeAdapter.typeToken.getType))
        case _ => None
      },
      responseTypeName = method.getReturnType.getName,
      responseSchema = if (allowedSources.contains(CommandSource.REST)) {
        Some(jsonConverter.getSchemaFor(method.getGenericReturnType).getOrElse(
          Schema.fromType(method.getGenericReturnType)))
      } else None,
      serviceTypeName = serviceTypeName
    ) :: Nil
}
