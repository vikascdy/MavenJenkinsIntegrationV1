package com.edifecs.epp.security.data;

/**
 * Enumeration to represent the supported types of attribute values. Needed to support com.edifecs.epp.security.data validation.
 * 
 * @author hongliii
 *
 */
public enum CustomFieldValueType {
	// TODO: add other types later
	STRING("string", String.class);
	
	private final String name;
	private final Class<?> clazz;
	
	private CustomFieldValueType(String name, Class<?> clazz) {
		this.name = name;
		this.clazz = clazz;
	}

	/**
	 * @return a unique name of the attribute value type. Never null.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the class type of the attribute value type. Never null.
	 */
	public Class<?> getClazz() {
		return clazz;
	}
	
	public static CustomFieldValueType get(String name) {
		return name==null? null: valueOf(name.toUpperCase());
	}
}
