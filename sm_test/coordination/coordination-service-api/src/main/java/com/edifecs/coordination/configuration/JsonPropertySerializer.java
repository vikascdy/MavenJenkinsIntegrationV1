package com.edifecs.coordination.configuration;

import java.util.Collection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;

import com.edifecs.core.configuration.configuration.Property;

public class JsonPropertySerializer implements IPropertySerializer {

	private Gson gson;
    
	public JsonPropertySerializer() {
		gson = new Gson();
	}
	
	@Override
	public <T> byte[] serialize(Collection<? extends Serializable> properties, Class<T> clazz) throws Exception {
		if (clazz != Property.class && clazz != PropertyDefinition.class)
			throw new IllegalArgumentException("");
		
		return gson.toJson(properties).getBytes();
	}
	
	@Override
	public <T> String serializeToJsonString(Collection<? extends Serializable> properties, Class<T> clazz) throws Exception {
		if (clazz != Property.class && clazz != PropertyDefinition.class)
			throw new IllegalArgumentException("");
		
		return properties == null ? "" : gson.toJson(properties);
	}

	@Override
	public <T> Collection<T> deserialize(byte[] bytes, Class<T> clazz) throws Exception {
		if (clazz != Property.class && clazz != PropertyDefinition.class) 
			throw new IllegalArgumentException("");
		
		Type collectionType = new TypeToken<Collection<Property>>(){}.getType();
		if (clazz == PropertyDefinition.class) {
			collectionType = new TypeToken<Collection<PropertyDefinition>>(){}.getType();
		}
		return gson.fromJson(new String(bytes), collectionType);
	}

	@Override
	public <T> Collection<T> deserialize(String jsonString, Class<T> clazz) throws Exception {
		if (clazz != Property.class && clazz != PropertyDefinition.class) 
			throw new IllegalArgumentException("");
		
		Type collectionType = new TypeToken<Collection<Property>>(){}.getType();
		if (clazz == PropertyDefinition.class) {
			collectionType = new TypeToken<Collection<PropertyDefinition>>(){}.getType();
		}
		return gson.fromJson(jsonString, collectionType);
	}
}
