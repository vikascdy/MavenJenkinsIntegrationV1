package com.edifecs.epp.isc.core.command

import java.io.InputStream
import java.util.{EnumSet, Properties}
import java.lang.annotation.Annotation
import java.lang.reflect.{InvocationTargetException, Method, Type}

import scala.collection._
import scala.concurrent.duration._

import org.slf4j.LoggerFactory

import com.edifecs.epp.isc.{annotations => a, CommandCommunicator, Address, Isc, ICommandCommunicator}
import com.edifecs.epp.isc.async.MessageFuture
import com.edifecs.epp.isc.exception._
import com.edifecs.epp.security.ISecurityManager

import Isc._
import CommandRef._
import com.edifecs.epp.isc.json.JsonConverter
import com.edifecs.epp.isc.exception.HandlerConfigurationException
import com.edifecs.epp.isc.command.CommandSource

object CommandAnnotationProcessor {

  private lazy val logger = LoggerFactory.getLogger(getClass)

  @throws(classOf[HandlerConfigurationException])
  def processAnnotatedCommandHandler(
    handler: AbstractCommandHandler,
    commandCommunicator: Option[_ <: CommandCommunicator with Isc] = None
  ): Seq[CommandRef] =
    processAnnotatedCommandHandler(handler, commandCommunicator, null)

  @throws(classOf[HandlerConfigurationException])
  def processAnnotatedCommandHandler(
    handler: AbstractCommandHandler,
    commandCommunicator: Option[_ <: CommandCommunicator with Isc],
    serviceTypeName: String
  ): Seq[CommandRef] = {
    val cls = getInterfaceWithAnnotation(handler, classOf[a.CommandHandler]) getOrElse {
      throw new HandlerConfigurationException(
       s"Cannot register an instance of ${handler.getClass.getCanonicalName} as a command" +
        " handler: neither this class nor any of its interfaces is annotated with" +
       s" ${classOf[a.CommandHandler].getCanonicalName}.")
    }
    logger.debug("Processing command handler from annotations on class {}.", cls.getName)
    val globalAllowedSources = EnumSet.noneOf(classOf[CommandSource])
    var globalNullSessionAllowed = false
    var converter: Option[JsonConverter] = None
    var namespace: Option[String] = None
    globalAllowedSources.add(CommandSource.JGROUPS)

    // Parse class-level annotations.
    cls.getAnnotations foreach {
      case ann: a.JGroups =>
        if (!ann.enabled) globalAllowedSources.remove(CommandSource.JGROUPS)
      case ann: a.Rest =>
        if (ann.enabled) globalAllowedSources.add(CommandSource.REST)
      case ann: a.JsonSerialization =>
        try {
          if (ann.enabled) converter = Some(new JsonConverter(ann.adapters))
        } catch {
          case ex: Exception =>
            throw new HandlerConfigurationException(
             s"The message handler class '${getClass.getCanonicalName}' is wrongly annotated" +
             s" with @${classOf[a.JsonSerialization].getName}", ex)
        }
      case ann: a.CommandHandler =>
        namespace = Some(ann.namespace)
      case ann: a.NullSessionAllowed =>
        globalNullSessionAllowed = true
      case _ => // Do nothing with unrecognized annotations.
    }
    if (converter.isEmpty) {converter = Some(new JsonConverter(null))}
    if (namespace.isEmpty) {namespace = Some(defaultNamespace)}

    // Scan the class's methods and auto-discover annotated command methods.
    def methodsOf(cls: Class[_]): Array[Method] =
      cls.getDeclaredMethods ++
      Option(cls.getSuperclass).toArray.flatMap(methodsOf(_)) ++
      cls.getInterfaces.flatMap(methodsOf(_))

    val commands = methodsOf(handler.getClass).filter { m =>
      m.isAnnotationPresent(classOf[a.AsyncCommand]) ||
      m.isAnnotationPresent(classOf[a.SyncCommand]) ||
      m.isAnnotationPresent(classOf[a.Command])
    }.map(method => processAnnotatedCommandMethod(
      method = method,
      methodOwner = handler,
      globalAllowedSources = globalAllowedSources,
      globalNullSessionAllowed = globalNullSessionAllowed,
      securityManager = handler.getReceivingSecurityManager,
      converter = converter.get,
      namespace = namespace.get,
      serviceTypeName = serviceTypeName))

    if (commands.isEmpty) {
      throw new HandlerConfigurationException(
        s"The command handler interface '${cls.getCanonicalName}' does not contain any command" +
        s" handler methods annotated with @${classOf[a.Command].getSimpleName}. If this is" +
         " intentional, add at least one no-op method with the" +
        s" @${classOf[a.Command].getSimpleName} annotation to the interface to suppress this" +
         " error.")
    } else {
      // If we were given a CommandCommunicator, initialize the handler with it
      commandCommunicator.map(c => handler.initialize(c, c))
      commands
    }
  }

