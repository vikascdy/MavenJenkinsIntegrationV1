// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.servicemanager.test.services

import com.edifecs.epp.isc.Args

class __GeneratedProxy__ITestJettyCommandRecieverServiceHandler(isc: com.edifecs.epp.isc.Isc, serviceTypeName: java.lang.String) extends ITestJettyCommandRecieverServiceHandler {
  def address = isc.getAddressRegistry.getAddressForServiceTypeName(serviceTypeName)
  override def list = isc.sendSync(address, "list", Map.empty[String, java.io.Serializable]).asInstanceOf[java.util.List[com.edifecs.servicemanager.test.models.Student]]
  override def delStudent(id: Long) = isc.sendSync(address, "delStudent", Map[String, java.io.Serializable](("id", id))).asInstanceOf[Boolean]
  override def testCommand = isc.sendSync(address, "testCommand", Map.empty[String, java.io.Serializable]).asInstanceOf[Boolean]
  override def testCommand(id: java.lang.String, cName: java.lang.String) = isc.sendSync(address, "testParamCommand", Map[String, java.io.Serializable](("id", id), ("cName", cName))).asInstanceOf[java.lang.String]
  override def addStudent(student: com.edifecs.servicemanager.test.models.Student) = isc.sendSync(address, "addStudent", Map[String, java.io.Serializable](("student", student))).asInstanceOf[Boolean]
}