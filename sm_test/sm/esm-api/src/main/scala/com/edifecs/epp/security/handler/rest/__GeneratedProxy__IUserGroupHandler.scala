// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.epp.security.handler.rest

import com.edifecs.epp.isc.Args

class __GeneratedProxy__IUserGroupHandler(isc: com.edifecs.epp.isc.Isc, serviceTypeName: java.lang.String) extends IUserGroupHandler {
  def address = isc.getAddressRegistry.getAddressForServiceTypeName(serviceTypeName)
  override def get($arg0: java.lang.String) = throw new java.lang.UnsupportedOperationException("This method cannot be accessed remotely.")
  override def put($arg0: java.lang.String, $arg1: com.edifecs.epp.security.data.UserGroup) = throw new java.lang.UnsupportedOperationException("This method cannot be accessed remotely.")
  override def list($arg0: com.edifecs.epp.isc.core.command.Pagination) = throw new java.lang.UnsupportedOperationException("This method cannot be accessed remotely.")
  override def delete($arg0: java.lang.String) = throw new java.lang.UnsupportedOperationException("This method cannot be accessed remotely.")
  override def createGroup(group: com.edifecs.epp.security.data.UserGroup) = isc.sendSync(address, "group.createGroup", Map[String, java.io.Serializable](("group", group))).asInstanceOf[com.edifecs.epp.security.data.UserGroup]
  override def getGroupsForTenant(id: java.lang.Long, startRecord: Long, recordCount: Long) = isc.sendSync(address, "group.getGroupsForTenant", Map[String, java.io.Serializable](("id", id), ("startRecord", startRecord), ("recordCount", recordCount))).asInstanceOf[com.edifecs.epp.security.data.PaginatedList[com.edifecs.epp.security.data.UserGroup]]
  override def createGroupForTenant(tenant: com.edifecs.epp.security.data.Tenant, group: com.edifecs.epp.security.data.UserGroup) = isc.sendSync(address, "group.createGroupForTenant", Map[String, java.io.Serializable](("tenant", tenant), ("group", group))).asInstanceOf[com.edifecs.epp.security.data.UserGroup]
  override def getChildGroupsForGroup(group: com.edifecs.epp.security.data.UserGroup) = isc.sendSync(address, "group.getChildGroupsForGroup", Map[String, java.io.Serializable](("group", group))).asInstanceOf[java.util.Collection[com.edifecs.epp.security.data.UserGroup]]
  override def addGroupToUser(group: com.edifecs.epp.security.data.UserGroup, user: com.edifecs.epp.security.data.User) = isc.sendSync(address, "group.addGroupToUser", Map[String, java.io.Serializable](("group", group), ("user", user)))
  override def addChildGroupsToGroup(group: com.edifecs.epp.security.data.UserGroup, parentgGroup: com.edifecs.epp.security.data.UserGroup) = isc.sendSync(address, "group.addChildGroupsToGroup", Map[String, java.io.Serializable](("group", group), ("parentgGroup", parentgGroup)))
  override def addGroupsToUser(groups: java.util.ArrayList[com.edifecs.epp.security.data.UserGroup], user: com.edifecs.epp.security.data.User) = isc.sendSync(address, "group.addGroupsToUser", Map[String, java.io.Serializable](("groups", groups), ("user", user)))
  override def addUsersToGroup(group: com.edifecs.epp.security.data.UserGroup, users: java.util.ArrayList[com.edifecs.epp.security.data.User]) = isc.sendSync(address, "group.addUsersToGroup", Map[String, java.io.Serializable](("group", group), ("users", users)))
  override def addOrganizationToGroup(group: com.edifecs.epp.security.data.UserGroup, organization: com.edifecs.epp.security.data.Organization) = isc.sendSync(address, "group.addOrganizationToGroup", Map[String, java.io.Serializable](("group", group), ("organization", organization)))
  override def addOrganizationsToGroup(group: com.edifecs.epp.security.data.UserGroup, organizations: java.util.ArrayList[com.edifecs.epp.security.data.Organization]) = isc.sendSync(address, "group.addOrganizationsToGroup", Map[String, java.io.Serializable](("group", group), ("organizations", organizations)))
  override def removeOrganizationsFromGroup(group: com.edifecs.epp.security.data.UserGroup, organizations: java.util.ArrayList[com.edifecs.epp.security.data.Organization]) = isc.sendSync(address, "group.removeOrganizationsFromGroup", Map[String, java.io.Serializable](("group", group), ("organizations", organizations)))
  override def removeOrganizationFromGroup(group: com.edifecs.epp.security.data.UserGroup, organization: com.edifecs.epp.security.data.Organization) = isc.sendSync(address, "group.removeOrganizationFromGroup", Map[String, java.io.Serializable](("group", group), ("organization", organization)))
  override def removeUsersFromGroup(group: com.edifecs.epp.security.data.UserGroup, users: java.util.ArrayList[com.edifecs.epp.security.data.User]) = isc.sendSync(address, "group.removeUsersFromGroup", Map[String, java.io.Serializable](("group", group), ("users", users)))
  override def removeGroupsFromUser(groups: java.util.ArrayList[com.edifecs.epp.security.data.UserGroup], user: com.edifecs.epp.security.data.User) = isc.sendSync(address, "group.removeGroupsFromUser", Map[String, java.io.Serializable](("groups", groups), ("user", user)))
  override def removeGroupFromUser(group: com.edifecs.epp.security.data.UserGroup, user: com.edifecs.epp.security.data.User) = isc.sendSync(address, "group.removeGroupFromUser", Map[String, java.io.Serializable](("group", group), ("user", user)))
  override def getGroupsForUser(userId: Long, startRecord: Long, recordCount: Long) = isc.sendSync(address, "group.getGroupsForUser", Map[String, java.io.Serializable](("userId", userId), ("startRecord", startRecord), ("recordCount", recordCount))).asInstanceOf[com.edifecs.epp.security.data.PaginatedList[com.edifecs.epp.security.data.UserGroup]]
  override def importGroupsFromJson(inputStream: java.io.InputStream) = isc.sendSync(address, "group.importGroupsFromJson", Map[String, java.io.Serializable](("inputStream", com.edifecs.epp.isc.stream.MessageStream.fromInputStream(inputStream)))).asInstanceOf[java.lang.String]
  override def validateImportGroups(inputStream: java.io.InputStream) = isc.sendSync(address, "group.validateImportGroups", Map[String, java.io.Serializable](("inputStream", com.edifecs.epp.isc.stream.MessageStream.fromInputStream(inputStream)))).asInstanceOf[java.lang.String]
  override def post($arg0: com.edifecs.epp.security.data.UserGroup) = throw new java.lang.UnsupportedOperationException("This method cannot be accessed remotely.")
  override def deleteGroup(id: java.lang.Long) = isc.sendSync(address, "group.deleteGroup", Map[String, java.io.Serializable](("id", id))).asInstanceOf[Boolean]
  override def deleteGroups(ids: java.util.ArrayList[java.lang.Long]) = isc.sendSync(address, "group.deleteGroups", Map[String, java.io.Serializable](("ids", ids))).asInstanceOf[Boolean]
  override def getGroups(startRecord: Long, recordCount: Long) = isc.sendSync(address, "group.getGroups", Map[String, java.io.Serializable](("startRecord", startRecord), ("recordCount", recordCount))).asInstanceOf[com.edifecs.epp.security.data.PaginatedList[com.edifecs.epp.security.data.UserGroup]]
  override def getGroupById(id: java.lang.Long) = isc.sendSync(address, "group.getGroupById", Map[String, java.io.Serializable](("id", id))).asInstanceOf[com.edifecs.epp.security.data.UserGroup]
  override def updateGroup(group: com.edifecs.epp.security.data.UserGroup) = isc.sendSync(address, "group.updateGroup", Map[String, java.io.Serializable](("group", group))).asInstanceOf[com.edifecs.epp.security.data.UserGroup]
  override def restCommand($2dx$2drest$2dmethod: java.lang.String, $2dx$2durl$2dsuffix: java.lang.String, $2dx$2drequest$2dbody: com.edifecs.epp.isc.json.JsonArg, page: java.lang.Long, start: java.lang.Long, limit: java.lang.Long, query: java.lang.String, sort: java.lang.String, filter: java.lang.String) = isc.sendSync(address, "group", Map[String, java.io.Serializable](("-x-rest-method", $2dx$2drest$2dmethod), ("-x-url-suffix", $2dx$2durl$2dsuffix), ("-x-request-body", $2dx$2drequest$2dbody), ("page", page), ("start", start), ("limit", limit), ("query", query), ("sort", sort), ("filter", filter))).asInstanceOf[java.io.Serializable]
}