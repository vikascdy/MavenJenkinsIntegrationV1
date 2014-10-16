package com.edifecs.rest.spray.service

import dispatch._

import org.specs2.mutable._

class MultipartUploadTest extends Specification with MultipartFormServiceContext {

  private def auth(r: Req) =
    r.as_!("_System\\admin@edfx", "admin")

  private val str1 = "qwertyuiopasdfghjklzxcvbnm" * 50
  private val str2 = ("This is a really really long message that will be sent as a stream." +
    " The rain in Spain stays mainly on the plain. ") * 10

  "Multipart messages sent to spray-service" should {
    "be interpreted as stream arguments" in {
      val response = Http(auth(url("rest/service/multipart/uploadTwoFiles")) << s"""
--boundary
Content-Disposition: form-data; name="file1"

$str1
--boundary
Content-Disposition: form-data; name="file2"

$str2
--boundary--
""" <:< Map("Content-Type" -> "multipart/form-data; boundary=boundary") > as.String)
      response() must startWith("<html>") and
                      contain("<code id=\"file1\">" + str1 + "</code>") and
                      contain("<code id=\"file2\">" + str2 + "</code>")
    }
  }
}
