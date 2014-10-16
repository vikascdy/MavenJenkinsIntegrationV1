package com.edifecs.servicemanager.dashboard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Filter implements Serializable {

	// TODO : verify if needs to be persisted

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String fieldName;

	private String type;

	private List<String> values = new ArrayList<String>();

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public Filter() {
		super();
	}

}
