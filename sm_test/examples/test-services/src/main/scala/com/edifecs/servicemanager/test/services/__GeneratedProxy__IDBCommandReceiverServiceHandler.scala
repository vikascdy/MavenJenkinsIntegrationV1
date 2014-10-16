// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.servicemanager.test.services

import com.edifecs.epp.isc.Args

class __GeneratedProxy__IDBCommandReceiverServiceHandler(isc: com.edifecs.epp.isc.Isc, serviceTypeName: java.lang.String) extends IDBCommandReceiverServiceHandler {
  def address = isc.getAddressRegistry.getAddressForServiceTypeName(serviceTypeName)
  override def ExecuteCommand(command: java.lang.String) = isc.sendSync(address, "Execute", Map[String, java.io.Serializable](("command", command))).asInstanceOf[java.util.Map[java.lang.String, java.util.Map[java.lang.Integer, java.lang.String]]]
}