package com.edifecs.build

import java.lang.annotation.Annotation
import java.lang.reflect.Modifier

import sbt._
import sbt.Keys._
import sbt.classpath.ClasspathUtilities
import sbt.compiler.CompileFailed

object CodeGen {

  final val proxyClassPrefix = "__GeneratedProxy__"

  private final val proxyGeneratorClassName   = "com.edifecs.servicemanager.codegen.ProxyGenerator"
  private final val serviceAnnClassName       = "com.edifecs.servicemanager.annotations.Service"
  private final val handlerAnnClassName       = "com.edifecs.servicemanager.annotations.Handler"
  private final val commandHandlerAnnClassName= "com.edifecs.epp.isc.annotations.CommandHandler"
  private final val iscClassName              = "com.edifecs.epp.isc.Isc$"

  lazy val codeGenSettings = Seq(
    serviceInterfaces := Seq.empty,
    generateProxyClasses in Compile <<= Def.task {
      val log = streams.value.log
      try {
        val classpath = (fullClasspath in Runtime).value
        val javaSourcePath = (javaSource in Compile).value
        val scalaSourcePath = (scalaSource in Compile).value
        implicit val loader: ClassLoader = ClasspathUtilities.toLoader(
          classpath.map(_.data).map(_.getAbsoluteFile))
        // Delete any old, unused generated files.
        findGeneratedSourceFiles(scalaSourcePath, proxyClassPrefix).filter { f =>
          val scalaPath = f.getAbsolutePath.replace(proxyClassPrefix, "")
          val javaPath = javaSourcePath.getAbsolutePath +
            scalaPath.substring(scalaSourcePath.getAbsolutePath.length)
              .replace(".scala", ".java")
          !(new File(scalaPath).exists) && !(new File(javaPath).exists)
        } foreach { f =>
          log.warn(s"Deleting proxy source file ${f.getName} (no matching interface file).")
          f.delete()
        }
        // Generate and compile the proxy classes.
        val classes = getClassesToGenerateProxiesFor(
          serviceInterfaces.value,
          javaSourcePath, scalaSourcePath, (classDirectory in Compile).value)
        if (!classes.isEmpty) {
          log.info(s"Generating ${classes.size} proxy class source file(s) for project ${name.value}...")
          val files = classes.map(generateProxy(scalaSourcePath, _))
          log.info("Compiling proxy classes:")
          val inputs = (compileInputs in (Compile, compile)).value
          val newInputs = inputs.copy(
            config = inputs.config.copy(
              sources = inputs.config.sources ++ files))
          Compiler(newInputs, log)
        }
      } catch {
        case cf: CompileFailed =>
        // Do nothing. The logger should already log this kind of error.
        case ex: Exception =>
          log.error(s"${ex.getClass.getSimpleName}: ${ex.getMessage}")
          log.trace(ex)
      }
    } triggeredBy(compile in Compile),
    generateProxyClasses in Test <<= Def.task {
      (generateProxyClasses in Compile).value
    } triggeredBy(compile in Test),
    clean <<= (clean, scalaSource in Compile) map { (_, scalaSourcePath) =>
      findGeneratedSourceFiles(scalaSourcePath, proxyClassPrefix).foreach(_.delete())
    }
  )

  lazy val generateProxyClasses = taskKey[Any](
    "Code generation step; occurs after compilation and may trigger a recompile.")
  lazy val serviceInterfaces = settingKey[Seq[String]](
    "Names of service interfaces to generate proxy classes for.")
  
  private def loadSmClass(className: String)(implicit loader: ClassLoader): Class[_] =
    try {
      loader.loadClass(className)
    } catch {
      case e: ClassNotFoundException =>
        throw new IllegalStateException(
          s"Could not find class '$className' while attempting code generation. All projects " +
          " that use code generation must include the 'sm-codegen' project as a dependency.")
    }

  private def getSourceFile(root: File, className: String, suffix: String): File =
    (className.replace('.', '/') + suffix).split("/").foldLeft(root)((a: File, b) => a / b)

