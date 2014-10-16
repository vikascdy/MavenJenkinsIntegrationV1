import com.edifecs.build.plugin.{Bundle, EasDeploy}
import com.edifecs.build.{Versions, Common, Dependencies}
import sbt._
import sbt.LocalProject

Common.directorySettings

lazy val flexFieldsDB = Project(
  id = "flexfields-db",
  base = file("flexfields-db"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.flexfields,
    libraryDependencies ++= Dependencies.hibernate ++ Dependencies.databases)
).dependsOn(
    flexFieldsAPI
  )

lazy val flexFieldsAPI = Project(
  id = "flexfields-api",
  base = file("flexfields-api"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.flexfields,
    libraryDependencies ++= Seq(Dependencies.yaml, Dependencies.manifest))
).dependsOn(
    LocalProject("isc-api") % "provided"
  )

lazy val flexFieldsService = Project(
  id = "flexfields-service",
  base = file("flexfields-service"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.flexfields
  )
).dependsOn(
    flexFieldsAPI,
    LocalProject("isc-api") % "provided",
    LocalProject("isc-core") % "provided",
    LocalProject("configuration-api") % "provided",
    LocalProject("service-api") % "provided",
    LocalProject("sm-codegen"),
    LocalProject("flexfields-db"),
    LocalProject("coordination-service-api")
  )

lazy val flexfieldsDist = Project (
  id = "flexfields-dist",
  base = file("flexfields-dist"),
  settings =
    Common.commonSettings ++
    EasDeploy.easDeploySettings ++
    Bundle.bundleApplicationSettings ++ Seq(
    Bundle.bundleServices := Map(
      "flexfields-service" -> Map(
        file("flexfields") -> flexFieldsAPI,
        file("flexfields") -> flexFieldsService
      )
    ),
    version := Versions.flexfields,
    libraryDependencies ++= Dependencies.specs2
  )
).dependsOn(
    flexFieldsAPI,
    flexFieldsService
  )
