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

import java.util.Map;
import java.util.Properties;

import com.edifecs.epp.isc.annotations.*;
import com.edifecs.epp.isc.command.CommandSpecification;
import com.edifecs.epp.isc.exception.ServiceException;

@Akka(enabled = true)
@CommandHandler
@JsonSerialization(adapters = {
    @TypeAdapter(CommandSpecification.Adapter.class)
})
public interface INodeCommandHandler {
    
    @SyncCommand(name = "createService")
    @RequiresPermissions("platform:node:management:node:create")
    public Boolean createService(
        @Arg(name = "serviceName", required = true,
             description = "")
        String serviceName,

        @Arg(name = "serviceTypeName", required = true,
             description = "")
        String serviceTypeName,

        @Arg(name = "serviceVersion", required = true,
             description = "")
        String serviceVersion,

        @Arg(name = "serviceProperties", required = true,
             description = "")
        Properties serviceProperties,

        @Arg(name = "serviceProperties", required = true,
             description = "")
        Map<String, Properties> resourceProperties
    ) throws ServiceException;

    @SyncCommand(name = "discardNode")
    @RequiresPermissions("platform:node:management:node:discard")
    public Boolean discardNode() throws Exception;

    @SyncCommand(name = "getServiceLog")
    @RequiresPermissions("platform:node:reporting:service:view")
    public String getServiceLog(
        @Arg(name = "serviceName", required = true,
             description = "")
        String serviceName,

        @Arg(name = "logFileName", required = true,
             description = "")
        String logFileName
    ) throws Exception;
    
    @SyncCommand(name = "getNodeLog")
    @RequiresPermissions("platform:node:reporting:node:view")
    public String getNodeLog(
        @Arg(name = "logFileName", required = true,
             description = "")
        String logFileName
    ) throws Exception;

    @SyncCommand(name = "stopNode")
    @RequiresPermissions("platform:node:management:node:stop")
    public Boolean stopNode() throws Exception;

    @SyncCommand(name = "stopService")
    @RequiresPermissions("platform:node:management:service:stop")
    public Boolean stopService(
        @Arg(name = "serviceName", required = true,
             description = "")
        String serviceName
    ) throws Exception;
    
    @SyncCommand(name = "unregisterService")
    @RequiresPermissions("platform:node:management:service:discard")
    public Boolean unregisterService(
        @Arg(name = "serviceName", required = true,
             description = "")
        String serviceName
    ) throws Exception;
}
