package com.edifecs.rest.spray.service

import java.io.File
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.regex.Pattern

import akka.actor.{ActorLogging, _}
import com.edifecs.core.configuration.Configuration
import com.edifecs.core.configuration.helper.{SystemVariables, TypesafeConfigKeys}
import com.edifecs.epp.isc.async.MessageFuture
import com.edifecs.epp.isc.command.{CommandSource, CommandStackFrame}
import com.edifecs.epp.isc.exception.MessageException
import com.edifecs.epp.isc.json.JsonArg
import com.edifecs.epp.isc.stream.{MessageStream, MessageStreamBuilder}
import com.edifecs.epp.isc.{Args, CommandCommunicator, Isc}
import com.edifecs.epp.security.SessionId
import com.google.gson.{GsonBuilder, JsonArray}
import spray.can.Http.RegisterChunkHandler
import spray.http.HttpHeaders._
import spray.http.MediaTypes._
import spray.http._
import spray.httpx.marshalling.BasicMarshallers._
import spray.httpx.marshalling.MetaMarshallers._
import spray.httpx.unmarshalling.FormDataUnmarshallers
import spray.json._
import spray.routing.directives.CachingDirectives._
import spray.routing.{HttpService, Route}

import scala.collection.JavaConversions._
import scala.collection.immutable.Map
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class ProxyActor extends Actor with RestService with ActorLogging {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing,
  // timeout handling or alternative handler registration
  def receive = runRoute(jsonMessageRoute) orElse {
    case RestService.RegisterShortcutUrl(url, serviceType, command, urlSuffix) =>
      log.warning("Registering shortcut URL: " + url) // TODO: Implement this.
      sender() ! true
    case RestService.UnregisterShortcutUrl(url) =>
      log.warning("Unregistering shortcut URL: " + url) // TODO: Implement this.
      sender() ! true

    // This is important to allow the upload of streaming messages
    // FIXME: Working on this
//    case r@HttpRequest(HttpMethods.POST, restServicePath, headers, entity: HttpEntity.NonEmpty, protocol) =>
//      // emulate chunked behavior for POST requests to this path
//      val parts = r.asPartStream()
//      val client = sender
//      val handler = context.actorOf(Props(new FileUploadHandler(client, parts.head.asInstanceOf[ChunkedRequestStart])))
//      parts.tail.foreach(handler !)

    case s@ChunkedRequestStart(HttpRequest(HttpMethods.POST, restServicePath, _, _, _)) =>
      val client = sender

      val security = CommandCommunicator.getInstance.getSecurityManager
      // Unregister the current session tied to the thread. This is a fail safe to make absolutely
      // sure that, even if the initialization of the Spray Container is done in the wrong order,
      // there is no scenario where a user session can exist before the execution of the
      // authentication process.
      security.getSessionManager.unregisterCurrentSession()

      // TODO: Add an error when this case fails
      val r = s.request.uri.toRelative.toString()
      if(r.startsWith("/rest/service/")) {
        val splitPath = r.split("/")
        if (splitPath.length == 5) {
          val service = splitPath(3)
          val command = splitPath(4)

          var cookie: Option[String] = None
          for (c <- s.request.cookies){
            if (c.name.equals(EimDirectives.loginCookieName)) {
              cookie = Option(c.content)
            }
          }
          val sessionId = EimDirectives.deserializeSessionId(cookie.get)

          val session = sessionId.getOrElse(security.getSessionManager.createAndRegisterNewSession())
          //          s.request.cookies(0).content

          val handler = context.actorOf(Props(new FileUploadHandler(client, s, service, command, session)))
          sender ! RegisterChunkHandler(handler)
        }
      }
  }

  private def findCookie(name: String): HttpHeader ⇒ Option[HttpCookie] = {
    case Cookie(cookies) ⇒ cookies.find(_.name == name)
    case _               ⇒ None
  }
}

// this trait defines our service behavior independently from the service actor
trait RestService extends HttpService with FormDataUnmarshallers {

