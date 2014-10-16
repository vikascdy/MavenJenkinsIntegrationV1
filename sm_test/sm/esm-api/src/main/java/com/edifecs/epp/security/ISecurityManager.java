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

/**
 * This is a collection of interfaces that act upon the currently authenticated user. These act on the users session,
 * either locally, or remote depending on the scenario.
 * 
 * @author willclem
 */
public interface ISecurityManager {

    /**
     * @see com.edifecs.epp.security.IAuthenticationManager
     *
     * @return IAuthenticationManager
     */
    IAuthenticationManager getAuthenticationManager();

    /**
     *@see com.edifecs.epp.security.IAuthorizationManager
     *
     * @return IAuthorizationManager
     */
    IAuthorizationManager getAuthorizationManager();

    /**
     * @see com.edifecs.epp.security.ISubjectManager
     *
     * @return ISubjectManager
     */
    ISubjectManager getSubjectManager();

    /**
     * @see com.edifecs.epp.security.ISessionManager
     *
     * @return ISessionManager
     */
    ISessionManager getSessionManager();

}
