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

import com.edifecs.epp.isc.annotations.Arg;
import com.edifecs.epp.isc.annotations.CommandHandler;
import com.edifecs.epp.isc.annotations.NullSessionAllowed;
import com.edifecs.epp.isc.annotations.SyncCommand;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

import java.io.Serializable;

/**
 * Provides authentication methods for checking to see if the user is signed in,
 * and to login and logout if needed.
 *
 * @author willclem
 */
@CommandHandler(name = "SubjectCommandHandler")
public interface IAuthenticationManager {

    /**
     * Call mainly used internally to return the internally used ID of the user. It is only used in a couple places to
     * get back the user ID of the currently logged in user.
     * <p/>
     * This should be replaces with the ISubjectCommandHandler getUserId() command.
     *
     * @return PrincipalCollection which is a Shiro object containing the users id
     */
    @SyncCommand
    @Deprecated
    PrincipalCollection getSubjectPrincipals();

    /**
     * Checks if the user is currently signed in and authenticated or not. Will return true if the user is an
     * authenticated user and false if the user has not yet signed in.
     * <p/>
     * This is frequently used in health check type scenarios to check if the user is still logged in or not.
     *
     * @return true is user is authenticated
     */
    @SyncCommand
    @NullSessionAllowed
    boolean isSubjectAuthenticated();

    /**
     * Login command used for certificate based logins using UTF-8 encoded string representation.
     * <p/>
     *
     * @param domain      The domain that the user needs to login through.
     * @param certificate an UTF-8 base64 encoded String.
     * @throws Exception
     */
    @SyncCommand
    @NullSessionAllowed
    SessionId loginCertificate(
            @Arg(name = "domain") String domain,
            @Arg(name = "organization") String organization,
            @Arg(name = "certificate") String certificate,
            @Arg(name = "username") String username);

    @SyncCommand
    @NullSessionAllowed
    SessionId loginToken(
            @Arg(name = "authenticationToken") AuthenticationToken authenticationToken);

    @SyncCommand
    @NullSessionAllowed
    SessionId login(
            @Arg(name = "subject") Subject subject,
            @Arg(name = "authenticationToken") AuthenticationToken authenticationToken,
            @Arg(name = "username") String username,
            @Arg(name = "password") Serializable password,
            @Arg(name = "domain") String domain,
            @Arg(name = "organization") String organization,
            @Arg(name = "remember") Boolean remember);

    @SyncCommand
    void logout();

    @SyncCommand(name = "user.sendResetPasswordEmail")
    boolean initiatePasswordReset(
            @Arg(name = "email", description = "user email address", required = true) String email);

    @SyncCommand(name = "password.updatePassword")
    boolean updatePassword(
            @Arg(name = "newPasswd", description = "updated password", required = true) String newPasswd,
            @Arg(name = "token", description = "password reset token", required = true) String token);

}
