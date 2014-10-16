/**
 *  -----------------------------------------------------------------------------
 * Copyright (c) Edifecs Inc. All Rights Reserved.
 * This software is the confidential and proprietary information of Edifecs Inc.
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Edifecs.
 *
 * EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
 * ITS DERIVATIVES.
 * -----------------------------------------------------------------------------
 */
package com.edifecs.epp.security.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class SecurityRealm mapps SecurityRealmEntity.
 */
public class SecurityRealm implements Serializable {
	private static final long serialVersionUID = 4467657265836529414L;

	/**
	 * The id.
	 */
	private long id;

	/**
	 * The name.
	 */
	private String name;

	/**
	 * The relam type.
	 */
	private RealmType realmType;

	/**
	 * properties for configuring this realm, like connection settings for JDBC.
	 */
	private List<CustomProperty> properties = new ArrayList<>();
	/**
	 * The enabled .
	 */
	private boolean enabled;

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the realm type.
	 * 
	 * @return the realm type
	 */
	public RealmType getRealmType() {
		return realmType;
	}

	/**
	 * Sets the realm type.
	 * 
	 * @param realmType
	 *            the new realm type
	 */
	public void setRealmType(RealmType realmType) {
		this.realmType = realmType;
	}

	public List<CustomProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<CustomProperty> properties) {
		this.properties = properties;
	}

	/**
	 * Checks if is enabled.
	 * 
	 * @return true, if is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets the enabled.
	 * 
	 * @param enabled
	 *            the new enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
