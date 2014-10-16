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

import com.edifecs.epp.isc.CommandCommunicator;
import com.edifecs.epp.isc.exception.ServiceException;
import com.edifecs.epp.security.SessionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edifecs.servicemanager.api.ServiceRef;

public class ServiceStartThread extends Thread {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ServiceRef ref;
    private SessionId sessionId;

    public ServiceStartThread(final ServiceRef service, SessionId sessionId) {
        this.ref = service;
        this.sessionId = sessionId;
    }

    @Override
    public void run() {
        try {
            if (ref == null) {
                throw new ServiceException("Service not found.");
            }
            CommandCommunicator.getInstance().getSecurityManager().getSessionManager()
                    .registerCurrentSession(sessionId);
            Thread.currentThread().setContextClassLoader(ref.getClassLoader());
            ref.start();
            logger.info("Started Service: {}", ref.getId());

        // Catching Throwable here is important, otherwise a service can terminate a node.
        } catch (Throwable e) {
            throw new ServiceException(e);
        }
    }
}
