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

import java.util.Date;

public class PasswordResetToken implements IAuthenticationToken {
	private static final long serialVersionUID = 1L;

    private String domain;

    private String organization;

	private Date dateGenerated;

	private Date expiryDate;

	private String token;

	public PasswordResetToken(String token) {
		super();
		this.token = token;
	}

	public PasswordResetToken() {
		super();
	}
	
	public Date getDateGenerated() {
		return dateGenerated;
	}

	public void setDateGenerated(Date dateGenerated) {
		this.dateGenerated = dateGenerated;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

    public String getDomain() {
        throw new UnsupportedOperationException("Domains are not supported for cookie based authentication");
    }

    @Override
    public String getOrganization() {
        throw new UnsupportedOperationException("Domains are not supported for cookie based authentication");
    }

    @Override
    public Object getPrincipal() {
        // The principle is unneeded as it is looked up automatically VIA the credential.
        return null;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public boolean isRememberMe() {
        return false;
    }

    @Override
    public String getHost() {
        // Unneeded
        return null;
    }
	
}
