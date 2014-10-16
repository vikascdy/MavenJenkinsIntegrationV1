package com.edifecs.epp.isc.command

import java.lang.reflect.Type
import java.util.EnumSet

import com.edifecs.epp.isc.Isc
import com.edifecs.epp.isc.json.{JsonTypeAdapter, JsonWritable, Schema}
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.{JsonReader, JsonWriter}
import com.google.gson.{Gson, JsonArray, JsonObject, JsonPrimitive}

import scala.collection.JavaConversions._

/**
 * A JSON-serializable description of a command. Includes schemas for its
 * argument types and return types.
 *
 * @author c-adamnels
 */
case class CommandSpecification(
  name: String,
  arguments: Seq[ArgumentSpecification],
  serviceTypeName: String,
  description: String = "No description available.",
  url: Option[String] = None,
  accessibleBy: EnumSet[CommandSource] = EnumSet.noneOf(classOf[CommandSource]),
  responseTypeName: String = "java.lang.Object",
  requestBodySchema: Option[Schema.Element] = None,
  responseSchema: Option[Schema.Element] = None
) extends JsonWritable {

  lazy val supportsUrlSuffix = arguments.exists(_.name == Isc.urlSuffixArg)

  lazy val requestContentType =
    if (arguments.exists(_.stream)) Some("multipart/form-data")
    else if (arguments.exists(_.name == Isc.requestBodyArg)) Some("application/json")
    else None

  override def toJson = {
    import com.edifecs.epp.isc.command.CommandSpecification._
    val o = new JsonObject
    o.addProperty(_name, name)
    o.addProperty(_description, description)
    url.foreach(o.addProperty(_url, _))
    o.add(_arguments, {
      val a = new JsonArray
      arguments.filter(!_.name.startsWith(Isc.extraArgPrefix)).foreach(e => a.add(e.toJson))
      a
    })
    o.add(_accessibleBy, {
      val a = new JsonArray
      accessibleBy.foreach(e => a.add(new JsonPrimitive(e.getFriendlyName)))
      a
    })
    if (supportsUrlSuffix) o.addProperty(_supportsUrlSuffix, true)
    requestContentType.foreach(r => o.addProperty(_requestContentType, r))
    requestBodySchema.foreach(r => o.add(_requestBodySchema, r.toJson))
    o.addProperty(_responseType, responseTypeName)
    responseSchema.foreach(r => o.add(_responseSchema, r.toJson))
    o
  }

  override def getJsonSchema = CommandSpecification.schema
}

object CommandSpecification {
  final val _name = "name"
  final val _description = "description"
  final val _url = "url"
  final val _arguments = "arguments"
  final val _accessibleBy = "accessibleBy"
  final val _supportsUrlSuffix = "supportsUrlSuffix"
  final val _requestContentType = "requestContentType"
  final val _requestBodySchema = "requestBodySchema"
  final val _responseSchema = "responseSchema"
  final val _responseType = "responseType"

  lazy val schema = Schema.Object()
    .withRequiredProperty(_name, Schema.String)
    .withRequiredProperty(_description, Schema.String)
    .withProperty(_url, Schema.String)
    .withRequiredProperty(_arguments, Schema.Array(ArgumentSpecification.schema))
    .withRequiredProperty(_accessibleBy, Schema.Array(Schema.String.withEnum(
      CommandSource.values.map(_.getFriendlyName).map(new JsonPrimitive(_)): _*)))
    .withProperty(_supportsUrlSuffix, Schema.Boolean)
    .withProperty(_requestContentType, Schema.String)
    .withProperty(_requestBodySchema, Schema.Ref(Schema.schemaForSchema))
    .withProperty(_responseType, Schema.String)
    .withProperty(_responseSchema, Schema.Ref(Schema.schemaForSchema))

  class Adapter extends JsonTypeAdapter[CommandSpecification] {
    override lazy val typeToken = TypeToken.get(classOf[CommandSpecification])
    override def write(gson: Gson, out: JsonWriter, spec: CommandSpecification) =
      gson.toJson(spec.toJson, out)
    override def read(gson: Gson, in: JsonReader) =
      throw new UnsupportedOperationException("Cannot read a CommandSpecification.")
    override def getSchema(t: Type) = schema
  }
}

/**
 * A JSON-serializable description of a command argument. This is a part of a
 * {@link CommandSpecification}.
 *
 * @author c-adamnels
 */
case class ArgumentSpecification(
  name: String,
  description: String = "No description available.",
  required: Boolean = true,
  stream: Boolean = false,
  typeName: String = "java.lang.Object",
  schema: Option[Schema.Element] = None
) extends JsonWritable {

  override def toJson = {
    import com.edifecs.epp.isc.command.ArgumentSpecification._
    val o = new JsonObject
    o.addProperty(_name, name)
    o.addProperty(_description, description)
    o.addProperty(_required, required)
    if (stream) {
      o.addProperty(_stream, true)
    } else {
      o.addProperty(_type, typeName)
      this.schema.foreach(s => o.add(_schema, s.toJson))
    }
    o
  }

  override def getJsonSchema = ArgumentSpecification.schema
}

object ArgumentSpecification {
  final val _name = "name"
  final val _description = "description"
  final val _required = "required"
  final val _stream = "stream"
  final val _type = "type"
  final val _schema = "schema"

  lazy val schema = Schema.Object()
    .withRequiredProperty(_name, Schema.String)
    .withRequiredProperty(_description, Schema.String)
    .withRequiredProperty(_required, Schema.Boolean)
    .withProperty(_stream, Schema.Boolean)
    .withProperty(_type, Schema.String)
    .withProperty(_schema, Schema.Ref(Schema.schemaForSchema))
}
