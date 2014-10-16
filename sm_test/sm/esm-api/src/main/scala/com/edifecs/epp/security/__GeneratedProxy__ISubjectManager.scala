// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.epp.security

import com.edifecs.epp.isc.Args

class __GeneratedProxy__ISubjectManager(isc: com.edifecs.epp.isc.Isc, serviceTypeName: java.lang.String) extends ISubjectManager {
  def address = isc.getAddressRegistry.getAddressForServiceTypeName(serviceTypeName)
  override def getSite = isc.sendSync(address, "subject.getSite", Map.empty[String, java.io.Serializable]).asInstanceOf[com.edifecs.epp.security.data.Site]
  override def getTenant = isc.sendSync(address, "subject.getTenant", Map.empty[String, java.io.Serializable]).asInstanceOf[com.edifecs.epp.security.data.Tenant]
  override def getUser = isc.sendSync(address, "subject.getUser", Map.empty[String, java.io.Serializable]).asInstanceOf[com.edifecs.epp.security.data.User]
  override def getUserGroups = isc.sendSync(address, "subject.getUserGroups", Map.empty[String, java.io.Serializable]).asInstanceOf[java.util.Collection[com.edifecs.epp.security.data.UserGroup]]
  override def getUserId = isc.sendSync(address, "subject.getUserId", Map.empty[String, java.io.Serializable]).asInstanceOf[java.lang.Long]
  override def getOrganization = isc.sendSync(address, "subject.getOrganization", Map.empty[String, java.io.Serializable]).asInstanceOf[com.edifecs.epp.security.data.Organization]
}