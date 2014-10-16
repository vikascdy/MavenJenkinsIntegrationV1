package com.edifecs.xboard.portal;

import java.io.IOException;
import java.lang.reflect.Type;

import com.edifecs.epp.isc.json.JsonTypeAdapter;
import com.edifecs.epp.isc.json.Schema;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;


public class MenuEntrySerializer extends JsonTypeAdapter<DoormatMenuEntry> {

    static final String
        TEXT        = "text",
        LINK_URL    = "linkUrl",
        HREF_TARGET = "hrefTarget",
        JAVASCRIPT  = "javascript",
        WEIGHT      = "weight",
        SUB_MENU    = "subMenu";

    public static final Schema SCHEMA;
    static {
        Schema.Object s = Schema.Object()
            .withRequiredProperty(TEXT, Schema.String())
            .withProperty(LINK_URL, Schema.TypeUnion("string", "null"))
            .withProperty(HREF_TARGET, Schema.TypeUnion("string", "null"))
            .withProperty(JAVASCRIPT, Schema.TypeUnion("string", "null"));
        s.withProperty(SUB_MENU, Schema.Array(Schema.Ref(s)));
        s.withDefName("menuEntry");
        SCHEMA = Schema.Ref(s);
    }

    @Override
    public TypeToken<DoormatMenuEntry> typeToken() {
        return TypeToken.get(DoormatMenuEntry.class);
    }

    @Override
    public void write(Gson gson, JsonWriter out, DoormatMenuEntry entry) throws IOException {
        out.beginObject()
            .name(TEXT).value(entry.getText())
            .name(LINK_URL).value(entry.getLinkUrl())
            .name(WEIGHT).value(entry.getWeight())
            .name(HREF_TARGET).value(entry.getTarget())
            .name(JAVASCRIPT).value(entry.getJavascript());
        if (entry.hasSubMenu()) {
            out.name(SUB_MENU).beginArray();
            for (DoormatMenuEntry e : entry.getSubMenu()) write(gson, out, e);
            out.endArray();
        }
        out.endObject();
    }

    @Override
    public DoormatMenuEntry read(Gson gson, JsonReader in) {
        throw new UnsupportedOperationException("Cannot deserialize a DoormatMenuEntry.");
    }

    @Override
    public Schema getSchema(Type t) {
        return SCHEMA;
    }
}
