package com.edifecs.epp.isc.core.command

import java.io.{InputStream, Serializable}
import java.lang.reflect.{Modifier, Type}
import java.util.EnumSet

import com.edifecs.epp.isc.ICommandCommunicator
import com.edifecs.epp.isc.command.{CommandMessage, CommandSource}
import com.edifecs.epp.isc.exception.{InvalidCommandException, InvalidSourceException}
import com.edifecs.epp.isc.json.{JsonArg, JsonConverter}
import com.edifecs.epp.isc.stream.MessageStream
import com.edifecs.epp.security.SessionId
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import org.slf4j.Logger

import scala.collection.JavaConversions._
import scala.collection.immutable._
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

/**
 * A handler for a single command.
 * 
 * @author c-adamnels
 */
trait CommandRef extends CommandReceiver {

  import com.edifecs.epp.isc.Isc._
  import com.edifecs.epp.isc.core.command.CommandRef._

  protected val logger: Logger
  val name: String
  val arguments: Seq[Argument]
  val allowedSources: EnumSet[CommandSource]
  val jsonConverter: JsonConverter
  val nullSessionAllowed: Boolean

  private lazy val streamArgIndex = arguments.indexWhere(_.isInstanceOf[StreamArgument])
  private lazy val senderArgIndex = arguments.indexWhere(_ == SenderArgument)
  private lazy val argumentMap: Map[String, BasicArgument] =
    Map(arguments.flatMap {
      case a: BasicArgument => Seq(a.name -> a)
      case a: StreamArgument => Seq(a.name -> BasicArgument(
        a.name,
        a.description,
        classOf[MessageStream],
        classOf[MessageStream],
        required = true
      ))
      case _ => Seq.empty
    }: _*)

  /**
   * Parses a single command message (with the same `name` as this
   * command), and calls this command's method with the parameters contained
   * in the message.
   * 
   * @param command The command message to parse.
   * @return The return value of the method call triggered by the command.
   * @throws InvalidSourceException If this handler does not accept commands
   *     from the given source.
   * @throws InvalidCommandException If the command does not have the
   *     required names or types of parameters for this handler.
   * @throws SecurityException If the command's sender does not have the
   *     permissions necessary to execute the command.
   * @throws Exception If any exception occurs while executing the command, it
   *     will be rethrown.
   */
  override def receiveCommand(
    communicator: ICommandCommunicator,
    command: CommandMessage
  )(
   implicit context: ExecutionContext
  ): Future[Serializable] = try {
    // Validate the command's source.
    if (!(allowedSources contains command.source)) {
      throw new InvalidSourceException(command, command.source,
        s"The command '$name' is not accessible through ${command.source.getFriendlyName}.")
    }

    // Validate the argument list.
    val allArgs = argumentMap.keys
    val reqArgs = mutable.Set[String]()
    argumentMap foreach { e =>
      val (key, value) = e
      if (value.required) reqArgs += key
    }

    // Convert the arguments to an array.
    val mappedArgs = new Array[Object](arguments.length)

    command.args foreach { e =>
      var (key, value) = (e._1, e._2.asInstanceOf[Serializable])
      if (allArgs contains key) {
        if (reqArgs contains key) reqArgs -= key
        val arg = argumentMap.get(key).get
        var argType: Class[_] = arg.`class`
        var argIndex = arguments.indexOf(arg)
        if (argIndex == -1) argIndex = streamArgIndex

        // Parse any serialized JSON data.
        value match {
          case j: JsonArg if argType != classOf[JsonArg] =>
            value = parseJsonArg(key, j.getJson, arg)
          case _ =>
        }

        // spl handler, when maxRecords is -1 from UI replace -1 with Integer Max.
        if (key.equalsIgnoreCase("recordCount") && value == -1L) {
          value = Integer.MAX_VALUE.toLong
        }

        if (argType.isPrimitive) {
          if (value == null) {
            throw new InvalidCommandException(command,
              s"The argument '${arg.name}' for the command '$name' is of the primitive type" +
              s" ${argType.getName}, but received a null value.")
          }
          argType = primitivesToObjects.get(argType)
        }

        mappedArgs(argIndex) = value.asInstanceOf[AnyRef]

        if (mappedArgs(argIndex) != null &&
            !argType.isAssignableFrom(mappedArgs(argIndex).getClass)) {
          if (argIndex == streamArgIndex) {
            throw new InvalidCommandException(command,
              s"The argument '${arguments(streamArgIndex).asInstanceOf[StreamArgument].name}'" +
              s" for the command '${command.name}' is a legacy stream argument (@StreamArg), and" +
              s" must have a value of type ${classOf[MessageStream].getName}, but has a value of" +
              " type " + Option(mappedArgs(streamArgIndex)).map(_.getClass.getName).orNull + "")
          } else {
            throw new InvalidCommandException(command,
              s"The argument '${arg.name}' for the command '$name' is of type ${argType.getName}" +
              s", but received a value of type ${mappedArgs(argIndex).getClass.getName}.")
          }
        }

        // Convert legacy stream arguments.
        if (argIndex == streamArgIndex)
          mappedArgs(argIndex) = value.asInstanceOf[MessageStream].toInputStream
      } else {
        if (!key.startsWith(extraArgPrefix)) {
          // #7887 isSubjectAuthenticated method misleading message
          logger.debug(s"Command '$name' received unrecognized argument '$key'; ignoring.")
        }
      }
    }
    // Add the sender, if necessary.
    if (senderArgIndex >= 0) mappedArgs(senderArgIndex) = command.sender

    // Throw an exception if any required arguments are missing.
    if (reqArgs.size == 1) {
      throw new InvalidCommandException(command,
        s"The required argument '${reqArgs.head}' for command '$name' is missing.")
    } else if (reqArgs.size > 1) {
      val list = reqArgs.map("'" + _ + "'").mkString(", ")
      throw new InvalidCommandException(command,
        s"Multiple required arguments ($list) for command '$name' are missing.")
    }

    // Fill null primitives with default values, to prevent NPEs.
    (0 until mappedArgs.length) foreach { i =>
      if (mappedArgs(i) == null) {
        arguments.get(i) match {
          case a: BasicArgument =>
            mappedArgs(i) = getDefaultFor(a.`class`).asInstanceOf[Object]
          case _ =>
            // Do nothing.
        }
      }
    }

    // Execute the command itself.
    val isJson = command.args contains jsonArg
    executeCommand(communicator, mappedArgs, Option(command.session)) map {
      case is: InputStream => MessageStream.fromInputStream(is) // Handle legacy stream API
      case ms: MessageStream => ms
      case r: Serializable =>
        if (isJson) jsonConverter.toJson(r).asInstanceOf[Serializable]
        else r
      case null => null
    }
  } catch {
    case e: Exception => Future(throw e)
  }

