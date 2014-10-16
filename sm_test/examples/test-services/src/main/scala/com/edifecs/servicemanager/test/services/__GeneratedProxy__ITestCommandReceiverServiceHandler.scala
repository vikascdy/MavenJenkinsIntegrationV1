// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.servicemanager.test.services

import com.edifecs.epp.isc.Args

class __GeneratedProxy__ITestCommandReceiverServiceHandler(isc: com.edifecs.epp.isc.Isc, serviceTypeName: java.lang.String) extends ITestCommandReceiverServiceHandler {
  def address = isc.getAddressRegistry.getAddressForServiceTypeName(serviceTypeName)
  override def testSecurityUserSessionPassCommand = isc.sendSync(address, "testSecurityUserSessionPassCommand", Map.empty[String, java.io.Serializable]).asInstanceOf[java.io.Serializable]
  override def testSecurityUserSessionCommand = isc.sendSync(address, "testSecurityUserSessionCommand", Map.empty[String, java.io.Serializable]).asInstanceOf[java.io.Serializable]
  override def testCommand = isc.sendSync(address, "testCommand", Map.empty[String, java.io.Serializable]).asInstanceOf[Boolean]
  override def testNullSession = isc.sendSync(address, "testNullSession", Map.empty[String, java.io.Serializable]).asInstanceOf[Boolean]
  override def testSessionRequired = isc.sendSync(address, "testSessionRequired", Map.empty[String, java.io.Serializable]).asInstanceOf[Boolean]
  override def testPermissionRequired = isc.sendSync(address, "testPermissionRequired", Map.empty[String, java.io.Serializable]).asInstanceOf[Boolean]
  override def downloadAsStream(path: java.lang.String) = isc.sendSync(address, "downloadAsStream", Map[String, java.io.Serializable](("path", path))).asInstanceOf[com.edifecs.epp.isc.stream.MessageStream].toInputStream
  override def downloadAsString(path: java.lang.String) = isc.sendSync(address, "downloadAsString", Map[String, java.io.Serializable](("path", path))).asInstanceOf[java.lang.String]
}