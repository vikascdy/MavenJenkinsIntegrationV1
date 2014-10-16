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
package com.edifecs.epp.isc.api;

import com.edifecs.epp.isc.Address;
import com.edifecs.epp.isc.Args;
import com.edifecs.epp.isc.ICommandCommunicator;
import com.edifecs.epp.isc.exception.MessageException;
import com.edifecs.epp.isc.exception.NoSecurityServiceException;
import com.edifecs.epp.isc.exception.ServiceTypeNotFoundException;
import com.edifecs.epp.security.SessionId;
import com.edifecs.epp.security.exception.SecurityManagerException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.session.mgt.SessionManager;

import java.io.Serializable;

/**
 * Guarantees that sessions are created remotely, on the esm-service, and
 * that the only sessions generated locally are {@link RemoteSession}s which
 * delegate to the sessions on the esm-service.
 * 
 * @author i-adamnels
 */
public class RemoteSessionManager implements SessionManager {

    private final ICommandCommunicator communicator;

    public RemoteSessionManager(ICommandCommunicator communicator) {
        this.communicator = communicator;
    }

    private Address getAddress() throws NoSecurityServiceException, ServiceTypeNotFoundException {
        return communicator.getAddressRegistry().getAddressForServiceTypeName("esm-service");
    }

    @Override
    public Session start(SessionContext context) {
        try {
            final SessionId id = (SessionId) communicator.sendSyncMessage(getAddress(), "start",
                    new Args("context", (Serializable)context));
            return new RemoteSession(id, communicator);
        } catch (MessageException ex) {
            throw new RuntimeException(ex);
        } catch (SecurityManagerException ex) {
            throw new RuntimeException(ex);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Session getSession(SessionKey key) throws SessionException {
        final SessionId id = new SessionId(key.getSessionId());
        return new RemoteSession(id, communicator);
    }
}
