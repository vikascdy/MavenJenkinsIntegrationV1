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
import com.edifecs.epp.isc.exception.ServiceTypeNotFoundException;
import com.edifecs.epp.security.IAuthenticationManager;
import com.edifecs.epp.security.IAuthorizationManager;
import com.edifecs.epp.security.ISecurityManager;
import com.edifecs.epp.security.ISubjectManager;
import com.edifecs.epp.security.service.ISecurityService;

/**
 * @author willclem
 */
public class SecurityManager implements ISecurityManager {

    private IAuthenticationManager authenticationManager;
    private IAuthorizationManager authorizationManager;
    private ISubjectManager subjectManager;
    private SessionManager sessionManager;

    private org.apache.shiro.mgt.SecurityManager securityManager;

    public SecurityManager(org.apache.shiro.mgt.SecurityManager securityManager, Isc isc)
            throws ServiceTypeNotFoundException {
        this.securityManager = securityManager;

        sessionManager = new SessionManager(isc, securityManager);
        authenticationManager = new AuthenticationManager(isc, securityManager, sessionManager);
        authorizationManager = new AuthorizationManager(isc, securityManager, sessionManager);
        subjectManager = isc.getService(ISecurityService.class).subjects();
    }

    public IAuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public IAuthorizationManager getAuthorizationManager() {
        return authorizationManager;
    }

    public ISubjectManager getSubjectManager() {
        return subjectManager;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

	public org.apache.shiro.mgt.SecurityManager getSecurityManager() {
		return securityManager;
	}

}
