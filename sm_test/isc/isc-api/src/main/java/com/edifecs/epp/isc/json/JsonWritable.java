package com.edifecs.epp.isc.json;

import com.google.gson.JsonElement;

/**
 * Any object that can be serialized to JSON via a {@link #toJson()} method.
 *
 * @author c-adamnels
 */
public interface JsonWritable {

    JsonElement toJson();

    Schema getJsonSchema();
}
