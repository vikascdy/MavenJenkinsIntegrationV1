package com.edifecs.servicemanager.api.test

import scala.collection.immutable._

import com.edifecs.epp.isc.CommandCommunicator
import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder
import com.edifecs.servicemanager.api.{ServiceRef, ServiceAnnotationProcessor}
import com.edifecs.test.BeforeAfterSpecification

class ServiceMessagingTest extends BeforeAfterSpecification {

  var communicator: CommandCommunicator = null

  var service1: ServiceRef = null
  var service2: ServiceRef = null

  protected override def beforeAll(): Unit = {
    val builder = new CommandCommunicatorBuilder()
    communicator = builder.initializeTestMode
    communicator.connect()

    service1 = ServiceAnnotationProcessor.processAnnotatedService(
        new TestService(1), "test1", communicator)
    service2 = ServiceAnnotationProcessor.processAnnotatedService(
        new TestService(2), "test2", communicator)
    service1.start()
    service2.start()
  }

  protected override def afterAll(): Unit = {
    service1.stop()
    service2.stop()
    communicator.disconnect()
  }

  "The service messaging system" should {
    "send messages" in {
      communicator.sendSync(service1.getAddress, "testCommand") mustEqual true
    }
    "route messages to the correct services" in {
      (communicator.sendSync(service1.getAddress, "appendNumber", Map("message" -> "foo")
        ) mustEqual "foo 1") and
      (communicator.sendSync(service2.getAddress, "appendNumber", Map("message" -> "bar")
        ) mustEqual "bar 2")
    }
    "allow services to send messages to each other" in {
      communicator.sendSync(service1.getAddress, "appendNumberFromOtherService",
        Map("message" -> "hi", "destination" -> service2.getAddress)
      ) mustEqual "hi 2"
    }
  }
}
