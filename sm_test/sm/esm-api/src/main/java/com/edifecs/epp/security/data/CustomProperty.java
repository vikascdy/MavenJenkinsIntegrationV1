package com.edifecs.epp.security.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

public class CustomProperty implements Serializable {
	private static final long serialVersionUID = 8575903712284364667L;

	private Long propertyId;
	private String name;

	private String description;
	private String defaultVal;
	private String value;
	private boolean required;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getDefaultVal() {
		return defaultVal;
	}

	public void setDefaultVal(String defaultVal) {
		this.defaultVal = defaultVal;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static Properties parseProperties(
			List<CustomProperty> customProperties) {
		Properties p = new Properties();
		for (CustomProperty cp : customProperties)
			p.put(cp.getName(), cp.getValue());
		return p;
	}

	public static List<CustomProperty> fromProperties(Properties p) {
		List<CustomProperty> properties = new ArrayList<>();
		for (Entry<Object, Object> entry : p.entrySet()) {
			CustomProperty cp = new CustomProperty();
			cp.setName(entry.getKey().toString());
			cp.setValue(entry.getValue().toString());
			properties.add(cp);
		}
		return properties;
	}

	public void setPropertyId(Long propertyId) {
		this.propertyId = propertyId;
	}

	public Long getPropertyId() {
		return propertyId;
	}
}
