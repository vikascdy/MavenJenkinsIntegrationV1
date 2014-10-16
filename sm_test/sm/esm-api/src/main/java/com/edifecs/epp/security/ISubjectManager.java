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

package com.edifecs.epp.security;

import com.edifecs.epp.isc.annotations.CommandHandler;
import com.edifecs.epp.isc.annotations.SyncCommand;
import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.exception.SecurityManagerException;

import java.util.Collection;

/**
 * Contains all the methods to get information about the currently authenticated user. This is used to get back
 * information on the Site, Tenant, Organization, Groups and the User information configured in the security component.
 *
 * @author willclem
 */
@CommandHandler(namespace = "subject")
public interface ISubjectManager {

    /**
     * @return The Site that the user belongs too
     * @throws SecurityManagerException
     */
    @SyncCommand
    Site getSite() throws SecurityManagerException;

    /**
     * @return The Tenant that the user belongs too
     * @throws SecurityManagerException
     */
    @SyncCommand
    Tenant getTenant() throws SecurityManagerException;

    /**
     * @return The Organization that the user belongs too
     * @throws SecurityManagerException
     */
    @SyncCommand
    Organization getOrganization() throws SecurityManagerException;

    /**
     * @return The current logged in User.
     * @throws SecurityManagerException
     */
    @SyncCommand
    User getUser() throws SecurityManagerException;

    /**
     * @return A list of User Groups that the current User belongs too
     * @throws SecurityManagerException
     */
    @SyncCommand
    Collection<UserGroup> getUserGroups() throws SecurityManagerException;

    /**
     * @return The UserID of the currently logged in user
     * @throws SecurityManagerException
     */
    @SyncCommand
    Long getUserId() throws SecurityManagerException;
}
