// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.helloexample.handler

import com.edifecs.epp.isc.Args

class __GeneratedProxy__IHelloExampleHandler(isc: com.edifecs.epp.isc.Isc, serviceTypeName: java.lang.String) extends IHelloExampleHandler {
  def address = isc.getAddressRegistry.getAddressForServiceTypeName(serviceTypeName)
  override def greeting = isc.sendSync(address, "hello.greeting", Map.empty[String, java.io.Serializable]).asInstanceOf[com.edifecs.helloexample.api.HelloMessage]
  override def greetingFromTheFuture = isc.send(address, "hello.greetingFromTheFuture", Map.empty[String, java.io.Serializable]).as(classOf[com.edifecs.helloexample.api.HelloMessage])
}