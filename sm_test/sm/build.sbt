import com.edifecs.build.CodeGen._
import com.edifecs.build.plugin._
import com.edifecs.build.{Common, Dependencies, Versions}
import sbt.Keys._
import sbt.{LocalProject, _}
import sbtdocker.Plugin.DockerKeys._
import sbtdocker.{Dockerfile, ImageName}

Common.directorySettings

lazy val configurationApi = Project (
  id = "configuration-api",
  base = file("configuration-api"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.sm,
    libraryDependencies ++= Seq(Dependencies.commonsIo, Dependencies.gson, Dependencies.manifest))
)

lazy val esmApi = Project (
  id = "esm-api",
  base = file("esm-api"),
  settings = Common.commonSettings ++
    codeGenSettings ++ Seq(
    version := Versions.esm,
    serviceInterfaces += "com.edifecs.epp.security.service.ISecurityService",
    libraryDependencies ++= Seq(Dependencies.gson, Dependencies.shiro % "compile"))
).dependsOn(
  LocalProject("configuration-api"),
  LocalProject("isc-api") % "provided",
  LocalProject("sm-codegen") % "compile",
  LocalProject("flexfields-api")
)

lazy val memtracerApi = Project (
  id = "memtracer-api",
  base = file("memtracer-api"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.sm,
    libraryDependencies ++= Seq(Dependencies.sigar))
).dependsOn(configurationApi)

lazy val smClassLoader = Project (
  id = "sm-classloader",
  base = file("sm-classloader"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.sm
  )
)

lazy val smAgentApi = Project (
  id = "sm-agent-api",
  base = file("sm-agent-api"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.sm
  )
)

lazy val serviceApi = Project (
  id = "service-api",
  base = file("service-api"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.sm,
    libraryDependencies ++= Dependencies.specs2 ++ Seq(Dependencies.manifest)
  )
).dependsOn(
    LocalProject("configuration-api"),
    LocalProject("exception"),
    LocalProject("isc-core"),
    LocalProject("isc-api"),
    LocalProject("esm-api"),
    LocalProject("content-repository-api"),
    LocalProject("sm-classloader")
  )

lazy val resourceValidationService = Project (
  id = "resource-validation-service",
  base = file("resource-validation-service"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.sm
  )
).dependsOn(
    serviceApi,
    LocalProject("isc-core"),
    LocalProject("isc-api")
  )

lazy val smLauncherApi = Project (
  id = "sm-launcher-api",
  base = file("sm-launcher-api"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.sm,
    libraryDependencies ++= Seq(Dependencies.jansi, Dependencies.manifest))
).dependsOn(
    serviceApi,
    smAgentApi,
    smClassLoader,
    LocalProject("memtracer-api"),
    LocalProject("sm-classloader"),
    LocalProject("configuration-api"),
    LocalProject("service-api"),
    LocalProject("esm-api"),
    LocalProject("isc-core"),
    LocalProject("zip-util")
  )

lazy val smLauncher = Project(
  id = "sm-launcher",
  base = file("sm-launcher"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.sm,
    libraryDependencies ++= Dependencies.tomcat)
).dependsOn(
    smClassLoader,
    smLauncherApi,
    smStopper
  )

lazy val smStopper = Project(
  id = "sm-stopper",
  base = file("sm-stopper"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.sm,
    libraryDependencies ++= Seq(
      Dependencies.sigar
    )
  )
).dependsOn(
    serviceApi,
    smLauncherApi
  )

lazy val smAgent = Project(
  id = "sm-agent",
  base = file("sm-agent"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.sm,
    libraryDependencies ++= Seq(Dependencies.manifest)
  )
).dependsOn(
    smAgentApi,
    smLauncherApi,
    LocalProject("configuration-api"),
    LocalProject("memtracer-api"),
    LocalProject("esm-api"),
    LocalProject("isc-api"),
    LocalProject("isc-core")
  )

