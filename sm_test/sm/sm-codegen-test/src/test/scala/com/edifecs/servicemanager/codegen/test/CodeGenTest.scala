package com.edifecs.servicemanager.codegen.test

import com.edifecs.servicemanager.codegen.ProxyGenerator

import org.specs2.mutable._

import treehugger.forest._

class CodeGenTest extends Specification {
  "The code generator" should {
    "generate proxy classes from command handler interfaces" in {
      println(treeToString(ProxyGenerator.proxyForCommandHandler(classOf[ITestServiceCommandHandler])))
      todo
    }
    "generate proxy classes from service interfaces" in {
      println(treeToString(ProxyGenerator.proxyForService(classOf[ITestService])))
      todo
    }
  }
}

