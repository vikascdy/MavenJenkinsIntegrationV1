package com.edifecs.rest.spray.service

import akka.actor.Actor
import spray.can.Http
import spray.json._
import spray.http._
import HttpMethods._
import StatusCodes._
import com.edifecs.epp.isc.CommandCommunicator
import akka.actor.ActorLogging

class MessageProxyActor extends Actor with ActorLogging {
  import context.dispatcher // ExecutionContext for scheduler
  import Uri._
  import Uri.Path._

  def jsonResponseEntity = HttpEntity(
    contentType = ContentTypes.`application/json`,
    string = JsObject("message" -> JsString("Hello, World!")).compactPrint
  )

  def fastPath: Http.FastPath = {
    case HttpRequest(GET, Uri(_, _, Slash(Segment("fast-ping", Path.Empty)), _, _), _, _, _) =>
      HttpResponse(entity = "FAST-PONG!")

    case r@HttpRequest(GET, Uri(_, _, Slash(Segment("json", Path.Empty)), _, _), _, _, _) =>
      log.info("-----------------------------")
      log.info(r.uri.path.toString())
      
      log.info("-----------------------------")
      CommandCommunicator.getInstance
      HttpResponse(entity = jsonResponseEntity)
  }

  def receive = {
    // when a new connection comes in we register ourselves as the connection handler
    case _: Http.Connected => sender ! Http.Register(self, fastPath = fastPath)

    case HttpRequest(GET, Uri.Path("/"), _, _, _) => sender ! HttpResponse(
      entity = HttpEntity(MediaTypes.`text/html`,
        <html>
          <body>
            <h1>Tiny <i>spray-can</i> benchmark server</h1>
            <p>Defined resources:</p>
            <ul>
              <li><a href="/ping">/ping</a></li>
              <li><a href="/fast-ping">/fast-ping</a></li>
              <li><a href="/json">/json</a></li>
              <li><a href="/stop">/stop</a></li>
            </ul>
          </body>
        </html>.toString()
      )
    )

    case HttpRequest(GET, Uri.Path("/ping"), _, _, _) => sender ! HttpResponse(entity = "PONG!")

    case _: HttpRequest => sender ! HttpResponse(NotFound, entity = "Unknown resource!")
  }
}
