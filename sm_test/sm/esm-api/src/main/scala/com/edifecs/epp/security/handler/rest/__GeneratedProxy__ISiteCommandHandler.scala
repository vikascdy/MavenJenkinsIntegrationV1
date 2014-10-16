// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.epp.security.handler.rest

import com.edifecs.epp.isc.Args

class __GeneratedProxy__ISiteCommandHandler(isc: com.edifecs.epp.isc.Isc, serviceTypeName: java.lang.String) extends ISiteCommandHandler {
  def address = isc.getAddressRegistry.getAddressForServiceTypeName(serviceTypeName)
  override def getSite = isc.sendSync(address, "site.getSite", Map.empty[String, java.io.Serializable]).asInstanceOf[com.edifecs.epp.security.data.Site]
  override def updateSite(Site: com.edifecs.epp.security.data.Site) = isc.sendSync(address, "site.updateSite", Map[String, java.io.Serializable](("Site", Site))).asInstanceOf[com.edifecs.epp.security.data.Site]
}