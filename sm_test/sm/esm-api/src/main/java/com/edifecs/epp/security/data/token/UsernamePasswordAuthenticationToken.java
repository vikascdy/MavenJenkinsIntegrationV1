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

import org.apache.shiro.authc.UsernamePasswordToken;

import java.util.Date;

public class UsernamePasswordAuthenticationToken extends UsernamePasswordToken implements IAuthenticationToken {

    private static final long serialVersionUID = 1L;

    private String domain;

    private String organization;

    private Long id;

    private Date expireDateTime;

    private final String authType = "UsernamePasswordAuthentication";

    public UsernamePasswordAuthenticationToken(String domain, String username, char[] password) {
        super(username, password);
        this.domain = domain;
    }

    public UsernamePasswordAuthenticationToken(String domain, String username, String charArray) {
        super(username, charArray);
        this.domain = domain;
    }

    public UsernamePasswordAuthenticationToken(String domain, String username, String password, boolean rememberMe) {
        super(username, password, rememberMe);
        this.domain = domain;
    }

    public UsernamePasswordAuthenticationToken(String domain, String organization, String username, char[] password) {
		super(username, password);
        this.domain = domain;
        this.organization = organization;
	}

    public UsernamePasswordAuthenticationToken(String domain, String organization, String username, String charArray) {
        super(username, charArray);
        this.domain = domain;
        this.organization = organization;
    }

    public UsernamePasswordAuthenticationToken(String domain, String organization, String username, char[] password, boolean rememberMe) {
        super(username, password, rememberMe);
        this.domain = domain;
        this.organization = organization;
    }

    public UsernamePasswordAuthenticationToken(String domain, String organization, String username, String password, boolean rememberMe) {
        super(username, password, rememberMe);
        this.domain = domain;
        this.organization = organization;
    }

	public String getAuthType() {
        return authType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getExpireDateTime() {
        return expireDateTime;
    }

    public void setExpireDateTime(Date expireDateTime) {
        this.expireDateTime = expireDateTime;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public boolean isRememberMe() {
        return true;
    }

    @Override
    public String getHost() {
        // Unneeded
        return null;
    }

    public String getOrganization() {
        return organization;
    }

}
