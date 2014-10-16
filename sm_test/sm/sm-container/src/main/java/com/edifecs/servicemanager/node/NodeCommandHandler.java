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

import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.isc.core.command.AbstractCommandHandler;
import com.edifecs.epp.isc.exception.ServiceException;
import com.edifecs.servicemanager.api.ServiceRef;
import com.edifecs.servicemanager.api.ServiceRegistry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.Properties;

public class NodeCommandHandler extends AbstractCommandHandler implements INodeCommandHandler {
    
    private static final int BUFFER = 1024;

    private final NodeService nodeService;

    public NodeCommandHandler(NodeService ns) {
        nodeService = ns;
    }

    public Boolean createService(
        String serviceName,
        String serviceTypeName,
        String serviceVersion,
        Properties serviceProperties,
        Map<String, Properties> resourceProperties
    ) throws ServiceException {
        return nodeService.createService(serviceName, serviceTypeName, serviceVersion,
                serviceProperties, resourceProperties);
    }

    public Boolean discardNode() throws Exception {
        nodeService.shutdownAsync();

        return true;
    }

    public String getServiceLog(String serviceName, String logFileName) throws Exception {
        return nodeService.getLog(serviceName, logFileName);
    }
    
    public String getNodeLog(String logFileName) throws Exception {
        
        File file = new File(SystemVariables.LOG_PATH + System.getProperty(SystemVariables.NODE_NAME_KEY) +
                File.separator + logFileName);
        
        StringBuffer fileData = new StringBuffer(BUFFER);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        char[] buf = new char[BUFFER];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[BUFFER];
        }
        reader.close();
        return fileData.toString();
    }

    public Boolean stopNode() throws Exception {
        return nodeService.stopAllServices();
    }

    public Boolean stopService(String serviceName) throws Exception {
        return nodeService.stopService(serviceName);
    }

    public Boolean unregisterService(String serviceName) throws Exception {
        // TODO: Make sure the service is stopped properly.
        ServiceRef registeredService = ServiceRegistry.getLocalService(serviceName);
        if (registeredService != null) {
            registeredService.stop();
            return true;
        } else {
            return false;
        }
    }
}
