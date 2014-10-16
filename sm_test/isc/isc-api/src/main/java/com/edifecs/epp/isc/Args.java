package com.edifecs.epp.isc;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.edifecs.epp.isc.json.JsonArg;
import com.edifecs.epp.isc.command.CommandMessage;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Utility class that allows argument maps for {@link CommandMessage}s to be
 * constructed easily using varargs constructors and method chaining.
 * 
 * @author c-adamnels
 */
public class Args extends    AbstractMap<String, Serializable> 
                  implements Serializable {
    private static final long serialVersionUID = 5_21_14;

    /**
     * Builds an argument map from a JSON string, and attaches a special flag
     * argument indicating that the message is a JSON message.
     */
    public static Args fromJson(String jsonData) {
        final Args args = new Args();
        // TODO: If this is an invalid JSON, throw a friendly exception
        final JsonObject obj = new JsonParser().parse(jsonData).getAsJsonObject();
        for (Entry<String, JsonElement> e : obj.entrySet()) {
            args.put(e.getKey(), new JsonArg(e.getValue()));
        }
        return args.and("-x-json", true); // FIXME: Make the param name a reference to a global constant again.
    }
    
    private final Map<String, Serializable> map = new HashMap<>();
    
    public Args() {}
    
    public Args(String k1, Serializable v1) {
        put(k1, v1);
    }
    
    public Args(String k1, Serializable v1,
                String k2, Serializable v2) {
        put(k1, v1);
        put(k2, v2);
    }
    
    public Args(String k1, Serializable v1,
                String k2, Serializable v2,
                String k3, Serializable v3) {
        put(k1, v1);
        put(k2, v2);
        put(k3, v3);
    }
    
    public Args(String k1, Serializable v1,
                String k2, Serializable v2,
                String k3, Serializable v3,
                String k4, Serializable v4,
                Serializable... rest) {
        put(k1, v1);
        put(k2, v2);
        put(k3, v3);
        put(k4, v4);
        if (rest.length % 2 == 1)
            throw new IllegalArgumentException("The Args constructor expects an"
                + " even number of arguments.");
        for (int i=0; i<rest.length; i+=2) {
            if (rest[i] instanceof String)
                put((String)rest[i], rest[i+1]);
            else if (rest[i] == null)
                throw new NullPointerException(
                    "Argument names may not be null.");
            else
                throw new IllegalArgumentException("Argument names must be"
                    + " strings; got an object of type " + rest[i].getClass()
                    + " instead.");
        }
    }
    
    /**
     * Chainable version of {@link #put(String, Serializable)}. Returns the
     * `Args` object itself.
     */
    public Args and(String key, Serializable value) {
        put(key, value);
        return this;
    }
    
    @Override public Serializable get(Object key) {
        return map.get(key);
    }

    @Override public Serializable put(String key, Serializable value) {
        if (key == null)
            throw new NullPointerException("Argument names may not be null.");
        return map.put(key, value);
    }
    
    @Override public Set<java.util.Map.Entry<String, Serializable>> entrySet() {
        return map.entrySet();
    }

    @Override public Set<String> keySet() {
        return map.keySet();
    }
}
