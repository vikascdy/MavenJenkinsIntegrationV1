import com.earldouglas.xsbtwebplugin.WebPlugin
import com.edifecs.build.{CodeGen, Common, Dependencies, Versions}
import com.edifecs.build.plugin.{Bundle, EasDeploy}
import sbt.LocalProject
import sbt.LocalProject

Common.directorySettings

lazy val contentRepositoryApi = Project (
  id = "content-repository-api",
  base = file("content-repository-api"),
  settings = Common.commonSettings ++ CodeGen.codeGenSettings ++
    Seq(
      version := Versions.contentRepository,
      CodeGen.serviceInterfaces += "com.edifecs.contentrepository.IContentRepositoryService"
    )
).dependsOn(
    LocalProject("esm-api") % "compile",
    LocalProject("configuration-api") % "provided",
    LocalProject("isc-api") % "provided",
    LocalProject("sm-codegen") % "compile"
  )

lazy val contentRepositoryJackrabbitApi = Project (
  id = "content-repository-jackrabbit-api",
  base = file("content-repository-jackrabbit-api"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.contentRepository,
    libraryDependencies ++= Seq(
      Dependencies.jcr,
      Dependencies.h2,
      Dependencies.jackrabbit,
      Dependencies.jackrabbitSpi
    ))
).dependsOn(
    contentRepositoryApi,
    LocalProject("configuration-api") % "provided",
    LocalProject("esm-api") % "provided"
  )

lazy val contentRepositoryService = Project (
  id = "content-repository-service",
  base = file("content-repository-service"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.contentRepository,
    libraryDependencies ++= Seq(Dependencies.jcr, Dependencies.esmService % "test"))
).dependsOn(
    LocalProject("service-api") % "provided",
    LocalProject("configuration-api") % "provided",
    LocalProject("isc-core") % "provided",
    LocalProject("isc-api") % "provided",
    LocalProject("esm-api") % "provided",
    contentRepositoryJackrabbitApi,
    contentRepositoryApi
  )

lazy val contentRepositoryUploadTool = Project (
  id = "content-repository-upload-tool",
  base = file("content-repository-upload-tool"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.contentRepository,
    libraryDependencies ++= Seq(Dependencies.commonscli))
).dependsOn(LocalProject("service-api"),
    LocalProject("isc-core") % "provided"
  )

//lazy val contentRepositoryUi = Project (
//  id = "content-repository-ui",
//  base = file("content-repository-ui"),
//  settings =
//    Common.commonSettings ++
//      WebPlugin.webSettings ++ Seq(
//      version := Versions.contentRepository,
//      libraryDependencies ++= Seq(
//        Dependencies.tomcatEmbedCore % "container",
//        Dependencies.tomcatEmbedJasper % "container",
//        Dependencies.tomcatEmbedLogging % "container"
//      )
//    )
//)

lazy val contentRepositoryDist = Project (
  id = "content-repository-dist",
  base = file("content-repository-dist"),
  settings =
    Common.commonSettings ++
      Bundle.bundleApplicationSettings ++
      EasDeploy.easDeploySettings ++ Seq(
      Bundle.bundleServices := Map(
        "content-repository-service" -> Map(
          file("content-repository") -> contentRepositoryApi,
          file("content-repository") -> contentRepositoryJackrabbitApi,
          file("content-repository") -> contentRepositoryService
//          file("content-repository") -> contentRepositoryUi
        )
      ),
      version := Versions.contentRepository,
      libraryDependencies ++= Dependencies.specs2 ++ Seq(Dependencies.manifest % "test")
    )
).dependsOn(
    contentRepositoryApi,
    contentRepositoryJackrabbitApi,
    contentRepositoryService
  )

