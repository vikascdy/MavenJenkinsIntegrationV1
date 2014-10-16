package com.edifecs.build

import sbt._

object Dependencies {
  // Versions

  val hibernateVersion = "4.3.5.Final"
  val slf4jVersion = "1.7.7"
  val tomcatVersion = "7.0.35"

  // Libraries
  ///////////////////////

  val shiro = "org.apache.shiro" % "shiro-core" % "1.2.3"

  val slf4j = Seq(
    "org.slf4j" % "slf4j-simple" % slf4jVersion % "test",
    "org.slf4j" % "slf4j-api" % slf4jVersion)

  val jsonschemavalidator = Seq(
    "com.github.fge" % "json-schema-validator" % "2.1.7"
  )

  // Test Libraries
  ///////////////////////

  // These two go together. Only way to run junit tests in sbt.
  val junit = Seq(
    "junit" % "junit" % "4.11" % "test",
    "com.novocode" % "junit-interface" % "0.10" % "test")

  val specs2Version = "2.3.11"
  val specs2 = Seq(
    "org.specs2" %% "specs2-core" % specs2Version % "test",
    "org.specs2" %% "specs2-matcher-extra" % specs2Version % "test",
    "org.specs2" %% "specs2-junit" % specs2Version % "test")

  // WARN: Can't use with spray-routing because of conflicting versions of 
  //       'shapeless' library. spray-routing uses 1.2.4 of 'shapeless'
  //       They need to upgrade to 2.0.0 before we can start using with 'gwt'.
  val spec2gwt = "org.specs2" %% "specs2-gwt" % specs2Version % "test"

  val scalatest = "org.scalatest" %% "scalatest" % "2.1.3" % "test"

  // SM Dependencies
  ///////////////////////

  val esmApi = "com.edifecs.epp" % "esm-api" % "1.7.0.0-SNAPSHOT"
  val iscApi = "com.edifecs.epp" % "isc-api" % "1.7.0.0-SNAPSHOT"
  val iscCore = "com.edifecs.epp" % "isc-core" % "1.7.0.0-SNAPSHOT"
  val flexfieldDB = "com.edifecs.epp" % "flexfields-db" % "1.7.0.0-SNAPSHOT"
  val configurationApi = "com.edifecs.epp" % "configuration-api" % "1.7.0.0-SNAPSHOT"
  val exception = "com.edifecs.epp" % "exception" % "1.7.0.0-SNAPSHOT"
  val serviceApi = "com.edifecs.epp" % "service-api" % "1.7.0.0-SNAPSHOT"
  val manifest = "com.edifecs.epp" % "manifest" % "1.7.0.0-SNAPSHOT"
  val flexfieldService = "com.edifecs.epp" % "flexfields-service" % "1.7.0.0-SNAPSHOT"

  // Build Dependencies
  /////////////////////////

  val tomcatEmbedCore = "org.apache.tomcat.embed" % "tomcat-embed-core" % tomcatVersion
  val tomcatEmbedJasper = "org.apache.tomcat.embed" % "tomcat-embed-jasper" % tomcatVersion
  val tomcatEmbedLogging = "org.apache.tomcat.embed" % "tomcat-embed-logging-log4j" % tomcatVersion

  val tomcat = Seq(
    tomcatEmbedCore % "container",
    tomcatEmbedJasper % "container",
    tomcatEmbedLogging % "container")


  // Global
  /////////////////////////

  val sharedDependencies = junit ++ slf4j

  // Relational Database Dependencies
  /////////////////////////

  val mysql = "mysql" % "mysql-connector-java" % "5.1.31"

  val hsql = "org.hsqldb" % "hsqldb" % "2.2.4"

  val h2 = "com.h2database" % "h2" % "1.3.160"

  val jtds = "net.sourceforge.jtds" % "jtds" % "1.3.1"

  val databases = Seq(
    mysql,
    h2,
    hsql,
    jtds
  )

  val hibernate = Seq(
    "org.hibernate" % "hibernate-core" % hibernateVersion,
    "org.hibernate" % "hibernate-entitymanager" % hibernateVersion,
    "org.hibernate" % "hibernate-c3p0" % hibernateVersion,
    hsql % "test",
    h2 % "test")

}
