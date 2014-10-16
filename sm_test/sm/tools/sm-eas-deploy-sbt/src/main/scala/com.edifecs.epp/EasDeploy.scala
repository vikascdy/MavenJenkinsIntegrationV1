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

import scala.sys.process._
import com.edifecs.epp.sm.eas.deploy.SmEasDeploy

object EasDeploy extends Plugin {

  val easDeploy = TaskKey[Unit]("eas-deploy",
    "Build Docker container from ZIP distribution, then deploy to repo and EAS")

  val zipFile = TaskKey[File]("zip-file", "File reference to the zip file")

  lazy val easDeploySettings = Seq(
    easDeploy <<= Def.task {
      val eas = new SmEasDeploy(zipFile.value.getAbsolutePath, Option.apply(name.value))
    }
  )
}
