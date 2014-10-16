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
package com.edifecs.epp.isc.communicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.isc.IAddressRegistry;
import com.edifecs.epp.isc.IServerServiceRegistry;
import com.edifecs.epp.isc.Address;
import com.edifecs.epp.isc.core.ServiceInformation;
import com.edifecs.epp.isc.exception.ServiceTypeNotFoundException;

/**
 * Maintains an up-to-date map of the cluster. It relates our internal
 * addresses with the Akka cluster registry. It is used to see what is
 * connected to the cluster.
 * 
 * @author willclem
 */
public class AddressRegistry implements IAddressRegistry {

    private final Map<Address, akka.actor.Address> registeredNodes = new ConcurrentHashMap<>();

    /**
     * Stores status and service information for all services within the
     * cluster.
     */
    private Map<Address, IServerServiceRegistry> clusterServiceRegistry = new ConcurrentHashMap<>();

    /**
     * Default protected constructor, this should only be done within the
     * ClusterConnection class.
     */
    public AddressRegistry() {
    }

    public final void registerNode(final Address nodeAddress, final akka.actor.Address address) {
        if (nodeAddress != null) {
            if (!isNodeRegistered(nodeAddress)) {
                registeredNodes.put(nodeAddress, address);
            }
        }
    }

    @Override
    public final void unregisterNode(final Address nodeAddress) {
        if (nodeAddress != null) {
            registeredNodes.remove(nodeAddress);
        }
    }

    public final void updateRegistry(final List<akka.actor.Address> viewMembers) {
        // TODO: Update to also set Service status

        // Unregister disconnected Nodes
        for (Address nodeAddress : registeredNodes.keySet()) {
            if (!viewMembers.contains(registeredNodes.get(nodeAddress))) {
                unregisterNode(nodeAddress);
            }
        }

        // Register new Nodes
        for (akka.actor.Address address : viewMembers) {
            Address id = Address.fromString(address.toString());
            if (!isNodeRegistered(id)) {
                registerNode(id, address);
            }
        }
    }

    @Override
    public boolean isNodeRegistered(final Address nodeAddress) {
        if (nodeAddress != null) {
            return registeredNodes.containsKey(nodeAddress);
        } else {
            return false;
        }
    }

    public akka.actor.Address getAddressForNode(final Address nodeAddress) {
        if (nodeAddress != null) {
            Address plainAddress = new Address(nodeAddress.getServerName(),
                    nodeAddress.getNodeName());
            return registeredNodes.get(plainAddress);
        } else {
            return null;
        }
    }

    @Override
    public Address getAddressForNodeName(final String nodeName) {
        for (Entry<Address, akka.actor.Address> address : registeredNodes.entrySet()) {
            Address key = address.getKey();
            if (key.getNodeName() != null && key.getNodeName().equals(nodeName)) {
                return key;
            }
        }
        return null;
    }

    @Override
    public Set<Address> getRegisteredAddresses() {
        return registeredNodes.keySet();
    }

    @Override
    public Address getAddressForServer(final String serverName) {
        for (Entry<Address, akka.actor.Address> address : registeredNodes.entrySet()) {
            if (address.getKey().getServerName().toUpperCase().equals(serverName.toUpperCase())) {
                Address newaddress = new Address(address.getKey().getServerName());
                return newaddress;
            }
        }

        return null;
    }
    
    @Override
    public List<Address> getConfiguredNodeAddressesForServer(final String serverName) {
        List<Address> addresses = new ArrayList<>();

        for (Entry<Address, akka.actor.Address> address : registeredNodes.entrySet()) {
            // Get configured nodes by the configured servername
            if (address.getKey().getServerName().toUpperCase().equals(serverName.toUpperCase())
                    && (address.getKey().getNodeName() != null) && !(address.getKey().getNodeName().equals(SystemVariables.CORE_NODE_NAME))) {
                addresses.add(address.getKey());
            }
        }

        return addresses;
    }

    @Override
    public List<Address> getNodeAddressesForServer(final String serverName) {
        List<Address> addresses = new ArrayList<>();

        for (Entry<Address, akka.actor.Address> address : registeredNodes.entrySet()) {
            // Get configured nodes by the configured servername
            if (address.getKey().getServerName().toUpperCase().equals(serverName.toUpperCase())
                    && (address.getKey().getNodeName() != null)) {
                addresses.add(address.getKey());
            }
        }

        return addresses;
    }

    @Override
    public List<Address> getRegisteredAgentAddresses() {
        List<Address> addresses = new ArrayList<>();

        for (Entry<Address, akka.actor.Address> address : registeredNodes.entrySet()) {
            if (address.getKey().isAgent()) {
                addresses.add(address.getKey());
            }
        }

        return addresses;
    }

