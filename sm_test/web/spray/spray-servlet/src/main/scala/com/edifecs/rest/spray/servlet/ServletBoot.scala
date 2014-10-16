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

package com.edifecs.rest.spray.servlet

import akka.actor.{ActorSystem, Props}
import spray.servlet.WebBoot

// This class is instantiated by the servlet initializer.
// It can either define a constructor with a single
// `javax.servlet.ServletContext` parameter or a
// default constructor.
// It must implement the spray.servlet.WebBoot trait.
class ServletBoot (ctx: javax.servlet.ServletContext) extends WebBoot {


  // we need an ActorSystem to host our application in
  val system = ActorSystem("SetupActor")

  // the service actor replies to incoming HttpRequests
  val serviceActor = system.actorOf(Props(classOf[SetupActor], ctx), "SetupActor")

  system.registerOnTermination {
    // put additional cleanup code here
    system.log.info("Application shut down")
  }
}
