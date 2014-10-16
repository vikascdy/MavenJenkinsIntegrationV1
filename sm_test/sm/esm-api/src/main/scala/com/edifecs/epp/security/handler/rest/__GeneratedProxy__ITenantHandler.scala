// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.epp.security.handler.rest

import com.edifecs.epp.isc.Args

class __GeneratedProxy__ITenantHandler(isc: com.edifecs.epp.isc.Isc, serviceTypeName: java.lang.String) extends ITenantHandler {
  def address = isc.getAddressRegistry.getAddressForServiceTypeName(serviceTypeName)
  override def get($arg0: java.lang.String) = throw new java.lang.UnsupportedOperationException("This method cannot be accessed remotely.")
  override def put($arg0: java.lang.String, $arg1: com.edifecs.epp.security.data.Tenant) = throw new java.lang.UnsupportedOperationException("This method cannot be accessed remotely.")
  override def list($arg0: com.edifecs.epp.isc.core.command.Pagination) = throw new java.lang.UnsupportedOperationException("This method cannot be accessed remotely.")
  override def delete($arg0: java.lang.String) = throw new java.lang.UnsupportedOperationException("This method cannot be accessed remotely.")
  override def post($arg0: com.edifecs.epp.security.data.Tenant) = throw new java.lang.UnsupportedOperationException("This method cannot be accessed remotely.")
  override def createTenant(tenant: com.edifecs.epp.security.data.Tenant) = isc.sendSync(address, "tenant.createTenant", Map[String, java.io.Serializable](("tenant", tenant))).asInstanceOf[com.edifecs.epp.security.data.Tenant]
  override def deleteTenant(id: java.lang.Long) = isc.sendSync(address, "tenant.deleteTenant", Map[String, java.io.Serializable](("id", id))).asInstanceOf[Boolean]
  override def deleteTenants(ids: java.util.ArrayList[java.lang.Long]) = isc.sendSync(address, "tenant.deleteTenants", Map[String, java.io.Serializable](("ids", ids))).asInstanceOf[Boolean]
  override def getTenantById(id: java.lang.Long) = isc.sendSync(address, "tenant.getTenantById", Map[String, java.io.Serializable](("id", id))).asInstanceOf[com.edifecs.epp.security.data.Tenant]
  override def getTenants(startRecord: Long, recordCount: Long) = isc.sendSync(address, "tenant.getTenants", Map[String, java.io.Serializable](("startRecord", startRecord), ("recordCount", recordCount))).asInstanceOf[com.edifecs.epp.security.data.PaginatedList[com.edifecs.epp.security.data.Tenant]]
  override def updateTenant(tenant: com.edifecs.epp.security.data.Tenant) = isc.sendSync(address, "tenant.updateTenant", Map[String, java.io.Serializable](("tenant", tenant))).asInstanceOf[com.edifecs.epp.security.data.Tenant]
  override def getTenantLogo(tenant: java.lang.String) = isc.sendSync(address, "tenant.getTenantLogo", Map[String, java.io.Serializable](("tenant", tenant))).asInstanceOf[java.lang.String]
  override def createTenantForSite(site: com.edifecs.epp.security.data.Site, tenant: com.edifecs.epp.security.data.Tenant) = isc.sendSync(address, "tenant.createTenantForSite", Map[String, java.io.Serializable](("site", site), ("tenant", tenant))).asInstanceOf[com.edifecs.epp.security.data.Tenant]
  override def getTenantByName(canonicalName: java.lang.String) = isc.sendSync(address, "tenant.getTenantByName", Map[String, java.io.Serializable](("canonicalName", canonicalName))).asInstanceOf[com.edifecs.epp.security.data.Tenant]
  override def updateTenantPasswordPolicy(tenantId: java.lang.Long, policy: com.edifecs.epp.security.data.PasswordPolicy) = isc.sendSync(address, "tenant.updateTenantPasswordPolicy", Map[String, java.io.Serializable](("tenantId", tenantId), ("policy", policy))).asInstanceOf[com.edifecs.epp.security.data.Tenant]
  override def updateTenantLogo(tenantId: java.lang.Long, data: java.lang.String) = isc.sendSync(address, "tenant.updateTenantLogo", Map[String, java.io.Serializable](("tenantId", tenantId), ("data", data))).asInstanceOf[java.lang.Boolean]
  override def updateTenantLandingPage(tenantId: java.lang.Long, landingPage: java.lang.String) = isc.sendSync(address, "tenant.updateTenantLandingPage", Map[String, java.io.Serializable](("tenantId", tenantId), ("landingPage", landingPage))).asInstanceOf[java.lang.Boolean]
  override def setTenantLandingPage(tenantId: java.lang.Long, landingPage: java.lang.String) = isc.sendSync(address, "tenant.setTenantLandingPage", Map[String, java.io.Serializable](("tenantId", tenantId), ("landingPage", landingPage))).asInstanceOf[java.lang.Boolean]
  override def getTenantLandingPage(tenantId: java.lang.Long) = isc.sendSync(address, "tenant.getTenantLandingPage", Map[String, java.io.Serializable](("tenantId", tenantId))).asInstanceOf[java.lang.String]
  override def importTenantFromJson(inputStream: java.io.InputStream) = isc.sendSync(address, "tenant.importTenantFromJson", Map[String, java.io.Serializable](("inputStream", com.edifecs.epp.isc.stream.MessageStream.fromInputStream(inputStream)))).asInstanceOf[java.lang.String]
  override def validateImportTenants(inputStream: java.io.InputStream) = isc.sendSync(address, "tenant.validateImportTenants", Map[String, java.io.Serializable](("inputStream", com.edifecs.epp.isc.stream.MessageStream.fromInputStream(inputStream)))).asInstanceOf[java.lang.String]
  override def restCommand($2dx$2drest$2dmethod: java.lang.String, $2dx$2durl$2dsuffix: java.lang.String, $2dx$2drequest$2dbody: com.edifecs.epp.isc.json.JsonArg, page: java.lang.Long, start: java.lang.Long, limit: java.lang.Long, query: java.lang.String, sort: java.lang.String, filter: java.lang.String) = isc.sendSync(address, "tenant", Map[String, java.io.Serializable](("-x-rest-method", $2dx$2drest$2dmethod), ("-x-url-suffix", $2dx$2durl$2dsuffix), ("-x-request-body", $2dx$2drequest$2dbody), ("page", page), ("start", start), ("limit", limit), ("query", query), ("sort", sort), ("filter", filter))).asInstanceOf[java.io.Serializable]
}