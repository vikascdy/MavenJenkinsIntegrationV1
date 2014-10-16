package codegentest

import java.nio.file.Files

import org.specs2.mutable._

import com.edifecs.epp.isc.Isc
import com.edifecs.servicemanager.codegen.CodeGenTool

class CodeGenToolTest extends Specification {
  "The CodeGenTool" should {
    "work" in {
      val tempDir = Files.createTempDirectory("codegentest")
      CodeGenTool.main(Array(
        CodeGenTool.outputSwitch, tempDir.toString, classOf[CodeGenTestService].getName))
      (Files.exists(tempDir.resolve("codegentest").resolve(Isc.proxyClassPrefix + "CodeGenTestService.scala")) must beTrue) and
      (Files.exists(tempDir.resolve("codegentest").resolve(Isc.proxyClassPrefix + "HandlerA.scala")) must beTrue) and
      (Files.exists(tempDir.resolve("codegentest").resolve(Isc.proxyClassPrefix + "HandlerB.scala")) must beTrue)
    }
  }
}


