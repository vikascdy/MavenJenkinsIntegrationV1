import java.lang.management.{ManagementFactory, RuntimeMXBean}

import com.earldouglas.xsbtwebplugin.WebPlugin
import com.edifecs.build._
import com.edifecs.build.plugin.Bundle
import sbt._
import sbt.Keys._
import sbtfilter.Plugin.FilterKeys._

Common.directorySettings

lazy val sprayRoutingWithJson = Seq(Dependencies.sprayRouting, Dependencies.sprayJson)

lazy val sprayCanWithRoutingAndJson = Seq(Dependencies.sprayCan, Dependencies.sprayRouting, Dependencies.sprayJson, Dependencies.sprayCaching)

lazy val sprayServletWithRoutingAndJson = Seq(Dependencies.sprayServlet, Dependencies.sprayRouting, Dependencies.sprayJson)

lazy val sprayService = Project (
  id = "spray-service",
  base = file("spray-service"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.spray,
    libraryDependencies ++= sprayCanWithRoutingAndJson ++ Dependencies.specs2 ++ Seq(
      Dependencies.akka % "provided",
      Dependencies.gson % "provided",
      Dependencies.javaxServletApi,
      Dependencies.h2 % "test",
      Dependencies.dispatch % "test",
      Dependencies.esmService % "test"
    )
  )
).dependsOn(
  LocalProject("spray-service-api"),
  LocalProject("isc-core") % "provided",
  LocalProject("esm-api") % "provided",
  LocalProject("xboard-portal-service") % "test"
)

lazy val sprayServiceApi = Project (
  id = "spray-service-api",
  base = file("spray-service-api"),
  settings = Common.commonSettings ++ CodeGen.codeGenSettings ++ Seq(
    CodeGen.serviceInterfaces += "com.edifecs.rest.spray.service.ISprayService",
    version := Versions.spray
  )
).dependsOn(
  LocalProject("isc-api"),
  LocalProject("service-api"),
  LocalProject("sm-codegen")
)

lazy val sprayServiceDist = Project (
  id = "spray-service-dist",
  base = file("spray-service-dist"),
  settings =
    Common.commonSettings ++
      Bundle.bundleApplicationSettings ++ Seq(
      Bundle.bundleServices := Map(
        "spray-service" -> Map(
          file("web/spray") -> sprayServiceApi,
          file("web/spray") -> sprayService,
          file("web/spray") -> sprayServletUi
        )
      ),
      bundleHtmlContextRoot := "config",
      version := Versions.spray
    )
).dependsOn(
  sprayService,
  sprayServletUi
)

lazy val sprayServletUi = Project (
  id = "spray-servlet-ui",
  base = file("spray-servlet-ui"),
  settings = Common.commonSettings ++
    WebPlugin.webSettings ++ Seq(
    version := Versions.spray,
    libraryDependencies ++= sprayServletWithRoutingAndJson ++ Seq(
      Dependencies.akka,
      Dependencies.gson,
      // These are required only for compile time to build the war file
      Dependencies.tomcatEmbedCore % "container",
      Dependencies.tomcatEmbedJasper % "container",
      Dependencies.tomcatEmbedLogging % "container"
    )
  )
).dependsOn(
  sprayService,
  LocalProject("isc-api"),
  LocalProject("isc-core")
)

lazy val sprayServlet = Project (
  id = "spray-servlet",
  base = file("spray-servlet"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.spray,
    libraryDependencies ++= sprayServletWithRoutingAndJson ++ Dependencies.specs2 ++ Seq(
      Dependencies.akka % "provided",
      Dependencies.gson % "provided",
      Dependencies.javaxServletApi,
      Dependencies.h2 % "test",
      Dependencies.dispatch % "test",
      Dependencies.esmService % "test"
    )
  )
).dependsOn(
    sprayService,
    LocalProject("isc-api") % "provided",
    LocalProject("isc-core") % "provided",
    LocalProject("service-api") % "provided",
    LocalProject("esm-api") % "provided"
  )
