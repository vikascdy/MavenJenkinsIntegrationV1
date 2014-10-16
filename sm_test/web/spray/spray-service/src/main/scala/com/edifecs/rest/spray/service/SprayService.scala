package com.edifecs.rest.spray.service

import java.util.Properties
import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, ActorRef, Props}
import akka.io.IO
import akka.pattern.ask
import com.edifecs.core.configuration.helper.TypesafeConfigKeys

import spray.can.Http

import com.edifecs.epp.isc.async.Implicits._
import com.edifecs.epp.isc.core.command.AbstractCommandHandler
import com.edifecs.servicemanager.api.AbstractService
import RestService._

import scala.concurrent.Await

class SprayService extends AbstractService with ISprayService {

  private lazy val port =
    Integer.parseInt(Option(getProperties.getProperty("http.port")) getOrElse "8080")
  // 0.0.0.0 Attaches Spray-Can to all interfaces
  private lazy val interface =
    Option(getProperties.getProperty("http.interface")) getOrElse "0.0.0.0"
  private implicit var system: ActorSystem = null
  private var proxyActorRef: ActorRef = null
  implicit lazy val timeout: akka.util.Timeout = isc.getConfig.getDuration(
    TypesafeConfigKeys.ASYNC_MESSAGE_TIMEOUT, TimeUnit.MILLISECONDS)

  override protected def start() = {
    logger.info("HTTP service on port {}...", port)
    system = ActorSystem("spray")
    proxyActorRef = system.actorOf(Props[ProxyActor], "spray-proxy-actor")

    Await.result(IO(Http) ? Http.Bind(proxyActorRef, interface = interface, port = port),
      timeout.duration)
  }

  override protected def stop() = {
    logger.info("Stopping Spray HTTP service...")
    IO(Http) ! Http.Unbind
    system.shutdown()
    system.awaitTermination()
    system = null
    proxyActorRef = null
  }

  override protected def getTestProperties: Properties = {
    val p = new Properties()
    p.setProperty("http.port", try {
      val s = new java.net.ServerSocket(0)
      try s.getLocalPort.toString finally s.close()
    } catch {
      case e: Exception =>
        throw new RuntimeException("Unable to obtain random port", e)
    })
    p
  }

//  override lazy val cache = new AbstractCommandHandler with ISprayCacheHandler {
//
//  }

  override lazy val urls = new AbstractCommandHandler with ISprayUrlHandler {

    override def unregisterCommandShortcutUrl(url: String) =
      (proxyActorRef ? UnregisterShortcutUrl(url)) as classOf[java.lang.Boolean]

    override def registerCommandShortcutUrl(
      url: String,
      serviceType: String,
      command: String,
      urlSuffix: String = null
    ) = (proxyActorRef?RegisterShortcutUrl(url,serviceType,command,urlSuffix)) as
        classOf[java.lang.Boolean]
  }
}
