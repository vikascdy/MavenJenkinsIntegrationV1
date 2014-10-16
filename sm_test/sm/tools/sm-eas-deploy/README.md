# SM EAS Deploy Utility

## Requirements

  * Linux OS with the correct version of Docker installed
  * Network access to the docker registry
  * Network access to EAS
  * Java 7+ installed

## Usage

### Command line

    sm-eas-deploy [--id id] filename
    
where id is the name of the application to deploy and filename is the application zip file.

### Maven Plugin

// TODO: This needs to be filled in when implemented

### SBT Plugin

// TODO: This needs to be filled in when implemented

    package com.edifecs.build.plugin
    
    import java.io.IOException
    
    import sbt._
    import sbt.Keys._
    import sbtassembly.Plugin.AssemblyKeys._
    import Bundle.bundleZipArchive
    
    import scala.sys.process._
    
    object EasDeploy extends Plugin {
    
      val easDeploy = TaskKey[Unit]("eas-deploy",
        "Build Docker container from ZIP distribution, then deploy to repo and EAS")
    
      lazy val easDeploySettings = Seq(
        easDeploy <<= Def.task {
          val toolJarFile = (assembly in LocalProject("sm-eas-deploy")).value
          val zipFile = bundleZipArchive.value
    
          // Check if Java can be launched from the command line.
          val nullLogger = ProcessLogger({_=>},{_=>})
          try {
            if (scala.sys.process.Process("java -version").!(nullLogger) != 0) {
              sys.error("Java returned nonzero exit code; possible permissions issue.")
            }
          } catch {
            case ex: IOException =>
              sys.error("The 'java' executable is not installed or cannot be launched.")
          }
    
          // Launch the sm-eas-deploy tool in an external process.
          val errorLines = new StringBuilder
          val errLogger = ProcessLogger({l => println(l)},{l => errorLines.append(l + "\n")})
          if (scala.sys.process.Process(Seq("java", "-jar", toolJarFile.getAbsolutePath, zipFile.getAbsolutePath)).!(errLogger) != 0) {
            sys.error("eas-deploy failed:\n" + errorLines)
          }
        }
      )
    }

## Design

The sm-eas-deploy utility makes the deployment to EAS simple for SM Services, and other Application Factory components
like XBoard.

EAS (Edifecs Application Store) consists of two components.

  * Manifest Registry
  * Docker Registry

Taking the ([App Zip Structure](Packaging.md)) as an input it:

  * Deploys the applications Manifest file from the app zip file and uploads it too the Manifest Registry.
  * Turns the app zip into a docker container and uploads it to the specified docker registry.

This tool can be run as a standalone utility, as a SBT plugin, or Maven plugin.