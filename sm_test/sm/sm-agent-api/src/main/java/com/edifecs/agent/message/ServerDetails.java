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
package com.edifecs.agent.message;

import java.io.Serializable;

/**
 * Contains information about a server to be sent through the message API.
 * 
 * @author willclem
 * 
 */
public class ServerDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String hostname;
	private final String ipAddress;
	private final String os;
	private final String arch;
	private final Integer cpuCores;
	private final Integer cpuMHz;
	private final Integer memMB;

	/**
	 * Creates a new ServerDetails object. All values must be passed in on
	 * creation.
	 * 
	 * @param hostname
	 *            Hostname of the machine
	 * @param ipAddress
	 *            IP Address in use by the message API
	 * @param os
	 *            OS of the machine
	 * @param arch
	 *            Architecture of the machine
	 * @param cpuCores
	 *            Number of CPU's
	 * @param cpuMHz
	 *            Number of MHZ of processor.
	 * @param memMB
	 *            Amount of memory of the machine.
	 */
	public ServerDetails(final String hostname, final String ipAddress,
			final String os, final String arch, final Integer cpuCores,
			final Integer cpuMHz, final Integer memMB) {
		super();
		this.hostname = hostname;
		this.ipAddress = ipAddress;
		this.os = os;
		this.arch = arch;
		this.cpuCores = cpuCores;
		this.cpuMHz = cpuMHz;
		this.memMB = memMB;
	}

	/**
	 * 
	 * @return Hostname of the machine
	 */
	public final String getHostname() {
		return hostname;
	}

	/**
	 * @return IPAddress of the machine
	 */
	public final String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @return the OS of the machine
	 */
	public final String getOs() {
		return os;
	}

	/**
	 * @return The Architecture of the machine
	 */
	public final String getArch() {
		return arch;
	}

	/**
	 * @return The number of CPU Cores on the machine
	 */
	public final Integer getCpuCores() {
		return cpuCores;
	}

	/**
	 * @return Get the number of MHz of the processor
	 */
	public final Integer getCpuMHz() {
		return cpuMHz;
	}

	/**
	 * @return Get the amount of memory on machine in MB
	 */
	public final Integer getMemMB() {
		return memMB;
	}

}
