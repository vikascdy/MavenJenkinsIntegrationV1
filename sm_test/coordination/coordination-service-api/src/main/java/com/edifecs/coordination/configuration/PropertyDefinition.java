// -----------------------------------------------------------------------------
//  Copyright (c) Edifecs Inc. All Rights Reserved.
//
// This software is the confidential and proprietary information of Edifecs Inc.
// ("Confidential Information").  You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the license
// agreement you entered into with Edifecs.
//
// EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
// SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
// NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
// LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
// ITS DERIVATIVES.
// -----------------------------------------------------------------------------
package com.edifecs.coordination.configuration;

import java.io.Serializable;
import java.util.List;


public class PropertyDefinition implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;

	private String defaultValue;
	
	private String description;

	private Boolean required;

	private Boolean editable;

	private PropertyType type;

	private String regEx;

	private String regExError;

	private List<String> selectOneValues;

	public enum PropertyType {
		STRING("STRING"), DOUBLE("DOUBLE"), LONG("LONG"), DATE("DATE"), BOOLEAN("BOOLEAN"), SELECTONE("SELECTONE");

		private String text;

		private PropertyType(final String newText) {
			text = newText;
		}

		public final String getText() {
			return text;
		}

		public static final PropertyType fromString(final String text) {
			if (text != null) {
				for (PropertyType b : PropertyType.values()) {
					if (text.equalsIgnoreCase(b.text)) {
						return b;
					}
				}
			}
			return null;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public Boolean getEditable() {
		return editable;
	}

	public void setEditable(Boolean editable) {
		this.editable = editable;
	}

	public PropertyType getType() {
		return type;
	}

	public void setType(PropertyType type) {
		this.type = type;
	}

	public String getRegEx() {
		return regEx;
	}

	public void setRegEx(String regEx) {
		this.regEx = regEx;
	}

	public String getRegExError() {
		return regExError;
	}

	public void setRegExError(String regExError) {
		this.regExError = regExError;
	}

	public List<String> getSelectOneValues() {
		return selectOneValues;
	}

	public void setSelectOneValues(List<String> selectOneValues) {
		this.selectOneValues = selectOneValues;
	}

}
