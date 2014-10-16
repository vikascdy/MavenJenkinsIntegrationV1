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
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * List of JVM Properties that are used during the creation of a Node's JVM.
 * 
 * @author willclem
 */
@XmlRootElement(name = "JVMOptions")
public class JVMOptions implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Property>    properties;

    /**
     * Gets a list of JVM Options in the form of Properties.
     * 
     * @return List of Properties
     */
    @XmlElement(name = "Property")
    public final List<Property> getProperties() {
        if (properties == null) {
            properties = new ArrayList<Property>();
        }
        return properties;
    }

    /**
     * Sets a list of JVM Options in the form of Properties.
     * 
     * @param newProperties List of Properties
     */
    public void setProperties(final List<Property> newProperties) {
        this.properties = newProperties;
    }

}
