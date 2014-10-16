// -----------------------------------------------------------------------------
//  Copyright (c) Edifecs Inc. All Rights Reserved.
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
package com.edifecs.epp.isc.json

import java.lang.reflect.{GenericArrayType, ParameterizedType, Type}
import com.edifecs.epp.isc.annotations.TypeAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken

/**
 * Converts command arguments and responses to and from JSON. It supports
 * custom conversion rules defined in {@link JsonSerialization} annotations on
 * a command handler interface.
 *
 * @author c-adamnels
 * @author ashipras
 */
class JsonConverter(adapters: Array[TypeAdapter]) {

  private val adapterInstances: Seq[(TypeToken[_], JsonTypeAdapter[_])] =
    if (adapters == null) Seq.empty
    else Seq(adapters map { a =>
      val instance = a.value.newInstance()
      instance.typeToken -> instance
    }: _*)

  private val gson = {
    val builder = new GsonBuilder
    adapterInstances foreach { e =>
      builder.registerTypeAdapter(e._1.getType, e._2)
      builder.registerTypeAdapterFactory(e._2)
    }
    builder.create
  }

  def getSchemaFor(t: Type): Option[Schema] = {
    adapterInstances.find(_._1.isAssignableFrom(t)).map(_._2.getSchema(t))
  }

  def getGson: Gson = {
    return gson
  }

  def toJson[T](obj: T): String = {
    return gson.toJson(obj)
  }

  def fromJson[T](json: String, `type`: Class[T]): T = {
    return gson.fromJson(json, `type`)
  }

  def fromJson[T](json: JsonElement, typeOfT: Type): T = {
    return gson.fromJson(json, typeOfT)
  }

  def fromJson[T](json: JsonElement, `type`: Class[T]): T = {
    return gson.fromJson(json, `type`)
  }
}