  import com.edifecs.rest.spray.service.RestConstants._

  val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  val simpleCache = routeCache(maxCapacity = 1000, timeToIdle = Duration("30 min"))

  private final val httpDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz")

  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler
  implicit def executionContext = actorRefFactory.dispatcher

  protected lazy val numberRegex = Pattern.compile("^-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?$")

  protected val restServicePath = pathPrefix("rest" / "service" / Segment / Segment)
  protected val restServicePathWithSuffix = pathPrefix("rest" / "service" / Segment / Segment / Rest)
  protected val setupLoginPath = pathPrefix("rest" / "setup" / Segment)
  protected val restLoginPath = path("rest" / "login")
  protected val restCommandsPath = path("rest" / "commands")
  protected val rootPath = path( Rest )

  private def restServiceQueryStringRoute(
    method: String,
    service: String,
    command: String,
    suffix: Option[String]
  ): Route =
    EimDirectives.authenticateWithEim { id =>
      parameterMap { params =>
        val args = new Args(Isc.restMethodArg, method, Isc.jsonArg, true)
        params foreach { pair =>
          if (pair._2 == "true" || pair._2 == "false" || pair._2 == "null" ||
              numberRegex.matcher(pair._2).matches() ||
              pair._2.contains("\"") || pair._2.contains("{") || pair._2.contains("[")) {
            args.put(pair._1, new JsonArg(pair._2))
          } else {
            args.put(pair._1, new JsonArg(JsString(pair._2).toString()))
          }
        }
        suffix match {
          case Some(s) => sendCommand(service, command, args.and(Isc.urlSuffixArg, s), id)
          case None => sendCommand(service, command, args, id)
        }
      }
    }

  private def restServiceRequestBodyRoute(
    method: String,
    service: String,
    command: String,
    suffix: Option[String]
  ): Route =
    EimDirectives.authenticateWithEim { id =>
      headerValuePF {
        case `Content-Type`(ct) => ct.mediaType
      } {
        case `application/json` =>
          entity(as[String]) { json: String =>
            val args = Args.fromJson(
              JsObject(Map(Isc.requestBodyArg -> JsonParser(json))).toString()
            ).and(Isc.restMethodArg, method)
            suffix match {
              case Some(s) => sendCommand(service, command, args.and(Isc.urlSuffixArg, s), id)
              case None => sendCommand(service, command, args, id)
            }
          }
        case `application/x-www-form-urlencoded` =>
          entity(as[FormData]) { form: FormData =>
            val formMap = Map[String, String]() ++ form.fields
            val args = if (formMap contains jsonKey)
              Args.fromJson(formMap.get(jsonKey).get).and(Isc.restMethodArg, method)
            else
              new Args(Isc.restMethodArg, method)
            suffix match {
              case Some(s) => sendCommand(service, command, args.and(Isc.urlSuffixArg, s), id)
              case None => sendCommand(service, command, args, id)
            }
          }
        case mt: MediaType if mt.isMultipart =>
          entity(as[MultipartFormData]) { form: MultipartFormData =>
            val args = new Args()
            lazy val chunkSize = CommandCommunicator.getInstance.getConfig.getBytes(
              TypesafeConfigKeys.STREAM_CHUNK_SIZE)
            form.fields.filter(_.name.isDefined).foreach { field =>
              if (field.name.get == jsonKey) {
                args.putAll(Args.fromJson(field.entity.asString))
              } else {
                val builder = new MessageStreamBuilder
                field.headers.find(_ is "content-type").foreach(h =>
                  builder.httpContentType(h.value))
                field.entity.data.toChunkStream(chunkSize).foldLeft(MessageFuture(null))(
                  (future, data) =>
                    future andThen (builder chunk data.toByteString.compact andThen MessageFuture(null))
                  ) andThenDo builder.end() orCatch (builder.error(_))
                args.put(field.name.get, builder.toStream)
              }
            }
            suffix match {
              case Some(s) => sendCommand(service, command, args.and(Isc.urlSuffixArg, s), id)
              case None => sendCommand(service, command, args, id)
            }
          }
        case mediaType: MediaType =>
          respondWithStatus(StatusCodes.UnsupportedMediaType) {
            complete(method.toUpperCase + " requests must have one of the following media types:" +
              " application/json, application/x-www-form-urlencoded, multipart/form-data. Got " +
              mediaType.value + " instead.")
          }
      }
    }

