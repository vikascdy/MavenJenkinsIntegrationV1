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
package com.edifecs.epp.security.data;

import java.io.Serializable;

/**
 * Attribute name and value pairs.
 * 
 * @author willclem
 */
public class CustomField implements Serializable {
	private static final long serialVersionUID = -9220229033721068987L;

	private Long id;
	private Long ownerId;
	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	private CustomFieldType type;
    private String value;
	private CustomFieldOwnerType customFieldOwnerType;
 
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
    /**
     * @return the value of the property. May be null.
     */
    public final String getValue() {
        return value;
    }

    /**
     * Sets the value of the property.
     * 
     * @param newValue Value
     */
    public final void setValue(final String newValue) {
        value = newValue;
    }

    /**
     * @return the type of attribute. Should never be null.
     */
	public CustomFieldType getType() {
		return type;
	}

	public void setType(CustomFieldType type) {
		this.type = type;
	}

	/**
	 * @return the type of entity that owns this attribute. Should never be null.
	 */
	public CustomFieldOwnerType getCustomFieldOwnerType() {
		return customFieldOwnerType;
	}

	public void setCustomFieldOwnerType(CustomFieldOwnerType customFieldOwnerType) {
		this.customFieldOwnerType = customFieldOwnerType;
	}
}
