package com.edifecs.epp.security.service.util;

import java.io.InputStream;
import java.io.InputStreamReader;

import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.report.ProcessingReport;



public class JsonValidator {

    public String ValidateJsonFromSchema(InputStream schemaPath, String jsonStream) throws Exception {
        final com.fasterxml.jackson.databind.JsonNode productSchema = JsonLoader.fromReader(new InputStreamReader(schemaPath));
        final com.fasterxml.jackson.databind.JsonNode jsonFile = JsonLoader.fromString(jsonStream);	
        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        final JsonSchema schema = factory.getJsonSchema(productSchema);
        ProcessingReport report = schema.validate(jsonFile);
        if (report.isSuccess()){
        	String response = report.toString();
        	if (response.contains(":")){
        		response = response.split(":")[1];
        	}
			return response.trim();
		} else {
			return report.iterator().next().asJson().toString();
		}
    }
    
    
    /*public static void main(String[] args) {
		try {
			InputStream schemaPath = JsonValidator.class.getResourceAsStream("/role_validation_schema.json");
			InputStream jsonStream = JsonValidator.class.getResourceAsStream("/test_roles_import.json");
			String report = new JsonValidator().ValidateJsonFromSchema(schemaPath, jsonStream);
			System.out.println(report);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

}
