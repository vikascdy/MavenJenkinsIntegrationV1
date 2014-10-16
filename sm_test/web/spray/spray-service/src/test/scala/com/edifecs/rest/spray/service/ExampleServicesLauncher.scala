package com.edifecs.rest.spray.service

import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder
import com.edifecs.epp.security.service.SecurityService
import com.edifecs.rest.spray.service.testservices.{MultipartFormService, EchoService}
import com.edifecs.servicemanager.api.ServiceAnnotationProcessor.processAnnotatedService

object ExampleServicesLauncher extends App {
  val communicator = new CommandCommunicatorBuilder().initializeTestMode()
  communicator.connect()
  val securityService = new SecurityService()
  val sprayService = new SprayService()
  val echoService = new EchoService()
  val multipartFormService = new MultipartFormService()
  val securityRef = processAnnotatedService(securityService, "securityService", communicator)
  securityRef.startTestMode()
  val sprayRef = processAnnotatedService(sprayService, "sprayService", communicator)
  sprayRef.startTestMode()
  val echoRef = processAnnotatedService(echoService, "echoService", communicator)
  echoRef.startTestMode()
  val multipartRef = processAnnotatedService(multipartFormService, "multipartFormService", communicator)
  multipartRef.startTestMode()

  println(s"""


Spray server started. Browse to http://localhost:${sprayService.getProperties.get("http.port")}/rest/service/multipart/form

Press ENTER to terminate the server.


""")
  System.in.read()

  multipartRef.stop()
  echoRef.stop()
  sprayRef.stop()
  securityRef.stop()
  communicator.disconnect()
  System.exit(0)
}
