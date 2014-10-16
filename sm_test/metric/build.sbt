import com.edifecs.build.{Dependencies, Versions, Common}
import sbt._
import sbt.Keys._

Common.directorySettings

lazy val metricapi = Project (
  id = "metric-api",
  base = file("metric-api"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.metric,
    libraryDependencies ++= Dependencies.codahale ++ Dependencies.slf4j ++ Seq(
      Dependencies.commonsIo,
      Dependencies.opencsv,
      Dependencies.apacheHttpClient
    )
  )
)
