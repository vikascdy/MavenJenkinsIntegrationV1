package com.edifecs.xboard.portal

import com.edifecs.core.configuration.Configuration
import org.specs2.mutable._

class TestDoormatConfiguration extends Specification {

  "Dashboard Service" should {
    "get the sample json files from the application path" in {
      println(getClass().getResource("/").getPath)
      Configuration.getNavigationFiles(getClass().getResource("/").getPath) must not have size (0)
    }

  }
}
