package com.edifecs.rest.spray.service

import akka.actor.ActorSystem
import akka.io.IO
import spray.can.Http
import akka.actor.Props
import akka.actor.ActorRef

class SprayServer(interface: String = "localhost", port: Int = 8081) {

  val system = ActorSystem()

  def start() {
    implicit val system = this.system

    val myListener: ActorRef = system.actorOf(Props[ProxyActor], "ProxyActor")

    IO(Http) ! Http.Bind(myListener, interface = "0.0.0.0", port = port)
  }

  def stop() {
    implicit val system = this.system

    IO(Http) ! Http.Unbind
  }
}
