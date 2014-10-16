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

package com.edifecs.servicemanager.test.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.edifecs.epp.isc.Address;
import com.edifecs.epp.isc.core.ServiceInformation;
import com.edifecs.epp.isc.core.ServiceStatus;
import com.edifecs.servicemanager.annotations.Service;
import com.edifecs.servicemanager.api.AbstractService;

/**
 * Service designed to supply status information updates.
 * 
 * @author abhising
 */

@Service(
    name = "Test Status Information",
    version = "1.0",
    description = "supply's status information updates"
)
public class TestStatusInformationService extends AbstractService {

    Map<String, ServiceStatus> map;
    String SERVICE_NAME = "Test Status Information";

    @Override public void start() throws Exception {
        getLogger().debug("{}: Service Successfully Started", this.getClass());

        map = new HashMap<String, ServiceStatus>();

        // Get Service Statuses
        for (Entry<Address, ServiceInformation> serviceMap :
             getCommandCommunicator().getAddressRegistry().getAllServiceInformation().entrySet()) {
            map.put(serviceMap.getKey().toString(), serviceMap.getValue().getServiceStatus());
        }

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override public void run() {
                for (Entry<Address, ServiceInformation> newServiceMap :
                     getCommandCommunicator().getAddressRegistry().getAllServiceInformation().entrySet()) {

                    if (map.containsKey(newServiceMap.getKey().toString())) {
                        if (!map.get(newServiceMap.getKey().toString()).equals(
                                newServiceMap.getValue().getServiceStatus())) {
                            map.put(newServiceMap.getKey().toString(),
                                    newServiceMap.getValue().getServiceStatus());
                            getLogger().debug("Service Status Updated for {}, STATUS : {}",
                                newServiceMap.getKey().toString(),
                                newServiceMap.getValue().getServiceStatus());
                        }
                    } else {
                        // getLogger().debug("Key not found : {} , adding the key to the service map. ",
                        //     newServiceMap.getKey().toString());
                        map.put(newServiceMap.getKey().toString(),
                                newServiceMap.getValue().getServiceStatus());
                    }
                }
            }
        }, 10, 60, TimeUnit.SECONDS);
    }

    @Override public void stop() throws Exception {
        getLogger().debug("{}: Service Successfully Stopped", this.getClass());
    }
}

