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
package com.edifecs.epp.security;

import com.edifecs.epp.isc.annotations.*;
import com.edifecs.epp.security.handler.serializer.SessionIdSerializer;

/**
 * Handler is designed to assist in checking user access. Given a session ID it
 * provides ways to check if users are assigned specific roles, permissions, or
 * access rights.
 *
 * @author willclem
 */
@CommandHandler(namespace = "authorization")
@JsonSerialization(adapters = {@TypeAdapter(SessionIdSerializer.class)})
public interface IAuthorizationManager {

    /**
     * Given a permission, return true if the current user has this permission.
     *
     * @param permission {@link com.edifecs.epp.security.data.Permission}
     * @return
     */
    @SyncCommand
    boolean isPermitted(
            @Arg(name = "permission", required = true) String permission);

    /**
     * Given a permission, throws an exception if the user does not have the permission, if the user does have the
     * permission, nothing is thrown.
     *
     * @param permission String in the format: {@link com.edifecs.epp.security.data.Permission}
     * @throws com.edifecs.epp.security.exception.AuthorizationFailureException If user does not have the permission
     */
    @SyncCommand
    void checkPermission(
            @Arg(name = "permission", required = true) String permission);

    /**
     * Given a list of permissions, throws an exception if the user does not have the permission, if the user does have the
     * permission, nothing is thrown.
     *
     * @param permissions Array of Strings in the format: {@link com.edifecs.epp.security.data.Permission}
     * @throws com.edifecs.epp.security.exception.AuthorizationFailureException If user does not have the permission
     */
    @SyncCommand
    void checkPermissions(
            @Arg(name = "permissions", required = true) String[] permissions);

    /**
     * <b>Permissions should be used. Roles can change, and are more dynamic. This is mostly an internal call</b>
     *
     * Given a role, throws an exception if the user does not have the role, if the user does have the
     * role, nothing is thrown.
     *
     * @param roleIdentifier identifier for the role
     * @throws com.edifecs.epp.security.exception.AuthorizationFailureException If user does not have the role
     */
    @SyncCommand
    void checkRole(
            @Arg(name = "roleIdentifier", required = true) String roleIdentifier);

    /**
     * <b>Permissions should be used. Roles can change, and are more dynamic. This is mostly an internal call</b>
     *
     * Given an array of roles, throws an exception if the user does not have the role, if the user does have the
     * role, nothing is thrown.
     *
     * @param roleIdentifiers List of role identifiers to validate
     * @throws com.edifecs.epp.security.exception.AuthorizationFailureException If user does not have the role
     */
    @SyncCommand
    void checkRoles(
            @Arg(name = "roleIdentifiers", required = true) String[] roleIdentifiers);

    /**
     * Given a list of permissions, returns a list of those permissions that the user has.
     * <p/>
     * This is very useful for UI work and for applications that are very dependent on permission driven conditions
     * as one call can get you the list of permissions that a user actually has.
     *
     * @param permissions Array of Strings in the format: {@link com.edifecs.epp.security.data.Permission}
     * @return List of permission strings of those permissions the user has
     * @throws com.edifecs.epp.security.exception.AuthorizationFailureException If user does not have the permission
    */
    @SyncCommand
    public String[] getPermittedPermissions(
            @Arg(name = "permissions", required = true) String[] permissions);
}
