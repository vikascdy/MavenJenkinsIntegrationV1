import com.earldouglas.xsbtwebplugin.WebPlugin
import com.edifecs.build.plugin.{Bundle, EasDeploy}
import com.edifecs.build.{Versions, Dependencies, Common, CheckstyleSettings}

Common.directorySettings

lazy val xboardUi = Project (
  id = "xboard-ui",
  base = file("xboard-ui"),
  settings = Common.commonSettings ++ WebPlugin.webSettings ++ Seq(
    version := Versions.xboard,
    // TODO: Checkstyle is disabled, as it is scanning all of EXTJS source code and causes failures in generating the XML report
    //CheckstyleSettings.checkstyle := {},
    libraryDependencies ++= Seq(
      Dependencies.tomcatEmbedCore % "container",
      Dependencies.tomcatEmbedJasper % "container",
      Dependencies.tomcatEmbedLogging % "container"
    )
  )
).dependsOn(LocalProject("isc-core") % "provided")

lazy val xboardDist = Project (
  id = "xboard-dist",
  base = file("xboard-dist"),
  settings =
    Common.commonSettings ++
    EasDeploy.easDeploySettings ++
    Bundle.bundleApplicationSettings ++ Seq(
    Bundle.bundleServices := Map(
      "xboard-service" -> Map(
        file("xboard") -> xboardUi
      )
    ),
    bundleHtmlContextRoot := "",
    version := Versions.xboard,
    libraryDependencies ++= Dependencies.specs2
  )
).dependsOn(
  xboardUi
)

lazy val xboardPortal = Project (
  id = "xboard-portal",
  base = file("xboard-portal")
)
