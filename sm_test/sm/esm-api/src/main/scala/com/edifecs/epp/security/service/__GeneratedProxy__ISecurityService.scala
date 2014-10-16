// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.epp.security.service

class __GeneratedProxy__ISecurityService(isc: com.edifecs.epp.isc.Isc) extends ISecurityService {
  val serviceType = "esm-service"
  override lazy val groups = new com.edifecs.epp.security.handler.rest.__GeneratedProxy__IUserGroupHandler(isc, serviceType)
  override lazy val permissions = new com.edifecs.epp.security.handler.rest.__GeneratedProxy__IPermissionHandler(isc, serviceType)
  override lazy val authentication = new com.edifecs.epp.security.__GeneratedProxy__IAuthenticationManager(isc, serviceType)
  override lazy val authorization = new com.edifecs.epp.security.__GeneratedProxy__IAuthorizationManager(isc, serviceType)
  override lazy val flexfields = new com.edifecs.epp.security.flexfields.__GeneratedProxy__IFlexFieldHandler(isc, serviceType)
  override lazy val roles = new com.edifecs.epp.security.handler.rest.__GeneratedProxy__IRoleHandler(isc, serviceType)
  override lazy val subjects = new com.edifecs.epp.security.__GeneratedProxy__ISubjectManager(isc, serviceType)
  override lazy val users = new com.edifecs.epp.security.handler.rest.__GeneratedProxy__IUserHandler(isc, serviceType)
  override lazy val organizations = new com.edifecs.epp.security.handler.rest.__GeneratedProxy__IOrganizationHandler(isc, serviceType)
  override lazy val sites = new com.edifecs.epp.security.handler.rest.__GeneratedProxy__ISiteCommandHandler(isc, serviceType)
  override lazy val tenants = new com.edifecs.epp.security.handler.rest.__GeneratedProxy__ITenantHandler(isc, serviceType)
  override lazy val appstore = new com.edifecs.epp.security.apps.handler.__GeneratedProxy__IAppStoreHandler(isc, serviceType)
  override lazy val administrativeData = new com.edifecs.epp.security.handler.__GeneratedProxy__IAdministrativeDataCommandHandler(isc, serviceType)
  override lazy val sessions = new com.edifecs.epp.security.__GeneratedProxy__ISessionManager(isc, serviceType)
}