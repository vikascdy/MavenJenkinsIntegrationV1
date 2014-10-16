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

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.edifecs.core.configuration.helper.TypesafeConfigKeys;
import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.StoppedSessionException;

import com.edifecs.epp.isc.Args;
import com.edifecs.epp.isc.ICommandCommunicator;
import com.edifecs.epp.isc.Address;
import com.edifecs.epp.isc.exception.MessageException;
import com.edifecs.epp.isc.exception.ServiceTypeNotFoundException;
import com.edifecs.epp.security.SessionId;
import com.edifecs.epp.security.exception.SecurityManagerException;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

/**
 * A Shiro {@link Session} that delegates all of its functions to the security
 * service. This allows the esm-service to maintain a single, canonical
 * representation of the session, which is shared between all services.
 * 
 * @author i-adamnels
 */
class RemoteSession implements Session {

    private Date lastAccessTime;
    private long sessionTimeout = 0;

    private final SessionId id;
    private final ICommandCommunicator communicator;
    private final Duration messageTimeout;
    
    private Address getAddress() throws ServiceTypeNotFoundException {
        return communicator.getAddressRegistry().getAddressForServiceTypeName("esm-service");
    }

    RemoteSession(SessionId id, ICommandCommunicator communicator) {
        this.id = id;
        this.communicator = communicator;
        // Timeout for sending sync messages is 1/2 of the standard message
        // timeout, so that security message timeouts are actually reported.
        this.messageTimeout = Duration.apply(Math.min(
                communicator.getConfig().getDuration(
                        TypesafeConfigKeys.ASYNC_MESSAGE_TIMEOUT, TimeUnit.MILLISECONDS),
                communicator.getConfig().getDuration(
                        TypesafeConfigKeys.SYNC_MESSAGE_TIMEOUT, TimeUnit.MILLISECONDS)) / 2,
            TimeUnit.MILLISECONDS);
        communicator.getSecurityManager().getSessionManager().registerCurrentSession(id);
        try {
            this.sessionTimeout = sendCommand("getSessionTimeout", new Args(), Long.class);
        } catch (Exception ex) {
            this.sessionTimeout = 0;
        }
        // touch();
    }

    private <T> T sendCommand(String command, Map<String, ? extends Serializable> args,
            Class<T> type) throws Exception {
        return Await.result(
            communicator.send(getAddress(), command, args).as(type).asScalaFuture(),
            messageTimeout
        );
    }

    private void checkSessionValidity() throws InvalidSessionException {
        if (sessionTimeout < 0) {
            throw new StoppedSessionException("Session has been stopped.");
        } else if (sessionTimeout > 0 && lastAccessTime != null
                && lastAccessTime.getTime() + sessionTimeout < System.currentTimeMillis()) {
            throw new ExpiredSessionException("Session has expired.");
        }
    }

