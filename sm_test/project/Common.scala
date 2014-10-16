package com.edifecs.build

import sbt._
import sbt.Keys._
import sbt.Package._
import java.util.jar.Attributes.Name._
import com.typesafe.sbt.SbtSite._
import aether.Aether._

object Common {

  def directorySettings: Seq[Def.Setting[_]] = Defaults.defaultSettings ++ Seq(
    // disable publishing
    publishArtifact in (Compile) := false,
    publishMavenStyle := false,
    publish := { },
    publishLocal := {}
  )

  lazy val publishSetting = publishTo := {
      val nexus = "https://ennexus.corp.edifecs.com/"
      if (version.value.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots/")
      else
        Some("releases" at nexus + "content/repositories/releases/")
  }

  lazy val credentialsSetting = credentials += {
    if (System.getProperty("build.publish.user") != null ) {
      Credentials("Sonatype Nexus Repository Manager", "ennexus.corp.edifecs.com",
        System.getProperty("build.publish.user"),
        System.getProperty("build.publish.password"))
    } else if ((Path.userHome / ".ivy2" / ".credentials").exists()) {
      Credentials(Path.userHome / ".ivy2" / ".credentials")
    } else {
      Credentials("Sonatype Nexus Repository Manager", "ennexus.corp.edifecs.com", "guest", "")
    }
  }

  lazy val dependencyGraphSettings = net.virtualvoid.sbt.graph.Plugin.graphSettings

  lazy val commonSettings: Seq[Def.Setting[_]] =
    Defaults.defaultSettings ++
      dependencyGraphSettings ++
      aetherPublishBothSettings ++
      site.settings ++
      site.includeScaladoc() ++
    Seq(

    // TODO: Investigate this line as it causes the javadoc generation to fail when publishing
//    javacOptions in Compile ++= Seq("-source", "1.7", "-target", "1.7", "-Xlint:-options"),
    scalaVersion := "2.10.3",
    crossScalaVersions := Seq("2.10.1"),
    organization := "com.edifecs.epp",
    version := Versions.default,
    // disable using the Scala version in output paths and artifacts
    crossPaths := false,
    resolvers ++= Resolvers.defaultResolvers,
    libraryDependencies ++= Dependencies.sharedDependencies,

    // CheckStyle Settings


    // Test Settings

    // Run tests in a separate JVM's
    fork in Test := true,
    // we need lots of heap space
    // -Djava.net.preferIPv4Stack=true is needed ISC to use ipv4 and not ipv6 for testing to reduce the complexity of
    // the hosts file configuration on linux machines.
    javaOptions in Test ++= Seq("-Xmx1G", "-XX:MaxPermSize=512M", "-Djava.net.preferIPv4Stack=true"),
    javaOptions += "-Xmx1G",

    scalacOptions in(Compile, doc) <++= (version, baseDirectory in LocalRootProject).map {
      (v, bd) =>
        val tagOrBranch = if (v.endsWith("-SNAPSHOT")) "develop" else "v" + v
        val docSourceUrl = "https://gitlab/platform/repo/tree/" + tagOrBranch + "€{FILE_PATH}.scala"
        Seq("-sourcepath", bd.getAbsolutePath, "-doc-source-url", docSourceUrl)
    },

    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked"),
    packageOptions ++= Seq[PackageOption](ManifestAttributes(
      (IMPLEMENTATION_TITLE, "Edifecs Partner Platform"),
      (IMPLEMENTATION_URL, "http://www.edifecs.com"),
      (IMPLEMENTATION_VENDOR, "Edifecs Inc."),
      (SEALED, "false"))
    ),

    // Maven publish Settings
    publishSetting,
    credentialsSetting,
    publishMavenStyle := true,
    publishArtifact in Test := false,
    isSnapshot := version.value.endsWith("-SNAPSHOT"),

      CheckstyleSettings.checkstyleTask,

    // Pom configuration settings
    pomExtra := (
      <url>http://www.edifecs.com</url>
        <licenses>
          <license>
            <name>Private</name>
            <url>http://www.opensource.org/licenses/bsd-license.php</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@gitlab:platform/repo.git</url>
          <connection>scm:git:git@gitlab:platform/repo.git</connection>
        </scm>
        <developers>
          {
          Seq(
            ("pradeep.krishnan", "Pradeep Krishnan"),
            ("william.clements", "William Clements"),
            ("i-adam.nelson", "Adam Nelson"),
            ("saad.khawaja", "Saad Khawaja"),
            ("andrey.kozyrev", "Andrey Kozyrev"),
            ("virendra.prasad", "Virendra Prasad")
          ).map {
            case (id, name) =>
              <developer>
                <id>{id}</id>
                <name>{name}</name>
                <url>http://gitlab/u/{id}</url>
              </developer>
          }
          }
        </developers>
      )
  )

}
