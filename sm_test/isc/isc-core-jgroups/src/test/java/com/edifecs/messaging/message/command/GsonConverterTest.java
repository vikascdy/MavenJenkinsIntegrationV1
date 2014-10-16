package com.edifecs.messaging.message.command;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.edifecs.epp.isc.json.JsonConverter;
import com.edifecs.epp.security.data.Permission;
import com.edifecs.epp.security.data.Role;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class GsonConverterTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        String json = "{'role':{'id':1,'canonicalName':'System Role','description':''},'permissions':[{'id':61,'productCanonicalName':'platform','categoryCanonicalName':'agent','typeCanonicalName':'management','subTypeCanonicalName':'agent','canonicalName':'stop','sortOrder':0},{'id':55,'productCanonicalName':'platform','categoryCanonicalName':'agent','typeCanonicalName':'management','subTypeCanonicalName':'configuration','canonicalName':'deploy','sortOrder':0}]}";
        
        final JsonParser parser = new JsonParser();
        final JsonObject root = (JsonObject) parser.parse(json);
        
        JsonConverter converter = new JsonConverter(null);
        
        
        for (Map.Entry<String, JsonElement> jsonArg : root.entrySet()) {
                final String argKey = jsonArg.getKey();
                
                if (argKey.equals("role")) {
                    Role role = converter.fromJson(jsonArg.getValue(), Role.class);
                    
                    System.out.println(role);
                } else if (argKey.equals("permissions")) {
                    Type collectionType = new TypeToken<Collection<Permission>>(){}.getType();
                    
                    List<Permission> permissions = converter.fromJson(jsonArg.getValue(), collectionType);
                    
                    System.out.println(permissions);
                }
                
            }
    }

}
