package com.edifecs.epp.security.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

@Deprecated
@Entity(name = "Custom_Property")
public class CustomPropertyEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Custom_Property_Id", unique = true, nullable = false)
	private Long propertyId;;

	@Column(name = "Custom_Property_Name")
	private String name;

	@Type(type = "text")
	@Column(name = "Custom_Property_Description")
	private String description;

	@Column(name = "Custom_Property_DefaultVal")
	private String defaultVal;

	@Column(name = "Custom_Property_Value")
	private String value;

	@Column(name = "Custom_Property_Required")
	private Boolean required;

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public Boolean getRequired() {
        return required;
    }

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

	public boolean isRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

}
