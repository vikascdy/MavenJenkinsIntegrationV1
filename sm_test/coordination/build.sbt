import com.edifecs.build.Common
import com.edifecs.build.Dependencies
import com.edifecs.build.Versions

Common.directorySettings

lazy val coordinationServiceApi = Project(
  id = "coordination-service-api",
  base = file("coordination-service-api"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.sm,
    libraryDependencies ++= Seq(
      Dependencies.gson,
      Dependencies.commonslang
    )
  )
).dependsOn(LocalProject("configuration-api"))

lazy val serviceRegistryApi = Project(
  id = "service-registry-api",
  base = file("service-registry-api"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.sm
  )
).dependsOn(LocalProject("isc-core"))
 .dependsOn(LocalProject("configuration-api"))