    @Override
    public Serializable getAttribute(Object key) throws InvalidSessionException {
        checkSessionValidity();
        if (key instanceof Serializable) {
            try {
                communicator.getSecurityManager().getSessionManager().registerCurrentSession(id);
                return sendCommand(
                    "getSessionAttribute",
                    new Args("attributeKey", (Serializable)key),
                    Serializable.class);
            } catch (MessageException ex) {
                throw new InvalidSessionException("Session inaccessible due to MessageException.",
                        ex);
            } catch (SecurityManagerException ex) {
                throw new InvalidSessionException(
                        "Session inaccessible because esm-service is not available.", ex);
            } catch (Exception ex) {
                throw new InvalidSessionException("Session inaccessible due to Exception.", ex);
            }
        } else {
            throw new IllegalArgumentException("A session attribute key must be"
                    + " serializable; the type '" + key.getClass().getCanonicalName()
                    + "' does not implement Serializable.");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Object> getAttributeKeys() throws InvalidSessionException {
        checkSessionValidity();
        try {
            communicator.getSecurityManager().getSessionManager().registerCurrentSession(id);
            return sendCommand("getSessionAttributeKeys", new Args(), Collection.class);
        } catch (MessageException ex) {
            throw new InvalidSessionException("Session inaccessible due to MessageException.", ex);
        } catch (SecurityManagerException ex) {
            throw new InvalidSessionException(
                    "Session inaccessible because esm-service is not available.", ex);
        } catch (Exception ex) {
            throw new InvalidSessionException("Session inaccessible due to Exception.", ex);
        }
    }

    @Override
    public String getHost() {
        return null;
    }

    @Override
    public Serializable getId() {
        return id.getSessionId();
    }

    @Override
    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    @Override
    public Date getStartTimestamp() {
        try {
            communicator.getSecurityManager().getSessionManager().registerCurrentSession(id);
            return sendCommand("getSessionStartTimestamp", new Args(), Date.class);
        } catch (MessageException ex) {
            throw new InvalidSessionException("Session inaccessible due to MessageException.", ex);
        } catch (SecurityManagerException ex) {
            throw new InvalidSessionException(
                    "Session inaccessible because esm-service is not available.", ex);
        } catch (Exception ex) {
            throw new InvalidSessionException("Session inaccessible due to Exception.", ex);
        }
    }

    @Override
    public long getTimeout() throws InvalidSessionException {
        return sessionTimeout;
    }

    @Override
    public Serializable removeAttribute(Object key) throws InvalidSessionException {
        checkSessionValidity();
        if (key instanceof Serializable) {
            try {
                communicator.getSecurityManager().getSessionManager().registerCurrentSession(id);
                return sendCommand("removeSessionAttribute",
                    new Args("attributeKey", (Serializable)key), Serializable.class);
            } catch (MessageException ex) {
                throw new InvalidSessionException("Session inaccessible due to MessageException.",
                        ex);
            } catch (SecurityManagerException ex) {
                throw new InvalidSessionException(
                        "Session inaccessible because esm-service is not available.", ex);
            } catch (Exception ex) {
                throw new InvalidSessionException("Session inaccessible due to Exception.", ex);
            }
        } else {
            throw new IllegalArgumentException("A session attribute key must be"
                    + " serializable; the type '" + key.getClass().getCanonicalName()
                    + "' does not implement Serializable.");
        }
    }

    @Override
    public void setAttribute(Object key, Object value) throws InvalidSessionException {
        checkSessionValidity();
        if (!(key instanceof Serializable)) {
            throw new IllegalArgumentException("A session attribute key must be"
                    + " serializable; the type '" + key.getClass().getCanonicalName()
                    + "' does not implement Serializable.");
        }
        if (!(value instanceof Serializable)) {
            throw new IllegalArgumentException("A session attribute value must be"
                    + " serializable; the type '" + value.getClass().getCanonicalName()
                    + "' does not implement Serializable.");
        }
        try {
            communicator.getSecurityManager().getSessionManager().registerCurrentSession(id);
            sendCommand(
                "setSessionAttribute",
                new Args(
                    "attributeKey", (Serializable)key,
                    "value",        (Serializable)value),
                Serializable.class);
        } catch (MessageException ex) {
            throw new InvalidSessionException("Session inaccessible due to MessageException.", ex);
        } catch (SecurityManagerException ex) {
            throw new InvalidSessionException(
                    "Session inaccessible because esm-service is not available.", ex);
        } catch (Exception ex) {
            throw new InvalidSessionException("Session inaccessible due to Exception.", ex);
        }
    }

    @Override
    public void setTimeout(long ms) throws InvalidSessionException {
        sessionTimeout = ms;
        // TODO: Propagate timeout changes to the security-service.
        touch();
    }

    @Override
    public void stop() throws InvalidSessionException {
        sessionTimeout = -1;
        // TODO: Propagate timeout changes to the security-service.
    }

    @Override
    public void touch() throws InvalidSessionException {
        lastAccessTime = new Date();
        try {
            communicator.getSecurityManager().getSessionManager().registerCurrentSession(id);
            sendCommand("touchSession", new Args(), Serializable.class);
        } catch (MessageException ex) {
            throw new InvalidSessionException("Session inaccessible due to MessageException.", ex);
        } catch (SecurityManagerException ex) {
            throw new InvalidSessionException(
                    "Session inaccessible because esm-service is not available.", ex);
        } catch (Exception ex) {
            throw new InvalidSessionException("Session inaccessible due to Exception.", ex);
        }
    }
}
