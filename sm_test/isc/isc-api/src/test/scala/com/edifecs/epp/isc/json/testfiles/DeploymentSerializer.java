package com.edifecs.epp.isc.json.testfiles;

import java.lang.reflect.Type;

import com.edifecs.epp.isc.json.JsonTypeAdapter;
import com.edifecs.epp.isc.json.Schema;
import com.google.gson.reflect.TypeToken;

public class DeploymentSerializer extends JsonTypeAdapter<IDeployment<String>> {
	
	
/*	@Override
	public IDeployment<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		@SuppressWarnings("serial")
		Type type = new TypeToken<Deployment<String>>(){}.getType();
		System.out.println("******************************");
		System.out.println(type.toString());
		return context.deserialize(json, type);
	}*/

	
	
	@Override
	public Schema getSchema(Type arg0) {
		return Schema.fromType(arg0);
	}

	@Override
	public TypeToken<IDeployment<String>> typeToken() {
        return new TypeToken<IDeployment<String>>(){};
	}

}
