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

package com.edifecs.epp.security.remote;

import com.edifecs.epp.isc.Isc;
import com.edifecs.epp.security.IAuthenticationManager;
import com.edifecs.epp.security.SessionId;
import com.edifecs.epp.security.exception.NotAuthenticatedException;
import com.edifecs.epp.security.exception.NullSessionException;
import com.edifecs.epp.security.exception.SecurityManagerException;
import com.edifecs.epp.security.service.ISecurityService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Created by willclem on 3/3/14.
 */
public class AuthenticationManager implements IAuthenticationManager {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Isc isc;
    private org.apache.shiro.mgt.SecurityManager securityManager;
    private SessionManager sessionManager;

    public AuthenticationManager(Isc isc, org.apache.shiro.mgt.SecurityManager securityManager, SessionManager sessionManager) {
        this.isc = isc;
        this.sessionManager = sessionManager;
        this.securityManager = securityManager;
    }

    private Subject getSubject(Serializable sessionId) {
        return new Subject.Builder(securityManager).sessionId(sessionId).buildSubject();
    }

    @Override
    public SessionId loginToken(AuthenticationToken token) {
        if (!(token instanceof AuthenticationToken)) {
            throw new IllegalArgumentException("Token for login must be a Shiro AuthenticationToken.");
        }
        SessionId currentSession = sessionManager.getCurrentSession();
        if (currentSession == null) {
            currentSession = sessionManager.createAndRegisterNewSession();
            if (currentSession == null) {
                throw new NullSessionException();
            }
        }
        final Subject subject = new Subject.Builder(securityManager).sessionId(
                currentSession.getSessionId()).buildSubject();
        subject.login(token);
        if (!subject.isAuthenticated()) {
            throw new NotAuthenticatedException();
        }
        final SessionId newSession = new SessionId(subject.getSession().getId());
        sessionManager.registerCurrentSession(newSession);
        return newSession;
    }

    @Override
    public SessionId login(Subject subject, AuthenticationToken authenticationToken, String username, Serializable password, String domain, String organization, Boolean remember) {
        return null;
    }

    @Override
    public void logout() throws SecurityManagerException {
        final SessionId currentSession = sessionManager.getCurrentSession();
        if (currentSession == null) {
            logger.warn("No user session is bound to the current thread; cannot logout a nonexistent session.");
            return;
        }
        final Subject subject = new Subject.Builder(securityManager).sessionId(
                currentSession.getSessionId()).buildSubject();
        try {
            subject.logout();
        } catch (AuthenticationException ex) {
            throw new SecurityManagerException(ex);
        }
    }

    @Override
    public boolean initiatePasswordReset(String email) {
        return isc.getService(ISecurityService.class).authentication().initiatePasswordReset(email);
    }

    @Override
    public boolean updatePassword(String newPasswd, String token) {
        return isc.getService(ISecurityService.class).authentication().updatePassword(newPasswd, token);
    }

    @Override
    public PrincipalCollection getSubjectPrincipals() {
        return getSubject(sessionManager.getCurrentSession()).getPrincipals();
    }

    @Override
    public boolean isSubjectAuthenticated() {
        if(sessionManager.getCurrentSession() == null) {
            return false;
        }
        return getSubject(sessionManager.getCurrentSession()).isAuthenticated();
    }

    @Override
    public SessionId loginCertificate(String domain, String organization, String certificate, String username) {
        return null;
    }

}
