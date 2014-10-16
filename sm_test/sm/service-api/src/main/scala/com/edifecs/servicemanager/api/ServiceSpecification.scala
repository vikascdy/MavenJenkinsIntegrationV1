package com.edifecs.servicemanager.api

import com.edifecs.epp.isc.Address
import com.edifecs.epp.isc.command.CommandSpecification
import com.edifecs.epp.isc.json.{JsonTypeAdapter, Schema, JsonWritable}
import com.google.gson.{Gson, JsonArray, JsonObject, JsonElement}
import java.lang.reflect.Type
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.{JsonReader, JsonWriter}

/**
 * A JSON-serializable description of a service.
 *
 * @author c-adamnels
 */
case class ServiceSpecification(
  address: Address,
  serviceType: String,
  version: String,
  description: String = "No description available.",
  commands: Seq[CommandSpecification] = Nil
) extends JsonWritable {

  override def toJson: JsonElement = {
    val o = new JsonObject
    o.addProperty("address", address.toString)
    o.addProperty("serviceType", serviceType)
    o.addProperty("version", version)
    o.addProperty("description", description)
    o.add("commands", {
      val a = new JsonArray
      commands.foreach(c => a.add(c.toJson))
      a
    })
    o
  }

  override def getJsonSchema = ServiceSpecification.schema
}

object ServiceSpecification {
  lazy val schema = Schema.Object()
    .withRequiredProperty("address", Schema.String)
    .withRequiredProperty("serviceType", Schema.String)
    .withRequiredProperty("version", Schema.String)
    .withRequiredProperty("description", Schema.String)
    .withRequiredProperty("commands", Schema.Array(CommandSpecification.schema))

  class Adapter extends JsonTypeAdapter[ServiceSpecification] {
    override lazy val typeToken = TypeToken.get(classOf[ServiceSpecification])
    override def write(gson: Gson, out: JsonWriter, spec: ServiceSpecification) =
      gson.toJson(spec.toJson, out)
    override def read(gson: Gson, in: JsonReader) =
      throw new UnsupportedOperationException("Cannot read a ServiceSpecification.")
    override def getSchema(t: Type) = schema
  }
}
