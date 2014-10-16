addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.2.5")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.5.0")

addSbtPlugin("com.earldouglas" % "xsbt-web-plugin" % "0.9.0")

addSbtPlugin("org.xerial.sbt" % "sbt-pack" % "0.5.1")

addSbtPlugin("com.orrsella" % "sbt-sublime" % "1.0.9")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.4")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.10.2")

addSbtPlugin("se.marcuslonnberg" % "sbt-docker" % "0.3.0")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

// USed to override the default maven plugin information
addSbtPlugin("no.arktekk.sbt" % "aether-deploy" % "0.11")


//  For SBT Site Generation
//
//  Enables the use of:
//    - make-site
//    - package-site
//    - publish
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.7.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.1.0")

// similar to maven filter resources
addSbtPlugin("com.github.sdb" % "xsbt-filter" % "0.4")

//js hint plugin
addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.1")

addSbtPlugin("org.scala-sbt.plugins" % "sbt-onejar" % "0.8")



// Add PMD and Checkstyle libraries.
val checkstyle = Seq(
  "com.puppycrawl.tools" % "checkstyle" % "5.5" exclude ("org.slf4j", "slf4j-log4j12"),
  "net.sourceforge.pmd" % "pmd" % "5.0.0" exclude ("org.slf4j", "slf4j-log4j12")
)

// Add PMD and Checkstyle libraries.
libraryDependencies ++= checkstyle
