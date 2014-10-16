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
package com.edifecs.contentrepository.api.model;

import java.io.Serializable;

public class UserEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String SEPARATOR = ":";

    private String username;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String email;
    private AuthLevel level;
    private String userkey;
    private boolean confirmed;

    public UserEntry(final String username, final String passwordHash, final String firstName,
            final String lastName,
            final String email, final AuthLevel level, final String userkey, final boolean confirmed) {
        if (username.matches("~ /[^\\w]/")) {
            throw new IllegalArgumentException(
                    "Username '${username}' contains invalid characters."
                            + " Only letters, numbers, and underscores are allowed.");
        }
        if (firstName.contains(SEPARATOR) || lastName.contains(SEPARATOR)
                || email.contains(SEPARATOR)) {
            throw new IllegalArgumentException("The character '" + SEPARATOR
                    + "' is illegal in all user information.");
        }
        this.username = username;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.level = level;
        this.userkey = userkey;
        this.confirmed = confirmed;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public AuthLevel getLevel() {
        return level;
    }

    public String getUserkey() {
        return userkey;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (confirmed ? 1231 : 1237);
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((level == null) ? 0 : level.hashCode());
        result = prime * result + ((passwordHash == null) ? 0 : passwordHash.hashCode());
        result = prime * result + ((userkey == null) ? 0 : userkey.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UserEntry other = (UserEntry) obj;
        if (confirmed != other.confirmed) {
            return false;
        }
        if (email == null) {
            if (other.email != null) {
                return false;
            }
        } else if (!email.equals(other.email)) {
            return false;
        }
        if (firstName == null) {
            if (other.firstName != null) {
                return false;
            }
        } else if (!firstName.equals(other.firstName)) {
            return false;
        }
        if (lastName == null) {
            if (other.lastName != null) {
                return false;
            }
        } else if (!lastName.equals(other.lastName)) {
            return false;
        }
        if (level != other.level) {
            return false;
        }
        if (passwordHash == null) {
            if (other.passwordHash != null) {
                return false;
            }
        } else if (!passwordHash.equals(other.passwordHash)) {
            return false;
        }
        if (userkey == null) {
            if (other.userkey != null) {
                return false;
            }
        } else if (!userkey.equals(other.userkey)) {
            return false;
        }
        if (username == null) {
            if (other.username != null) {
                return false;
            }
        } else if (!username.equals(other.username)) {
            return false;
        }
        return true;
    }

    @Override
    public final String toString() {
        return username + SEPARATOR
                + passwordHash + SEPARATOR
                + firstName + SEPARATOR
                + lastName + SEPARATOR
                + email + SEPARATOR
                + level.ordinal() + SEPARATOR
                + userkey + SEPARATOR
                + confirmed;
    }
}