  @throws(classOf[HandlerConfigurationException])
  def processAnnotatedCommandMethod(
    method: Method,
    methodOwner: Object,
    globalAllowedSources: EnumSet[CommandSource] = EnumSet.noneOf(classOf[CommandSource]),
    globalNullSessionAllowed: Boolean = false,
    securityManager: => Option[ISecurityManager] = None,
    converter: JsonConverter = new JsonConverter(null),
    namespace: String = defaultNamespace,
    serviceTypeName: String = null
  ): CommandRef = {

    @inline def fullMethodName: String =
      s"${methodOwner.getClass.getCanonicalName}.${method.getName}()"

    val nullSessionAllowed = globalNullSessionAllowed ||
                             method.isAnnotationPresent(classOf[a.NullSessionAllowed])
    val allowedSources = EnumSet.copyOf(globalAllowedSources)
    var name = method.getName

    // Check for and parse extra annotations.
    def annotationException =
      new HandlerConfigurationException(
        s"Command method '$fullMethodName' must have exactly one of the following annotations: " +
        Seq(classOf[a.AsyncCommand], classOf[a.SyncCommand], classOf[a.Command]).map { ann =>
          "@" + ann.getSimpleName
        }.mkString(", ") + ".")
    var async = false
    var description = "No description available."
    var timeout: Option[Duration] = None
    var commandAnnotationFound = false

    method.getAnnotations foreach {
      case ann: a.AsyncCommand =>
        if (commandAnnotationFound) {throw annotationException}
        commandAnnotationFound = true
        async = true
        timeout = Some(ann.timeoutMs milliseconds)
        if (!ann.name.isEmpty) {name = ann.name}
        description = ann.description
        if (ann.root) {name = ""}
      case ann: a.SyncCommand =>
        if (commandAnnotationFound) throw annotationException
        commandAnnotationFound = true
        if (!ann.name.isEmpty) {name = ann.name}
        description = ann.description
        if (ann.root) {name = ""}
      case ann: a.Command =>
        if (commandAnnotationFound) throw annotationException
        commandAnnotationFound = true
        if (!ann.name.isEmpty) {name = ann.name}
        description = ann.description
      case ann: a.JGroups =>
        if (ann.enabled) {allowedSources.add(CommandSource.JGROUPS)}
        else             {allowedSources.remove(CommandSource.JGROUPS)}
      case ann: a.Rest =>
        if (ann.enabled) {allowedSources.add(CommandSource.REST)}
        else             {allowedSources.remove(CommandSource.REST)}
      case _ => // Do nothing with unrecognized annotations.
    }

    if (!namespace.isEmpty) {
      if (name == "") name = namespace
      else name = namespace + commandSeparator + name
    }
    if (async && method.getReturnType != classOf[MessageFuture[_]]) {
      throw new HandlerConfigurationException(
        s"Command method '$fullMethodName` is asynchronous, but returns a value of type" +
        s" ${method.getReturnType}. Asynchronous command methods must return values of type" +
        s" ${classOf[MessageFuture[_]].getCanonicalName}.")
    }
    logger.debug("Processing command '{}' from annotations on method {}.{}.",
      name, method.getDeclaringClass.getSimpleName, method.getName)

    // Parse the method's arguments' annotations, and verify that all arguments
    // are annotated with @Arg, @StreamArg, or @Sender.
    var streamArgIndex = -1
    var senderIndex = -1
    val args = mutable.Buffer[Argument]()
    val argTypes = method.getGenericParameterTypes
    val argClasses = method.getParameterTypes
    val argAnnotations = method.getParameterAnnotations
    (0 until argTypes.length) foreach { i =>
      val (atype, acls, annotations) = (argTypes(i), argClasses(i), argAnnotations(i))
      var myAnnotation: Option[Annotation] = None
      @inline def setMyAnnotation(ann: Annotation):Unit = {
        if (myAnnotation.isDefined) {
          throw new HandlerConfigurationException(
            s"Argument #${i+1} of method '$fullMethodName' is annotated with both" +
            s" ${myAnnotation.get} and $ann. Only one of these annotations may be used at a time" +
             " on the same argument.")
        } else {
          myAnnotation = Some(ann)
        }
      }
      annotations foreach {
        case ann: a.Arg =>
          setMyAnnotation(ann)
          if (acls == classOf[InputStream]) {
            throw new HandlerConfigurationException(
              s"Argument #${i+1} of method '$fullMethodName' is annotated with" +
              s" @${classOf[a.Arg].getSimpleName}, but is of type java.io.InputStream. Stream" +
              s" arguments should be annotated with @${classOf[a.StreamArg].getSimpleName}.")
          }
          args += BasicArgument(
            name        = ann.name,
            description = ann.description,
            `type`      = atype,
            `class`     = acls,
            required    = ann.required)
        case ann: a.StreamArg =>
          setMyAnnotation(ann)
          if (acls != classOf[InputStream]) {
            throw new HandlerConfigurationException(
              s"Argument #${i+1} of method '$fullMethodName' is annotated with" +
              s" @${classOf[a.StreamArg].getSimpleName}, but is not of type" +
              s" ${classOf[InputStream].getCanonicalName}.")
          }
          if (streamArgIndex >= 0) {
            throw new HandlerConfigurationException(
              s"Two or more arguments to the method '$fullMethodName' are annotated with" +
              s" ${classOf[a.StreamArg].getSimpleName}. Only one stream argument is allowed per" +
               " method.")
          } else {
            streamArgIndex = i
          }
          args += StreamArgument(ann.name, ann.description)
        case ann: a.Sender =>
          setMyAnnotation(ann)
          if (acls != classOf[Address]) {
            throw new HandlerConfigurationException(
              s"Argument #${i+1} of method '$fullMethodName' is annotated with" +
              s" @${classOf[a.Sender].getSimpleName}, but is not of type" +
              s" ${classOf[Address].getCanonicalName}.")
          } else {
            senderIndex = i
          }
          args += SenderArgument
        case _ =>
          // Do nothing with unrecognized annotations.
      }
      if (myAnnotation.isEmpty) {
        throw new HandlerConfigurationException(
          s"The method '$fullMethodName' has at least one argument (Argument #${i+1}, of type" +
          s" ${acls.getCanonicalName}) that is not annotated with either" +
          s" @${classOf[a.Arg].getSimpleName}, @${classOf[a.StreamArg].getSimpleName}, or" +
          s" @${classOf[a.Sender].getSimpleName}. Annotations are required for all arguments to" +
           " command handler methods.")
      }
    }

    new MethodCommandRef(
      name = name,
      method = method,
      methodOwner = methodOwner,
      arguments = args.toList,
      description = description,
      async = async,
      timeout = timeout,
      allowedSources = allowedSources,
      securityManager = securityManager,
      jsonConverter = converter,
      nullSessionAllowed = nullSessionAllowed,
      serviceTypeName = serviceTypeName)
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

