// -----------------------------------------------------------------------------
// Copyright (c) Edifecs Inc. All Rights Reserved.
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
import com.edifecs.epp.isc.annotations.RequiresPermissions;
import com.edifecs.epp.isc.annotations.RequiresRoles;
import com.edifecs.epp.isc.api.SecurityPermissionAnnotationHandler;
import com.edifecs.epp.isc.api.SecurityRoleAnnotationHandler;
import com.edifecs.epp.security.ISessionManager;
import com.edifecs.epp.security.SessionId;
import com.edifecs.epp.security.data.RealmType;
import com.edifecs.epp.security.exception.AuthorizationFailureException;
import com.edifecs.epp.security.exception.NotAuthenticatedException;
import com.edifecs.epp.security.exception.SecurityManagerException;
import com.edifecs.epp.security.service.ISecurityService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.session.mgt.DefaultSessionContext;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.apache.shiro.util.ThreadState;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.*;

public class SessionManager implements ISessionManager {

	private static final int SESSION_ID_LENGTH = 64;
	private static final char[] SESSION_ID_CHARS = "0123456789abcdef".toCharArray();

	// Inheritable Thread locals are automatically passed from parent threads to
	// any thread spawned from this thread.
	private static final ThreadLocal<SessionId> boundSession = new InheritableThreadLocal<>();

	private final org.apache.shiro.mgt.SecurityManager securityManager;

    private final Isc isc;

	private Subject getSubject(Serializable sessionId) {
		return new Subject.Builder(securityManager).sessionId(sessionId).buildSubject();
	}

	public SessionManager(Isc isc, org.apache.shiro.mgt.SecurityManager securityManager) {
        this.isc = isc;
		this.securityManager = securityManager;
	}

	/**
	 * Binds a session object to the current thread, to be retrieved later using
	 * {@link #getCurrentSession()}.
	 * 
	 * @param newCurrentSession
	 *            The session to bind to the current thread.
	 * @see #createAndRegisterNewSession()
	 * @see #unregisterCurrentSession()
	 * @see #getCurrentSession()
	 */
	public void registerCurrentSession(SessionId newCurrentSession) {
		if (newCurrentSession == null) {
			unregisterCurrentSession();
		} else {
			boundSession.set(newCurrentSession);
		}
	}

	/**
	 * If any session is bound to the current thread, removes it, resetting the
	 * current session to {@code null}.
	 * 
	 * @see #registerCurrentSession(SessionId)
	 * @see #getCurrentSession()
	 */
	public void unregisterCurrentSession() {
		boundSession.set(null);
	}

	/**
	 * Returns the session bound to the current thread. May return {@code null}
	 * if no session has been bound to the current thread with
	 * {@link #registerCurrentSession(SessionId)}.
	 * 
	 * @see #registerCurrentSession(SessionId)
	 * @see #unregisterCurrentSession()
	 */
	public SessionId getCurrentSession() {
        return boundSession.get();
	}

	/**
	 * Creates and returns a new empty, unauthenticated session with a unique
	 * ID, and binds it to the current thread using
	 * {@link #registerCurrentSession(SessionId)}.
	 * 
	 * @return A new empty, unauthenticated session with a unique ID
	 * @throws SecurityManagerException
	 *             If the session cannot be created due to a network error or
	 *             authorization failure.
	 * @see #createNewSession()
	 * @see #getCurrentSession()
	 * @see #registerCurrentSession(SessionId)
	 */
	public SessionId createAndRegisterNewSession() {
		final SessionId session = createNewSession();
		registerCurrentSession(session);
		return session;
	}

	public SessionId createNewSession() throws SecurityManagerException {
		final SessionContext context = new DefaultSessionContext();
		context.setSessionId(generateSessionId());
		return start(context);
	}

	public Serializable generateSessionId() {
		final StringBuilder sb = new StringBuilder();
		final Random rnd = new SecureRandom();
		for (int i = 0; i < SESSION_ID_LENGTH; i++) {
			sb.append(SESSION_ID_CHARS[rnd.nextInt(SESSION_ID_CHARS.length)]);
		}
		return sb.toString();
	}

	public Object callMethodAsUser(SessionId session, Object owner,
			Method method, Object[] args) throws SecurityManagerException,
			InvocationTargetException, IllegalAccessException {
		registerCurrentSession(session);
		final Subject subject = getSubject(session.getSessionId());
		final ThreadState threadState = new SubjectThreadState(subject);
		threadState.bind();
		try {
			if (method.isAnnotationPresent(RequiresRoles.class)) {
				new SecurityRoleAnnotationHandler().assertAuthorized(method
						.getAnnotation(RequiresRoles.class));
			}
			if (method.isAnnotationPresent(RequiresPermissions.class)) {
				new SecurityPermissionAnnotationHandler()
						.assertAuthorized(method
								.getAnnotation(RequiresPermissions.class));
			}
			Object obj = method.invoke(owner, args);
			if(null == obj && method.getReturnType().equals(Void.TYPE)) {
				return true;
			}
			
			return obj;
		} catch (AuthorizationException ex) {
			throw new AuthorizationFailureException(ex);
		} catch (AuthenticationException ex) {
			throw new NotAuthenticatedException(ex);
		} finally {
			threadState.restore();
		}
	}

    public Long getUserId() throws SecurityManagerException {
        final Subject s = getSubject(getCurrentSession().getSessionId());
        if (s == null || s.getPrincipal() == null) {
            return null;
        }
        return (Long) s.getPrincipals().fromRealm(RealmType.DATABASE.getVal())
                .toArray()[0];
    }

    // Remote Calls

    @Override
    public SessionId start(SessionContext context) {
        return isc.getService(ISecurityService.class).sessions().start(context);
    }

    @Override
    public Serializable getSessionAttribute(Serializable attributeKey) {
        return isc.getService(ISecurityService.class).sessions().getSessionAttribute(attributeKey);
    }

    @Override
    public Collection<Object> getSessionAttributeKeys() {
        return isc.getService(ISecurityService.class).sessions().getSessionAttributeKeys();
    }

    @Override
    public void setSessionAttribute(Serializable attributeKey, Serializable value) {
        isc.getService(ISecurityService.class).sessions().setSessionAttribute(attributeKey, value);
    }

    @Override
    public Serializable removeSessionAttribute(Serializable attributeKey) {
        return isc.getService(ISecurityService.class).sessions().removeSessionAttribute(attributeKey);
    }

    @Override
    public Date getSessionStartTimestamp() {
        return isc.getService(ISecurityService.class).sessions().getSessionStartTimestamp();
    }

    @Override
    public long getSessionTimeout() {
        return isc.getService(ISecurityService.class).sessions().getSessionTimeout();
    }

    @Override
    public void touchSession() {
        isc.getService(ISecurityService.class).sessions().touchSession();
    }
}
