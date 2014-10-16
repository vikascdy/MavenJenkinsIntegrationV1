package com.edifecs.rest.spray.service

import org.specs2.mutable._
import org.specs2.matcher.JsonMatchers

import dispatch._

class CommandListTest extends Specification
                         with EchoServiceContext
                         with JsonMatchers {
  "The REST command list" should {
    "include all REST commands" in {
      val response = for (
        r <- Http(url("rest/commands"))
      ) yield r.getResponseBody
      val json = response()
      (json must */("name" -> "echoArg")) and
      (json must */("name" -> "echoMethod")) and
      (json must */("name" -> "echoError"))
    }
  }
}
