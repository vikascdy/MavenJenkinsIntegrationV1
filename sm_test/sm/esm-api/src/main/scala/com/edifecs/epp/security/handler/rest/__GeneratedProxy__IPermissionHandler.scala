// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.epp.security.handler.rest

import com.edifecs.epp.isc.Args

class __GeneratedProxy__IPermissionHandler(isc: com.edifecs.epp.isc.Isc, serviceTypeName: java.lang.String) extends IPermissionHandler {
  def address = isc.getAddressRegistry.getAddressForServiceTypeName(serviceTypeName)
  override def get($arg0: java.lang.String) = throw new java.lang.UnsupportedOperationException("This method cannot be accessed remotely.")
  override def put($arg0: java.lang.String, $arg1: com.edifecs.epp.security.data.Permission) = throw new java.lang.UnsupportedOperationException("This method cannot be accessed remotely.")
  override def getPermissions(startRecord: Long, recordCount: Long) = isc.sendSync(address, "permission.getPermissions", Map[String, java.io.Serializable](("startRecord", startRecord), ("recordCount", recordCount))).asInstanceOf[com.edifecs.epp.security.data.PaginatedList[com.edifecs.epp.security.data.Permission]]
  override def list($arg0: com.edifecs.epp.isc.core.command.Pagination) = throw new java.lang.UnsupportedOperationException("This method cannot be accessed remotely.")
  override def delete($arg0: java.lang.String) = throw new java.lang.UnsupportedOperationException("This method cannot be accessed remotely.")
  override def post($arg0: com.edifecs.epp.security.data.Permission) = throw new java.lang.UnsupportedOperationException("This method cannot be accessed remotely.")
  override def deletePermission(id: java.lang.Long) = isc.sendSync(address, "permission.deletePermission", Map[String, java.io.Serializable](("id", id))).asInstanceOf[Boolean]
  override def deletePermissions(ids: java.util.ArrayList[java.lang.Long]) = isc.sendSync(address, "permission.deletePermissions", Map[String, java.io.Serializable](("ids", ids))).asInstanceOf[Boolean]
  override def getPermissionById(id: java.lang.Long) = isc.sendSync(address, "permission.getPermissionById", Map[String, java.io.Serializable](("id", id))).asInstanceOf[com.edifecs.epp.security.data.Permission]
  override def getAllPermissions = isc.sendSync(address, "permission.getAllPermissions", Map.empty[String, java.io.Serializable]).asInstanceOf[java.util.Collection[com.edifecs.epp.security.data.Permission]]
  override def updatePermission(permission: com.edifecs.epp.security.data.Permission) = isc.sendSync(address, "permission.updatePermission", Map[String, java.io.Serializable](("permission", permission))).asInstanceOf[com.edifecs.epp.security.data.Permission]
  override def getPermissionsForUser(user: com.edifecs.epp.security.data.User, startRecord: Long, recordCount: Long) = isc.sendSync(address, "permission.getPermissionsForUser", Map[String, java.io.Serializable](("user", user), ("startRecord", startRecord), ("recordCount", recordCount))).asInstanceOf[com.edifecs.epp.security.data.PaginatedList[com.edifecs.epp.security.data.Permission]]
  override def getPermissionsForUserId(id: java.lang.Long, startRecord: Long, recordCount: Long) = isc.sendSync(address, "permission.getPermissionsForUserId", Map[String, java.io.Serializable](("id", id), ("startRecord", startRecord), ("recordCount", recordCount))).asInstanceOf[com.edifecs.epp.security.data.PaginatedList[com.edifecs.epp.security.data.Permission]]
  override def addPermissionToRole(permission: com.edifecs.epp.security.data.Permission, role: com.edifecs.epp.security.data.Role) = isc.sendSync(address, "permission.addPermissionToRole", Map[String, java.io.Serializable](("permission", permission), ("role", role)))
  override def addPermissionsToRole(permissions: java.util.ArrayList[com.edifecs.epp.security.data.Permission], role: com.edifecs.epp.security.data.Role) = isc.sendSync(address, "permission.addPermissionsToRole", Map[String, java.io.Serializable](("permissions", permissions), ("role", role)))
  override def createPermission(permission: com.edifecs.epp.security.data.Permission) = isc.sendSync(address, "permission.createPermission", Map[String, java.io.Serializable](("permission", permission))).asInstanceOf[com.edifecs.epp.security.data.Permission]
  override def removePermissionFromRole(permission: com.edifecs.epp.security.data.Permission, role: com.edifecs.epp.security.data.Role) = isc.sendSync(address, "permission.removePermissionFromRole", Map[String, java.io.Serializable](("permission", permission), ("role", role)))
  override def removePermissionsFromRole(permissions: java.util.ArrayList[com.edifecs.epp.security.data.Permission], role: com.edifecs.epp.security.data.Role) = isc.sendSync(address, "permission.removePermissionsFromRole", Map[String, java.io.Serializable](("permissions", permissions), ("role", role)))
  override def getPermissionsForRole(role: com.edifecs.epp.security.data.Role, startRecord: Long, recordCount: Long) = isc.sendSync(address, "permission.getPermissionsForRole", Map[String, java.io.Serializable](("role", role), ("startRecord", startRecord), ("recordCount", recordCount))).asInstanceOf[com.edifecs.epp.security.data.PaginatedList[com.edifecs.epp.security.data.Permission]]
  override def getPermissionsForRoleId(id: java.lang.Long, startRecord: Long, recordCount: Long) = isc.sendSync(address, "permission.getPermissionsForRoleId", Map[String, java.io.Serializable](("id", id), ("startRecord", startRecord), ("recordCount", recordCount))).asInstanceOf[com.edifecs.epp.security.data.PaginatedList[com.edifecs.epp.security.data.Permission]]
  override def restCommand($2dx$2drest$2dmethod: java.lang.String, $2dx$2durl$2dsuffix: java.lang.String, $2dx$2drequest$2dbody: com.edifecs.epp.isc.json.JsonArg, page: java.lang.Long, start: java.lang.Long, limit: java.lang.Long, query: java.lang.String, sort: java.lang.String, filter: java.lang.String) = isc.sendSync(address, "permission", Map[String, java.io.Serializable](("-x-rest-method", $2dx$2drest$2dmethod), ("-x-url-suffix", $2dx$2durl$2dsuffix), ("-x-request-body", $2dx$2drequest$2dbody), ("page", page), ("start", start), ("limit", limit), ("query", query), ("sort", sort), ("filter", filter))).asInstanceOf[java.io.Serializable]
}