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

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.edifecs.epp.isc.core.ServiceInformation;
import com.edifecs.epp.isc.exception.ServiceTypeNotFoundException;

public interface IAddressRegistry {

    /**
     * Unregisters a node from the ClusterNodeRegistry.
     * 
     * @param nodeAddress
     *            Internal Node address
     */
    void unregisterNode(Address nodeAddress);

    Address getAddressForNodeName(String nodeName);

    boolean isNodeRegistered(Address nodeAddress);

    List<Address> getRegisteredAgentAddresses();

    List<Address> getNodeAddressesForServer(String serverName);

    Address getAddressForServer(String serverName);

    Set<Address> getRegisteredAddresses();

    List<Address> getRegisteredNodeAddresses();

    ServiceInformation getServiceInformation(Address address, String serviceName);

    ServiceInformation getServiceInformation(Address address);

    Map<String, ServiceInformation> getAllServiceStatusForAddress(Address address, String serviceName);

    IServerServiceRegistry getAllServiceStatusForAddress(Address address);

    Map<Address, IServerServiceRegistry> getClusterServiceRegistry();

    Map<Address, ServiceInformation> getAllServiceInformation();

    Address getRegisteredServiceAddress(String serviceName);

    /**
     * Given the name of the service type, returns a list of all service
     * addresses with that Service Type.
     * 
     * @return
     */
    List<Address> getAddressesForServiceTypeName(String serviceTypeName);

    Address getAddressForServiceTypeName(String serviceTypeName) throws ServiceTypeNotFoundException;
    
    void updateLocalServiceInformation(Address address, String serviceName,
            ServiceInformation serviceInformation);

    void updateAllServiceInformation(Address address,
            Map<String, ServiceInformation> serviceInformationMap);

    List<Address> getConfiguredNodeAddressesForServer(String serverName);

}
