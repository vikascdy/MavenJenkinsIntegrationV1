// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.contentrepository

import com.edifecs.epp.isc.Args

class __GeneratedProxy__IContentRepositoryHandler(isc: com.edifecs.epp.isc.Isc, serviceTypeName: java.lang.String) extends IContentRepositoryHandler {
  def address = isc.getAddressRegistry.getAddressForServiceTypeName(serviceTypeName)
  override def getProperties(path: java.lang.String) = isc.sendSync(address, "getProperties", Map[String, java.io.Serializable](("path", path))).asInstanceOf[java.util.HashMap[java.lang.String, java.lang.String]]
  override def getFile(path: java.lang.String, filename: java.lang.String, version: java.lang.String) = isc.sendSync(address, "getFile", Map[String, java.io.Serializable](("path", path), ("filename", filename), ("version", version))).asInstanceOf[com.edifecs.epp.isc.stream.MessageStream].toInputStream
  override def copyNode(srcPath: java.lang.String, destPath: java.lang.String) = isc.sendSync(address, "copyNode", Map[String, java.io.Serializable](("srcPath", srcPath), ("destPath", destPath)))
  override def isAvailable = isc.sendSync(address, "isAvailable", Map.empty[String, java.io.Serializable]).asInstanceOf[Boolean]
  override def addFile(path: java.lang.String, filename: java.lang.String, inputStream: java.io.InputStream) = isc.sendSync(address, "addFile", Map[String, java.io.Serializable](("path", path), ("filename", filename), ("inputStream", com.edifecs.epp.isc.stream.MessageStream.fromInputStream(inputStream)))).asInstanceOf[java.lang.String]
  override def setupTenantRepository(repositoryPath: java.lang.String, tenant: com.edifecs.epp.security.data.Tenant) = isc.sendSync(address, "setupTenantRepository", Map[String, java.io.Serializable](("repositoryPath", repositoryPath), ("tenant", tenant)))
  override def addUserToTenantRepository(tenant: com.edifecs.epp.security.data.Tenant, username: java.lang.String, password: java.lang.String, admin: Boolean) = isc.sendSync(address, "addUserToTenantRepository", Map[String, java.io.Serializable](("tenant", tenant), ("username", username), ("password", password), ("admin", admin)))
  override def shareContentWithUser(path: java.lang.String, filename: java.lang.String, username: java.lang.String) = isc.sendSync(address, "shareContentWithUser", Map[String, java.io.Serializable](("path", path), ("filename", filename), ("username", username))).asInstanceOf[java.lang.String]
  override def getNode(path: java.lang.String) = isc.sendSync(address, "getNode", Map[String, java.io.Serializable](("path", path))).asInstanceOf[com.edifecs.contentrepository.api.ContentNode]
  override def createFolder(path: java.lang.String) = isc.sendSync(address, "createFolder", Map[String, java.io.Serializable](("path", path)))
  override def deleteFile(path: java.lang.String, filename: java.lang.String) = isc.sendSync(address, "deleteFile", Map[String, java.io.Serializable](("path", path), ("filename", filename)))
  override def deleteFolder(path: java.lang.String) = isc.sendSync(address, "deleteFolder", Map[String, java.io.Serializable](("path", path)))
  override def getHistory(path: java.lang.String) = isc.sendSync(address, "getHistory", Map[String, java.io.Serializable](("path", path))).asInstanceOf[java.util.ArrayList[com.edifecs.contentrepository.api.FileVersion]]
  override def getStatistics = isc.sendSync(address, "getStatistics", Map.empty[String, java.io.Serializable]).asInstanceOf[java.lang.String]
  override def moveNode(srcPath: java.lang.String, destPath: java.lang.String) = isc.sendSync(address, "moveNode", Map[String, java.io.Serializable](("srcPath", srcPath), ("destPath", destPath)))
  override def updateFile(path: java.lang.String, filename: java.lang.String, inputStream: java.io.InputStream) = isc.sendSync(address, "updateFile", Map[String, java.io.Serializable](("path", path), ("filename", filename), ("inputStream", com.edifecs.epp.isc.stream.MessageStream.fromInputStream(inputStream)))).asInstanceOf[java.lang.String]
  override def viewFolder(path: java.lang.String) = isc.sendSync(address, "viewFolder", Map[String, java.io.Serializable](("path", path))).asInstanceOf[java.util.ArrayList[com.edifecs.contentrepository.api.ContentNode]]
}