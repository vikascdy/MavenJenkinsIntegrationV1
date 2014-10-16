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

package com.edifecs.esm.dist.test

import com.edifecs.epp.packaging.manifest.Manifest

import org.specs2.mutable._
import org.specs2.matcher.JsonMatchers
import com.edifecs.epp.util.yaml.YamlParser

import java.io.{File, FileInputStream}

class ManifestTest extends Specification with JsonMatchers {

  val stream = new FileInputStream(new File(getClass.getResource("/MANIFEST.yaml").toURI))

  val manifest = for {
    map ← YamlParser.yamlAsMap(stream)
    a ← Manifest(map)(s"test")
  } yield (a)

  "manifest file" should {
    "contain the name esm" in {
      manifest.get.name mustEqual "esm"
    }
    "not contain the name somethingelse" in {
      manifest.get.name mustNotEqual "somethingelse"
    }

  }

}