  val jsonMessageRoute: Route =
    get {
      restServicePathWithSuffix { (service: String, command: String, suffix: String) =>
        restServiceQueryStringRoute("get", service, command, Some(suffix))
      } ~
      restServicePath { (service: String, command: String) =>
        restServiceQueryStringRoute("get", service, command, None)
      } ~
      setupLoginPath { (command: String) =>
        sendSetupCommand(command)
      } ~
      restLoginPath {
        respondWithStatus(StatusCodes.MethodNotAllowed) {
          complete("Login must be performed via POST.")
      }
      } ~
      restCommandsPath {
        complete {
          val results = new JsonArray
          for (
            specs <- CommandCommunicator.getInstance.getAvailableCommands.values;
            spec <- specs if spec.accessibleBy contains CommandSource.REST
          ) {
            results.add(spec.copy(
              url = Some(s"/rest/service/" + URLEncoder.encode(spec.serviceTypeName, "UTF-8") +
                "/" + URLEncoder.encode(spec.name, "UTF-8"))).toJson)
          }
          new GsonBuilder().setPrettyPrinting().create().toJson(results)
        }
      } ~
      staticContentRoute()
    } ~
    post {
        restServicePathWithSuffix { (service: String, command: String, suffix: String) =>
          restServiceRequestBodyRoute("post", service, command, Some(suffix))
        } ~
        restServicePath { (service: String, command: String) =>
          restServiceRequestBodyRoute("post", service, command, None)
        } ~
        setupLoginPath { (command: String) =>
          sendSetupCommand(command)
        } ~
        restLoginPath {
          entity(as[FormData]) { form: FormData =>
            val formMap = Map[String, String]() ++ form.fields

            if (formMap contains jsonKey) {
              val args = Args.fromJson(formMap.get(jsonKey).get)

              val d = args.get("domain").toString
              val o = args.get("organization").toString
              val u = args.get("username").toString
              val p = args.get("password").toString

              EimDirectives.loginToEim(d, o, u, p) {
                case Success(()) => complete( """{"success": true}""")
                case Failure(t) => t match {
                  case ex: MessageException =>
                    logger.debug(s"Login command threw ${ex.getOriginalExceptionClassName}.", ex)
                    try {
                      completeWithError(ex.getOriginalException, ex.getCommandStack)
                    } catch {
                      case _: ClassCastException => completeWithMessageException(ex)
                    }
                  case ex: Exception =>
                    logger.debug(s"Login command threw ${ex.getClass.getName}.", ex)
                    completeWithError(ex, Array.empty)
                }
              }
            } else {
              (for (d <- formMap get "domain";
                    o <- formMap get "organization";
                    u <- formMap get "username";
                    p <- formMap get "password") yield
                EimDirectives.loginToEim(d, o, u, p) {
                  case Success(()) => complete( """{"success": true}""")
                  case Failure(ex) => completeWithError(ex, Array.empty)
                }) getOrElse
                respondWithStatus(StatusCodes.BadRequest) {
                  complete("Login requires both 'username' and 'password' fields.")
                }
            }
          }
        }

    } ~
    put {
      // PUT, like POST, uses the request body to pass parameters.
      restServicePathWithSuffix { (service: String, command: String, suffix: String) =>
        restServiceRequestBodyRoute("put", service, command, Some(suffix))
      } ~
      restServicePath { (service: String, command: String) =>
        restServiceRequestBodyRoute("put", service, command, None)
      }
    } ~
    delete {
      // DELETE, like GET, uses a query string to pass parameters.
      restServicePathWithSuffix { (service: String, command: String, suffix: String) =>
        restServiceQueryStringRoute("delete", service, command, Some(suffix))
      } ~
      restServicePath { (service: String, command: String) =>
        restServiceQueryStringRoute("delete", service, command, None)
      }
    }

