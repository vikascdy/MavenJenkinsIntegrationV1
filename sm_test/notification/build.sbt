import com.edifecs.build.{Versions, Dependencies, Common}

Common.directorySettings

lazy val emailMessagingService = Project(
  id = "email-messaging-service",
  base = file("email-messaging-service"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.notification,
    libraryDependencies ++= Seq(
      Dependencies.javaxMail
  ))
).dependsOn("service-api")
 .dependsOn("isc-api")
 .dependsOn("isc-core")
