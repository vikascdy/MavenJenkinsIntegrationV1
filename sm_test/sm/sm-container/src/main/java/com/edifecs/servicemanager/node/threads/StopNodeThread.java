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
package com.edifecs.servicemanager.node.threads;

import com.edifecs.core.configuration.helper.TypesafeConfigKeys;
import com.edifecs.epp.isc.CommandCommunicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * A Delayed system exit command that lets us trigger the shutdown process hook while still responding with a successful
 * message notification through the ISC.
 */
public class StopNodeThread extends Thread {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private CommandCommunicator commandCommunicator;

	public StopNodeThread(CommandCommunicator commandCommunicator) {
		this.commandCommunicator = commandCommunicator;
	}

	@Override
	public void run() {
        try {
            Thread.sleep(commandCommunicator.config().getDuration(TypesafeConfigKeys.SYNC_MESSAGE_TIMEOUT, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            logger.error("Interrupted Exception on Node shutdown thread.", e);
        }
        System.exit(0);
	}

}
