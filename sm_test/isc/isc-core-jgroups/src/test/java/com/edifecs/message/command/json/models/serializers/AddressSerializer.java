package com.edifecs.message.command.json.models.serializers;

import java.io.IOException;
import java.lang.reflect.Type;

import com.edifecs.epp.isc.Address;
import com.edifecs.epp.isc.json.JsonTypeAdapter;
import com.edifecs.epp.isc.json.Schema;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class AddressSerializer extends JsonTypeAdapter<Address> {

    @Override
    public TypeToken<Address> typeToken() {
        return TypeToken.get(Address.class);
    }

    @Override
    public void write(Gson gson, JsonWriter out, Address value) throws IOException {
        out.value(value.toString());
    }

    @Override
    public Address read(Gson gson, JsonReader in) throws IOException {
        return new Address(in.nextString());
    }

    @Override
    public Schema getSchema(Type t) {
        return Schema.String();
    }
}
