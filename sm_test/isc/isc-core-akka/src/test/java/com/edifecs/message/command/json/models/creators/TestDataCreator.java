package com.edifecs.message.command.json.models.creators;

import java.lang.reflect.Type;

import com.edifecs.epp.isc.json.JsonTypeAdapter;
import com.edifecs.epp.isc.json.Schema;
import com.edifecs.message.command.json.models.TestData;
import com.edifecs.message.command.json.models.TestDataImpl;
import com.google.gson.reflect.TypeToken;

public class TestDataCreator extends JsonTypeAdapter<TestData> {

    @Override
    public TypeToken<TestData> typeToken() {
        return TypeToken.get(TestData.class);
    }

	@Override
	public TestData createInstance(Type type) {				
		TestDataImpl impl = new TestDataImpl("1", "2", "3");
		return impl;
	}

    @Override
    public Schema getSchema(Type t) {
        return Schema.Wildcard(); // Placeholder.
    }
}
