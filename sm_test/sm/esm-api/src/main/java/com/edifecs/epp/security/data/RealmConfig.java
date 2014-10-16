package com.edifecs.epp.security.data;

public class RealmConfig extends CustomProperty {
	private static final long serialVersionUID = -1L;

	public RealmConfig(String name, String description, String val, boolean required) {
		super();
		setName(name);
		setDescription(description);
		setValue(val);
		setRequired(required);
	}
}