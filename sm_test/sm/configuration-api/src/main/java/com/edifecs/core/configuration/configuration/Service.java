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
 * Service definition containing all properties and resource dependencies.
 * 
 * @author willclem
 */
@XmlRootElement(name = "Service")
public class Service implements Serializable {
    private static final long       serialVersionUID = 1L;

    private String                  name;

    private String                  description;

    private String                  serviceType;

    private String                  version;

    private List<Property>          properties;

    private List<ResourceReference> resources;

    private List<ServiceReference>  services;



    /**
     * Gets the name of the service.
     * 
     * @return Name
     */
    @XmlElement(name = "Name")
    public final String getName() {
        return name;
    }

    /**
     * Sets the name of the service.
     * 
     * @param newName Name
     */
    public final void setName(final String newName) {
        name = newName;
    }

    /**
     * Gets the description of the server.
     * 
     * @return Description
     */
    @XmlElement(name = "Description")
    public final String getDescription() {
        return description;
    }

    /**
     * Sets the description of the server.
     * 
     * @param newDescription Description
     */
    public final void setDescription(final String newDescription) {
        this.description = newDescription;
    }

    /**
     * Gets the version of the service.
     * 
     * @return Version
     */
    @XmlElement(name = "Version")
    public final String getVersion() {
        return version;
    }

    /**
     * Sets the version of the service.
     * 
     * @param newVersion Version
     */
    public final void setVersion(final String newVersion) {
        version = newVersion;
    }

    /**
     * Gets the service type name. This is used to reference the data from
     * metadata configuration.
     * 
     * @return ServiceType
     */
    @XmlElement(name = "ServiceType")
    public final String getServiceType() {
        return serviceType;
    }

    /**
     * Sets the service type name. This is used to reference the data from
     * metadata configuration.
     * 
     * @param newServiceType ServiceType
     */
    public final void setServiceType(final String newServiceType) {
        serviceType = newServiceType;
    }

    /**
     * Gets the list of Properties for the Service.
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
     * Sets the list of Properties for a Service.
     * 
     * @param newProperties List of Properties
     */
    public final void setProperties(final List<Property> newProperties) {
        properties = newProperties;
    }

    /**
     * Gets the list of configured resources for the service.
     * 
     * @return List of Resources
     */
    @XmlElement(name = "Resource")
    public final List<ResourceReference> getResources() {
        if (resources == null) {
            resources = new ArrayList<ResourceReference>();
        }
        return resources;
    }

    /**
     * Sets the list of Properties for a Service.
     * 
     * @param newResources List of Resources
     */
    public final void setResources(final List<ResourceReference> newResources) {
        resources = newResources;
    }

    /**
     * Gets a list of dependent services.
     * 
     * @return List of Services
     */
    @XmlElement(name = "Service")
    public final List<ServiceReference> getServices() {
        if (services == null) {
            services = new ArrayList<ServiceReference>();
        }
        return services;
    }

    /**
     * Sets a list of dependent services.
     * 
     * @param newServices List of Services
     */
    public final void setServices(final List<ServiceReference> newServices) {
        services = newServices;
    }

}
