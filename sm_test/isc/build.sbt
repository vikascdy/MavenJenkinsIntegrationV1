import java.lang.management.{ManagementFactory, RuntimeMXBean}

import com.edifecs.build.{Versions, Common, Dependencies}
import sbt._
import sbtfilter.Plugin.FilterKeys._

Common.directorySettings

lazy val iscApi = Project (
  id = "isc-api",
  base = file("isc-api"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.isc,
    libraryDependencies ++= Dependencies.specs2 ++ Seq(Dependencies.gson, Dependencies.akka))
).dependsOn(
  LocalProject("exception")
)

lazy val iscCore = Project (
  id = "isc-core",
  base = file("isc-core"),
  settings = Common.commonSettings ++ filterSettings ++ Seq(
    extraProps += "build.number" -> systemOptional("BUILD_NUMBER","Not Found"),
    version := Versions.isc
  )
).dependsOn(
  iscCoreAkka
//  iscCoreJGroups
)

lazy val iscCoreAkka = Project (
  id = "isc-core-akka",
  base = file("isc-core-akka"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.isc,
    libraryDependencies ++= Dependencies.specs2 ++ Seq(
      Dependencies.akka, Dependencies.akkaSlf4j, Dependencies.akkaCluster))
).dependsOn(
  iscCoreCommon
)


//lazy val iscCoreJGroups = Project (
//  id = "isc-core-jgroups",
//  base = file("isc-core-jgroups"),
//  settings = Common.commonSettings ++ Seq(
//    version := Versions.isc,
//    libraryDependencies ++= Dependencies.specs2 ++ Seq(Dependencies.jgroups)
//  )
//).dependsOn(
//  iscCoreCommon
//  iscJGroups
//)

//lazy val iscJGroups = Project (
//  id = "isc-jgroups",
//  base = file("isc-jgroups"),
//  settings = Common.commonSettings ++ Seq(
//    version := Versions.isc,
//    libraryDependencies := Seq(Dependencies.jgroups))
//).dependsOn(
//  LocalProject("etcd-client")
//)

lazy val iscCoreCommon = Project (
  id = "isc-core-common",
  base = file("isc-core-common"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.isc
  )
).dependsOn(
  iscApi,
  LocalProject("exception"),
  LocalProject("esm-api"),
  LocalProject("metric-api"),
  LocalProject("configuration-api")
)

def systemOptional(key: String, `def`: String): String = {
  val bean: RuntimeMXBean = ManagementFactory.getRuntimeMXBean
  import scala.collection.JavaConversions._
  bean.getInputArguments map (x =>
    if (x.contains(key)) {
      x match {
        case x if (x.contains("=")) => return x.split("=")(1)
        case x => return x
      }
    })
  `def`
}