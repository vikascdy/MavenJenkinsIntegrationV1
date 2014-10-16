// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.epp.security

import com.edifecs.epp.isc.Args

class __GeneratedProxy__IAuthorizationManager(isc: com.edifecs.epp.isc.Isc, serviceTypeName: java.lang.String) extends IAuthorizationManager {
  def address = isc.getAddressRegistry.getAddressForServiceTypeName(serviceTypeName)
  override def checkPermission(permission: java.lang.String) = isc.sendSync(address, "authorization.checkPermission", Map[String, java.io.Serializable](("permission", permission)))
  override def isPermitted(permission: java.lang.String) = isc.sendSync(address, "authorization.isPermitted", Map[String, java.io.Serializable](("permission", permission))).asInstanceOf[Boolean]
  override def checkRole(roleIdentifier: java.lang.String) = isc.sendSync(address, "authorization.checkRole", Map[String, java.io.Serializable](("roleIdentifier", roleIdentifier)))
  override def checkRoles(roleIdentifiers: Array[java.lang.String]) = isc.sendSync(address, "authorization.checkRoles", Map[String, java.io.Serializable](("roleIdentifiers", roleIdentifiers)))
  override def getPermittedPermissions(permissions: Array[java.lang.String]) = isc.sendSync(address, "authorization.getPermittedPermissions", Map[String, java.io.Serializable](("permissions", permissions))).asInstanceOf[Array[java.lang.String]]
  override def checkPermissions(permissions: Array[java.lang.String]) = isc.sendSync(address, "authorization.checkPermissions", Map[String, java.io.Serializable](("permissions", permissions)))
}