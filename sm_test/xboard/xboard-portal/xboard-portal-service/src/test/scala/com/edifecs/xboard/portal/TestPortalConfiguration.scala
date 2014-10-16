package com.edifecs.xboard.portal

import java.io.{FileInputStream, InputStreamReader}

import com.edifecs.core.configuration.Configuration
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import org.specs2.mutable._

/**
 * Created by abhising on 25-08-2014.
 */
class TestPortalConfiguration extends Specification {

  "Xboard Portal Service " should {

    "parse portal configuration " in {
      val gson = new Gson()
      val files = Configuration.getFeatureItemFiles(getClass.getResource("/").getPath)
      import scala.collection.JavaConversions._
      files.foreach(f => {
        val reader = new JsonReader(new InputStreamReader(new FileInputStream(f)))
        val conf = gson.fromJson(reader, classOf[FeaturedItemJsonWrapper]).asInstanceOf[FeaturedItemJsonWrapper]
        println(s"parsed json : ${conf.getApp}")
        conf mustNotEqual (null)
      })
      done
    }
  }
}
