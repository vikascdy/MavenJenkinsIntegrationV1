package com.edifecs.epp.isc.async.test

import akka.actor.Actor

import com.edifecs.epp.isc.async.{MessageFuture, ScalaMessageFutureWrapper}

import scala.concurrent.ExecutionContext.Implicits.global

class DelayedActor extends Actor {
  
  def name = self.path.name
  
  def receive = {
    case DelayMessage(m, d) => {
      //println(s"Actor '$name' received message '$m'.")
      println(s"Actor '$name' waiting for $d second(s).")
      Thread.sleep(d * 1000)
      sender ! ResponseMessage(m + " from " + name)
    }
  }
}

