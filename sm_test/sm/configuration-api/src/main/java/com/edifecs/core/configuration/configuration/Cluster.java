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
 * Cluster definition within a configuration. Currently the system only supports
 * one cluster within a configuration at a time.
 * 
 * @author willclem
 */
@XmlRootElement(name = "Cluster")
public class Cluster implements Serializable {
    private static final long serialVersionUID = 1L;

    private String            name;

    private String            description;

    private String            environmentType;

    private List<Server>      servers;

    private List<Resource>    resources;

    /**
     * Gets the name of the cluster. This is used along with the EnvironmentType
     * to create or join the cluster.
     * 
     * @return Name
     */
    @XmlElement(name = "Name")
    public final String getName() {
        return name;
    }

    /**
     * Sets the name of the cluster. This is used along with the EnvironmentType
     * to create or join the cluster.
     * 
     * @param newName Name
     */
    public final void setName(final String newName) {
        name = newName;
    }

    /**
     * Gets the description of the cluster.
     * 
     * @return Description
     */
    @XmlElement(name = "Description")
    public final String getDescription() {
        return description;
    }

    /**
     * Sets the description of the cluster.
     * 
     * @param newDescription Description
     */
    public final void setDescription(final String newDescription) {
        this.description = newDescription;
    }

    /**
     * Gets the environment type. This is used along with the Name to create or
     * join the cluster.
     * 
     * @return EnvironmentType
     */
    @XmlElement(name = "EnvironmentType")
    public final String getEnvironmentType() {
        return environmentType;
    }

    /**
     * Sets the environment type. This is used along with the Name to create or
     * join the cluster.
     * 
     * @param newEnvironmentType EnvironmentType
     */
    public final void setEnvironmentType(final String newEnvironmentType) {
        environmentType = newEnvironmentType;
    }

    /**
     * Gets the list of configured servers within the cluster.
     * 
     * @return List of Servers
     */
    @XmlElement(name = "Server")
    public final List<Server> getServers() {
        if (servers == null) {
            servers = new ArrayList<Server>();
        }
        return servers;
    }

    /**
     * Sets the list of configured servers within the cluster.
     * 
     * @param newServers List of Servers
     */
    public final void setServers(final List<Server> newServers) {
        servers = newServers;
    }

    /**
     * Gets the list of configured resources within the cluster.
     * 
     * @return List of Resources
     */
    @XmlElement(name = "Resource")
    public final List<Resource> getResources() {
        if (resources == null) {
            resources = new ArrayList<Resource>();
        }
        return resources;
    }

    /**
     * Sets the list of configured resources within the cluster.
     * 
     * @param newResources List of Resources
     */
    public final void setResources(final List<Resource> newResources) {
        resources = newResources;
    }
}
