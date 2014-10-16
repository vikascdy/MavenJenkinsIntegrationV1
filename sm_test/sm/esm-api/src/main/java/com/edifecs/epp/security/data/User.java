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
package com.edifecs.epp.security.data;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
	private static final long serialVersionUID = 7442626568687602952L;

	private Long id;

	/**
	 * This field is transitive and should never be set. It is used only as a
	 * convenience field for when the user is retrieved as it sits in the
	 * Credential object.
	 */
	private String username;

	private boolean humanUser;

	private boolean active;

	private boolean deleted;

	private boolean suspended;

	private Date createdDateTime;

	private Date modifiedDateTime;

	private Date lastLoginDateTime;

	private Contact contact;

    // FIXME: Should this me here or on the credential itself?
	private boolean changePasswordAtFirstLogin;

	// FIXME: This should be in the contact Object, not the user object
	private String jobTitle;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	/**
	 * This field is transitive and should never be set. It is used only as a
	 * convenience field for when the user is retrieved as it sits in the
	 * Credential object.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * This field is transitive and should never be set. It is used only as a
	 * convenience field for when the user is retrieved as it sits in the
	 * Credential object.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isHumanUser() {
		return humanUser;
	}

	public void setHumanUser(boolean humanUser) {
		this.humanUser = humanUser;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean enabled) {
		this.active = enabled;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Date getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public Date getModifiedDateTime() {
		return modifiedDateTime;
	}

	public void setModifiedDateTime(Date modifiedDateTime) {
		this.modifiedDateTime = modifiedDateTime;
	}

	public Date getLastLoginDateTime() {
		return lastLoginDateTime;
	}

	public void setLastLoginDateTime(Date lastLoginDateTime) {
		this.lastLoginDateTime = lastLoginDateTime;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public boolean isSuspended() {
		return suspended;
	}

	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}

	public boolean isChangePasswordAtFirstLogin() {
		return changePasswordAtFirstLogin;
	}

	public void setChangePasswordAtFirstLogin(boolean changePasswordAtFirstLogin) {
		this.changePasswordAtFirstLogin = changePasswordAtFirstLogin;
	}

	@Override
	public String toString() {
		return String.format("User [id=%d, username=%s, humanUser=%s,"
				+ " enabled=%s, deleted=%s, lastLoginDateTime=%s, contact=%s]",
				id, username, humanUser, active, deleted, lastLoginDateTime,
				contact);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id.hashCode();
		result = prime * result + User.class.getCanonicalName().hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		User other = (User) obj;
		if (id == null || !id.equals(other.id)) {
			return false;
		}
		return true;
	}
}
