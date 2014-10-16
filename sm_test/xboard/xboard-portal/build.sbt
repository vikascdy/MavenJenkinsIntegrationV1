import com.edifecs.build.plugin.{Bundle, EasDeploy}
import com.edifecs.build.{Dependencies, Versions, Common}
import sbt.LocalProject
import sbt.LocalProject

Common.directorySettings

lazy val xboardPortalService = Project (
  id = "xboard-portal-service",
  base = file("xboard-portal-service"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.xboard,
    libraryDependencies ++= Dependencies.specs2
  )
).dependsOn(
    LocalProject("isc-api"),
    LocalProject("isc-core"),
    LocalProject("configuration-api"),
    LocalProject("service-api"),
    LocalProject("esm-api"),
    LocalProject("coordination-service-api")
  )

lazy val xboardPortalDist = Project (
  id = "xboard-portal-dist",
  base = file("xboard-portal-dist"),
  settings =
    Common.commonSettings ++
    EasDeploy.easDeploySettings ++
    Bundle.bundleApplicationSettings ++ Seq(
    Bundle.bundleServices := Map(
      "xboard-portal-service" -> Map(
        file("xboard/xboard-portal") -> xboardPortalService
      )
    ),
    version := Versions.xboard,
    libraryDependencies ++= Dependencies.specs2 ++ Seq(Dependencies.manifest % "test")
  )
).dependsOn(
    xboardPortalService
  )