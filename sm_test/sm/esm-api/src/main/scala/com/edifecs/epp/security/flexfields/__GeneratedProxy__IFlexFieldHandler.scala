// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.epp.security.flexfields

import com.edifecs.epp.isc.Args

class __GeneratedProxy__IFlexFieldHandler(isc: com.edifecs.epp.isc.Isc, serviceTypeName: java.lang.String) extends IFlexFieldHandler {
  def address = isc.getAddressRegistry.getAddressForServiceTypeName(serviceTypeName)
  override def getFields(contextMap: java.util.HashMap[java.lang.String, java.lang.String]) = isc.sendSync(address, "FlexField.getFields", Map[String, java.io.Serializable](("contextMap", contextMap))).asInstanceOf[java.util.Collection[com.edifecs.epp.flexfields.model.FlexGroup]]
  override def setFlexFieldValue(flexFieldValue: com.edifecs.epp.flexfields.model.FlexFieldValue) = isc.sendSync(address, "FlexField.setFlexFieldValue", Map[String, java.io.Serializable](("flexFieldValue", flexFieldValue))).asInstanceOf[com.edifecs.epp.flexfields.model.FlexFieldValue]
}