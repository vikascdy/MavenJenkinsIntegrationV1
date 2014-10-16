// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.servicemanager.test.services

import com.edifecs.epp.isc.Args

class __GeneratedProxy__ITestCommandSenderServiceHandler(isc: com.edifecs.epp.isc.Isc, serviceTypeName: java.lang.String) extends ITestCommandSenderServiceHandler {
  def address = isc.getAddressRegistry.getAddressForServiceTypeName(serviceTypeName)
  override def testSenderCommand = isc.sendSync(address, "testSenderCommand", Map.empty[String, java.io.Serializable]).asInstanceOf[Boolean]
}