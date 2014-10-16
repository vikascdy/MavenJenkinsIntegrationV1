// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package codegentest

import com.edifecs.epp.isc.Args

class __GeneratedProxy__HandlerA(isc: com.edifecs.epp.isc.Isc, serviceTypeName: java.lang.String) extends HandlerA {
  def address = isc.getAddressRegistry.getAddressForServiceTypeName(serviceTypeName)
  override def fooCommand(arg: java.lang.String) = isc.sendSync(address, "foo", Map[String, java.io.Serializable](("arg", arg))).asInstanceOf[java.lang.String]
  override def barCommand(arg: java.lang.String) = isc.send(address, "bar", Map[String, java.io.Serializable](("arg", arg))).as(classOf[java.lang.String])
  override def arrayCommand(charArray: Array[Char], stringArray: Array[java.lang.String]) = isc.sendSync(address, "arrayCommand", Map[String, java.io.Serializable](("charArray", charArray), ("stringArray", stringArray))).asInstanceOf[Array[Int]]
}