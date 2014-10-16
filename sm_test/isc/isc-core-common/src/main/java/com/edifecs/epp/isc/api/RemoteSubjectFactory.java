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

import com.edifecs.epp.isc.ICommandCommunicator;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;

import com.edifecs.epp.security.SessionId;

/**
 * Used to make sure that the {@link SecurityManager}s on client sessions
 * generate only {@link RemoteSubject}s.
 * 
 * @author i-adamnels
 */
public class RemoteSubjectFactory implements SubjectFactory {

    private final SecurityManager securityManager;
    private final ICommandCommunicator communicator;

    public RemoteSubjectFactory(SecurityManager securityManager,
                                ICommandCommunicator communicator) {
        this.securityManager = securityManager;
        this.communicator = communicator;
    }

    @Override
    public Subject createSubject(SubjectContext context) {
        if (context.getSessionId() != null) {
            final SessionId currentSession = communicator.getSecurityManager().getSessionManager().getCurrentSession();
            final Subject s;
            if (currentSession != null &&
                currentSession.getSessionId().equals(context.getSessionId())) {
                s = new RemoteSubject(currentSession, securityManager, communicator);
            } else {
                s = new RemoteSubject(new SessionId(context.getSessionId()), securityManager,
                        communicator);
            }
            if (context.getAuthenticationToken() != null) {
                s.login(context.getAuthenticationToken());
            }
            return s;
        }
        throw new IllegalArgumentException(
                "A RemoteSubject cannot be created without a session ID.");
    }

}
