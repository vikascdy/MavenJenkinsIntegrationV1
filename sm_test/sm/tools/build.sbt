import com.edifecs.build.Common
import com.edifecs.build.Versions
import com.edifecs.build.Dependencies

Common.directorySettings

lazy val smEasDeploy = Project (
  id = "sm-eas-deploy",
  base = file("sm-eas-deploy"),
  settings = Common.commonSettings ++ assemblySettings ++ Seq(
    version := Versions.sm,
    libraryDependencies ++= Seq(Dependencies.easClient)
  )
)

lazy val smEasDeploySbt = Project (
  id = "sm-eas-deploy-sbt",
  base = file("sm-eas-deploy-sbt")
).dependsOn(smEasDeploy)

//lazy val smBundlePlugin = Project (
//  id = "smbundle-plugin",
//  base = file("smbundle-plugin"),
//  settings = Common.commonSettings
//)