// -----------------------------------------------------------------------------
// Copyright (c) Edifecs Inc. All Rights Reserved.
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

package com.edifecs.contentrepository.test.unit;

import com.edifecs.contentrepository.jackrabbit.ContentLibrary;
import com.edifecs.epp.security.data.Organization;
import com.edifecs.epp.security.data.Tenant;
import com.edifecs.epp.security.data.User;

public class MockCententRepository extends ContentLibrary {
    private Tenant testTenant;
    private User testUser;
    private Organization testOrganization;

    MockCententRepository(Tenant tenant, User user, Organization organization) throws Exception {
        super(null);
        this.testTenant = tenant;
        this.testUser = user;
        this.testOrganization = organization;

        getRepositories().clear();
    }

    MockCententRepository(User user) throws Exception {
        this(null, user, null);
    }

    @Override
    protected Tenant getTenant() {
        return testTenant;
    }

    @Override
    protected User getCurrentUser() {
        return testUser;
    }

    @Override
    protected Organization getOrganization() {
        if(null == testOrganization){
            testOrganization = new Organization();
            testOrganization.setId(11L);
            testOrganization.setCanonicalName("EDFX");
        }
        return testOrganization;
    }

    public void setTestOrganization(Organization testOrganization) {
        this.testOrganization = testOrganization;
    }

    public void simulateUserLogin(User user) {
        this.testUser = user;
    }

    public Tenant getTestTenant() {
        return testTenant;
    }

    public void setTestTenant(Tenant testTenant) {
        this.testTenant = testTenant;
    }

    public User getTestUser() {
        return testUser;
    }

    public void setTestUser(User testUser) {
        this.testUser = testUser;
    }

}
