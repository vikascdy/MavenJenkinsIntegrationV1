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
package com.edifecs.registry;

import java.util.List;
import java.util.Map;

import com.edifecs.core.configuration.configuration.Service;
import com.edifecs.epp.isc.Address;

/**
 * A Service Registry is key too a healthy cluster. There are countless examples
 * of people being successful or failing on something as simple as how they
 * handle their cluster typography. The key is to have a solid and as accurate
 * as possible overview of your entire cluster.
 * 
 * @author willclem
 * 
 */
public interface IServiceRegistry {

	/**
	 * Registers a new instance of a service into the cluster. This should be
	 * used when a new instance joins the cluster.
	 * 
	 * @param serviceAddress
	 *            Address of the service to uniquely identify the instance in
	 *            the cluster
	 * @param service
	 *            Information about the service
	 * @param status
	 *            current status of the service
	 */
	void registerService(Address serviceAddress, Service service, Status status);

	/**
	 * Given a service address, remove the service from the cluster. This is
	 * used when a service has been abandoned and is being removed completely
	 * from the cluster.
	 * 
	 * @param serviceAddress
	 *            Address of the service to uniquely identify the instance in
	 *            the cluster
	 */
	void unregisterService(Address serviceAddress);

	/**
	 * Checks the address of the service against the registry and returns true
	 * if that service is currently registered or not.
	 * 
	 * @param serviceAddress
	 *            Address of the service to uniquely identify the instance in
	 *            the cluster
	 * @return returns true if the service is registered, false if not.
	 */
	boolean isServiceRegistered(Address serviceAddress);

	/**
	 * Given a service address, updates the status of that service to the value
	 * defined.
	 * 
	 * @param serviceAddress
	 * @param status
	 */
	void updateServiceStatus(Address serviceAddress, Status status);

	/**
	 * Given a map of addresses and statuses, update the status of those
	 * services in the cluster.
	 * 
	 * @param servicesStatus
	 */
	void updateServiceStatus(Map<Address, Status> servicesStatus);

	/**
	 * Gets the list of all registered addresses within the cluster.
	 * 
	 * @return
	 */
	List<Address> getRegisteredAddresses();

	/**
	 * Gets Service information about the service given the address of that
	 * service.
	 * 
	 * @param serviceAddress
	 * @return
	 */
	Service getService(Address serviceAddress);

	/**
	 * Gets the status of the service for the address given.
	 * 
	 * @param serviceAddress
	 * @return
	 */
	Status getServiceStatus(Address serviceAddress);

	/**
	 * Gets all services within the cluster with the address of the cluster.
	 * 
	 * @return
	 */
	Map<Address, Service> getAllService();

	/**
	 * Gets a list of addresses for all services given a service name. This is
	 * used to help with status information and can be used for an application
	 * to help with load balancing.
	 * 
	 * @param serviceTypeName
	 * @return
	 */
	List<Address> getAddressesForServiceType(String serviceTypeName);

	/**
	 * Returns a single address for a service to call based on the service type
	 * given. The value returned is based on the service types configured load
	 * balancing behavior if configured.
	 * 
	 * @param serviceTypeName
	 * @return
	 */
	Address getAddressForServiceTypeName(String serviceTypeName);

}
