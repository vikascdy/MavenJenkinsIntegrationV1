import com.earldouglas.xsbtwebplugin.WebPlugin
import com.edifecs.build.CodeGen._
import com.edifecs.build.{CodeGen, Dependencies, Common}
import com.edifecs.build.plugin.{Bundle, EasDeploy}
import sbt.Keys._
import sbt.LocalProject

Common.directorySettings

lazy val helloExampleApi = Project (
  id = "hello-example-api",
  base = file("hello-example-api"),
  settings = Common.commonSettings ++
    codeGenSettings ++ Seq(
    serviceInterfaces += "com.edifecs.helloexample.service.IHelloExampleService"
  )
).dependsOn(
    LocalProject("isc-api"),
    LocalProject("service-api"),
    LocalProject("sm-codegen")
  )

lazy val helloExampleDist = Project (
  id = "hello-example-dist",
  base = file("hello-example-dist"),
  settings = Common.commonSettings ++
    EasDeploy.easDeploySettings ++
    Bundle.bundleApplicationSettings ++ Seq(
    Bundle.bundleServices := Map(
      "hello-example-service" -> Map(
        file("examples/hello-example") -> helloExampleApi,
        file("examples/hello-example") -> helloExampleService,
        file("examples/hello-example") -> helloExampleUi
      )
    ),
    bundleHtmlContextRoot := "hello-example-ui",
    libraryDependencies ++= Dependencies.specs2 ++ Seq(Dependencies.manifest % "test")
  )
).dependsOn(
    helloExampleApi,
    helloExampleService,
    helloExampleUi
  )

lazy val helloExampleService = Project (
  id = "hello-example-service",
  base = file("hello-example-service"),
  settings = Common.commonSettings ++ Seq(
    libraryDependencies ++= Seq(
      Dependencies.esmService % "test"
    )
  )
).dependsOn(
    helloExampleApi,
    LocalProject("service-api"),
    LocalProject("isc-api"),
    LocalProject("isc-core")
  )

lazy val helloExampleUi = Project (
  id = "hello-example-ui",
  base = file("hello-example-ui"),
  settings = Common.commonSettings ++
    WebPlugin.webSettings ++ Seq(
    libraryDependencies ++= Seq(
      Dependencies.tomcatEmbedCore % "container",
      Dependencies.tomcatEmbedJasper % "container",
      Dependencies.tomcatEmbedLogging % "container"
    )
  )
)
