package com.edifecs.epp.isc.core.command

import java.io.Serializable
import java.lang.reflect.{InvocationTargetException, Method, Type}
import java.util.Collections.emptyList

import com.edifecs.epp.isc.annotations.{CommandHandler, NullSessionAllowed}
import com.edifecs.epp.isc.command.IRestCommandHandler
import com.edifecs.epp.isc.json.{JsonArg, JsonTypeAdapter}
import com.edifecs.epp.security.remote.SessionManager
import com.google.gson._
import com.google.gson.reflect.TypeToken

import scala.collection.JavaConversions._
import scala.collection._

abstract class RestCommandHandler[T <: Serializable with Object](
  val typeAdapter: JsonTypeAdapter[T]
) extends AbstractCommandHandler with IRestCommandHandler[T] {

  private final val sorterListType = new TypeToken[List[Sorter]](){}.getType
  private final val filterListType = new TypeToken[List[Filter]](){}.getType

  private val (listMethod, getMethod, postMethod, putMethod, deleteMethod) =
    try {
      var cls: Class[_] = getClass
      if (!cls.isAnnotationPresent(classOf[CommandHandler])) {
        cls = cls.getInterfaces.find(_.isAnnotationPresent(classOf[CommandHandler])) getOrElse cls
      }
      ( cls.getMethod("list",   classOf[Pagination]),
        cls.getMethod("get",    classOf[String]),
        cls.getMethod("post",   classOf[Serializable]),
        cls.getMethod("put",    classOf[String], classOf[Serializable]),
        cls.getMethod("delete", classOf[String]) )
   } catch {
    case ex: NoSuchMethodException =>
      throw new IllegalStateException(ex)
   }

  val gson = {
    val builder = new GsonBuilder().registerTypeAdapter(
      classOf[Sorter], new SorterDeserializer())
    builder.registerTypeAdapterFactory(typeAdapter)
    builder.registerTypeAdapter(typeAdapter.typeToken.getType, typeAdapter)
    builder.create
  }

  @throws(classOf[Exception])
  override def restCommand(
    method: String,
    urlSuffix: String,
    body: JsonArg,
    page: java.lang.Long,
    start: java.lang.Long,
    limit: java.lang.Long,
    query: String,
    sortersJson: String,
    filtersJson: String
  ): Serializable =
    method.toUpperCase match {
      case "GET" =>
        if (urlSuffix == null) {
          // list
          val sorters: Seq[Sorter] =
            if (sortersJson == null) Nil
            else {gson.fromJson(sortersJson, sorterListType)}
          val filters: Seq[Filter] =
            if (filtersJson == null) Nil
            else {gson.fromJson(filtersJson, filterListType)}
          // for getting total count, needed for pagination in UI
          val total = callWithSecurity(listMethod)(
            new Pagination(
              if (page == null) -1L else page,
              0L,
              Integer.MAX_VALUE,
              query, emptyList[Sorter], emptyList[Filter])
          ).asInstanceOf[java.util.Collection[T]].size
          val items = callWithSecurity(listMethod)(
            new Pagination(
              if (page == null) -1L else page,
              if (start == null) 0L else start,
              if (limit == null) Integer.MAX_VALUE else limit,
              query, sorters, filters)
          ).asInstanceOf[java.util.Collection[T]]
          val paginatedData = new java.util.HashMap[String, Serializable]()
          paginatedData.put("resultList", new java.util.ArrayList[T](items))
          paginatedData.put("total", total)
          paginatedData
        } else {
          // get
          callWithSecurity(getMethod)(urlSuffix).asInstanceOf[T]
        }
      case "POST" =>
        if (urlSuffix != null) {
          throw new IllegalArgumentException(
            "A POST request may only be made to the root URL, not a specific item URL.")
        }
        if (body == null) {
          throw new IllegalArgumentException(
            "A POST request to this URL must have a JSON message body.")
        }
        val item: Object = gson.fromJson(body.getJson, typeAdapter.typeToken.getType)
        callWithSecurity(postMethod)(item).asInstanceOf[T]
      case "PUT" =>
        if (urlSuffix == null) {
          throw new IllegalArgumentException(
            "A PUT request may only be made to a specific item URL, not to the root URL.")
        }
        if (body == null) {
          throw new IllegalArgumentException(
            "A PUT request to this URL must have a JSON message body.")
        }
        val item: Object = gson.fromJson(body.getJson, typeAdapter.typeToken.getType)
        callWithSecurity(putMethod)(urlSuffix, item).asInstanceOf[T]
      case "DELETE" =>
        if (urlSuffix == null) {
          throw new IllegalArgumentException(
            "A DELETE request may only be made to a specific item URL, not to the root URL.")
        }
        callWithSecurity(deleteMethod)(urlSuffix)
        null
      case _ =>
        throw new IllegalArgumentException("Unrecognized HTTP method: " + method)
    }

  private def callWithSecurity(method: Method)(arguments: Object*): Any = {
    try {
      val manager = getSecurityManager.getSessionManager.asInstanceOf[SessionManager]
      val session = manager.getCurrentSession
      if (session != null) {
        manager.callMethodAsUser(
          session, this, method, Array[Object](arguments: _*))
      } else {
        // In the event of a null session...
        if (getClass.isAnnotationPresent(classOf[NullSessionAllowed]) ||
            method.isAnnotationPresent(classOf[NullSessionAllowed])) {
          method.invoke(this, arguments: _*)
        } else {
          throw new SecurityException(
            "This REST handler does not allow access with a null session.")
        }
      }
    } catch {
      case ex: InvocationTargetException =>
        throw ex.getTargetException
    }
  }
}

case class Pagination(
     page: Long,
     start: Long,
     limit: Long,
     query: String,
     sorters: java.util.List[Sorter],
     filters: java.util.List[Filter])

case class Sorter(
     property: String,
     direction: Direction)

sealed trait Direction {
  val asc: Boolean
}

case object Ascending extends Direction {override val asc = true}
case object Descending extends Direction {override val asc = false}

case class Filter(
     property: String,
     value: Object)

class SorterDeserializer extends JsonDeserializer[Sorter] {
  @throws(classOf[JsonParseException])
  override def deserialize(
      json: JsonElement,
      `type`: Type,
      context: JsonDeserializationContext
      ): Sorter = {
    val obj = json.getAsJsonObject
    val property = obj.get("property").getAsString
    val direction = obj.get("direction").getAsString
    if (direction.toUpperCase.startsWith("DESC")) {
      Sorter(property, Descending)
    } else {
      Sorter(property, Ascending)
    }
  }
}
