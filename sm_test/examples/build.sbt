import com.edifecs.build.{Dependencies, Common, Versions}
import com.edifecs.build.CodeGen._

Common.directorySettings

lazy val helloExample = Project (
  id = "hello-example",
  base = file("hello-example")
)

lazy val testServices = Project (
  id = "test-services",
  base = file("test-services"),
  settings = Common.commonSettings ++
    codeGenSettings ++ Seq(
    version := Versions.sm,
    libraryDependencies ++= Seq(
      Dependencies.commonsFileupload,
      Dependencies.commonsIo),
    serviceInterfaces ++= Seq(
      "com.edifecs.servicemanager.test.services.IDBCommandReceiverService",
      "com.edifecs.servicemanager.test.services.ITestChatService",
      "com.edifecs.servicemanager.test.services.ITestCommandReceiverService",
      "com.edifecs.servicemanager.test.services.ITestCommandSenderService",
      "com.edifecs.servicemanager.test.services.ITestJettyCommandRecieverService"))
).dependsOn("service-api")
 .dependsOn("isc-api")
 .dependsOn("isc-core")
 .dependsOn("zip-util")
 .dependsOn("sm-codegen")
