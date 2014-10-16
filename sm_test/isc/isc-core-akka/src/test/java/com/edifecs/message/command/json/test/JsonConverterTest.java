package com.edifecs.message.command.json.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import com.edifecs.message.command.json.models.TestData;
import com.edifecs.message.command.json.models.TestDataImpl;
import com.edifecs.message.command.json.models.TestDataImpl1;
import com.edifecs.message.command.json.models.creators.TestDataCreator;
import com.edifecs.message.command.json.models.creators.TestDataCreator1;
import com.edifecs.message.command.json.models.serializers.AddressSerializer;
import com.edifecs.epp.isc.Address;
import com.edifecs.epp.isc.annotations.JsonSerialization;
import com.edifecs.epp.isc.annotations.TypeAdapter;
import com.edifecs.epp.isc.json.JsonConverter;

@JsonSerialization(adapters = {
	@TypeAdapter(AddressSerializer.class),
	@TypeAdapter(TestDataCreator.class),
	@TypeAdapter(TestDataCreator1.class)
})
public class JsonConverterTest {
	
	private JsonConverter converter;

	@Test
	public void testAddress() {
		Address addr = new Address("default", "Node1");
		
		String json = converter.toJson(addr);
		assert(null != json && json.length() > 0);
		System.out.println(json);
		
		Address addrNew = converter.fromJson(json, Address.class); 
		assert(null != addrNew);
		System.out.println(addrNew.toString());
	}
	
	@Test
	public void testDataImpl() {
		TestData impl = new TestDataImpl("11", "22", "33");
		
		String json = converter.toJson(impl);
		assert(null != json && json.length() > 0);
		System.out.println(json);
		
		TestData data = converter.fromJson(json, TestData.class); 
		assert(null != data);
		System.out.println(data.toString());
	}

	@Test
	public void testDataImpl1() {
		TestData impl = new TestDataImpl1("11", "22");
		
		String json = converter.toJson(impl);
		assert(null != json && json.length() > 0);
		System.out.println(json);
		
		TestData data = converter.fromJson(json, TestData.class); 
		assert(null != data);
		System.out.println(data.toString());
	}
	
	@Before
	public void setup() throws Exception {
		
		if (! this.getClass().getAnnotation(JsonSerialization.class).enabled()) {
			throw new Exception("JsonSerialization not enabled.");
		}
		
		converter = new JsonConverter(this.getClass().getAnnotation(JsonSerialization.class).adapters());
	}
	
	@After
	public void exit() {
		converter = null;
	}
}
