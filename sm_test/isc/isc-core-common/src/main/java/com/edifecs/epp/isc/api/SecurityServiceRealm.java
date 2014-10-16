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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.edifecs.epp.security.service.ISecurityService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.edifecs.epp.isc.ICommandCommunicator;
import com.edifecs.epp.isc.Address;
import com.edifecs.epp.isc.exception.NoSecurityServiceException;
import com.edifecs.epp.isc.exception.ServiceTypeNotFoundException;

/**
 * A Shiro realm that uses the esm-service directly for authorization and
 * authentication.
 *
 * @author i-adamnels
 */
public class SecurityServiceRealm extends AuthorizingRealm {

    private final ICommandCommunicator communicator;

    public SecurityServiceRealm(ICommandCommunicator communicator) {
        this.communicator = communicator;
    }

    private Address getAddress() throws NoSecurityServiceException, ServiceTypeNotFoundException {
        return communicator.getAddressRegistry().getAddressForServiceTypeName("esm-service");
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return true;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        throw new UnsupportedOperationException("Attempted to retrieve"
                + " authorization info, which is not accessible; this probably"
                + " means that one of the permission- or role-checking methods was"
                + " not correctly overridden.");
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPermitted(PrincipalCollection subjectPrincipal, String permission) {
        return communicator.getService(ISecurityService.class).authorization().isPermitted(permission);
    }

    @Override
    public boolean isPermitted(PrincipalCollection subjectPrincipal, Permission permission) {
        return communicator.getService(ISecurityService.class).authorization().isPermitted(permission.toString());
    }

    //TODO: Optimize this method
    @Override
    public boolean[] isPermitted(PrincipalCollection subjectPrincipal, String... permissions) {
        final boolean[] results = new boolean[permissions.length];
        for (int i = 0; i < permissions.length; i++) {
            results[i] = isPermitted(subjectPrincipal, permissions[i]);
        }
        return results;
    }

    //TODO: Optimize this method
    @Override
    public boolean[] isPermitted(PrincipalCollection subjectPrincipal, List<Permission> permissions) {
        final boolean[] results = new boolean[permissions.size()];
        for (int i = 0; i < permissions.size(); i++) {
            results[i] = isPermitted(subjectPrincipal, permissions.get(i));
        }
        return results;
    }

    //TODO: Optimize this method
    @Override
    public boolean isPermittedAll(PrincipalCollection subjectPrincipal, String... permissions) {
        for (String p : permissions) {
            if (!isPermitted(subjectPrincipal, p)) {
                return false;
            }
        }
        return true;
    }

    //TODO: Optimize this method
    @Override
    public boolean isPermittedAll(PrincipalCollection subjectPrincipal, Collection<Permission> permissions) {
        for (Permission p : permissions) {
            if (!isPermitted(subjectPrincipal, p)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void checkPermission(PrincipalCollection subjectPrincipal, String permission) throws AuthorizationException {
        communicator.getService(ISecurityService.class).authorization().checkPermission(permission);
    }

    @Override
    public void checkPermission(PrincipalCollection subjectPrincipal, Permission permission)
            throws AuthorizationException {
        communicator.getService(ISecurityService.class).authorization().checkPermission(permission.toString());
    }

    @Override
    public void checkPermissions(PrincipalCollection subjectPrincipal, String... permissions)
            throws AuthorizationException {
        communicator.getService(ISecurityService.class).authorization().checkPermissions(permissions);
    }

    @Override
    public void checkPermissions(PrincipalCollection subjectPrincipal, Collection<Permission> permissions)
            throws AuthorizationException {
        List<String> strings = new ArrayList<>();
        for (Permission permission : permissions) {
            strings.add(permission.toString());
        }
        communicator.getService(ISecurityService.class).authorization().checkPermissions(strings.toArray(new String[]{}));
    }

    @Override
    public boolean hasRole(PrincipalCollection subjectPrincipal, String roleIdentifier) {
        communicator.getService(ISecurityService.class).authorization().checkRole(roleIdentifier);
        return true;
    }

    //TODO: Optimize this method
    @Override
    public boolean[] hasRoles(PrincipalCollection subjectPrincipal, List<String> roleIdentifiers) {
        final boolean[] results = new boolean[roleIdentifiers.size()];
        for (int i = 0; i < roleIdentifiers.size(); i++) {
            results[i] = hasRole(subjectPrincipal, roleIdentifiers.get(i));
        }
        return results;
    }

    //TODO: Optimize this method
    @Override
    public boolean hasAllRoles(PrincipalCollection subjectPrincipal, Collection<String> roleIdentifiers) {
        for (String r : roleIdentifiers) {
            if (!hasRole(subjectPrincipal, r)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void checkRole(PrincipalCollection subjectPrincipal, String roleIdentifier) throws AuthorizationException {
        communicator.getService(ISecurityService.class).authorization().checkRole(roleIdentifier);
    }

    @Override
    public void
    checkRoles(PrincipalCollection subjectPrincipal, Collection<String> roleIdentifiers) throws AuthorizationException {
        communicator.getService(ISecurityService.class).authorization().checkRoles(roleIdentifiers.toArray(new String[]{}));
    }

    @Override
    public void checkRoles(PrincipalCollection subjectPrincipal, String... roleIdentifiers) throws AuthorizationException {
        communicator.getService(ISecurityService.class).authorization().checkRoles(roleIdentifiers);
    }
}
