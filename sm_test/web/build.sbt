import com.edifecs.build.{Versions, Common, Dependencies}

Common.directorySettings

lazy val spray = project

lazy val httpProxy = Project (
  id = "http-proxy",
  base = file("http-proxy"),
  settings = Common.commonSettings ++ Seq(
    version := Versions.web,
    libraryDependencies ++= Seq(
      Dependencies.javaxServletApi,
      Dependencies.apacheHttpClient,
      Dependencies.apacheHttpClientTests,
      Dependencies.httpunit % "test"
  ))
).dependsOn(LocalProject("isc-core") % "provided")
