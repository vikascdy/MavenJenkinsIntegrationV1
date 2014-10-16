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
 * Node Level of the Configuration. A Node is the definition of an OSGI
 * container running within a separate JVM process.
 * 
 * @author willclem
 */
@XmlRootElement(name = "Node")
public class Node implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    private JVMOptions jvmOptions;

    private List<Service> services;

    /**
     * Gets the Nodes Name. This value should be unique within the cluster
     * configuration.
     * 
     * @return Nodes Name
     */
    @XmlElement(name = "Name")
    public final String getName() {
        return name;
    }

    /**
     * Sets the Nodes Name. This value should be unique within the cluster
     * configuration.
     * 
     * @param newName
     *            Nodes Name
     */
    public final void setName(final String newName) {
        name = newName;
    }

    /**
     * Gets the description of the node.
     * 
     * @return Description
     */
    @XmlElement(name = "Description")
    public final String getDescription() {
        return description;
    }

    /**
     * Sets the description of the node.
     * 
     * @param newDescription
     *            Description
     */
    public final void setDescription(final String newDescription) {
        this.description = newDescription;
    }

    /**
     * Gets the Set of JVM options to use when launching the nodes separate JVM
     * process.
     * 
     * @return JVMOptions
     */
    @Deprecated
    @XmlElement(name = "JVMOptions")
    public final JVMOptions getJvmOptions() {
        return jvmOptions;
    }

    /**
     * Sets the Set of JVM options to use when launching the nodes separate JVM
     * process.
     * 
     * @param newJvmOptions
     *            JVMOptions
     */
    @Deprecated
    public final void setJvmOptions(final JVMOptions newJvmOptions) {
        jvmOptions = newJvmOptions;
    }

    /**
     * Gets the list of Services to run within this node.
     * 
     * @return List of Services
     */
    @XmlElement(name = "Service")
    public final List<Service> getServices() {
        if (services == null) {
            services = new ArrayList<Service>();
        }
        return services;
    }

    /**
     * Sets the list of Services to run within this node.
     * 
     * @param newServices
     *            List of Services
     */
    public final void setServices(final List<Service> newServices) {
        this.services = newServices;
    }

    /**
     * Returns the Service within this node with the supplied ServiceName.
     * 
     * @param serviceName
     *            Name of the service to return within this Node
     * @return Returns the Service with the supplied serviceName. Returns null
     *         if no Service is found.
     */
    public final Service getServiceByName(final String serviceName) {
        for (Service service : getServices()) {
            if (service.getName().equals(serviceName)) {
                return service;
            }
        }
        return null;
    }
}