  private def getOriginalAndProxySourceFiles(
    className: String,
    javaSrcRoot: File,
    scalaSrcRoot: File,
    classRoot: File
  )(
    implicit loader: ClassLoader
  ): (File, File, File) = {
    // Locate the original source file (necessary in order to compare
    // timestamps to determine whether to generate new proxy classes).
    var sourceFile = getSourceFile(javaSrcRoot, className, ".java")
    if (!sourceFile.exists) sourceFile = getSourceFile(scalaSrcRoot, className, ".scala")
    if (!sourceFile.exists) {
      throw new IllegalStateException(
        s"The class $className is not defined in a Java or Scala file with the same name in" +
        " this project. Proxy classes can only be generated from classes defined in files in the" +
        " current project, and with the same name and package path as the defined class.")
    }
    var destFile = getSourceFile(scalaSrcRoot, getProxyClassName(className), ".scala")
    var classFile = getSourceFile(classRoot, getProxyClassName(className), ".class")
    (sourceFile, destFile, classFile)
  }

  private def getProxyClassName(className: String)(implicit loader: ClassLoader): String = {
    val lastDotIndex = className.lastIndexOf(".") + 1
    className.substring(0, lastDotIndex) + proxyClassPrefix + className.substring(lastDotIndex)
  }

  private def getClassesToGenerateProxiesFor(
    serviceClassNames: Seq[String],
    javaSrcRoot: File,
    scalaSrcRoot: File,
    classRoot: File
  )(
    implicit loader: ClassLoader
  ) =
    serviceClassNames flatMap { className =>
      // Attempt to load the class first, so that missing classes will throw
      // ClassNotFoundExceptions rather than more cryptic errors.
      val serviceClass = loader.loadClass(className)
      val handlerClass = loadSmClass(handlerAnnClassName).asInstanceOf[Class[_ <: Annotation]]
      
      val (sourceFile, destFile, classFile) = getOriginalAndProxySourceFiles(className,
        javaSrcRoot, scalaSrcRoot, classRoot)
      if (!destFile.exists  || destFile.lastModified < sourceFile.lastModified ||
          !classFile.exists || classFile.lastModified < sourceFile.lastModified) {
        Seq(serviceClass) ++ serviceClass.getMethods.filter { m =>
          m.isAnnotationPresent(handlerClass) &&
          (m.getModifiers & Modifier.ABSTRACT) != 0 && {
            val (hsrcFile, hdestFile, hclsFile) = getOriginalAndProxySourceFiles(
              m.getReturnType.getName, javaSrcRoot, scalaSrcRoot, classRoot)
            !hdestFile.exists || hdestFile.lastModified < hsrcFile.lastModified ||
            !hclsFile.exists  || hclsFile.lastModified < hsrcFile.lastModified
          }
        }.map(_.getReturnType)
      } else Nil
    }

  private def generateProxy(baseFile: File, c: Class[_])(implicit loader: ClassLoader): File = {
    val serviceClass = loadSmClass(serviceAnnClassName).asInstanceOf[Class[_ <: Annotation]]
    lazy val handlerClass = loadSmClass(commandHandlerAnnClassName)
      .asInstanceOf[Class[_ <: Annotation]]
    lazy val proxyGenClass = loadSmClass(proxyGeneratorClassName)
    val src = if (c.isAnnotationPresent(serviceClass)) {
      proxyGenClass.getMethod("proxyForService", classOf[Class[_]]).invoke(null, c).toString
    } else if (c.isAnnotationPresent(handlerClass)) {
      proxyGenClass.getMethod("proxyForCommandHandler", classOf[Class[_]]).invoke(null, c).toString
    } else {
      throw new UnsupportedOperationException(
        s"Cannot generate a proxy for the class ${c.getName}: This class is not annotated with" +
        s" either $serviceAnnClassName or $commandHandlerAnnClassName.")
    }
    val path = getSourceFile(baseFile, getProxyClassName(c.getName), ".scala")
    IO.write(path, src)
    path
  }

  private def findGeneratedSourceFiles(root: File, prefix: String): Seq[File] =
    if (root.exists && root.isDirectory) {
      root.listFiles.flatMap { f =>
        if (f.isDirectory) findGeneratedSourceFiles(f, prefix)
        else if (f.getName.startsWith(prefix) && f.getName.endsWith(".scala")) f :: Nil
        else Nil
      }
    } else Nil
}

