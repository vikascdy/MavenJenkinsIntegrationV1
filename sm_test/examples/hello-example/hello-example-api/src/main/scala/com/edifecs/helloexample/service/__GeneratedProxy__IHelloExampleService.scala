// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.helloexample.service

class __GeneratedProxy__IHelloExampleService(isc: com.edifecs.epp.isc.Isc) extends IHelloExampleService {
  val serviceType = "hello-example-service"
  override lazy val getHelloExampleCommandHandler = new com.edifecs.helloexample.handler.__GeneratedProxy__IHelloExampleHandler(isc, serviceType)
}