  private def staticContentRoute(): Route = {
    staticConfigurationDirectoryContentRoute() ~
    staticAppsDirectoryContentRoute()
  }

  private def staticConfigurationDirectoryContentRoute(): Route = {
    // FIXME: Remove the global ui context
    var route = getFromDirectory("junkpath")

    val config = CommandCommunicator.getInstance.getConfig
    if (config.hasPath("dev.static.content.provider")) {
      val c = config.getConfig("dev.static.content.provider")
      for (entry <- c.entrySet()) {

        // Manually strip out ""'s if they made it through the parsing
        val path = new File(entry.getValue.unwrapped().toString.replaceAll("\"", ""))
        val context = entry.getKey.replaceAll("\"", "")

        if (!path.exists()) {
          logger.warn(s"Unable to load static web content from path: ${path}")
        } else {
          // Register the root path
          route = route ~ pathPrefix(context) {
            registerIndexFiles(path, path, route) ~
            getFromDirectory(path.getAbsolutePath)
          }
        }
      }
    }
    route
  }

  private def staticAppsDirectoryContentRoute(): Route = {
    // FIXME: Remove the global ui context
    var route = getFromDirectory("junkpath")
    for (
      p: File <- Configuration.getHtmlDirectories(SystemVariables.SERVICE_MANAGER_ROOT_PATH)
    ) {

      route = route ~ registerIndexFiles(p, p, route)

      // Register the root path
      route = route ~ getFromDirectory(p.getAbsolutePath)

    }
    route
  }

  // Look for index.html files to load automatically
  def registerIndexFiles(pt: File, root: File, ro: Route): Route = {
    var route = ro
    for (file: File <- pt.listFiles()) {
      if (file.isDirectory) {
          route = registerIndexFiles(file, root, route)
      }
      if(file.isFile && (file.getName.equals("index.html") || file.getName.equals("index.htm"))) {
        val pathdiff = file.getParentFile.getAbsolutePath.replace(root.getAbsolutePath, "").replaceAll("\\\\", "/").replaceFirst("/", "")
        if(pathdiff.equals("")) {
          route = route ~
            pathPrefix("") {
              pathEndOrSingleSlash {
                getFromFile(file.getAbsolutePath)
              }
            }
        } else {
          route = route ~
            pathPrefix(separateOnSlashes(pathdiff)) {
              pathSingleSlash {
                getFromFile(file.getAbsolutePath)
              } ~
              pathEnd {
                redirect( "/" + pathdiff + "/", StatusCodes.MovedPermanently)
              }
            }
        }
      }
    }
    route
  }

  def sendSetupCommand(command: String): Route = {
    val c = CommandCommunicator.getInstance
    try {
      val address = c.getAddress
      // TODO: Make this ASync
      c.sendSync(address, command) match {
        case response: String =>
          complete(JsObject(Map(
            "success" -> JsBoolean(true),
            "data" -> JsonParser(response)
          )).toString())
        case null =>
          complete(JsObject(Map(
            "success" -> JsBoolean(true),
            "data" -> JsNull
          )).toString())
        case r: Serializable =>
          completeWithMessageException(new MessageException(
            "Got non-JSON response of type " + r.getClass.getName))
      }
    } catch {
      // TODO: Change the HTTP status code based on the type of error.
      // TODO: Create an Edifecs-specific error class with an error code and HTTP status.
      case ex: MessageException =>
        logger.debug(s"Setup command '$command' threw ${ex.getOriginalExceptionClassName}.", ex)
        respondWithStatus(StatusCodes.InternalServerError) {
          try {
            completeWithError(ex.getOriginalException, ex.getCommandStack)
          } catch {
            case _: ClassCastException => completeWithMessageException(ex)
          }
        }
      case ex: Exception =>
        logger.debug(s"Setup command '$command' threw ${ex.getClass.getName}.", ex)
        respondWithStatus(StatusCodes.InternalServerError) {
          completeWithError(ex, Array.empty)
        }
    }
  }

