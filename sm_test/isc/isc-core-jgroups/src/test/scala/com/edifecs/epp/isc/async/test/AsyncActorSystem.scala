package com.edifecs.epp.isc.async.test

import akka.actor.{ActorRef, Props}
import akka.pattern.ask
import com.edifecs.epp.isc.async.{MessageFuture, ScalaMessageFutureWrapper}
import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class AsyncActorSystem {
  private val cc = new CommandCommunicatorBuilder().initializeTestMode()
  cc.connect()

  def spawn(name: String) = new AsyncActor(cc.getActorSystem.actorOf(Props[DelayedActor], name))

  def shutdown() = cc.disconnect()
}

class AsyncActor(ref: ActorRef) {
 
  def sendDelayedMessage(message: String, delayInSeconds: Int): MessageFuture[String] =
    new ScalaMessageFutureWrapper(
      for(r <- ask(ref, DelayMessage(message, delayInSeconds))((delayInSeconds+1) seconds)
               .mapTo[ResponseMessage])
      yield r.message)
}
