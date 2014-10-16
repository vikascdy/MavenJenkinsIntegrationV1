package com.edifecs.servicemanager.api.test

import org.specs2.mutable._

import com.edifecs.epp.isc.CommandCommunicator
import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder
import com.edifecs.servicemanager.api.{ServiceRef, ServiceAnnotationProcessor}
import scala.collection.JavaConversions._
import com.edifecs.epp.isc.core.ServiceStatus

class ListAvailableCommandsTest extends Specification {

  sequential

  "The list of available commands" should {
    "be accessible through the CommandCommunicator" in new ServiceEnv {
      val map = mapAsScalaMap(communicator.getAvailableCommands)
      (map must not beNull) and
      (map.keys must contain(service1.getAddress)) and
      (map.keys must contain(service2.getAddress)) and
      (map(service1.getAddress) must have size(3)) and
      (map(service2.getAddress) must have size(3))
    }
    "exclude stopped services" in new ServiceEnv {
      service2.stop()
      val map = communicator.getAvailableCommands
      (map.keys must contain(service1.getAddress)) and
      (map.keys must not(contain(service2.getAddress)))
    }
    "be uncached when the service list changes" in new ServiceEnv {
      val map1 = communicator.getAvailableCommands
      service2.stop()
      val map2 = communicator.getAvailableCommands
      (map1.keys must contain(service2.getAddress)) and
      (map2.keys must contain(service1.getAddress)) and
      (map2.keys must not(contain(service2.getAddress)))
    }
  }

  private trait ServiceEnv extends BeforeAfter {
    var communicator: CommandCommunicator = null

    var service1: ServiceRef = null
    var service2: ServiceRef = null

    override def before = {
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

    override def after = {
      if (service1.getStatus == ServiceStatus.Started) service1.stop()
      if (service2.getStatus == ServiceStatus.Started) service2.stop()
      communicator.disconnect()
    }
  }
}
