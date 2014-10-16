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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.edifecs.epp.isc.IServerServiceRegistry;
import com.edifecs.epp.isc.core.ServiceInformation;

/**
 * ServiceStatusGroup holds the status of all the services running or installed
 * on any node in the cluster.
 * 
 * @author willclem
 */
public class ServerServiceRegistry implements IServerServiceRegistry {
    private static final long serialVersionUID = 1L;

    private Map<String, ServiceInformation> statusMap = new ConcurrentHashMap<>();

    @Override
    public final Map<String, ServiceInformation> getServiceInformation() {
        return statusMap;
    }

    @Override
    public final ServiceInformation getServiceInformation(final String serviceName) {
        return statusMap.get(serviceName);
    }

    @Override
    public final Set<String> getAllServices() {
        return statusMap.keySet();
    }

    @Override
    public final void addServiceInformation(final String serviceName,
            final ServiceInformation serviceInformation) {
        statusMap.put(serviceName, serviceInformation);
    }

    @Override
    public final void addServiceInformation(final Map<String, ServiceInformation> serviceInformation) {
        statusMap.putAll(serviceInformation);
    }

}
