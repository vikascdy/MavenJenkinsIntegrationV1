// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.rest.spray.service

import com.edifecs.epp.isc.Args

class __GeneratedProxy__ISprayUrlHandler(isc: com.edifecs.epp.isc.Isc, serviceTypeName: java.lang.String) extends ISprayUrlHandler {
  def address = isc.getAddressRegistry.getAddressForServiceTypeName(serviceTypeName)
  override def registerCommandShortcutUrl$default$4 = throw new java.lang.UnsupportedOperationException("This method cannot be accessed remotely.")
  override def unregisterCommandShortcutUrl(url: java.lang.String) = isc.send(address, "unregisterCommandShortcutUrl", Map[String, java.io.Serializable](("url", url))).as(classOf[java.lang.Boolean])
  override def registerCommandShortcutUrl(url: java.lang.String, serviceType: java.lang.String, command: java.lang.String, urlSuffix: java.lang.String) = isc.send(address, "registerCommandShortcutUrl", Map[String, java.io.Serializable](("url", url), ("serviceType", serviceType), ("command", command), ("urlSuffix", urlSuffix))).as(classOf[java.lang.Boolean])
}