package com.edifecs.epp.security.handler.serializer;

import com.edifecs.epp.isc.json.JsonTypeAdapter;
import com.edifecs.epp.isc.json.Schema;
import com.edifecs.epp.security.SessionId;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

public class SessionIdSerializer extends JsonTypeAdapter<SessionId> {

    @Override
    public TypeToken<SessionId> typeToken() {
        return TypeToken.get(SessionId.class);
    }

    @Override
    public void write(Gson gson, JsonWriter out, SessionId id) throws IOException {
        out.value(id.getSessionId().toString());
    }

    @Override
    public SessionId read(Gson gson, JsonReader in) throws IOException {
        return new SessionId(in.nextString());
    }

    @Override
    public Schema getSchema(Type t) {
        return Schema.String();
    }
}
