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
package com.edifecs.epp.security.datastore;

/**
 * General interface for any backend service (such as a database) that can be
 * used to store and manipulate user information.
 * 
 * @author willclem
 */
public interface ISecurityDataStore {

	IUserGroupDataStore getUserGroupDataStore();

	IOrganizationDataStore getOrganizationDataStore();

	IPermissionDataStore getPermissionDataStore();

	IRoleDataStore getRoleDataStore();

	ITenantDataStore getTenantDataStore();

	IUserDataStore getUserDataStore();

	ISiteDataStore getSiteDataStore();

	void disconnect();
}
