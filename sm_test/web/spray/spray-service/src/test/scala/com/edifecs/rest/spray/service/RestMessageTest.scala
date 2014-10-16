package com.edifecs.rest.spray.service

import scala.collection.immutable.Map

import org.specs2.mutable._
import org.specs2.matcher.JsonMatchers

import dispatch._

class RestMessageTest extends Specification
                         with EchoServiceContext
                         with JsonMatchers {
  sequential
  private def auth(r: Req) =
    r.as_!("_System\\admin@edfx", "admin")

  "The REST messaging system" should {
    "send string arguments via GET query strings" in {
      val request = auth(url("rest/service/Echo%20Service/echoArg?arg=hello_world"))
      val response = Http(request > as.String)
      response() must /("success" -> true) and /("data" -> "hello_world")
    }
    "send numeric arguments via GET query strings" in {
      val request1 = auth(url("rest/service/Echo%20Service/echoLong?arg=91"))
      val response1 = Http(request1 > as.String)
      val request2 = auth(url("rest/service/Echo%20Service/echoDouble?arg=9.1"))
      val response2 = Http(request2 > as.String)
      (response1() must /("success" -> true) and /("data" -> 91.toLong)) and
      (response2() must /("success" -> true) and /("data" -> 9.1))
    }
    "send JSON arguments via the 'data' POST parameter" in {
      val request = auth(url("rest/service/Echo%20Service/echoArg")) << Map(
        "data" -> "{\"arg\": \"hello_world\"}"
      )
      val response = Http(request > as.String)
      response() must /("success" -> true) and /("data" -> "hello_world")
    }
    "support query strings for DELETE requests" in {
      val request = auth(url("rest/service/Echo%20Service/echoArg?arg=hello_world")).DELETE
      val response = Http(request > as.String)
      response() must /("success" -> true) and /("data" -> "hello_world")
    }
    "support the 'data' body parameter for PUT requests" in {
      val request = auth(url("rest/service/Echo%20Service/echoArg")).PUT << Map(
        "data" -> "{\"arg\": \"hello_world\"}"
      )
      val response = Http(request > as.String)
      response() must /("success" -> true) and /("data" -> "hello_world")
    }
  }

  "HTTP method detection" should {
    "work for GET requests" in {
      val response = Http(auth(url("rest/service/Echo%20Service/echoMethod")) > as.String)
      response() must /("data" -> "get")
    }
    "work for POST requests" in {
      val response = Http(auth(url("rest/service/Echo%20Service/echoMethod")) << Map(
        "data" -> "{}"
      ) > as.String)
      response() must /("data" -> "post")
    }
    "work for PUT requests" in {
      val response = Http(auth(url("rest/service/Echo%20Service/echoMethod")).PUT << Map(
        "data" -> "{}"
      ) > as.String)
      response() must /("data" -> "put")
    }
    "work for DELETE requests" in {
      val response = Http(auth(url("rest/service/Echo%20Service/echoMethod")).DELETE > as.String)
      response() must /("data" -> "delete")
    }
  }

  "URL suffix parameters" should {
    "work for GET requests" in {
      val response = Http(auth(url("rest/service/Echo%20Service/echoSuffix/hello_world")) > as.String)
      response() must /("data" -> "hello_world")
    }
    "work for POST requests" in {
      val response = Http(auth(url("rest/service/Echo%20Service/echoSuffix/hello_world")) << Map(
        "data" -> "{}"
      ) > as.String)
      response() must /("data" -> "hello_world")
    }
    "work for PUT requests" in {
      val response = Http(auth(url("rest/service/Echo%20Service/echoSuffix/hello_world")).PUT << Map(
        "data" -> "{}"
      ) > as.String)
      response() must /("data" -> "hello_world")
    }
    "work for DELETE requests" in {
      val response = Http(
        auth(url("rest/service/Echo%20Service/echoSuffix/hello_world")).DELETE > as.String)
      response() must /("data" -> "hello_world")
    }
  }

  "The JSON request body parameter" should {
    "work for POST requests" in {
      val response = Http(auth(url("rest/service/Echo%20Service/echoBody")) <<
        "\"hello_world\"" <:< Map(
          "Content-Type" -> "application/json"
      ) > as.String)
      response() must /("data" -> "hello_world")
    }
    "work for PUT requests" in {
      val response = Http(auth(url("rest/service/Echo%20Service/echoBody")).PUT <<
        "\"hello_world\"" <:< Map(
          "Content-Type" -> "application/json"
      ) > as.String)
      response() must /("data" -> "hello_world")
    }
  }

  "REST error messages" should {
    "have a non-200 HTTP status code" in {
      val status = for (
        r <- Http(auth(url("rest/service/Echo%20Service/echoError?message=hello_world")))
      ) yield r.getStatusCode
      status() mustNotEqual 200
    }
    "set \"success\" to false" in {
      val response = for (
        r <- Http(auth(url("rest/service/Echo%20Service/echoError?message=hello_world")))
      ) yield r.getResponseBody
      response() must /("success" -> false)
    }
    "provide the error message and class" in {
      val response = for (
        r <- Http(auth(url("rest/service/Echo%20Service/echoError?message=hello_world")))
      ) yield r.getResponseBody
      (response() must /("error") /("message" -> "hello_world")) and
      (response() must /("error") /("class" -> "java.lang.RuntimeException"))
    }
    "not provide the stack trace as an array" in {
      val response = for (
        r <- Http(auth(url("rest/service/Echo%20Service/echoError?message=hello_world")))
      ) yield r.getResponseBody
      response() mustNotEqual /("error") /("stackTrace")
    }
  }
}

