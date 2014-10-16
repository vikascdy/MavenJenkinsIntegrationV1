// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package codegentest

import com.edifecs.epp.isc.Args

class __GeneratedProxy__HandlerB(isc: com.edifecs.epp.isc.Isc, serviceTypeName: java.lang.String) extends HandlerB {
  def address = isc.getAddressRegistry.getAddressForServiceTypeName(serviceTypeName)
  override def alpha = isc.sendSync(address, "b.alpha", Map.empty[String, java.io.Serializable]).asInstanceOf[java.lang.String]
  override def beta = isc.send(address, "b.beta", Map.empty[String, java.io.Serializable]).as(classOf[java.lang.String])
}