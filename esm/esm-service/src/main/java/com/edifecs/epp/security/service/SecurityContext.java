package com.edifecs.epp.security.service;

import com.edifecs.epp.security.data.User;
import com.edifecs.epp.security.datastore.ISecurityDataStore;
import com.edifecs.epp.security.exception.NotYetInitializedException;
import com.edifecs.epp.security.remote.SecurityManager;

/**
 * Internal object used to pass lazy-loaded references to the SecurityManager,
 * SecurityDataStore, etc., to command handlers.
 *
 * @author c-adamnels
 */
public class SecurityContext {

    private static SecurityManager _manager;
    private static User systemUser;
    private ISecurityDataStore dataStore = null;
    private SecurityManager manager = null;

    public final static User getSystemUser() {
        return systemUser;
    }

    void setSystemUser(final User system) {
        systemUser = system;
    }

    public final static User getCurrentUser() {
        return _manager.getSubjectManager().getUser();
    }

    public SecurityManager manager() {
        if(manager == null) {
            throw new NotYetInitializedException();
        }
        return manager;
    }

    public org.apache.shiro.mgt.SecurityManager shiroManager() {
        return manager().getSecurityManager();
    }

    public ISecurityDataStore dataStore() {
        return dataStore;
    }

    public void initDataStore(ISecurityDataStore ds) {
        dataStore = ds;
    }

    public void initManager(SecurityManager sm) {
        manager = sm;
        _manager = sm;
    }
}

