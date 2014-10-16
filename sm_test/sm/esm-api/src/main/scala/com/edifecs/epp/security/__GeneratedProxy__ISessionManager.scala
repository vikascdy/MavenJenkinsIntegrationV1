// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.epp.security

import com.edifecs.epp.isc.Args

class __GeneratedProxy__ISessionManager(isc: com.edifecs.epp.isc.Isc, serviceTypeName: java.lang.String) extends ISessionManager {
  def address = isc.getAddressRegistry.getAddressForServiceTypeName(serviceTypeName)
  override def start(context: org.apache.shiro.session.mgt.SessionContext) = isc.sendSync(address, "start", Map[String, java.io.Serializable](("context", context.asInstanceOf[java.io.Serializable]))).asInstanceOf[com.edifecs.epp.security.SessionId]
  override def getSessionAttribute(attributeKey: java.io.Serializable) = isc.sendSync(address, "getSessionAttribute", Map[String, java.io.Serializable](("attributeKey", attributeKey))).asInstanceOf[java.io.Serializable]
  override def getSessionAttributeKeys = isc.sendSync(address, "getSessionAttributeKeys", Map.empty[String, java.io.Serializable]).asInstanceOf[java.util.Collection[java.lang.Object]]
  override def setSessionAttribute(attributeKey: java.io.Serializable, value: java.io.Serializable) = isc.sendSync(address, "setSessionAttribute", Map[String, java.io.Serializable](("attributeKey", attributeKey), ("value", value)))
  override def removeSessionAttribute(attributeKey: java.io.Serializable) = isc.sendSync(address, "removeSessionAttribute", Map[String, java.io.Serializable](("attributeKey", attributeKey))).asInstanceOf[java.io.Serializable]
  override def getSessionStartTimestamp = isc.sendSync(address, "getSessionStartTimestamp", Map.empty[String, java.io.Serializable]).asInstanceOf[java.util.Date]
  override def touchSession = isc.sendSync(address, "touchSession", Map.empty[String, java.io.Serializable])
  override def getSessionTimeout = isc.sendSync(address, "getSessionTimeout", Map.empty[String, java.io.Serializable]).asInstanceOf[Long]
}