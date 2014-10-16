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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edifecs.epp.isc.ICommandCommunicator;
import com.edifecs.epp.isc.exception.RegistryUpdateException;

public class ServiceRegistryUpdater extends Thread {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final int HEARTBEAT_TIMEOUT = 3000;

	private boolean running = true;

	/**
	 * Message Receiver in charge of handling all message from the cluster, and
	 * from all services.
	 */
	private ICommandCommunicator connection;

	public ServiceRegistryUpdater(ICommandCommunicator connection) {
	    // Set the name of the thread
	    this.setName("Service Registry Updater Thread");
	    
		this.connection = connection;
		this.setDaemon(true);
	}

	@Override
	public void run() {
		while (running) {
			try {
				Thread.sleep(HEARTBEAT_TIMEOUT);
				connection.requestServiceRegistryUpdate();
			} catch (RegistryUpdateException e) {
			    if(running) {
			        logger.warn("Unable to get heartbeat from cluster", e);
			    }
			} catch (Exception e) {
			    if(running) {
    				logger.error(e.getMessage(), e);
    				throw new RuntimeException(
    						"Exception thrown in the ServiceRegistryUpdater auto service updating disabled.",
    						e);
			    }
			}
		}
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

}
