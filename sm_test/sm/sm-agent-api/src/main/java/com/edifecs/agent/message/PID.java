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

/**
 * Stores PID information about a process for use within the message api.
 * 
 * @author willclem
 */
public class PID {
	
	private String process;
	
	private String pid;
	
	/**
	 * Creates an instance of PID.
	 * 
	 * @param process Name of the process.
	 * @param pid PID of the process
	 */
	public PID(String process, String pid) {
		this.process = process;
		this.pid = pid;
	}

	/**
	 * Gets the name of the process.
	 * 
	 * @return
	 */
	public String getProcess() {
		return process;
	}
	
	/**
	 * Gets the PID of the process.
	 * 
	 * @return
	 */
	public String getPid() {
		return pid;
	}
	
}
