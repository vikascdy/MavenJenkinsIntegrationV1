import com.earldouglas.xsbtwebplugin.WebPlugin
import com.edifecs.build.plugin.Bundle
import com.edifecs.build.{Common, Dependencies, Versions}
import sbt._

Common.directorySettings

lazy val esmDb = Project (
  id = "esm-db",
  base = file("esm-db"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.esm,
    libraryDependencies ++=
      Dependencies.hibernate ++
      Dependencies.databases ++
      Dependencies.jsonschemavalidator ++ Seq(
        Dependencies.esmApi % "provided",
        Dependencies.iscApi % "provided",
        Dependencies.exception % "provided",
        Dependencies.configurationApi % "provided",
        Dependencies.flexfieldDB % "provided"
      )
  )
)


lazy val esmService = Project (
  id = "esm-service",
  base = file("esm-service"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.esm,
    libraryDependencies ++= Dependencies.hibernate ++Seq(
      Dependencies.serviceApi % "provided",
      Dependencies.exception % "provided",
      Dependencies.flexfieldService
      ) ++
      Dependencies.jsonschemavalidator)
).dependsOn(
    esmDb
)


lazy val esmUi = Project (
  id = "esm-ui",
  base = file("esm-ui"),
  settings =
    Common.commonSettings ++
    WebPlugin.webSettings ++ Seq(
    JshintKeys.config := Option.apply(file("project/.jshintrc")),
    sourceDirectory in Assets := (sourceDirectory in Compile).value / "webapp",
    sourceDirectory in TestAssets  := (sourceDirectory in Compile).value / "webapp",
    version := Versions.esm,
    libraryDependencies ++= Dependencies.tomcat
  )
).enablePlugins(SbtWeb)


lazy val esmDist = Project (
  id = "esm-dist",
  base = file("esm-dist"),
  settings =
    Common.commonSettings ++
    Bundle.bundleApplicationSettings ++ Seq(
      Bundle.bundleServices := Map(
        "esm-service" -> Map(
          file("") -> esmDb,
          file("") -> esmService,
          file("") -> esmUi
        )
      ),
      bundleHtmlContextRoot := "esm",
      version := Versions.esm,
      libraryDependencies ++= Dependencies.specs2 ++ Seq(Dependencies.esmApi % "provided")
    )
).dependsOn(
    esmDb,
    esmService,
    esmUi
  )


