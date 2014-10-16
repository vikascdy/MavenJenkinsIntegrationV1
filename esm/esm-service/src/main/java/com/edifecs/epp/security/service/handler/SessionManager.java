package com.edifecs.epp.security.service.handler;

import com.edifecs.epp.isc.core.command.AbstractCommandHandler;
import com.edifecs.epp.security.ISessionManager;
import com.edifecs.epp.security.SessionId;
import com.edifecs.epp.security.remote.SecurityManager;
import com.edifecs.epp.security.service.SecurityContext;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.subject.Subject;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * Methods to assist in the management of the users session. It is used to
 * create new session, assign properties to the user session, and helps keep the
 * session alive.
 *
 * @author willclem
 */
public class SessionManager extends AbstractCommandHandler implements ISessionManager {

    private final SecurityContext sc;

    public SessionManager(SecurityContext context) {
        this.sc = context;
    }

    private SessionId getUserSession() {
        return sc.manager().getSessionManager().getCurrentSession();
    }

    private Session getSession() {
        final Subject subject = new Subject.Builder(sc.shiroManager()).sessionId(
                getUserSession().getSessionId()).buildSubject();
        return subject.getSession();
    }

    @Override
    public SessionId start(SessionContext context) {
        final Session session = sc.shiroManager().start(context);
        // TODO: Ability to set the timeout value of the user
        // session.setTimeout(5000);
        getLogger().debug("Created new session with id {}.", session.getId());
        final SessionId usession = new SessionId(session.getId());
        sc.manager().getSessionManager().registerCurrentSession(usession);
        return usession;
    }

    @Override
    public Serializable getSessionAttribute(Serializable attributeKey) {
        final Session session = getSession();
        session.touch();
        return (Serializable) session.getAttribute(attributeKey);
    }

    @Override
    public Collection<Object> getSessionAttributeKeys() {
        final Session session = getSession();
        session.touch();
        return session.getAttributeKeys();
    }

    @Override
    public void setSessionAttribute(Serializable attributeKey, Serializable value) {
        final Session session = getSession();
        session.touch();
        session.setAttribute(attributeKey, value);
    }

    @Override
    public Serializable removeSessionAttribute(Serializable attributeKey) {
        final Session session = getSession();
        session.touch();
        return (Serializable) session.removeAttribute(attributeKey);
    }

    @Override
    public Date getSessionStartTimestamp() {
        final Session session = getSession();
        return session.getStartTimestamp();
    }

    @Override
    public long getSessionTimeout() {
        final Session session = getSession();
        return session.getTimeout();
    }

    @Override
    public void touchSession() {
        final Session session = getSession();
        session.touch();
    }

    @Override
    public scala.Option<SecurityManager> getReceivingSecurityManager() {
        return scala.Option.apply((SecurityManager) sc.manager());
    }
}
