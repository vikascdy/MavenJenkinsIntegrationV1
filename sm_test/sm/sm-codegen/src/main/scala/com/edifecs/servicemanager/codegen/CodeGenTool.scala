package com.edifecs.servicemanager.codegen

import java.io.{File, FileOutputStream}
import java.lang.reflect.Modifier

import com.edifecs.epp.isc.Isc
import com.edifecs.epp.isc.annotations.CommandHandler
import com.edifecs.servicemanager.annotations.{Service, Handler}

object CodeGenTool {

  final val defaultOutputDir = "src/main/scala/"
  final val outputSwitch = "-o"
  final val usage = s"""
USAGE: CodeGenTool [$outputSwitch outputDir] fully.qualified.ServiceInterfaceNames...

At least one service interface name must be provided. All service interfaces
mst be on the Java classpath (if not, a ClassNotFoundException will occur).

If outputDir is not specified, it defaults to '$defaultOutputDir'.
"""

  def main(args: Array[String]): Unit = {
    try {
      if (args.isEmpty) {
        println(usage)
        System.exit(1)
      }
      var argsList = args.toList
      val outputDir = new File(
        if (argsList.head == outputSwitch) {
          argsList = argsList.tail
          if (argsList.isEmpty) {
            println(usage)
            System.exit(1)
          }
          val r = argsList.head
          argsList = argsList.tail
          r
        } else defaultOutputDir)

      val classes = getClassesToGenerateProxiesFor(argsList)
      if (classes.isEmpty) {
        println(usage)
        System.exit(1)
      } else {
        println(s"Generating ${classes.size} proxy class source file(s) in '$outputDir'...")
        val files = classes.map(generateProxy(outputDir, _))
        println("Done!")
      }
    } catch {
      case ex: Exception =>
        println("Code generation failed.")
        ex.printStackTrace()
        System.exit(1)
    }
  }

  private def getSourceFile(root: File, className: String, suffix: String): File = new File(
    (className.replace('.', '/') + suffix).split("/").foldLeft(root.getAbsolutePath) { (a, b) =>
      a + File.separator + b
    })

  private def getProxyClassName(className: String): String = {
    val lastDotIndex = className.lastIndexOf(".") + 1
    className.substring(0, lastDotIndex) + Isc.proxyClassPrefix + className.substring(lastDotIndex)
  }

  private def getClassesToGenerateProxiesFor(serviceClassNames: Seq[String]) =
    serviceClassNames flatMap { className =>
      val serviceClass = getClass.getClassLoader.loadClass(className)
      Seq(serviceClass) ++ serviceClass.getMethods.filter { m =>
        m.isAnnotationPresent(classOf[Handler]) && (m.getModifiers & Modifier.ABSTRACT) != 0
      }.map(_.getReturnType)
    }

  private def generateProxy(baseFile: File, c: Class[_]): File = {
    val src = if (c.isAnnotationPresent(classOf[Service])) {
      ProxyGenerator.proxyForService(c)
    } else if (c.isAnnotationPresent(classOf[CommandHandler])) {
      ProxyGenerator.proxyForCommandHandler(c)
    } else {
      throw new UnsupportedOperationException(
        s"Cannot generate a proxy for the class ${c.getName}: This class is not annotated with" +
        s" either ${classOf[Service].getName} or ${classOf[CommandHandler].getName}.")
    }
    val path = getSourceFile(baseFile, getProxyClassName(c.getName), ".scala")
    path.getParentFile.mkdirs()
    val out = new FileOutputStream(path)
    try out.write(src.getBytes)
    finally out.close()
    path
  }
}

