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
