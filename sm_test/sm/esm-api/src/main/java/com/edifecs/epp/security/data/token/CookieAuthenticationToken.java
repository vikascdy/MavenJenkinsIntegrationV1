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
package com.edifecs.epp.security.data.token;

/**
 * This is sued to try to authenticate and login a user VIA a configured cookie.
 * 
 * @author willclem
 */
public class CookieAuthenticationToken implements IAuthenticationToken {
    private static final long serialVersionUID = 1L;

    private String cookieId;

	public CookieAuthenticationToken(String cookieId) {
		this.cookieId = cookieId;
	}

	public String getCookieId() {
		return cookieId;
	}
	
    @Override
    public Object getPrincipal() {
        // The principle is unneeded as it is looked up automatically VIA the credential.
        return null;
    }

    @Override
    public Object getCredentials() {
        return cookieId;
    }

    @Override
    public boolean isRememberMe() {
        return true;
    }

    @Override
    public String getDomain() {
        throw new UnsupportedOperationException("Domains are not supported for cookie based authentication");
    }

    @Override
    public String getOrganization() {
        throw new UnsupportedOperationException("Domains are not supported for cookie based authentication");
    }

    @Override
    public String getHost() {
        // Unneeded
        return null;
    }
}
