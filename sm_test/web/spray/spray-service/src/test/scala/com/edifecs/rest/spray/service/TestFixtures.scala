package com.edifecs.rest.spray.service

import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder
import com.edifecs.epp.isc.{CommandCommunicator, ICommandCommunicator}
import com.edifecs.epp.security.service.SecurityService
import com.edifecs.rest.spray.service.testservices.{MultipartFormService, EchoService}
import com.edifecs.servicemanager.api.{AbstractService, ServiceAnnotationProcessor, ServiceRef}

import org.specs2.mutable._
import org.specs2.specification.{Step, Fragments}

// beforeAll/afterAll implementation taken from:
// http://stackoverflow.com/a/16952931
trait CommandCommunicatorContext extends Specification {
  var communicator: CommandCommunicator = null
  protected var servicesToInit: Seq[(AbstractService, String)] =
    (new SecurityService(), "securityService") :: Nil
  private var services: Seq[ServiceRef] = Nil

  // see http://bit.ly/11I9kFM (specs2 User Guide)
  override def map(fragments: =>Fragments) = 
    Step(beforeAll) ^ fragments ^ Step(afterAll)

  protected def beforeAll() = {
    // Initialize a CommandCommunicator instance.
    communicator = new CommandCommunicatorBuilder().initializeTestMode()
    communicator.connect()

    services = servicesToInit map { service =>
      val serviceRef = ServiceAnnotationProcessor.processAnnotatedService(
        service._1, service._2, communicator)
      serviceRef.startTestMode()
      serviceRef
    }
  }
  
  protected def afterAll() = {
    services.reverse.foreach(_.stop())
    if (communicator != null) communicator.disconnect()
  }
}

trait SprayServiceContext extends CommandCommunicatorContext {
  private val sprayService: SprayService = new SprayService()
  servicesToInit :+= (sprayService, "sprayService")

  def url(suffix: String) = 
    dispatch.url(s"http://localhost:${sprayService.getProperties.get("http.port")}/${suffix}")
}

trait EchoServiceContext extends SprayServiceContext {
  servicesToInit :+= (new EchoService(), "echoService")
}

trait MultipartFormServiceContext extends SprayServiceContext {
  servicesToInit :+= (new MultipartFormService(), "multipartFormService")
}
