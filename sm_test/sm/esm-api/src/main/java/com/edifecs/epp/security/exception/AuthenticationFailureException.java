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
package com.edifecs.epp.security.exception;


/**
 * <p>
 * Used to signify that the user is forbidden by the security framework from
 * performing some action or sending some message. Specifically, there are three
 * possible causes for this com.edifecs.epp.security.exception:
 * </p>
 * 
 * <ul>
 * <li>Authentication failure: A user attempted to log in with invalid
 * credentials.</li>
 * <li>Authorization failure: The current user session was not permitted to
 * perform some function, either because no user is logged in or because the
 * logged-in user does not have the necessary permissions.</li>
 * <li>Connection failure: It was not possible to connect to the security
 * service to retrieve some crucial piece of com.edifecs.epp.security.data, so the user is considered
 * unauthorized by default.</li>
 * </ul>
 * 
 * @author willclem
 */
public class AuthenticationFailureException extends SecurityManagerException {
    private static final long serialVersionUID = 1L;

    @Override
    public String getMessage() {
        return "Authentication Failure: Invalid username and password.";
    }

    public AuthenticationFailureException() {
        super();
    }

    /**
     * 
     * @param cause
     */
    public AuthenticationFailureException(Throwable cause) {
        super(cause);
    }
}
