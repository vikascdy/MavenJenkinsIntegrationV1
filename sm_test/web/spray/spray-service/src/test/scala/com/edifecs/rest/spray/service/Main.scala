package com.edifecs.rest.spray.service

import com.edifecs.core.configuration.helper.SystemVariables
import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder
import com.edifecs.epp.security.service.SecurityService
import com.edifecs.servicemanager.api.{ServiceRef, ServiceAnnotationProcessor}
import com.edifecs.xboard.portal.service.XboardPortalService

object Main {
  def main(args: Array[String]) {

    SystemVariables.SERVICE_MANAGER_ROOT_PATH = "C:\\Dev\\repo\\sm\\sm-dist\\target\\bundle\\ServiceManager\\";

    // Initialize a commandCommunicator Instance
    val communicator = new CommandCommunicatorBuilder().initializeTestMode()
    communicator.connect()

    // Initialize the Security Service
    val securityService = new SecurityService()
    val ref = ServiceAnnotationProcessor.processAnnotatedService(
      securityService,
      "securityServiceTestMode",
      communicator);
    ref.startTestMode();


    // Initialize the Security Service
    val doormatService = new XboardPortalService()
    val doormatServiceRef = ServiceAnnotationProcessor.processAnnotatedService(
      doormatService,
      "doormatServiceTestMode",
      communicator);
    doormatServiceRef.startTestMode();

    var server = new SprayServer()
    server.start
  }
}
