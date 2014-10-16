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
package com.edifecs.epp.isc;

import java.io.Serializable;

/**
 * Address represents the location to any cluster, server, node, or service
 * within the application. With it you are able to identify any level of the
 * system.
 * 
 * @author willclem
 * @author josefern
 * @authro c-adamnels
 */
public class Address implements Serializable, Cloneable {
    private static final long serialVersionUID = 6_05_14;

    public static final String MESSAGE_PATH_SEPARATOR = ":";
    
    private final String serverName;

    private final String nodeName;

    private final String service;

    /**
     * Creates an Address of the form `serverName`.
     * 
     * @param serverName The first part of the address.
     */
    public Address(final String serverName) {
        this(serverName, null, null);
    }

    /**
     * Creates an Address of the form `serverName:nodeName`.
     * 
     * @param serverName The first part of the address.
     * @param nodeName The second part of the address. May be null.
     */
    public Address(final String serverName, final String nodeName) {
        this(serverName, nodeName, null);
    }

    /**
     * Creates an Address of the form `serverName:nodeName:serviceName`.
     *
     * @param serverName The first part of the address.
     * @param nodeName The second part of the address. May be null.
     * @param serviceName The third part of the address. May be null.
     */
    public Address(final String serverName, final String nodeName, final String serviceName) {
        this.serverName = serverName;
        this.nodeName = nodeName;
        this.service = serviceName;
    }

    /** Returns the server name (the first part of the address). */
    public final String getServerName() {
        return serverName;
    }

    /** Returns the node name (the second part of the address). May be null. */
    public final String getNodeName() {
        return nodeName;
    }

    /** Returns the service name (the third part of the address). May be null. */
    public final String getService() {
        return service;
    }

    /** Returns true if the address points to an Agent. */
    public final boolean isAgent() {
        return (serverName != null) && (nodeName == null) && !serverName.startsWith("_")
                && !serverName.endsWith("_");
    }

    /** Returns true if this address points to a Node. */
    public final boolean isNode() {
        return (serverName != null) && (nodeName != null) && (service == null);
    }

    /** Returns true if this address points to a service. */
    public boolean isService() {
        return (serverName != null) && (nodeName != null) && (service != null);
    }

    /** Returns true if the address points to a resource or a third-party service. */
    public boolean isResource() {
        return (serverName != null) && (nodeName == null) && serverName.startsWith("_")
                && serverName.endsWith("_");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address a = (Address)o;
        return serverName.equalsIgnoreCase(a.serverName) &&
            (nodeName == null ? a.nodeName == null : nodeName.equalsIgnoreCase(a.nodeName)) &&
            (service == null  ? a.service == null  : service.equalsIgnoreCase(a.service));
    }

    @Override
    public int hashCode() {
        int result = serverName.toUpperCase().hashCode();
        result = 31 * result + (nodeName != null ? nodeName.toUpperCase().hashCode() : 0);
        result = 31 * result + (service != null ? service.toUpperCase().hashCode() : 0);
        return result;
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder().append(serverName);
        if (nodeName != null) {
           sb.append(MESSAGE_PATH_SEPARATOR).append(nodeName);
            if (service != null) {
                sb.append(MESSAGE_PATH_SEPARATOR).append(service);
            }
        }
        return sb.toString();
    }

    @Override
    public Address clone() {
        return new Address(serverName, nodeName, service);
    }

    public boolean isLocalAddress(Address address) {
        if (address.isAgent()) {
            if (!address.getServerName().equals(getServerName())) {
                return false;
            }
        } else if (address.isNode()) {
            if (!address.getServerName().equals(getServerName())
                    || !address.getNodeName().equals(getNodeName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Constructs a new Address from a string, which must match one of the
     * following formats:
     *
     * - `serverName`
     * - `serverName:nodeName`
     * - `serverName:nodeName:serviceName`
     *
     * @param str The string to parse into an Address.
     * @return A new Address, based on `str`.
     */
    public static Address fromString(final String str) {
        String[] names = str.split(MESSAGE_PATH_SEPARATOR);

        String server = null;
        String node = null;
        String service = null;

        if (names.length > 0) server  = names[0];
        if (names.length > 1) node    = names[1];
        if (names.length > 2) service = names[2];

        return new Address(server, node, service);
    }
}
