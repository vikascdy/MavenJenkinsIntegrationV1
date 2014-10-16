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
 * Defines server properties for within a cluster.
 * 
 * @author willclem
 */
@XmlRootElement(name = "Server")
public class Server implements Serializable {
    private static final long serialVersionUID = 1L;

    private String            name;

    private String            description;

    private String            ipAddress;

    private String            hostName;

    private List<Node>        nodes;
    
    /**
     * Gets the name of the server.
     * 
     * @return Name
     */
    @XmlElement(name = "Name")
    public final String getName() {
        return name;
    }

    /**
     * Sets the name of the server.
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
     * Gets the IP address of the server.
     * 
     * @return IPAddress
     */
    @XmlElement(name = "IPAddress")
    public final String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets the IP address of the server.
     * 
     * @param newIpAddress IPAddress
     */
    public final void setIpAddress(final String newIpAddress) {
        ipAddress = newIpAddress;
    }

    /**
     * Gets the servers network name.
     * 
     * @return ServerName
     */
    @XmlElement(name = "HostName")
    public final String getHostName() {
        return hostName;
    }

    /**
     * Sets the servers network name.
     * 
     * @param newServerName ServerName
     */
    public final void setHostName(final String newServerName) {
        hostName = newServerName;
    }

    /**
     * Gets the list of configured nodes.
     * 
     * @return List of Nodes
     */
    @XmlElement(name = "Node")
    public final List<Node> getNodes() {
        if (nodes == null) {
            nodes = new ArrayList<Node>();
        }
        return nodes;
    }

    /**
     * Sets the list of configured nodes.
     * 
     * @param newNodes List of Nodes
     */
    public final void setNodes(final List<Node> newNodes) {
        nodes = newNodes;
    }
    
    /**
     * Returns the node with the given nodeName.
     * 
     * @param nodeName Name of the node to return
     * @return Node
     */
    public final Node getNodeByName(final String nodeName) {
        for (Node node : getNodes()) {
            if (node.getName().equals(nodeName)) {
                return node;
            }
        }
        return null;
    }

}
