package com.edifecs.epp.isc.async.test

import com.edifecs.epp.isc.async.Implicits._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.{ActorSystem, Props}

object ScalaAsyncExample {
  def main(args: Array[String]) = {
    System.out.println("Lines starting with \"RESPONSE\" should be printed in the correct order")
    System.out.println("(Message 2 comes after Message 1, etc.)")
    System.out.println()
    
    val system = new AsyncActorSystem()
    
    val actor1 = system.spawn("actor1")
    val actor2 = system.spawn("actor2")
    
    actor1.sendDelayedMessage("Message 3", 3).flatMap(r => {
      System.out.println("RESPONSE: " + r)
      actor1.sendDelayedMessage("Message 5", 3)
    }).onSuccess {case r: String => {
      System.out.println("RESPONSE: " + r)
      system.shutdown()
    }}
    
    actor2.sendDelayedMessage("Message 1", 1).flatMap(r => {
      System.out.println("RESPONSE: " + r)
      actor2.sendDelayedMessage("Message 2", 1)
    }).flatMap(r => {
      System.out.println("RESPONSE: " + r)
      actor2.sendDelayedMessage("Message 4", 2)
    }).onSuccess {case r: String =>
      System.out.println("RESPONSE: " + r)
    }
  }
}

