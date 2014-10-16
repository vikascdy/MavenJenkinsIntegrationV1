package com.edifecs.epp.isc.core.command

import java.lang.reflect.Type

import com.google.gson._

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
      Sorter(property, Descending);
    } else {
      Sorter(property, Ascending)
    }
  }
}
