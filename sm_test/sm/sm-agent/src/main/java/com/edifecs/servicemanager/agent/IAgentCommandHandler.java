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
package com.edifecs.servicemanager.agent;

import java.util.ArrayList;

import com.edifecs.agent.exception.CommandException;
import com.edifecs.agent.message.PID;
import com.edifecs.agent.message.ServerDetails;
import com.edifecs.epp.isc.annotations.*;
import com.edifecs.epp.isc.core.LogFile;
import com.edifecs.epp.isc.annotations.Akka;

@Akka(enabled = true)
@CommandHandler
public interface IAgentCommandHandler {

	@Command(name = "getNodeCpu")
	@RequiresPermissions("platform:agent:reporting:node:view")
	public double[] getNodeCpu(@Arg(name = "nodeName", required = true,
			description = "") String nodeName) throws CommandException;

	@Command(name = "getNodeMem")
	@RequiresPermissions("platform:agent:reporting:node:view")
	public double[] getNodeMem(@Arg(name = "nodeName", required = true,
			description = "") String nodeName) throws CommandException;

	@Command(name = "getServerCpu")
	@RequiresPermissions("platform:agent:reporting:agent:view")
	public double[] getServerCpu();
	
	@Command(name = "getServerDetails")
	@RequiresPermissions("platform:agent:reporting:agent:view")
	public ServerDetails getServerDetails() throws CommandException;

	@Command(name = "getServerMem")
	@RequiresPermissions("platform:agent:reporting:agent:view")
	public double[] getServerMem();

	@Command(name = "stopServer")
	@RequiresPermissions("platform:agent:management:agent:stop")
	public Boolean stopServer() throws Exception;

	@Command(name = "getLogs")
	@RequiresPermissions("platform:agent:reporting:log:list")
	public ArrayList<LogFile> getLogs() throws Exception;
	
	@Command(name = "getAgentLog")
	@RequiresPermissions("platform:agent:reporting:log:view")
	public String getAgentLog(@Arg(name = "logFileName", required = true,
			description = "") String logFileName) throws Exception;

	@Command(name = "getAgentPID")
	@RequiresPermissions("platform:agent:reporting:agent:view")
	public PID getAgentPID();

	@Command(name = "getPIDForNode")
	@RequiresPermissions("platform:agent:reporting:node:view")
	public PID getPIDForNode(@Arg(name = "nodeName", required = true,
			description = "PID of nodeName") String nodeName);
}
