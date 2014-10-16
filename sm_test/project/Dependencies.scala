package com.edifecs.build

import sbt._

object Dependencies {
  // Versions

  val hibernateVersion = "4.3.5.Final"
  val slf4jVersion = "1.7.7"
  val tomcatVersion = "7.0.35"
  val akkaVersion = "2.3.2"

  // Spray version is tied to akka version. Clarify the relationship here.
  val sprayVersion: String = akkaVersion match {
    case "2.3.2" => "1.3.1"
  }
  val sprayJsonVersion = "1.2.5"

  // Libraries
  ///////////////////////
  val shiro = "org.apache.shiro" % "shiro-core" % "1.2.3"

  val gson = "com.google.code.gson" % "gson" % "2.2.4"

  val jgroups = "org.jgroups" % "jgroups" % "3.2.13.Final"

  // TODO: Review this dependency and remove if possible
  val oval = "net.sf.oval" % "oval" % "1.84"

  // Required for http-proxy project for developer web UI development
  val javaxServletApi = "javax.servlet" % "servlet-api" % "2.4"
  val apacheHttpClient = "org.apache.httpcomponents" % "httpclient" % "4.2.5"
  val apacheHttpClientTests = apacheHttpClient classifier "tests"

  val httpunit = "httpunit" % "httpunit" % "1.7"

  // Required for metric collection
  val codahale = Seq(
    ("com.codahale.metrics" % "metrics-core" % "3.0.1"),
    ("com.janramm" % "metrics-zabbix" % "0.0.1").exclude("org.slf4j","slf4j-log4j12"))

  val commonsIo = "commons-io" % "commons-io" % "2.4"

  val commonslang = "commons-lang" % "commons-lang" % "2.5"

  val commonscli = "commons-cli" % "commons-cli" % "1.2"

  val commonsFileupload = "commons-fileupload" % "commons-fileupload" % "1.3.1"

  val beansutil = "commons-beanutils" % "commons-beanutils" % "1.8.3"

  val opencsv = "net.sf.opencsv" % "opencsv" % "2.3"

  val sigar = "org.fusesource" % "sigar" % "1.6.4"

  val javaxMail = "javax.mail" % "mail" % "1.4.7"

  // Gives colored output to the conole error logs
  val jansi = "org.fusesource.jansi" % "jansi" % "1.11"

  // Tomcat Dependencies
  val tomcatDbcp = "org.apache.tomcat" % "tomcat-dbcp" % tomcatVersion
  val tomcatEmbedCore = "org.apache.tomcat.embed" % "tomcat-embed-core" % tomcatVersion
  val tomcatEmbedJasper = "org.apache.tomcat.embed" % "tomcat-embed-jasper" % tomcatVersion
  val tomcatEmbedLogging = "org.apache.tomcat.embed" % "tomcat-embed-logging-log4j" % tomcatVersion
  val ecj = "org.eclipse.jdt.core.compiler" % "ecj" % "3.7.2"

  val tomcat = Seq(
    tomcatDbcp,
    tomcatEmbedCore,
    tomcatEmbedJasper,
    tomcatEmbedLogging,
    ecj)

  // JackRabbit Dependencies
  <!-- The JCR API -->
  val jcr = "javax.jcr" % "jcr" % "2.0"
  val jackrabbit = "org.apache.jackrabbit" % "jackrabbit-core" % "2.6.5"
  val jackrabbitSpi = "org.apache.jackrabbit" % "jackrabbit-spi" % "2.6.5"

  val yaml = "org.yaml" % "snakeyaml" % "1.13"

  val semver = "com.github.zafarkhaja" % "java-semver" % "0.7.2"

  val treehugger = "com.eed3si9n" %% "treehugger" % "0.3.0"

  val slf4j = Seq(
    "org.slf4j" % "slf4j-simple" % slf4jVersion % "test",
    "org.slf4j" % "slf4j-api" % slf4jVersion)

  val slf4jOverLog4j12 = "org.slf4j" % "slf4j-log4j12" % slf4jVersion

  val akka = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaContrib = "com.typesafe.akka" %% "akka-contrib" % akkaVersion
  val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % akkaVersion
  val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"

