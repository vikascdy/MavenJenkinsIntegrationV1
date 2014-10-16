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
package com.edifecs.epp.security.service.handler;

import com.edifecs.epp.isc.core.command.AbstractCommandHandler;
import com.edifecs.epp.security.IAuthorizationManager;
import com.edifecs.epp.security.SessionId;
import com.edifecs.epp.security.service.SecurityContext;
import com.edifecs.epp.security.remote.SecurityManager;
import org.apache.shiro.subject.Subject;
import scala.Option;
import scala.Some;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler is designed to assist in checking user access. Given a session ID it
 * provides ways to check if users are assigned specific roles, permissions, or
 * access rights.
 *
 * @author willclem
 */
public class AuthorizationManager extends AbstractCommandHandler implements IAuthorizationManager {

    private final SecurityContext sc;

    public AuthorizationManager(SecurityContext context) {
        this.sc = context;
    }

    private Subject getSubject(SessionId sessionId) {
        return new Subject.Builder(sc.shiroManager()).sessionId(
                sessionId.getSessionId()).buildSubject();
    }

    @Override
    public Option<SecurityManager> getReceivingSecurityManager() {
        return Some.<SecurityManager>apply(sc.manager());
    }

    @Override
    public boolean isPermitted(String permission) {
        final Subject subject = getSubject(getSecurityManager().getSessionManager().getCurrentSession());
        return sc.shiroManager().isPermitted(subject.getPrincipals(), permission);
    }

    @Override
    public void checkPermission(String permission) {
        final Subject subject = getSubject(getSecurityManager().getSessionManager().getCurrentSession());
        sc.shiroManager().checkPermission(subject.getPrincipals(), permission);
    }

    @Override
    public void checkPermissions(String[] permissions) {
        final Subject subject = getSubject(getSecurityManager().getSessionManager().getCurrentSession());
        sc.shiroManager().checkPermissions(subject.getPrincipals(), permissions);
    }

    @Override
    public String[] getPermittedPermissions(String[] permissions) {
        final Subject subject = getSubject(getSecurityManager().getSessionManager().getCurrentSession());
        boolean [] permittedArray = subject.isPermitted(permissions);
        List<String> perm = new ArrayList<>();
        for (int k = 0; k < permissions.length; k++) {
            if (permittedArray[k]) {
                perm.add(permissions[k]);
            }
        }
        return perm.toArray(new String[perm.size()]);
    }

    @Override
    public void checkRole(String roleIdentifier) {
        final Subject subject = getSubject(getSecurityManager().getSessionManager().getCurrentSession());
        sc.shiroManager().checkRole(subject.getPrincipals(), roleIdentifier);
    }

    @Override
    public void checkRoles(String[] roleIdentifiers) {
        final Subject subject = getSubject(getSecurityManager().getSessionManager().getCurrentSession());
        sc.shiroManager().checkRoles(subject.getPrincipals(), roleIdentifiers);
    }
}
