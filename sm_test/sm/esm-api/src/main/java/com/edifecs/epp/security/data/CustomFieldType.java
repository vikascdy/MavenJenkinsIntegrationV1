package com.edifecs.epp.security.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines the definition for a type of {@link CustomField}.
 * 
 * @author hongliii
 *
 */
public class CustomFieldType implements Serializable {
	private static final long serialVersionUID = 6541812356088322988L;
	
	private Long id;
	private String name;
	private String label;
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	private String namespace;
	private String description;
	
	// Only if the event is fired, the system will check whether the custom field should be created.
	private final List<EventType> triggerEvents = new ArrayList<>();
	public List<EventType> getTriggerEvents() {
		return triggerEvents;
	}

	public void setTriggerEvents(List<EventType> triggerEvents) {
		this.triggerEvents.clear();
		this.triggerEvents.addAll(triggerEvents);
	}

	// creation of the custom field will triggered only if the matcher expression is satisfied
	private String matcher;
	private String defaultValue;
	private String valueExpression;
	private boolean required;
	private CustomFieldOwnerType customFieldOwnerType;
	private CustomFieldValueType customFieldValueType = CustomFieldValueType.STRING;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * @return name of the attribute. Should never be empty.
	 */
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
	
	/**
	 * @return the default value for the defined Attribute. May be empty.
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
	
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	/**
	 * @return an expression that evaluates to the Attribute value for the defined Attribute. May be empty.
	 */
	public String getValueExpression() {
		return valueExpression;
	}
	
	public void setValueExpression(String valueExpression) {
		this.valueExpression = valueExpression;
	}
	
	/**
	 * @return a boolean to indicate whether the defined Attribute is required for the attribute owner object.
	 */
	public boolean isRequired() {
		return required;
	}
	
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * @return the class type that owns the defined Attribute type. May be null. If null, the attribute owner type 
	 * 	for the Attribute will be set the owner type at the time of attribute creation. 
	 */
	public CustomFieldOwnerType getCustomFieldOwnerType() {
		return customFieldOwnerType;
	}

	public void setCustomFieldOwnerType(CustomFieldOwnerType ownerType) {
		this.customFieldOwnerType = ownerType;
	}

	/**
	 * @return attribute value type as {@link CustomFieldValueType}. Should never be null.
	 */
	public CustomFieldValueType getCustomFieldValueType() {
		return customFieldValueType;
	}

	public void setCustomFieldValueType(CustomFieldValueType customFieldValueType) {
		this.customFieldValueType = customFieldValueType;
	}

	public String getMatcher() {
		return matcher;
	}

	public void setMatcher(String matcher) {
		this.matcher = matcher;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
}