lazy val smContainer = Project(
  id = "sm-container",
  base = file("sm-container"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.sm,
    libraryDependencies ++= Dependencies.specs2 ++ Seq(
      Dependencies.slf4jOverLog4j12,
      Dependencies.manifest
    )
  )
).dependsOn(
    smLauncherApi,
    LocalProject("configuration-api"),
    LocalProject("memtracer-api"),
    LocalProject("service-api"),
    LocalProject("esm-api"),
    LocalProject("sm-classloader"),
    LocalProject("isc-api"),
    LocalProject("isc-core")
  )

lazy val smMinDist = Project (
  id = "sm-min-dist",
  base = file("sm-min-dist"),
  settings =
    Common.commonSettings ++
      Bundle.bundleSettings ++ Seq(
      version := Versions.sm
    )
).dependsOn(
    smLauncher,
    smAgent,
    smContainer
  )

lazy val smDist = Project (
  id = "sm-dist",
  base = file("sm-dist"),
  settings =
    Common.commonSettings ++
    Bundle.bundleSettings ++ Seq(
    version := Versions.sm,
    // FIXME: Need to figure out how to manage these dependencies
    bundleLibraryApplications ++= Map(
      ("esm-dist", s"https://ennexus.corp.edifecs.com/service/local/artifact/maven/redirect?r=snapshots&g=com.edifecs.epp&a=esm-dist&v=${Versions.esm}&e=zip&c=dist")
    ),
    Bundle.bundleApplications ++= Seq (
      LocalProject("content-repository-dist"),
      LocalProject("xboard-portal-dist"),
      LocalProject("xboard-dist"),
      //LocalProject("tomcat-dist"),
      LocalProject("spray-service-dist"),
      LocalProject("flexfields-dist")
    )
  )
).dependsOn(
    smLauncher,
    smAgent,
    smContainer
  )

lazy val smdockerDist = Project (
  id = "sm-docker-dist",
  base = file("sm-docker-dist"),
  settings =
    Common.commonSettings ++
      Bundle.bundleSettings ++
      dockerSettings ++ Seq(
      // Need to find a better way to handle this kind of issue.
      Bundle.bundleResourceDir ++= Seq("../sm-dist/src/main/bundle"),
      version := Versions.sm,
      docker <<= docker dependsOn bundle,
      dockerfile in docker := {
        val targetPath: File = (target in compile).value
        val bundlePath = bundle.value
        new Dockerfile {
          from("registry:5000/js") // Java/Scala base
          copyToStageDir(bundlePath, file("/opt/edifecs"))
          add("/opt/edifecs", "/opt/edifecs")
          run("chmod", "+x", "/opt/edifecs/ServiceManager/bin/startNode.sh")
          expose(8080)
          expose(7800)
        }
      },
      imageName in docker := {
        ImageName(
          repository = "registry:5000/sm",
          tag = Some(Versions.sm))
      }
    )
).dependsOn(
    smLauncher,
    smAgent,
    smContainer
  )

lazy val smCodegen = Project(
  id = "sm-codegen",
  base = file("sm-codegen"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.sm,
    libraryDependencies ++= Dependencies.specs2 ++ Seq(Dependencies.treehugger)
  )
).dependsOn(
    LocalProject("isc-api")
  )

lazy val smCodegenTest = Project(
  id = "sm-codegen-test",
  base = file("sm-codegen-test"),
  settings = Common.commonSettings ++
    codeGenSettings ++ Seq(
    version := Versions.sm,
    serviceInterfaces += "codegentest.CodeGenTestService",
    libraryDependencies ++= Dependencies.specs2
  )
).dependsOn(
    LocalProject("isc-api"),
    LocalProject("isc-core"),
    LocalProject("service-api"),
    LocalProject("sm-codegen")
  )

lazy val tools = Project (
  id = "tools",
  base = file("tools")
)
