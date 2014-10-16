import com.edifecs.build.Common
import sbt._

Common.directorySettings

lazy val shared = project

lazy val metric = project

lazy val coordination = project

lazy val contentRepository = Project (
  id = "content-repository",
  base = file("content-repository")
)

lazy val sm = project

lazy val isc = project

lazy val web = project

lazy val notification = project

lazy val xboard = project

lazy val examples = project

lazy val flexfields = project