  def sendCommand(service: String, command: String, params: Args, session: SessionId): Route = {
    val c = CommandCommunicator.getInstance
    c.getSecurityManager.getSessionManager.registerCurrentSession(session)
    val address = c.getAddressRegistry.getAddressForServiceTypeName(service)
    onComplete(c.send(address, command, params).asScalaFuture) {
      case Success(response) =>
        response match {
          case stream: MessageStream =>
            optionalHeaderValueByName("If-Modified-Since") { ifModifiedSince =>
              ifModifiedSince flatMap { str => try {
                val date = httpDateFormat.parse(str)
                if (stream.lastModifiedTimestamp > date.getTime) {
                  Some(respondWithStatus(StatusCodes.NotModified)(complete("")))
                } else None
              } catch {
                case ex: Exception => None
              }
              } getOrElse {
                respondWithLastModifiedHeader(stream.lastModifiedTimestamp) {
                  respondWithMediaType(MediaType.custom(stream.httpContentType)) {
                    respondWithHeader(HttpHeaders.`Content-Disposition`.apply("attachment", Map("filename" -> stream.filename.getOrElse { command }))) {
                      complete(stream.toScalaStream)
                    }
                  }
                }
              }
            }
          case response: String =>
            respondWithHeader(`Content-Type`(`text/html`)) {
              complete(JsObject(Map(
                "success" -> JsBoolean(true),
                "data" -> JsonParser(response)
              )).toString())
            }
          case null =>
            respondWithHeader(`Content-Type`(`text/html`)) {
              complete(JsObject(Map(
                "success" -> JsBoolean(true),
                "data" -> JsNull
              )).toString())
            }
          case r: Serializable =>
            completeWithMessageException(new MessageException(
              "Got non-JSON response of type " + r.getClass.getName))
        }

      case Failure(t) => t match {
        case ex: MessageException =>
          logger.debug(s"Service '$service', command '$command' threw ${ex.getOriginalExceptionClassName}.", ex)
          respondWithStatus(StatusCodes.InternalServerError) {
            completeWithMessageException(ex)
          }
        case ex: Exception =>
          logger.debug(s"Service '$service', command '$command' threw ${ex.getClass.getName}.", ex)
          respondWithStatus(StatusCodes.InternalServerError) {
            completeWithError(ex, Array.empty)
          }

      }
    }
  }


  private final val messageExceptionRegex = Pattern.compile(raw"^<[\w.]+> ([^\n]+)")

  private def completeWithMessageException(ex: MessageException) = {
    complete(JsObject(Map(
      "success" -> JsBoolean(false),
      "error" -> JsObject(Map(
        "message" -> JsString(ex.getOriginalMessage),
        "class" -> JsString(ex.getOriginalExceptionClassName)
        //        "stackTrace" -> JsArray(
        //          ex.getCommandStack.toList.map(e => JsString("from command " + e.toString)) ++
        //          ex.getStackTrace.toList.map(e => JsString(e.toString)))))
      ))
    )).toString())
  }

  private def completeWithError(ex: Throwable, stack: Array[CommandStackFrame]) =
    complete(JsObject(Map(
      "success" -> JsBoolean(false),
      "error" -> JsObject(Map(
        "message" -> JsString(ex.getLocalizedMessage),
        "class" -> JsString(ex.getClass.getName)
//        "stackTrace" -> JsArray(
//          stack.toList.map(e => JsString("from command " + e.toString)) ++
//          ex.getStackTrace.toList.map(e => JsString(e.toString))
      ))
    )).toString())
}

object RestService {
  case class RegisterShortcutUrl(
    url: String,
    serviceType: String,
    command: String,
    urlSuffix: String = null
  ) extends PossiblyHarmful

  case class UnregisterShortcutUrl(
    url: String
  ) extends PossiblyHarmful
}

