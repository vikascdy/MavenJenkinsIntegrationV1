package com.edifecs.epp.isc.json

import com.edifecs.epp.isc.json.testfiles.{IDeploymentHandler, DeploymentSerializer, Deployment, IDeploymentInfo}
import com.google.gson.Gson

import org.specs2.mutable._
import org.specs2.matcher.JsonMatchers
import java.util.Date
import com.edifecs.epp.isc.annotations.{JsonSerialization, TypeAdapter}
import java.lang.annotation.Annotation
import com.edifecs.epp.isc.annotations
import com.edifecs.epp.isc.command.CommandSource
import com.edifecs.epp.isc.exception.HandlerConfigurationException

import scala.reflect.runtime.{universe => ru}

/**
 * Created by willclem on 6/5/2014.
 */
class JsonTypeAdapterTest extends Specification with JsonMatchers {
  // Return Type IDeploymentInfo

  // Return Impl
  val deployment = new Deployment[String]()
  deployment.setId("someId")
  deployment.setBpmnData("some data")
  deployment.setActivateOn(new Date())
  deployment.setName("someName")
  deployment.setCategory("somCategory")
  deployment.setTenantId("tenantId")

//  sequential
//  "Gson" should {
//    "properly convert complex interface classes to JSON" in {
//      val gson = new Gson();
//      val json = gson.toJson(deployment)
//      println("********************")
//      println(json)
//      println("********************")
//
//      //{"bpmnData":"some data","name":"someName","category":"somCategory","activateOn":"Jun 5, 2014 12:25:15 PM","tenantId":"tenantId","id":"someId"}
//      json shouldNotEqual(null)
//    }
//  }
  "JsonConverter" should {
    "properly convert complex interface classes to JSON" in {
      var json: String = ""
      val handler = new IDeploymentHandler()
      handler.getClass.getAnnotations foreach {
        case ann: JsonSerialization =>
          try {
            if (ann.enabled) {
              val converter = new JsonConverter(ann.adapters)
              json = converter.toJson(deployment)

              println("********************")
              println(json)
              println("********************")
            }
          } catch {
            case ex: Exception =>
              throw new HandlerConfigurationException(
                s"The message handler class '${getClass.getCanonicalName}' is wrongly annotated" +
                  s" with @${classOf[JsonSerialization].getName}", ex)
          }
        case _ => // Do nothing with unrecognized annotations.
      }

      //{"bpmnData":"some data","name":"someName","category":"somCategory","activateOn":"Jun 5, 2014 12:25:15 PM","tenantId":"tenantId","id":"someId"}
      json shouldNotEqual("")
    }
  }


}
