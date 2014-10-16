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
package com.edifecs.core.configuration.configuration;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Property name and value pairs.
 * 
 * @author willclem
 */
@XmlRootElement(name = "Property")
public class Property implements Serializable {
    private static final long serialVersionUID = 1L;

    private String            name;

    private String            value;

    /**
     * Gets the property name. The value of name corresponds to a property name
     * within the metadata configuration files that define what the property is.
     * 
     * @return Name
     */
    @XmlElement(name = "Name")
    public final String getName() {
        return name;
    }

    /**
     * Sets the property name.
     * 
     * @param newName Name
     */
    public final void setName(final String newName) {
        name = newName;
    }

    /**
     * Gets the value of the property.
     * 
     * @return Value
     */
    @XmlElement(name = "Value")
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

}
