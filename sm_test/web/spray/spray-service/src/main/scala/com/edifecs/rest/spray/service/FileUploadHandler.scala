package com.edifecs.rest.spray.service

import java.io.{File, FileInputStream, FileOutputStream, InputStream}
import java.util.regex.Pattern

import akka.actor._
import com.edifecs.core.configuration.helper.{SystemVariables, TypesafeConfigKeys}
import com.edifecs.epp.isc.exception.MessageException
import com.edifecs.epp.isc.stream.MessageStream
import com.edifecs.epp.isc.{Args, CommandCommunicator}
import com.edifecs.epp.security.SessionId
import org.jvnet.mimepull.{MIMEMessage, MIMEPart}
import spray.http.HttpHeaders.{RawHeader, _}
import spray.http.MediaTypes._
import spray.http._
import spray.http.parser.HttpParser
import spray.io.CommandWrapper
import spray.json.{JsBoolean, JsObject, JsString, JsonParser}

import scala.annotation.tailrec
import scala.concurrent.Await
import scala.concurrent.duration._

class FileUploadHandler(client: ActorRef, start: ChunkedRequestStart, service: String, command: String, sessionId: SessionId) extends Actor with ActorLogging {
  import start.request._

  val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  val c = CommandCommunicator.getInstance

  // Authenticate the Upload Handler

  // TODO: Remove this need and stream directly through ISC
  // Temporally Store the file
  client ! CommandWrapper(SetRequestTimeout(Duration.Inf)) // cancel timeout
  val tmpDir = new File(SystemVariables.TEMP_PATH + "temp")
  tmpDir.mkdirs()
  val tmpFile = File.createTempFile("chunked-receiver", ".tmp", tmpDir)
  tmpFile.deleteOnExit()
  val output = new FileOutputStream(tmpFile)
  val Some(HttpHeaders.`Content-Type`(ContentType(multipart: MultipartMediaType, _))) = header[HttpHeaders.`Content-Type`]
  val boundary = multipart.parameters("boundary")

  log.info(s"Got start of chunked request $method $uri with multipart boundary '$boundary' writing to $tmpFile")
  var bytesWritten = 0L

  def receive = {
    case c: MessageChunk =>
      log.debug(s"Got ${c.data.length} bytes of chunked request $method $uri")

      output.write(c.data.toByteArray)
      bytesWritten += c.data.length

    case e: ChunkedMessageEnd =>
      log.info(s"Got end of chunked request $method $uri")
      output.close()
      client ! HttpResponse(status = 200, entity = renderResult())
      client ! CommandWrapper(SetRequestTimeout(2.seconds)) // reset timeout to original value
      tmpFile.delete()
      context.stop(self)
  }

  import scala.collection.JavaConverters._
  def renderResult(): HttpEntity = {
    val message = new MIMEMessage(new FileInputStream(tmpFile), boundary)

    // Get and Stream the proper message chunks
    val parts = message.getAttachments.asScala.toSeq

    val args = new Args()
    lazy val chunkSize = CommandCommunicator.getInstance.getConfig.getBytes(
      TypesafeConfigKeys.STREAM_CHUNK_SIZE)

    parts.map { part =>
      val name = nameForPart(part).getOrElse("<unknown>")
      val fileName = fileNameForPart(part).getOrElse("<unknown>")
      name match {
        case RestConstants.jsonKey =>
          val data = scala.io.Source.fromInputStream(part.readOnce()).mkString
          args.putAll(Args.fromJson(data))
          logger.debug(s"Data found: ${data}")
        case _ =>
          val stream = part.readOnce()
          logger.debug(s"Stream found for file '${fileName}'")
          args.put(name, MessageStream.fromInputStream(stream))
      }
    }

    sendCommand(service, command, args, sessionId)
  }
  def fileNameForPart(part: MIMEPart): Option[String] =
    for {
      dispHeader <- part.getHeader("Content-Disposition").asScala.toSeq.lift(0)
      Right(disp: `Content-Disposition`) = HttpParser.parseHeader(RawHeader("Content-Disposition", dispHeader))
      name <- disp.parameters.get("filename")
    } yield name

  def nameForPart(part: MIMEPart): Option[String] =
    for {
      dispHeader <- part.getHeader("Content-Disposition").asScala.toSeq.lift(0)
      Right(disp: `Content-Disposition`) = HttpParser.parseHeader(RawHeader("Content-Disposition", dispHeader))
      name <- disp.parameters.get("name")
    } yield name

  def sizeOf(is: InputStream): Long = {
    val buffer = new Array[Byte](65000)

    @tailrec def inner(cur: Long): Long = {
      val read = is.read(buffer)
      if (read > 0) inner(cur + read)
      else cur
    }

    inner(0)
  }

  def sendCommand(service: String, command: String, params: Args, session: SessionId): HttpEntity = {
    try {
      c.getSecurityManager.getSessionManager.registerCurrentSession(session)

      val address = c.getAddressRegistry.getAddressForServiceTypeName(service)
      val result = Await.result(c.send(address, command, params).asScalaFuture, 5 minutes)

      result match {
        // TODO: Handle download of a stream based on a large input
        //    case stream: MessageStream =>
        //      HttpEntity(`application/octet-stream`, stream.toScalaStream.)
        case response: String =>
          HttpEntity(`text/html`, JsObject(Map(
            "success" -> JsBoolean(true),
            "data" -> JsonParser(response)
          )).toString())
        case null =>
          HttpEntity(`text/html`, JsObject(Map(
            "success" -> JsBoolean(true)
          )).toString())
        case r: Serializable =>
          completeWithMessageException(new MessageException(
            "Got non-JSON response of type " + r.getClass.getName))
        case _ =>
          completeWithMessageException(new MessageException(
            "Got non-JSON response"))
      }
    } catch {
      case e: Exception => completeWithError(e)
    }
  }

  // TODO: Duplicate Class Logic
  private final val messageExceptionRegex = Pattern.compile(raw"^<[\w.]+> ([^\n]+)")

  private def completeWithMessageException(ex: MessageException): HttpEntity = {
    val actualMessage = messageExceptionRegex.matcher(ex.getMessage).group(1)
    HttpEntity(`text/html`, JsObject(Map(
      "success" -> JsBoolean(false),
      "error" -> JsObject(Map(
        "message" -> JsString(actualMessage),
        "class" -> JsString(ex.getOriginalExceptionClassName)
        //        "stackTrace" -> JsArray(
        //          ex.getCommandStack.toList.map(e => JsString("from command " + e.toString)) ++
        //          ex.getStackTrace.toList.map(e => JsString(e.toString)))))
      ))
    )).toString())
  }

  private def completeWithError(ex: Throwable): HttpEntity =
    HttpEntity(`text/html`, JsObject(Map(
      "success" -> JsBoolean(false),
      "error" -> JsObject(Map(
        "message" -> JsString(ex.getMessage),
        "class" -> JsString(ex.getClass.getName)
        //        "stackTrace" -> JsArray(
        //          stack.toList.map(e => JsString("from command " + e.toString)) ++
        //          ex.getStackTrace.toList.map(e => JsString(e.toString))
      ))
    )).toString())
}