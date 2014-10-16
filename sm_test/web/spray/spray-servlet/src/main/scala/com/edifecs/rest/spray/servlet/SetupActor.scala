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

import java.io.{File, FileNotFoundException, PrintWriter}
import java.net.InetAddress

import akka.actor.{Actor, ActorLogging}
import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder
import com.edifecs.epp.isc.json.JsonArg
import com.edifecs.epp.isc.{Address, Args, Isc}
import com.edifecs.rest.spray.service.RestConstants._
import com.edifecs.rest.spray.service.RestService
import spray.http.HttpHeaders.`Content-Type`
import spray.http.MediaTypes._
import spray.http.{FormData, StatusCodes}
import spray.httpx.unmarshalling.FormDataUnmarshallers
import spray.json.{JsBoolean, JsNumber, JsObject, JsonParser}
import spray.routing._

import scala.io.Source

/**
 * SetupActor is designed to be used by the spray servlet. It extends the functionality of the basic default ISC spray
 * router and enables the configuration of the cluster and some simple administrative tools.
 *
 * @author willclem
 */
class SetupActor(val ctx: javax.servlet.ServletContext) extends Actor with RestSetup with ActorLogging {

   // the HttpService trait defines only one abstract member, which
   // connects the services environment to the enclosing actor or test
   def actorRefFactory = context



   // this actor only runs our route, but you could add
   // other things here, like request stream processing,
   // timeout handling or alternative handler registration

   def receive = runRoute(jsonRoute)



 }


// this trait defines our service behavior independently from the service actor
trait RestSetup extends RestService with FormDataUnmarshallers {

  val ctx:javax.servlet.ServletContext
  //TODO--consider making the name configurable somewhere...
  val cachedConnectionFilePath = this.ctx.getRealPath("/WEB-INF/")+File.separator+"connection.json";
  private val setupPath = pathPrefix("rest" / "setup" / "cluster")
  private val connectPath = path("rest" / "setup" / "connect" )

  var jsonRoute: Route =

    connectPath {


      get {


        respondWithStatus(StatusCodes.Accepted) {
          complete(checkCachedConnection)
        }

      }
    } ~
    setupPath {
      put {

        headerValuePF {
          case `Content-Type`(ct) => ct.mediaType
        } {
          _ match {
            case `application/json` =>
              entity(as[String]) {
                json: String =>
                  val args = Args.fromJson(
                    JsObject(Isc.requestBodyArg -> JsonParser(json)).toString
                  )


                  setupCluster(args)

              }
            case `application/x-www-form-urlencoded` =>
              entity(as[FormData]) { form: FormData =>
                val formMap = Map[String, String]() ++ form.fields
                val args = Args.fromJson(formMap.get(jsonKey).get)
                setupCluster(args)
              }
            case _ =>
              respondWithStatus(StatusCodes.UnsupportedMediaType) {
                complete("Requests must have one of the following media types:" +
                  " application/json, application/x-www-form-urlencoded")
              }
          }
        }
      }
    } ~
    jsonMessageRoute ~
    noop {
      respondWithStatus(StatusCodes.BadRequest) {
        complete("Requests must have one of the following media types:" +
          " application/json, application/x-www-form-urlencoded")
      }
    }

  /**
   * Parses a string of connection json and connects to the cluster.
   * @param jsonString
   * @return
   */
  def setupClusterFromString(jsonString : String) : Boolean = {
    try {
        val json = new JsonArg(jsonString)
        val clusterName = json.getJson.getAsJsonObject.getAsJsonPrimitive("clusterName").getAsString
        val environmentName = json.getJson.getAsJsonObject.getAsJsonPrimitive("environmentName").getAsString
        val connectionType = json.getJson.getAsJsonObject.getAsJsonPrimitive("connectionType").getAsString
        val listeningPort = json.getJson.getAsJsonObject.getAsJsonPrimitive("listeningPort").getAsString
        val hostId = json.getJson.getAsJsonObject.getAsJsonPrimitive("hostId").getAsString

        val builder = new CommandCommunicatorBuilder
        // builder.setTransportProtocol()
        builder.setAddress(new Address(getHostname, "Spray-Servlet"))
        builder.setClusterName(clusterName)

        val cc = builder.initialize()

        cc.connect()
        cacheConnection(json.getJsonString());

        return true
  } catch {
          //TODO--log exception
        case e: Exception => throw e
  }

  }

  /**
   *
   * @return json if there is connection file available.  None if not.
   *
   *         garyruss
   */
  def checkCachedConnection: String = {


    var cachedConnection: Option[String] = getCachedConnection
    cachedConnection match {
      case Some(json) => {


        setupClusterFromString(json)

        val ret = JsObject(
          "success" -> JsBoolean(true),
          "time" -> JsNumber(System.currentTimeMillis())
        ).toString
        return ret
      }

      case None => {
        val ret = JsObject(
          "success" -> JsBoolean(false)
        ).toString
        return ret
      }
    }
  }



  def setupCluster(params: Args): Route = {
    System.out.println("++++++++++++++++++++++++++++++")
    params.get("-x-request-body") match {
      case g2: JsonArg => {
        try {
            setupClusterFromString(g2.getJsonString())
            cacheConnection(g2.getJsonString());

            respondWithStatus(StatusCodes.Accepted) {
              complete(JsObject(
                "success" -> JsBoolean(true)
              ).toString)
            }



        } catch {
          case e: Exception => respondWithStatus(StatusCodes.InternalServerError) {
            complete(JsObject(
              "success" -> JsBoolean(false)
            ).toString)
          }
        }
      }
      case _ => {
        respondWithStatus(StatusCodes.InternalServerError) {
          complete(JsObject(
            "success" -> JsBoolean(false)
          ).toString)
        }
      }
    }
  }




  def getHostname: String = {
    var name: String = null
    try {
      name = InetAddress.getLocalHost.getHostName
    } catch {
      case e: Exception => name = "localhost"
    }
    return name
  }

  /**
   * Attempts to get the connection string from the connection file.]
   * If the file does not exist, the string is null.  Exceptions from
   * file operations should be handled by the calling method.
   *
   * @return The JSON string.  NULL if the file does not exist.
   */
  def getCachedConnection: Option[String] = {
    var json: String = null;
      System.out.println("Getting Cached Connection")
      try{

        var jsonFile=Source.fromFile(this.cachedConnectionFilePath)
        json=jsonFile.mkString
      jsonFile.close()

      Some(json)


    }
    catch {
      case e: FileNotFoundException => None; //TODO--log exception
      case e: Exception => throw e
    }
  }

  /**
   * Write the JSON connection data to a file.
   * @param json
   */
  def cacheConnection(json: String) : Unit = {

    val pw=new PrintWriter(this.cachedConnectionFilePath);
    pw.print(json);
    pw.close();

  }
}
