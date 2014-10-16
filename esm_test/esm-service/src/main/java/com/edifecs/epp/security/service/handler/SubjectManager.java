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

package com.edifecs.epp.security.service.handler;

import com.edifecs.epp.isc.core.command.AbstractCommandHandler;
import com.edifecs.epp.security.ISubjectManager;
import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.exception.NotAuthenticatedException;
import com.edifecs.epp.security.exception.SecurityDataException;
import com.edifecs.epp.security.exception.SecurityManagerException;
import com.edifecs.epp.security.remote.SecurityManager;
import com.edifecs.epp.security.service.SecurityContext;

import java.util.Collection;

/**
 * @author willclem
 */
public class SubjectManager extends AbstractCommandHandler implements ISubjectManager {
    private final SecurityContext sc;

    public SubjectManager(SecurityContext context) {
        this.sc = context;
    }

    @Override
    public Site getSite() throws SecurityManagerException {
        try {
            return sc.dataStore().getSiteDataStore().getSite();
        } catch (SecurityDataException e) {
            throw new SecurityManagerException(e);
        }
    }

    @Override
    public Tenant getTenant() throws SecurityManagerException {
        try {
            return sc.dataStore().getTenantDataStore()
                    .getTenantByUserId(getUserId());
        } catch (SecurityDataException e) {
            throw new SecurityManagerException(e);
        }
    }

    @Override
    public Organization getOrganization() throws SecurityManagerException {
        try {
            return sc.dataStore().getOrganizationDataStore()
                    .getUserOrganizationByUserId(getUserId());
        } catch (SecurityDataException e) {
            throw new SecurityManagerException(e);
        }
    }

    @Override
    public User getUser() throws SecurityManagerException {
        try {
            return sc.dataStore().getUserDataStore().getUserByUserId(getUserId());
        } catch (SecurityDataException e) {
            throw new SecurityManagerException(e);
        }
    }

    @Override
    public Collection<UserGroup> getUserGroups() throws SecurityManagerException {
        try {
            return sc.dataStore().getUserGroupDataStore().getTransitiveUserGroupsForUser(getUserId());
        } catch (SecurityDataException e) {
            throw new SecurityManagerException(e);
        }
    }

    @Override
    public Long getUserId() throws SecurityManagerException {
        Long userId = sc.manager().getSessionManager().getUserId();
        if (userId == null) {
            throw new NotAuthenticatedException();
        }
        return userId;
    }

    @Override
    public scala.Option<SecurityManager> getReceivingSecurityManager() {
        return scala.Option.apply(sc.manager());
    }
}
