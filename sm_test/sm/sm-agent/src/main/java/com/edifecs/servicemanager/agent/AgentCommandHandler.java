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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edifecs.agent.exception.CommandException;
import com.edifecs.agent.message.PID;
import com.edifecs.agent.message.ServerDetails;
import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.core.memtracer.MemoryTracer;
import com.edifecs.core.memtracer.NodeNotFoundException;
import com.edifecs.core.memtracer.ProcessDetector;
import com.edifecs.epp.isc.Address;
import com.edifecs.epp.isc.exception.MessageException;
import com.edifecs.epp.isc.MessageResponse;
import com.edifecs.epp.isc.core.LogFile;
import com.edifecs.epp.isc.core.command.AbstractCommandHandler;

public class AgentCommandHandler extends AbstractCommandHandler implements IAgentCommandHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final int BUFFER = 1024;
	
	private AgentService agentService;
	
	public AgentCommandHandler(AgentService agentService) {
	    this.agentService = agentService;
	}

	public double[] getNodeCpu(String nodeName) throws CommandException {

        try {
            MemoryTracer tracer = agentService.getTracerForNode(nodeName);
            return tracer.getCpuTraces();
        } catch (NodeNotFoundException ex) {
            throw new CommandException(ex.getMessage());
        } catch (IllegalStateException ex) {
            throw new CommandException(ex.getMessage());
        }
	}

	public double[] getNodeMem(String nodeName) throws CommandException {

        try {
            MemoryTracer tracer = agentService.getTracerForNode(nodeName);
            return tracer.getMemTraces();
        } catch (NodeNotFoundException ex) {
            throw new CommandException(ex.getMessage());
        } catch (IllegalStateException ex) {
            throw new CommandException(ex.getMessage());
        }
	}

	public double[] getServerCpu() {
		return agentService.getTracer().getCpuTraces();
	}

	public ServerDetails getServerDetails() throws CommandException {
		try {
			ServerDetails serverDetails = new ServerDetails(InetAddress
					.getLocalHost().getHostName(), InetAddress.getLocalHost()
					.getHostAddress(), System.getProperty("os.name"),
					System.getProperty("os.arch"), Runtime.getRuntime()
							.availableProcessors(), null, (int) (Runtime
							.getRuntime().maxMemory() / (BUFFER * BUFFER)));

			return serverDetails;
		} catch (UnknownHostException e) {
			throw new CommandException(e);
		}
	}

	public double[] getServerMem() {
		return agentService.getTracer().getMemTraces();
	}

	public Boolean stopServer() throws Exception {

		Address address = agentService.getAddress();

		// Get list of this agents Nodes
		List<Address> addresses = agentService.getCommandCommunicator()
				.getAddressRegistry()
				.getNodeAddressesForServer(address.getServerName());

		try {
			MessageResponse response = agentService
					.getCommandCommunicator()
					.sendSyncMessage(addresses, "discardNode");

			if (response.isCompleteResponse()) {
				return true;
			}

		} catch (MessageException e) {
			throw new CommandException(e);
		}

		return false;
	}

	public ArrayList<LogFile> getLogs() throws Exception {
		ArrayList<LogFile> logfiles = new ArrayList<LogFile>();
		// adding server level logs
		File logFolder = new File(SystemVariables.LOG_PATH);

		if (logFolder.isDirectory()) {
			File[] logs = logFolder.listFiles();

			for (File file : logs) {
				if (file.exists() && file.isFile()) {
					logfiles.add(new LogFile(file.getName(),
							getCommandCommunicator().getAddress(), file
									.getAbsolutePath(), file.lastModified(),
							file.length()));
				}
			}
		}

		// adding server - agent level logs
		File agentFolder = new File(SystemVariables.LOG_PATH + File.separator
				+ "Agent");

		if (agentFolder.isDirectory()) {
			File[] alogs = agentFolder.listFiles();

			for (File afile : alogs) {
				if (afile.exists() && afile.isFile()) {
					logfiles.add(new LogFile(afile.getName(),
							getCommandCommunicator().getAddress(), afile
									.getAbsolutePath(), afile.lastModified(),
							afile.length()));
				}
			}
		}

		return logfiles;
	}

	public String getAgentLog(String logFileName) throws Exception {

		File logFolder = new File(SystemVariables.LOG_PATH);

		File file = new File(logFolder, logFileName);

		if (!file.exists()) {
			file = new File(logFolder, "Agent" + File.separator + logFileName);
		}

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

	public PID getAgentPID() {
		PID pid = new PID("Agent", ProcessDetector.getAgentPID());
		return pid;
	}

	public PID getPIDForNode(String nodeName) {
		PID pid = new PID(nodeName, ProcessDetector.getPIDForNode(nodeName));
		return pid;
	}
}
