// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.servicemanager.test.services

class __GeneratedProxy__ITestChatService(isc: com.edifecs.epp.isc.Isc) extends ITestChatService {
  val serviceType = "Test Chat Service"
  override lazy val getTestChatServiceHandler = new com.edifecs.servicemanager.test.services.__GeneratedProxy__ITestChatServiceHandler(isc, serviceType)
}