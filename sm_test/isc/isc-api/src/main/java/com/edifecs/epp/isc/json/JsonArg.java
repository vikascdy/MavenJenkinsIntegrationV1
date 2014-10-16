package com.edifecs.epp.isc.json;

import java.io.Serializable;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * A serialized JSON argument. Will be converted to a Java object by the command
 * handler, based on the expected type of the argument.
 * 
 * @author i-adamnels
 */
public class JsonArg implements Serializable {
	private static final long serialVersionUID = 9_09_13;

	private final String jsonData; // Stored as a string because JsonElement is
									// not serializable.

	public JsonArg(String jsonString) {
		jsonData = jsonString;
	}

	public JsonArg(JsonElement el) {
		// JSON Supports three types of elements: Boolean, Long, String, JSON.
		// This is needed to support these different
		// JSON Types
		// if (el.isJsonPrimitive()) {
		// jsonData = el.getAsString();
		// } else {
		jsonData = el.toString();
		// }
	}

	public String getJsonString() {
		return jsonData;
	}

	public JsonElement getJson() {
		return new JsonParser().parse(jsonData);
	}

	@Override
	public String toString() {
        if(jsonData.startsWith("\"") && jsonData.endsWith("\"") ) {
            return jsonData.substring(1, jsonData.length() - 1);
        }
		return jsonData;
	}
}
