package com.edifecs.epp.isc.core.command.test

import java.io.Serializable

import scala.collection.JavaConversions._
import scala.collection._
import scala.concurrent.Await
import scala.concurrent.duration._

import org.specs2.specification.Scope

import com.edifecs.epp.isc.CommandCommunicator
import com.edifecs.epp.isc.annotations._
import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder
import com.edifecs.epp.isc.command.CommandMessage
import com.edifecs.epp.isc.core.command._
import com.edifecs.epp.isc.json.{JsonArg, JsonTypeAdapter}
import com.edifecs.test.BeforeAfterSpecification

class RestCommandHandlerTest extends BeforeAfterSpecification {

  private var cc: CommandCommunicator = null

  "A REST command handler" should {
    "respond to a root GET request with a list of items" in new RestContext {
      val rsp = sendRest("get").asInstanceOf[java.util.Map[String, Any]]
      val list = rsp.get("resultList").asInstanceOf[java.util.Collection[String]]
      (list.size mustEqual 3) and
      (rsp.get("total") mustEqual 3) and
      ((list contains "one") must beTrue) and
      ((list contains "two") must beTrue) and
      ((list contains "three") must beTrue)
    }
    // TODO: Test pagination.
    "respond to a suffixed GET request with an individual item" in new RestContext {
      (sendRest("get", urlSuffix="1") mustEqual "one") and
      (sendRest("get", urlSuffix="2") mustEqual "two") and
      (sendRest("get", urlSuffix="3") mustEqual "three") and
      (sendRest("get", urlSuffix="4") must throwA[NoSuchElementException])
    }
    "create new items in response to a POST request" in new RestContext {
      val postResult = sendRest("post", body="newItem")
      val newItem  = sendRest("get", urlSuffix="4")
      (postResult mustEqual "newItem") and
      (newItem mustEqual "newItem")
    }
    "create new items in response to a PUT request" in new RestContext {
      val putResult = sendRest("put", urlSuffix="91", body="newItem")
      val newItem  = sendRest("get", urlSuffix="91")
      (putResult must beNull) and
      (newItem mustEqual "newItem")
    }
    "delete items in response to a PUT request" in new RestContext {
      sendRest("delete", urlSuffix="1")
      (sendRest("get", urlSuffix="1") must throwA[NoSuchElementException]) and
      (sendRest("get", urlSuffix="2") mustEqual "two") and
      (sendRest("get", urlSuffix="3") mustEqual "three")
    }
    "support additional commands" in new RestContext {
      send("extraCommand") mustEqual "extra command"
    }
  }

  protected override def beforeAll() = {
    cc = new CommandCommunicatorBuilder().initializeTestMode
    cc.connect()
  }

  protected override def afterAll() = {
    cc.disconnect()
  }

  trait RestContext extends Scope {
    
    @CommandHandler
    @NullSessionAllowed
    object TestHandler extends RestCommandHandler[String](
        JsonTypeAdapter.defaultForClass(classOf[String])) {
      val db = mutable.Map[Int, String](
        1 -> "one",
        2 -> "two",
        3 -> "three")
      
      override def get(url: String) =
        db.get(url.toInt).get

      override def list(pg: Pagination) =
        // TODO: Incorporate pagination somehow.
        db.values

      override def post(newItem: String) = {
        var done = false
        var index = 1
        while (!done) {
          if (!(db contains index)) {
            db.put(index, newItem)
            done = true
          } else index += 1
        }
        newItem
      }

      override def put(url: String, item: String) =
        db.put(url.toInt, item).orNull

      override def delete(url: String) =
        db.remove(url.toInt)

      @SyncCommand
      def extraCommand = "extra command"
    }

    val receiver = new MultiCommandReceiver(
      CommandAnnotationProcessor.processAnnotatedCommandHandler(TestHandler, Some(cc)))

    def send(
      command: String,
      args: immutable.Map[String, Serializable] = Map.empty
    ): Any = {
      Await.result(receiver.receiveCommand(
        cc, new CommandMessage(command, args)
      ), DurationInt(1).seconds)
    }

    def sendRest(
      method: String,
      urlSuffix: String     = null,
      body: String          = null,
      page: java.lang.Long  = null,
      start: java.lang.Long = null,
      limit: java.lang.Long = null,
      query: String         = null,
      sortersJson: String   = null,
      filtersJson: String   = null
    ): Any =
      send("", immutable.Map(
        "-x-rest-method"  -> method,
        "-x-url-suffix"   -> urlSuffix,
        "-x-request-body" -> (if (body == null) null else new JsonArg(body)),
        "page"            -> page,
        "start"           -> start,
        "limit"           -> limit,
        "query"           -> query,
        "sort"            -> sortersJson,
        "filter"          -> filtersJson))
  }
}

