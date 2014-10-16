//// -----------------------------------------------------------------------------
//// Copyright (c) Edifecs Inc. All Rights Reserved.
////
//// This software is the confidential and proprietary information of Edifecs Inc.
//// ("Confidential Information").  You shall not disclose such Confidential
//// Information and shall use it only in accordance with the terms of the license
//// agreement you entered into with Edifecs.
////
//// EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
//// SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
//// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
//// NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
//// LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
//// ITS DERIVATIVES.
//// -----------------------------------------------------------------------------
//
//package com.edifecs.servicemanager.node
//
//import org.specs2.mutable.Specification
//import java.io.File
//import com.edifecs.epp.isc.core.ServiceStatus
//import com.edifecs.core.configuration.helper.SystemVariables
//import java.util.Properties
//import java.lang.Boolean
//import java.util
//import com.edifecs.epp.security.data.PasswordPolicy
//import com.edifecs.servicemanager.api.ServiceRegistry
//
///**
//* <b>If this test breaks, it means that backwards capabilities of a service is broken and all services running on the
//* this version of SM Must be updated!!!</b>
//*
//* Created by willclem on 4/25/2014.
//**/
//class NodeServiceTest extends Specification {
//
//  val node = new NodeService()
//  val appPath = new File(getClass().getResource("/apps/esm/").toURI.getPath)
//  val nodeName = "testNode"
//  val serviceTypeName = "esm-service"
//  val serviceName = "test-esm-service"
//  val applicationName = "esm"
//
//  sequential
//  "the service container should" should {
//    "start properly without errors" in {
//      node.start(nodeName, getClass.getResourceAsStream("/config.properties")) must not(throwAn[Exception])
//    }
//    "deploy application zip file" in {
//      node.installCartridge(appPath) must not(throwAn[Exception])
//    }
//    "the configuration should contain the installed cartridge path" in {
//      node.getConfiguration().getCartridgeByServiceName(serviceTypeName).getManifest.name contentEquals(applicationName)
//      node.getConfiguration().getCartridgeByServiceName(serviceTypeName).getManifest.getPhysicalComponents.get(0).name contentEquals(serviceTypeName)
//    }
//    "the service can be created" in {
//      val properties: Properties = new Properties
//      properties.put(PasswordPolicy.PASSWD_MAX_ATTEMPTS, "0")
//      properties.put(PasswordPolicy.PASSWD_RESET_LOCKOUT_INTERVL, "5")
//      properties.put(PasswordPolicy.PASSWD_HISTORY, "3")
//      properties.put(PasswordPolicy.PASSWD_AGE, "120")
//      properties.put(PasswordPolicy.PASSWD_RESET_LOGIN, "false")
//      properties.put(PasswordPolicy.PASSWD_REGEX, "")
//      properties.put(PasswordPolicy.PASSWD_REGEX_DESC, "")
//      properties.put(PasswordPolicy.PASSWD_LOCKOUT_DURATION, "10")
//
//      val resourceProperties: Properties = new Properties
//      resourceProperties.put("Username", "sa")
//      resourceProperties.put("Password", "")
//      resourceProperties.put("Driver", "org.h2.Driver")
//      resourceProperties.put("URL", "jdbc:h2:mem:TestDatabase")
//      resourceProperties.put("Dialect", "org.hibernate.dialect.H2Dialect")
//      resourceProperties.put("AutoCreate", new Boolean(true))
//
//      val resources = new util.HashMap[String, Properties]()
//      resources.put("Security Database", resourceProperties)
//
//      // Test Certificate
//      SystemVariables.SECURITY_CERTIFICATE_FILE = getClass.getResource("/keystore.jks").toURI.getPath
//
//      val service = node.createService(serviceName, serviceTypeName, "1.0.0.0", properties, resources)
//
//      service mustNotEqual(null)
//    }
//    "the service starts properly" in {
//      node.startService(serviceName, true) must beTrue
//      while (ServiceRegistry.getLocalServiceStatus(serviceName) == ServiceStatus.Starting) {
//        Thread.sleep(500)
//      }
//      ServiceRegistry.getLocalServiceStatus(serviceName) must beEqualTo(ServiceStatus.Started)
//    }
//    "the service properly registered a command handler" in {
//      val address = node.getCommandCommunicator.getAddressRegistry.getAddressForServiceTypeName(serviceTypeName)
//      address.getService must beEqualTo(serviceName)
//    }
//    "shutdown properly without open threads" in {
//      node.shutdown() must not(throwA[Exception])
//    }
//  }
//}
