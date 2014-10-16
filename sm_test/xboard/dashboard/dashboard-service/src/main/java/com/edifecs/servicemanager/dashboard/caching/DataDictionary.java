package com.edifecs.servicemanager.dashboard.caching;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataDictionary {

	public enum Dictionary {
		STRING(String.class), INTEGER(Integer.class), DOUBLE(Double.class), DATE(
				Date.class);

		public final Class<?> type;

		Dictionary(Class<?> type) {
			this.type = type;
		}

		public static Dictionary getType(Class<?> classType) {
			for (Dictionary d : Dictionary.values()) {
				if (d.type.equals(classType)) {
					return d;
				}
			}
			return null;
		}
	}

	public static Map<String, Class<?>> dMap = new HashMap<String, Class<?>>();

	static {
		dMap.put("VARCHAR", String.class);
		dMap.put("CHAR", String.class);
		dMap.put("BLOB", String.class);
		dMap.put("TEXT", String.class);
		dMap.put("FLOAT", Integer.class);
		dMap.put("REAL", Double.class);
		dMap.put("DOUBLE", Double.class);
		dMap.put("NUMERIC", Integer.class);
		dMap.put("DECIMAL", Double.class);
		dMap.put("TINYINT", Integer.class);
		dMap.put("TINYINT UNSIGNED", Integer.class);
		dMap.put("SMALLINT", Integer.class);
		dMap.put("SMALLINT UNSIGNED", Integer.class);
		dMap.put("MEDIUMINT", Integer.class);
		dMap.put("INT UNSIGNED", Integer.class);
		dMap.put("INT", Integer.class);
		dMap.put("BIGINT", Integer.class);
		dMap.put("YEAR", Date.class);
		dMap.put("DATE", Date.class);
		dMap.put("TIME", Date.class);
		dMap.put("DATETIME", Date.class);
		dMap.put("TIMESTAMP", Date.class);
	}

	public static Dictionary getJavaType(String dataType) throws ClassNotFoundException {
		dataType = dataType.toUpperCase();
		Dictionary dictionary = Dictionary.getType((Class<?>) dMap.get(dataType));
		if(dictionary == null) {
		    throw new ClassNotFoundException("Unable to find java type for data type: " + dataType);
		}
		return dictionary;
	}

}
