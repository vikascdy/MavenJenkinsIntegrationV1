// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package codegentest

class __GeneratedProxy__CodeGenTestService(isc: com.edifecs.epp.isc.Isc) extends CodeGenTestService {
  val serviceType = "Code Generation Test Service"
  override lazy val handlerB = new codegentest.__GeneratedProxy__HandlerB(isc, serviceType)
  override lazy val handlerA = new codegentest.__GeneratedProxy__HandlerA(isc, serviceType)
}