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
import java.util.Map;
import java.util.Set;

import com.edifecs.epp.isc.core.ServiceInformation;

public interface IServerServiceRegistry extends Serializable {

    /**
     * @return Map<String, ServiceInformation> for the provided service name.
     */
    Map<String, ServiceInformation> getServiceInformation();

    /**
     * @param serviceName
     *            String representing the name of the service for which the
     *            ServiceStatus need to be requested.
     * @return ServiceStatus for the provided service name.
     */
    ServiceInformation getServiceInformation(final String serviceName);

    /**
     * @return Set<String> Set of all services for this ServiceStatusGroup.
     */
    Set<String> getAllServices();

    /**
     * Adds the ServiceStatus to this ServiceStatusGroup.
     * 
     * @param serviceName
     *            String
     * @param serviceInformation
     *            ServiceInformation
     */
    void addServiceInformation(final String serviceName, final ServiceInformation serviceInformation);

    /**
     * Adds all the service statuses to this ServiceStatusGroup.
     * 
     * @param serviceInformation
     *            Map<String, ServiceStatus> Map of all the service names and
     *            their related ServiceStatus.
     */
    void addServiceInformation(final Map<String, ServiceInformation> serviceInformation);

}
