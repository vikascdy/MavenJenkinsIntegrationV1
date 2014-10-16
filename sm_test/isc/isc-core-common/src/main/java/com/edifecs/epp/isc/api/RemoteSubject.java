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
import com.edifecs.epp.security.SessionId;
import com.edifecs.epp.security.exception.AuthenticationFailureException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.support.DelegatingSubject;

import java.io.Serializable;

/**
 * A Shiro {@link org.apache.shiro.subject.Subject} that retrieves some of its information from the
 * esm-service, rather than storing it locally.
 * 
 * @author i-adamnels
 */
public class RemoteSubject extends DelegatingSubject {

    private SessionId id;
    private final ICommandCommunicator communicator;
    
    private Address getAddress() {
        return communicator.getAddressRegistry().getAddressForServiceTypeName("esm-service");
    }

    public RemoteSubject(SessionId id, SecurityManager securityManager,
                         ICommandCommunicator communicator) {
        super(null,  // Principals
              false, // Authenticated
              null,  // Host
              new RemoteSession(id, communicator), false, // sessionCreationEnabled
              securityManager);
        this.id = id;
        this.communicator = communicator;
    }

    public Serializable getSessionId() {
        return id.getSessionId();
    }

    @Override
    public Object getPrincipal() {
        final PrincipalCollection principals = getPrincipals();
        if (principals != null) {
            return principals.getPrimaryPrincipal();
        }
        return null;
    }

    @Override
    public PrincipalCollection getPrincipals() {
        try {
            communicator.getSecurityManager().getSessionManager().registerCurrentSession(id);
            final PrincipalCollection principals = (PrincipalCollection) communicator
                .sendSyncMessage(getAddress(), "getSubjectPrincipals");
            if (principals == null) {
                return null;
            }
            return new SessionIdPrincipalCollection(principals, getSessionId());
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public boolean isAuthenticated() {
        communicator.getSecurityManager().getSessionManager().registerCurrentSession(id);
        return (Boolean) communicator.sendSyncMessage(getAddress(), "isSubjectAuthenticated");
    }

    @Override
    public void login(AuthenticationToken token) {
        try {
            communicator.getSecurityManager().getSessionManager().registerCurrentSession(id);
            final SessionId newSession = (SessionId) communicator.sendSyncMessage(
                    getAddress(), "login",
                    new Args("authenticationToken", token));
            id = newSession;
            communicator.getSecurityManager().getSessionManager().registerCurrentSession(id);
            session = new RemoteSession(id, communicator);
        } catch (Exception e) {
            throw new AuthenticationFailureException(e);
        }
    }

    @Override
    public void logout() {
        communicator.getSecurityManager().getSessionManager().registerCurrentSession(id);
        communicator.sendSyncMessage(getAddress(), "logout");
    }
}
