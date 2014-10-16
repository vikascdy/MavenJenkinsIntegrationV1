// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.servicemanager.test.services

import com.edifecs.epp.isc.Args

class __GeneratedProxy__ITestChatServiceHandler(isc: com.edifecs.epp.isc.Isc, serviceTypeName: java.lang.String) extends ITestChatServiceHandler {
  def address = isc.getAddressRegistry.getAddressForServiceTypeName(serviceTypeName)
  override def removeChatServiceClientCommand(addr: com.edifecs.epp.isc.Address) = isc.sendSync(address, "removeChatServiceClientCommand", Map[String, java.io.Serializable](("addr", addr))).asInstanceOf[java.lang.Boolean]
  override def sendChatMessageCommand(message: java.lang.String, client: java.lang.String, addr: com.edifecs.epp.isc.Address) = isc.sendSync(address, "sendChatMessageCommand", Map[String, java.io.Serializable](("message", message), ("client", client), ("addr", addr))).asInstanceOf[java.lang.Boolean]
  override def addChatServiceClientCommand(addr: com.edifecs.epp.isc.Address) = isc.sendSync(address, "addChatServiceClientCommand", Map[String, java.io.Serializable](("addr", addr))).asInstanceOf[java.lang.Boolean]
}