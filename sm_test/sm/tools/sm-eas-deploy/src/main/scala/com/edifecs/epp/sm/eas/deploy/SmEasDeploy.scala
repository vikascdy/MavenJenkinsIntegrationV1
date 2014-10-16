// -----------------------------------------------------------------------------
// Copyright (c) Edifecs Inc. All Rights Reserved.
//
// This software is the confidential and proprietary information of Edifecs Inc.
// ("Confidential Information").  You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the license
// agreement you entered into with Edifecs.
//
// EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
// SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
// NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
// LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
// ITS DERIVATIVES.
// -----------------------------------------------------------------------------

package com.edifecs.epp.sm.eas.deploy

import java.io.{IOException, BufferedInputStream, ByteArrayOutputStream}
import java.lang.Integer.parseInt
import java.nio.file._
import java.util.regex.Pattern
import java.util.zip.{ZipFile, ZipOutputStream}

import scala.annotation.tailrec
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.sys.process._
import scala.util.{Success, Failure}

import akka.actor.ActorSystem

import com.edifecs.epp.ecm.values.EndPoint
import com.edifecs.epp.eas.protocol.{Id, Version}
import com.edifecs.epp.eas.rest.client.EasClient
import com.edifecs.epp.packaging.manifest.Manifest

object MainApp extends App {

  final val usage = """
    Usage: sm-eas-deploy [--id id] filename
"""

  // Read the arguments.
  if (args.isEmpty) {
    println(usage)
    sys.exit(1)
  }
  val options = {
    @tailrec def nextOption(map: Map[Symbol, Any], list: List[String]): Map[Symbol, Any] = {
      def isSwitch(s: String) = s(0) == '-'
      list match {
        case Nil => map
        case "--id" :: value :: tail =>
          nextOption(map ++ Map('id -> value.toString), tail)
        case string :: opt2 :: tail if isSwitch(opt2) =>
          nextOption(map ++ Map('appZip -> string), list.tail)
        case string :: Nil =>
          nextOption(map ++ Map('appZip -> string), list.tail)
        case option :: tail =>
          println("Unknown option " + option)
          sys.exit(1)
      }
    }
    nextOption(Map.empty, args.toList)
  }

  try {
    val deploy = new SmEasDeploy(
      options.get('appZip).get.toString,
      options.get('id).map(_.toString))
    deploy.buildAndPushDockerContainer()
    println("\nDocker build and push complete. Uploading manifest to EAS...\n")
    deploy.publishManifestToEas()
  } catch {
    case ex: EasDeployException =>
      System.err.println(ex.getMessage)
      sys.exit(1)
  }
}

class SmEasDeploy(
  zipFileName: String,
  id: Option[String]
) {
  final val dockerRepository = "registry:5000"
  final val easEndPoint = new EndPoint("10.30.50.13", 9000)

  // Get the manifest file.
  val zipFile = new ZipFile(zipFileName)
  val manifest = {
    val manifestEntry =
      zipFile.entries.find(_.getName.toLowerCase.endsWith("manifest.yaml")) getOrElse {
        throw new EasDeployException(
          s"The zip file '$zipFileName' does not contain a MANIFEST.yaml file." +
          "Cannot publish an app without a manifest.")
      }
    Manifest.fromYaml(zipFile.getInputStream(manifestEntry), "eas-deploy").get
  }

  private val realId = id getOrElse manifest.name

  def buildAndPushDockerContainer() = {
    // Check if Docker is available.
    val versionRegex = Pattern.compile(raw"Docker version (\d+)[.]\d+[.]\d+")
    val nullLogger = ProcessLogger({_=>},{_=>})
    try {
      val result = "docker --version".!!(nullLogger)
      val matcher = versionRegex.matcher(result)
      if (!matcher.find) {
        throw new EasDeployException(
          "Unsupported Docker version.\n" +
          "`docker --version` returned unprocessable response: " + result)
      } else if (parseInt(matcher.group(1)) < 1) {
        throw new EasDeployException(
          matcher.group(0) + " is not supported. Docker >= 1.0.0 is required.")
      }
    } catch {
      case ex: IOException =>
        throw new EasDeployException(
          "Docker is not installed or cannot be launched.\n" +
          "This tool must be run on a Linux system with Docker installed.")
    }

    // Create a temporary directory for Docker container generation.
    val tmpDir = Files.createTempDirectory("docker")
    val addDir = Files.createDirectory(tmpDir.resolve("add"))
    val appsDir = Files.createDirectory(addDir.resolve("apps"))
    val specificAppDir = Files.createDirectory(appsDir.resolve(manifest.name))
    for (e <- zipFile.entries if !e.isDirectory) {
      val path = e.getName.split("/").foldLeft(specificAppDir)(_ resolve _)
      if (!Files.exists(path.getParent)) Files.createDirectories(path.getParent)
      Files.copy(zipFile.getInputStream(e), path)
    }
    val dockerFile = Files.createFile(tmpDir.resolve("Dockerfile"))
    val executables = Seq(
      "start.sh", "stop.sh", "startAgent.sh", "startNode.sh", "startNodeWithConfig.sh")
    Files.write(dockerFile, s"""
      |FROM $dockerRepository/sm:1.6.0.0-SNAPSHOT
      |ADD /add/apps /opt/edifecs/ServiceManager/apps
    """.stripMargin.getBytes("UTF-8"))

    // Build the Docker container.
    val imageName = s"$dockerRepository/${manifest.name}:${manifest.version.fullVersion}"
    val buildCmd = Seq("docker", "build", "-t", imageName, ".")
    val pushCmd = Seq("docker", "push", imageName)
    if (Process(buildCmd, tmpDir.toFile).! != 0) {
      throw new EasDeployException("Failed to build docker container.")
    }
    if (Process(pushCmd, tmpDir.toFile).! != 0) {
      throw new EasDeployException("Failed to push docker container to repository.")
    }
  }

  def publishManifestToEas() = {
    // Initialize the Actor System for the EAS client
    val system = ActorSystem()
    val easClient = new EasClient(easEndPoint)(system)
    val easBaos = new ByteArrayOutputStream()
    val easPackage = new ZipOutputStream(easBaos)

    // Parse out the EAS Information from an app package for deployment
    // (Copy all YAML files to a new zip, and upload it.)
    for (e <- zipFile.entries if e.getName.toLowerCase.endsWith(".yaml")) yield {
      easPackage.putNextEntry(e)
      val in = new BufferedInputStream(zipFile.getInputStream(e))
      var b = in.read()
      while (b > -1) {
        easPackage.write(b)
        b = in.read()
      }
      in.close()
      easPackage.closeEntry()
    }
    easPackage.close()

    easClient.appProtocol.put(Id(realId), easBaos.toByteArray).andThen {
      case Success(Right(value)) =>
        println("App successfully uploaded: " + value)
        easClient.appProtocol.get(Id(realId), Version(manifest.version.fullVersion)).andThen {
          case Success(Right(value)) =>
            println("App successfully uploaded: " + value)
            system.shutdown()
          case Success(Left(t)) =>
            println("An error has occurred: " + t.message)
            system.shutdown()
          case Failure(t) =>
            println("An error has occurred: " + t.getMessage)
            system.shutdown()
        }
      case Success(Left(t)) =>
        println("An error has occurred: " + t.message)
        system.shutdown()
      case Failure(t) =>
        println("An error has occurred: " + t.getMessage)
        system.shutdown()
    }

    // Shutdown after timeout
    system.awaitTermination(10 minutes)
  }
}

class EasDeployException(message: String) extends RuntimeException(message)
