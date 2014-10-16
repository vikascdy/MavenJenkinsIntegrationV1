package com.edifecs.coordination.configuration;

import java.io.Serializable;
import java.util.Collection;

public interface IPropertySerializer {

	/**
     * Serialize collection of properties into byte array
     *
     * @param properties the collection of properties
     * @param clazz Class of property
     * @return byte array representing the collection of properties
     * @throws Exception any errors
     */
    public <T> byte[] serialize(Collection<? extends Serializable> properties, Class<T> clazz) throws Exception;

    /**
     * Serialize collection of properties into json string
     *
     * @param properties the collection of properties
     * @param clazz Class of property
     * @return json string representing the collection of properties
     * @throws Exception any errors
     */
    public <T> String serializeToJsonString(Collection<? extends Serializable> properties, Class<T> clazz) throws Exception;
    
    /**
     * Deserialize a byte array into a collection of properties
     *
     * @param bytes the byte array
     * @param clazz Class of property
     * @return collection of properties
     * @throws Exception any errors
     */
    public <T> Collection<T> deserialize(byte[] bytes, Class<T> clazz) throws Exception;
    
    /**
     * Deserialize a json string into collection of properties 
     *
     * @param jsonString the json string of properties
     * @param clazz Class of property
     * @return collection of properties
     * @throws Exception any errors
     */
    public <T> Collection<T> deserialize(String jsonString, Class<T> clazz) throws Exception;
    
}
