// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.epp.security.apps.handler

import com.edifecs.epp.isc.Args

class __GeneratedProxy__IAppStoreHandler(isc: com.edifecs.epp.isc.Isc, serviceTypeName: java.lang.String) extends IAppStoreHandler {
  def address = isc.getAddressRegistry.getAddressForServiceTypeName(serviceTypeName)
  override def getTenantAppConfigurations = isc.sendSync(address, "AppStore.getTenantAppConfigurations", Map.empty[String, java.io.Serializable]).asInstanceOf[java.util.Map[java.lang.String, java.util.List[com.edifecs.epp.flexfields.model.FlexGroup]]]
  override def getInstalledApps(tenantId: Long) = isc.sendSync(address, "AppStore.getInstalledApps", Map[String, java.io.Serializable](("tenantId", tenantId))).asInstanceOf[Array[com.edifecs.epp.security.apps.model.App]]
  override def getAppCatalog(startRecord: Long, recordCount: Long) = isc.sendSync(address, "AppStore.getAppCatalog", Map[String, java.io.Serializable](("startRecord", startRecord), ("recordCount", recordCount))).asInstanceOf[Array[java.lang.Object]]
  override def getAppStatus(appName: java.lang.String, tenantId: Long) = isc.sendSync(address, "AppStore.getAppStatus", Map[String, java.io.Serializable](("appName", appName), ("tenantId", tenantId))).asInstanceOf[com.edifecs.epp.security.apps.model.AppStatus]
  override def getAvailableApps(tenantId: Long) = isc.sendSync(address, "AppStore.getAvailableApps", Map[String, java.io.Serializable](("tenantId", tenantId))).asInstanceOf[Array[com.edifecs.epp.security.apps.model.App]]
  override def getAppConfiguration(appName: java.lang.String, tenantId: Long) = isc.sendSync(address, "AppStore.getAppConfiguration", Map[String, java.io.Serializable](("appName", appName), ("tenantId", tenantId))).asInstanceOf[java.util.List[com.edifecs.epp.flexfields.model.FlexGroup]]
  override def sendInstallAppRequest(appName: java.lang.String, tenantId: Long) = isc.sendSync(address, "AppStore.sendInstallAppRequest", Map[String, java.io.Serializable](("appName", appName), ("tenantId", tenantId))).asInstanceOf[Boolean]
  override def getAppStoreName = isc.sendSync(address, "AppStore.getAppStoreName", Map.empty[String, java.io.Serializable]).asInstanceOf[java.lang.String]
}