  // Spray IO Dependencies
  val sprayRouting = "io.spray" % "spray-routing" % sprayVersion
  val sprayJson = "io.spray" %% "spray-json" % sprayJsonVersion
  val sprayCan = "io.spray" % "spray-can" % sprayVersion
  val sprayServlet = "io.spray" % "spray-servlet" % sprayVersion
  val sprayCaching = "io.spray" % "spray-caching" % sprayVersion
  val sprayClient = "io.spray" % "spray-client" % sprayVersion
  val sprayTestkit = "io.spray" % "spray-testkit" % sprayVersion % "test"

  val dispatch = "net.databinder.dispatch" %% "dispatch-core" % "0.11.0"

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

  val mockito = "org.mockito" % "mockito-all" % "1.9.5" % "test"

  // Edifecs Dependencies
  /////////////////////////

  val esmService = "com.edifecs.epp" % "esm-service" % "1.7.0.0-SNAPSHOT"

  val manifest = "com.edifecs.epp" % "manifest" % Versions.manifest

//  val esmDist = "com.edifecs.epp" % "esm-dist" % "1.7.0.0-SNAPSHOT" artifacts(Artifact("esm-dist", "dist", "dist"))

  val easClient = "com.edifecs.epp" % "eas-client" % Versions.eas

  // Projects
  /////////////////////////

  val sharedDependencies = junit ++ slf4j

  val kafka = ("org.apache.kafka" %% "kafka" % "0.8.0")
    .exclude("org.slf4j", "slf4j-simple")
    .exclude("org.slf4j", "slf4j-log4j12")
    .exclude("org.apache.zookeeper", "zookeeper")
    .exclude("log4j","log4j")
    .exclude("org.slf4j" ,"slf4j-api") 

	val storm = "org.apache.storm" % "storm-core" % "0.9.1-incubating"
    val kyro = "com.esotericsoftware.kryo" % "kryo" % "2.24.0"    

    val storm_pkg = Seq(
       (storm % "provided").exclude("com.twitter","carbonite"),
       kyro % "provided")

	/*
  val storm = ("storm" % "storm" % "0.9.0.1")
    .exclude("javax.servlet", "servlet-api")
    .exclude("org.mortbay.jetty", "servlet-api")
    .exclude("org.slf4j", "log4j-over-slf4j")
    .exclude("org.ow2.asm", "asm")
    .exclude("ch.qos.logback", "logback-classic")
	*/


  val chMetrics = "com.codahale.metrics" % "metrics-core" % "3.0.1"
  val chMetricsAnnotatons = "com.codahale.metrics" % "metrics-annotation" % "3.0.1"
  val chMetricsGraphite = "com.codahale.metrics" % "metrics-graphite" % "3.0.1"

  val typeSafeConfig = ("com.typesafe" % "config" % "1.2.0")

  val hadoop_hdfs = ("org.apache.hadoop" % "hadoop-hdfs" % "2.3.0")
    .exclude("javax.servlet", "servlet-api")
    .exclude("org.mortbay.jetty", "servlet-api")
    .exclude("org.slf4j", "slf4j-log4j12")
    .exclude("log4j","log4j")

  val hadoop_common = ("org.apache.hadoop" % "hadoop-common" % "2.3.0")
    .exclude("commons-beanutils", "commons-beanutils")
    .exclude("commons-beanutils", "commons-beanutils-core")
    .exclude("org.slf4j", "slf4j-log4j12")
    .exclude("log4j","log4j")

  val mongo = "org.reactivemongo" %% "reactivemongo" % "0.10.0"

  val curator = ("org.apache.curator" % "curator-test" % "2.4.1" % "test").exclude("org.apache.zookeeper", "zookeeper")

  val guava = "com.google.guava" % "guava" % "17.0"

  val jodatime = Seq("joda-time" % "joda-time" % "2.3",
    "org.joda" % "joda-convert" % "1.6")

  val saxon = "net.sf.saxon" % "Saxon-HE" % "9.5.1-5"

  val jackson = "com.fasterxml.jackson.core" % "jackson-core" % "2.3.2"
  
  val httpclient = "org.apache.httpcomponents" % "httpclient" % "4.3.4"

  val commonslogging = "commons-logging" % "commons-logging" % "1.1.3"

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

  val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.1.2"

	// Added json validator dependency to the project
  val jsonschemavalidator = Seq(
	"com.github.fge" % "json-schema-validator" % "2.1.7"
  )

  val empi = ("com.edifecs.clinical.gateway.empi" % "empi-openempi" % "8.4.1-SNAPSHOT").exclude("com.sun.jersey","jersey-bundle")

}
