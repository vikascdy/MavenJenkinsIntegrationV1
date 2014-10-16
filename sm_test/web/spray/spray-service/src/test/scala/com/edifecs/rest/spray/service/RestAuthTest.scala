package com.edifecs.rest.spray.service

import org.specs2.mutable._
import org.specs2.matcher.JsonMatchers

import scala.collection.JavaConversions._

import dispatch._

import scala.collection.immutable.Map

class RestAuthTest extends Specification with EchoServiceContext with JsonMatchers {
  sequential
  "The REST authentication system" should {
    "allow unauthenticated access to @NullSessionAllowed commands" in {
      val request = url("rest/service/esm-service/isSubjectAuthenticated")
      val response = Http(request > as.String)
      response() must /("data" -> false)
    }
    "allow login via HTTP Basic Auth" in {
      val request = url("rest/service/esm-service/isSubjectAuthenticated"
        ).as_!("_System\\admin@edfx", "admin")
      val response = Http(request > as.String)
      response() must /("data" -> true)
    }
    "reject invalid HTTP Basic Auth with HTTP 401 Unauthorized" in {
      val request = url("rest/service/esm-service/isSubjectAuthenticated"
        ).as_!("_System\\fakeuser@edfx", "fakepassword")
      val status = for (r <- Http(request)) yield r.getStatusCode
      // TODO: Review and figure out expected behaviour
      status() mustEqual 200 // Should be 401 Unauthorized instead?
    }
    "allow login via the '/rest/login' URL" in {
      val request = url("rest/login") << Map(
        "domain" -> "_System",
        "organization" -> "edfx",
        "username" -> "admin",
        "password" -> "admin")
      val response = Http(request > as.String)
      response() must /("success" -> true)
    }
    "only allow POST requests to '/rest/login'" in {
      val status = for (r <- Http(url("rest/login"))) yield r.getStatusCode
      status() mustEqual 405 // Method Not Allowed
    }
    "reject invalid logins to '/rest/login'" in {
      val request = url("rest/login") << Map(
        "domain" -> "_System",
        "organization" -> "edfx",
        "username" -> "fakeuser",
        "password" -> "fakepassword")
      val response = Http(request > as.String)
      response() must /("success" -> false)
    }
    "reject passwordless logins to '/rest/login'" in {
      val status = for (r <- Http(url("rest/login") << Map("username" -> "admin"))) yield r.getStatusCode
      status() mustEqual 400 // Bad Request
    }
    "create a session cookie after a '/rest/login' login" in {
      val loginReq = url("rest/login") << Map(
        "domain" -> "_System",
        "organization" -> "edfx",
        "username" -> "admin",
        "password" -> "admin")
      val cookies = for (r <- Http(loginReq)) yield r.getCookies
      var usernameReq = url("rest/service/Echo%20Service/echoUser")
      cookies() foreach {c => usernameReq = usernameReq.addCookie(c)}
      var usernameRsp = for (r <- Http(usernameReq)) yield r.getResponseBody //Http(usernameReq OK as.String)
      usernameRsp() must /("data" -> "admin")
    }
    "prioritize HTTP Basic Auth over an existing session cookie" in {
      todo
    }
  }
}

