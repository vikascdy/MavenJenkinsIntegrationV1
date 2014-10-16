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
package com.edifecs.servicemanager.node;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.edifecs.core.configuration.configuration.Service;
import com.edifecs.epp.isc.core.LogFile;
import com.edifecs.epp.isc.core.ServiceInformation;
import com.edifecs.epp.isc.exception.ServiceException;
import com.edifecs.servicemanager.api.IServiceRegistry;

/**
 * Interface that defines all externally available methods within
 * ServiceManager.
 * 
 * @author willclem
 */
public interface IServiceManager {

    /**
     * Output a complete list of all running OSGI bundles and services to the
     * logger.
     * 
     * @throws NodeServiceException Thrown if there is an issue outputting
     *             the OSGI information.
     */
    void logOSGIServiceDump() throws NodeServiceException;

    /**
     * Stops ServiceManager. This will shutdown all services, unregister all
     * bundles and stop.
     * 
     * @return
     */
    Boolean shutdown();

    /**
     * Gets the Service Registry reference from Service Manager.
     * 
     * @return ServiceRegistry
     */
    IServiceRegistry getServiceRegistry();

    Boolean createService(String serviceName, String serviceType, String version, Properties properties, Map<String, Properties> resources) throws ServiceException;

    void installServiceType(String serviceTypeName, String serviceTypeVersion) throws ServiceException;

    /**
     * Starts the service supplied within ServiceManager.
     * 
     * @param serviceName Service name
     * @throws Exception Thrown if there is a problem launching the service
     */
    Boolean startService(String serviceName) throws ServiceException;

    void installCreateStartServices(List<Service> services) throws Exception;

    Boolean stopService(String serviceName) throws ServiceException;

    Boolean restartService(String serviceName) throws ServiceException;

    Boolean unregisterService(String serviceName) throws ServiceException;

    Boolean startAllServices() throws ServiceException;

    Boolean stopAllServices() throws ServiceException;

    void sendServiceStatus(String serviceName, ServiceInformation serviceInformation) throws Exception;

    void stopNode(String nodeName) throws Exception;

    void startNode(String nodeName) throws Exception;

    String getLog(String serviceName, String logFileName) throws ServiceException;

    List<LogFile> getLogs(String serviceName) throws ServiceException;

    void installCreateService(Service service) throws ServiceException;

    void startService(Service service) throws ServiceException;

    void startService(String serviceName, String serviceTypeName, String version, Properties properties) throws ServiceException;

    void installCreateService(String serviceName, String serviceTypeName, String version,
            Properties properties, Map<String, Properties> resources) throws ServiceException;

}
