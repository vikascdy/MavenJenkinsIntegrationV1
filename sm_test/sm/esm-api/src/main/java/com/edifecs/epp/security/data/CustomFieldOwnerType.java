package com.edifecs.epp.security.data;

public enum CustomFieldOwnerType {
	USER("user", User.class);
	
	private final String name;
	private final Class<?> clazz;
	
	private CustomFieldOwnerType(String name, Class<?> clazz) {
		this.name = name;
		this.clazz = clazz;
	}

	public String getName() {
		return name;
	}

	public Class<?> getClazz() {
		return clazz;
	}
	
	public static CustomFieldOwnerType get(String name) {
		return name==null? null: valueOf(name.toUpperCase());
	}
	
	public static CustomFieldOwnerType get(Class<?> clazz) {
		for (CustomFieldOwnerType customFieldOwnerType : values()) {
			if (customFieldOwnerType.getClazz().equals(clazz)) return customFieldOwnerType;
		}
		return null;
	}
	
}
