// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.epp.security.handler

import com.edifecs.epp.isc.Args

class __GeneratedProxy__IAdministrativeDataCommandHandler(isc: com.edifecs.epp.isc.Isc, serviceTypeName: java.lang.String) extends IAdministrativeDataCommandHandler {
  def address = isc.getAddressRegistry.getAddressForServiceTypeName(serviceTypeName)
  override def testLdapConnection(realm: com.edifecs.epp.security.data.SecurityRealm) = isc.sendSync(address, "testLdapConnection", Map[String, java.io.Serializable](("realm", realm))).asInstanceOf[Boolean]
  override def getRealmPropertiesMeta(realmType: java.lang.String) = isc.sendSync(address, "getRealmPropertiesMeta", Map[String, java.io.Serializable](("realmType", realmType))).asInstanceOf[java.util.List[com.edifecs.epp.security.data.RealmConfig]]
  override def isEmailServiceAvailable = isc.sendSync(address, "isEmailServiceAvailable", Map.empty[String, java.io.Serializable]).asInstanceOf[java.lang.Boolean]
}