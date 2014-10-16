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

/**
 * Permissions contain a unique identifier for a single permission. A permission consists of separate ID's that make up
 * a single unique permission. This is represented as a string like:
 * <p/>
 * <b>product:category:type:subtype:name</b>
 * <p/>
 * Methods are available to convert to and from the Permission and String.
 *
 */
public class Permission implements Comparable<Permission>, Serializable {
	private static final long serialVersionUID = 6128502088636311556L;

	private Long id;

	private String productCanonicalName;

	private String categoryCanonicalName;

	private String typeCanonicalName;

	private String subTypeCanonicalName;

	private String canonicalName;

	private Long sortOrder;

	private String description;

    private RolePermissionType permissionType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProductCanonicalName() {
		return productCanonicalName;
	}

	public void setProductCanonicalName(String productCanonicalName) {
		this.productCanonicalName = productCanonicalName;
	}

	public String getCategoryCanonicalName() {
		return categoryCanonicalName;
	}

	public void setCategoryCanonicalName(String categoryCanonicalName) {
		this.categoryCanonicalName = categoryCanonicalName;
	}

	public String getTypeCanonicalName() {
		return typeCanonicalName;
	}

	public void setTypeCanonicalName(String typeCanonicalName) {
		this.typeCanonicalName = typeCanonicalName;
	}

	public String getSubTypeCanonicalName() {
		return subTypeCanonicalName;
	}

	public void setSubTypeCanonicalName(String subTypeCanonicalName) {
		this.subTypeCanonicalName = subTypeCanonicalName;
	}

	public String getCanonicalName() {
		return canonicalName;
	}

	public void setCanonicalName(String canonicalName) {
		this.canonicalName = canonicalName;
	}

	public Long getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Long sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    /**
     * Converts from String into a permission object
     *
     * @param permissionString String of the format - <b>product:category:type:subtype:name</b>
     * @return Populated Permission Object.
     */
	public static Permission fromString(String permissionString) {

		Permission permission = new Permission();

		// parse permission string
		final String[] permissionArray = permissionString.split(":");
		switch (permissionArray.length) {
		case 5:
			permission.setCanonicalName(permissionArray[4]);
			permission.setSubTypeCanonicalName(permissionArray[3]);
			permission.setTypeCanonicalName(permissionArray[2]);
			permission.setCategoryCanonicalName(permissionArray[1]);
			permission.setProductCanonicalName(permissionArray[0]);
			break;
		case 4:
			permission.setCanonicalName(permissionArray[3]);
			permission.setTypeCanonicalName(permissionArray[2]);
			permission.setCategoryCanonicalName(permissionArray[1]);
			permission.setProductCanonicalName(permissionArray[0]);
			break;
		case 3:
			permission.setCanonicalName(permissionArray[2]);
			permission.setCategoryCanonicalName(permissionArray[1]);
			permission.setProductCanonicalName(permissionArray[0]);
			break;
		case 2:
			permission.setCanonicalName(permissionArray[1]);
			permission.setProductCanonicalName(permissionArray[0]);
			break;
		default:
			throw new SecurityException("'" + permissionString
					+ "' is not a valid permission string.");
		}

		return permission;
	}

    /**
     * Converts the permission object into a string.
     *
     * @return String representing the permission in the format = <b>product:category:type:subtype:name</b>
     */
	@Override
	public String toString() {
		return new StringBuilder().append(getProductCanonicalName())
				.append(":").append(getCategoryCanonicalName()).append(":")
				.append(getTypeCanonicalName()).append(":")
				.append(getSubTypeCanonicalName()).append(":")
				.append(getCanonicalName()).toString();
	}

	@Override
	public int compareTo(Permission other) {
		Long diff = (sortOrder == null ? 0 : sortOrder) - (other.sortOrder == null ? 0 : other.sortOrder);
		if (diff != 0 && getCanonicalName() != null) {
			return getCanonicalName().compareTo(other.getCanonicalName());
		} else {
			return diff.intValue();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((id == null) ? canonicalName.hashCode() : id.hashCode());
		result = prime * result
				+ Permission.class.getCanonicalName().hashCode();
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
		Permission other = (Permission) obj;
		if (id == null || !id.equals(other.id)) {
			return false;
		}
		return true;
	}

    public RolePermissionType getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(RolePermissionType permissionType) {
        this.permissionType = permissionType;
    }
}