    @Override
    public List<Address> getRegisteredNodeAddresses() {
        List<Address> addresses = new ArrayList<>();

        for (Entry<Address, akka.actor.Address> address : registeredNodes.entrySet()) {
            if (address.getKey().isNode()) {
                addresses.add(address.getKey());
            }
        }

        return addresses;
    }

    @Override
    public ServiceInformation getServiceInformation(final Address address, final String serviceName) {
        if (clusterServiceRegistry.get(address) != null) {
            return clusterServiceRegistry.get(address).getServiceInformation(serviceName);
        }
        return null;
    }

    @Override
    public ServiceInformation getServiceInformation(final Address address) {
        Address newAddress = new Address(address.getServerName(), address.getNodeName());

        if (clusterServiceRegistry.get(newAddress) != null) {
            return clusterServiceRegistry.get(newAddress).getServiceInformation(
                    address.getService());
        }
        return null;
    }

    @Override
    public Map<String, ServiceInformation> getAllServiceStatusForAddress(final Address address,
            final String serviceName) {
        if (clusterServiceRegistry.get(address) != null) {
            return clusterServiceRegistry.get(address).getServiceInformation();
        }

        return new HashMap<>();
    }

    @Override
    public IServerServiceRegistry getAllServiceStatusForAddress(final Address address) {
        return clusterServiceRegistry.get(address);
    }

    @Override
    public Map<Address, IServerServiceRegistry> getClusterServiceRegistry() {
        return clusterServiceRegistry;
    }

    @Override
    public Map<Address, ServiceInformation> getAllServiceInformation() {
        Map<Address, ServiceInformation> map = new HashMap<>();

        for (Address address : clusterServiceRegistry.keySet()) {
            for (String service : clusterServiceRegistry.get(address).getAllServices()) {

                Address newAddress = new Address(address.getServerName(), address.getNodeName(),
                        service);

                map.put(newAddress,
                        clusterServiceRegistry.get(address).getServiceInformation(service));
            }
        }
        return map;
    }

    @Override
    public Address getRegisteredServiceAddress(String serviceName) {
        for (Entry<Address, IServerServiceRegistry> address : clusterServiceRegistry.entrySet()) {
            if (address.getValue().getAllServices().contains(serviceName)) {
                return address.getKey().clone();
            }
        }
        return null;
    }

    @Override
    public List<Address> getAddressesForServiceTypeName(String serviceTypeName) {
        List<Address> addresses = new ArrayList<>();

        for (Entry<Address, IServerServiceRegistry> entry : clusterServiceRegistry.entrySet()) {
            Address address = entry.getKey();

            for (Entry<String, ServiceInformation> serviceInformationMap : entry.getValue()
                    .getServiceInformation().entrySet()) {
                String serviceName = serviceInformationMap.getKey();

                if (serviceInformationMap.getValue() != null
                        && serviceInformationMap.getValue().getServiceType() != null) {
                    if (serviceInformationMap.getValue().getServiceType().equals(serviceTypeName)) {
                        addresses.add(new Address(address.getServerName(), address.getNodeName(),
                                serviceName));
                    }
                }
            }
        }
        return addresses;
    }

    @Override
    public Address getAddressForServiceTypeName(String serviceTypeName) throws ServiceTypeNotFoundException {
        List<Address> addresses = getAddressesForServiceTypeName(serviceTypeName);

        if(addresses.size() > 0) {
            return addresses.get(0);
        } else {
            throw new ServiceTypeNotFoundException(serviceTypeName);
        }
    }

    @Override
    public void updateLocalServiceInformation(final Address address, final String serviceName,
            final ServiceInformation serviceInformation) {
        Address nodeAddress = new Address(address.getServerName(), address.getNodeName());
        if (clusterServiceRegistry.get(nodeAddress) != null) {
            clusterServiceRegistry.get(nodeAddress).addServiceInformation(serviceName,
                    serviceInformation);
        } else {
            ServerServiceRegistry statusGroup = new ServerServiceRegistry();
            statusGroup.addServiceInformation(serviceName, serviceInformation);
            clusterServiceRegistry.put(nodeAddress, statusGroup);
        }
    }

    @Override
    public void updateAllServiceInformation(final Address address,
            final Map<String, ServiceInformation> serviceInformationMap) {
        Address nodeAddress = new Address(address.getServerName(), address.getNodeName());
        if (serviceInformationMap != null) {
            if (clusterServiceRegistry.get(nodeAddress) != null) {
                clusterServiceRegistry.get(nodeAddress).addServiceInformation(serviceInformationMap);
            } else {
                ServerServiceRegistry statusGroup = new ServerServiceRegistry();
                statusGroup.addServiceInformation(serviceInformationMap);
                clusterServiceRegistry.put(nodeAddress, statusGroup);
            }
        }
    }
}