  final override def respondsTo(command: String): Boolean =
    command == name

  protected def executeCommand(
    cc: ICommandCommunicator,
    arguments: Array[Object],
    userSession: Option[SessionId]
  )(
    implicit context: ExecutionContext
  ): Future[Serializable]

  private def parseJsonArg(key: String, value: JsonElement, arg: BasicArgument): Serializable = {
    val argType = arg.`class`
    val resultType =
      if (argType.isPrimitive) {
        primitivesToObjects.get(argType)
      } else if (argType.isInterface || Modifier.isAbstract(argType.getModifiers)) {
        classOf[java.lang.Object]
      } else {
        TypeToken.get(arg.`type`).getType
      }
    jsonConverter.fromJson(value, resultType).asInstanceOf[Serializable]
  }
}

object CommandRef {

  final val primitivesToObjects = Map(
    java.lang.Long.TYPE      -> classOf[java.lang.Long],
    java.lang.Integer.TYPE   -> classOf[java.lang.Integer],
    java.lang.Short.TYPE     -> classOf[java.lang.Short],
    java.lang.Byte.TYPE      -> classOf[java.lang.Byte],
    java.lang.Double.TYPE    -> classOf[java.lang.Double],
    java.lang.Float.TYPE     -> classOf[java.lang.Float],
    java.lang.Boolean.TYPE   -> classOf[java.lang.Boolean],
    java.lang.Character.TYPE -> classOf[java.lang.Character]
  )
  
  /**
   * Returns a default value for any type, including primitive types. Will
   * return `null` for any type that extends `Object`, but will return 0 or
   * `false` as necessary for primitive types to prevent {@link
   * NullPointerException}s.
   * 
   * @param t The type to return a default value for.
   */
  def getDefaultFor(t: Class[_]): Any = t match {
    case java.lang.Boolean.TYPE => false
    case java.lang.Byte.TYPE => 0.toByte
    case java.lang.Short.TYPE => 0.toShort
    case java.lang.Integer.TYPE => 0
    case java.lang.Long.TYPE => 0L
    case java.lang.Float.TYPE => 0.0f
    case java.lang.Double.TYPE => 0.0
    case java.lang.Character.TYPE => '\0'
    case _ => null
  }

  sealed trait Argument extends Serializable

  case class BasicArgument(
    name:        String,
    description: String,
    `type`:      Type,
    `class`:     Class[_],
    required:    Boolean
  ) extends Argument

  case class StreamArgument(
    name:        String,
    description: String
  ) extends Argument

  case object SenderArgument extends Argument
}
