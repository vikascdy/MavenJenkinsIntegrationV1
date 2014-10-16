package com.edifecs.epp.isc.json

import com.google.gson._
import java.lang.reflect.{ParameterizedType, Type}
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.{JsonWriter, JsonReader}
import java.io.IOException

/**
 * A combination {@link TypeAdapter} and {@link InstanceCreator}, with schema
 * support.
 *
 * @author c-adamnels
 */
abstract class JsonTypeAdapter[T] extends TypeAdapterFactory with InstanceCreator[T] {

  def typeToken: TypeToken[T]

  @throws[IOException]
  def write(gson: Gson, out: JsonWriter, value: T): Unit =
    gson.getDelegateAdapter(this, typeToken).write(out, value)

  @throws[IOException]
  def read(gson: Gson, in: JsonReader): T =
    gson.getDelegateAdapter(this, typeToken).read(in)

  override def createInstance(`type`: Type): T = `type` match {
    case c: Class[T] =>
      try c.newInstance()
      catch {
        case ex: InstantiationException =>
          throw new UnsupportedOperationException("Cannot create an instance of type <" + c + ">.",
            ex)
        case ex: IllegalAccessException =>
          throw new UnsupportedOperationException("Cannot create an instance of type <" + c + ">.",
            ex)
      }
    case pt: ParameterizedType =>
      createInstance(pt.getRawType)
    case _ =>
      throw new UnsupportedOperationException("Cannot create an instance of type <"+`type`+">.")
  }

  def getSchema(t: Type): Schema

  private class Adapter(gson: Gson) extends TypeAdapter[T] {
    @throws[IOException]
    override def write(out: JsonWriter, value: T): Unit =
      JsonTypeAdapter.this.write(gson, out, value)

    @throws[IOException]
    override def read(in: JsonReader): T =
      JsonTypeAdapter.this.read(gson, in)
  }

  override final def create[U](gson: Gson, `type`: TypeToken[U]): TypeAdapter[U] =
    if (typeToken.isAssignableFrom(`type`)) {
      new Adapter(gson).asInstanceOf[TypeAdapter[U]]
    } else null
}

object JsonTypeAdapter {
  def defaultForClass[T](cls: Class[T]) = new JsonTypeAdapter[T] {
    override lazy val typeToken = TypeToken.get(cls)
    override def getSchema(t: Type) = Schema.fromType(cls)
  }
}