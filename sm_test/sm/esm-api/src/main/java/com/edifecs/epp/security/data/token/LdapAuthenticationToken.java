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
 * LdapAuthenticationToken is used for storing the user name of ldap user.
 * <b>This class should never be used for any kind of authentication</b>.
 */
public class LdapAuthenticationToken implements IAuthenticationToken {

	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	private String domain;

    private String organization;

	/**
	 * The username.
	 */
	private String username;

	/**
	 * Gets the username.
	 * 
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username.
	 * 
	 * @param username
	 *            the new username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Instantiates a new ldap authentication com.edifecs.epp.security.token.
	 * 
	 * @param username
	 *            the username
	 */
	public LdapAuthenticationToken(String domain, String organization, String username) {
		super();
		this.domain = domain;
        this.organization = organization;
		this.username = username;
	}

	@Override
	public Object getPrincipal() {
		return username;
	}

	@Override
	public Object getCredentials() {
		return username;
	}

	@Override
	public boolean isRememberMe() {
		return true;
	}

	@Override
	public String getDomain() {
		return domain;
	}

    public String getOrganization() {
        return organization;
    }

    @Override
	public String getHost() {
		// Unneeded
		return null;
	}

}
