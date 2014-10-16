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
 * A Resource is a set of properties that define a common third party interface.
 * 
 * @author willclem
 */
@XmlRootElement(name = "Resource")
public class Resource implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The unique name of the Resource.
     */
    private String            name;

    /**
     * Maps to the name of ResourceType
     */
    private String            type;
    
    private String            description;

    private List<Property>    properties;

    /**
     * Set of possible resource types available to be used. Each resource type
     * is a specific set of properties which are defined within the metadata
     * configurations.
     * 
     * @author willclem
     */
    public enum ResourceType {
        /**
         * TODO: Put in list of required Properties.
         */
        Database("Database"),
        /**
         * TODO: Put in list of required Properties.
         */
        JMSQueue("JMSQueue"),
        /**
         * TODO: Put in list of required Properties.
         */
        EmailServer("EmailServer"),
        /**
         * TODO: Put in list of required Properties.
         */
        ApplicationServer("ApplicationServer"),
        /**
         * TODO: Put in list of required Properties.
         */
        SharedFolder("SharedFolder"),
        /**
         * TODO: Put in list of required Properties.
         */
        Artifact("Artifact");

        private String text;

        private ResourceType(final String newText) {
            this.text = newText;
        }

        /**
         * Gets a String representation of the ResourceType enum.
         * 
         * @return String
         */
        public final String getText() {
            return text;
        }

        /**
         * Returns the ResourceType enum object from a given String.
         * 
         * @param text String representation of the enum value
         * @return ResourceType
         */
        public static final ResourceType fromString(final String text) {
            if (text != null) {
                for (ResourceType b : ResourceType.values()) {
                    if (text.equalsIgnoreCase(b.text)) {
                        return b;
                    }
                }
            }
            return null;
        }

    }

    /**
     * Gets the name of the resource. This is a user defined value.
     * 
     * @return Name
     */
    @XmlElement(name = "Name")
    public final String getName() {
        return name;
    }

    /**
     * Sets the name of the resource.
     * 
     * @param newName Name
     */
    public final void setName(final String newName) {
        name = newName;
    }

    /**
     * Gets the description of the resource.
     * 
     * @return Description
     */
    @XmlElement(name = "Description")
    public final String getDescription() {
        return description;
    }

    /**
     * Sets the description of the resource.
     * 
     * @param newDescription Description
     */
    public final void setDescription(final String newDescription) {
        this.description = newDescription;
    }

    /**
     * Gets the resource type.
     * 
     * @return ResourceType
     */
    @XmlElement(name = "Type")
    public final String getType() {
        return type;
    }

    /**
     * Sets the resource type.
     * 
     * @param newResourceType ResourceType
     */
    public final void setType(final String newResourceType) {
        type = newResourceType;
    }

    /**
     * Gets the list of properties for the resource.
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
     * Sets the list of properties for the resource.
     * 
     * @param newProperties List of Properties
     */
    public final void setProperties(final List<Property> newProperties) {
        properties = newProperties;
    }

}
