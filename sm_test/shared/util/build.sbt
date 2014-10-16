import com.edifecs.build.Common
import com.edifecs.build.Dependencies
import com.edifecs.build.Versions
import sbt._

Common.directorySettings

lazy val zipUtil = Project (
  id = "zip-util",
  base = file("zip-util"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.sm,
    libraryDependencies ++= Seq(Dependencies.commonsIo)
  )
)

lazy val exception = project