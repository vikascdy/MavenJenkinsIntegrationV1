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
import com.edifecs.epp.security.IAuthorizationManager;
import com.edifecs.epp.security.exception.SecurityManagerException;
import org.apache.shiro.subject.Subject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author willclem
 */
public class AuthorizationManager implements IAuthorizationManager {

    private org.apache.shiro.mgt.SecurityManager securityManager;
    private SessionManager sessionManager;

    public AuthorizationManager(Isc isc, org.apache.shiro.mgt.SecurityManager securityManager, SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.securityManager = securityManager;
    }

    private Subject getSubject(Serializable sessionId) {
        return new Subject.Builder(securityManager).sessionId(sessionId).buildSubject();
    }

    @Override
    public boolean isPermitted(String permissionName) {
        return getSubject(sessionManager.getCurrentSession()).isPermitted(permissionName);
    }

    @Override
    public void checkPermission(String permissionName) {
        getSubject(sessionManager.getCurrentSession()).checkPermission(permissionName);
    }

    @Override
    public void checkPermissions(String[] permissions) {
        getSubject(sessionManager.getCurrentSession()).checkPermissions(permissions);
    }

    @Override
    public String[] getPermittedPermissions(String[] permissions) throws SecurityManagerException {
        boolean[] permittedArray = getSubject(sessionManager.getCurrentSession()).isPermitted(permissions);
        List<String> perm = new ArrayList<>();
        int k = 0;
        for (String permission : permissions) {
            if (permittedArray[k]) {
                perm.add(permission);
            }
            k++;
        }
        return perm.toArray(new String[perm.size()]);
    }

    @Override
    public void checkRole(String roleName) {
        getSubject(sessionManager.getCurrentSession()).checkRole(roleName);
    }

    @Override
    public void checkRoles(String[] roleNames) {
        getSubject(sessionManager.getCurrentSession()).checkRoles(roleNames);
    }
}
