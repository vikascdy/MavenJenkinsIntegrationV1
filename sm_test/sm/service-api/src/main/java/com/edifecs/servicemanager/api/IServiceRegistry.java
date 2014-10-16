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
package com.edifecs.servicemanager.api;

import com.edifecs.epp.isc.core.LogFile;
import com.edifecs.epp.isc.core.ServiceInformation;
import com.edifecs.epp.isc.core.ServiceStatus;
import com.edifecs.epp.isc.exception.ServiceException;
import com.edifecs.epp.isc.exception.CommandHandlerRegistrationException;

import java.io.IOException;
import java.util.List;

public interface IServiceRegistry {

    void registerLocalService(ServiceRef service) throws CommandHandlerRegistrationException;

    boolean unregisterLocalService(String serviceName);

    boolean isServiceLocal(String serviceName);

    List<String> getLocalServiceNames();

    ServiceRef getLocalService(String serviceName);

    void updateServiceInformation(String serviceName, ServiceInformation serviceInformation) throws Exception;

    ServiceStatus getLocalServiceStatus(String serviceName);

    List<LogFile> getLocalServiceLogFiles(String serviceName) throws ServiceException;

    String getLocalServiceLogFile(String serviceName, String logFileName) throws IOException, ServiceException;

    void updateServiceStatus(String serviceName, ServiceStatus status) throws Exception;

    void updateServiceStatus(String serviceName, ServiceStatus status, String message) throws Exception;

    ServiceInformation getServiceInformation(String serviceName) throws ServiceException;

}
