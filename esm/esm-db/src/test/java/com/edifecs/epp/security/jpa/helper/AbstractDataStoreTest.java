package com.edifecs.epp.security.jpa.helper;

import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.data.token.UsernamePasswordAuthenticationToken;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class AbstractDataStoreTest {

    protected static DatabaseDataStore dds = null;
    protected static User superUser = null;
    protected static Organization organization = null;
    protected static Tenant tenant = null;
    protected static Site site = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        dds = new DatabaseDataStore();

        superUser = new User();
        superUser.setActive(true);

        Contact contact = new Contact();
        contact.setFirstName("System");
        contact.setLastName("Test");
        superUser.setContact(contact);
        superUser = dds.getUserDataStore().create(superUser, null);

        site = new Site();
        site = dds.getSiteDataStore().create(site, superUser);

        tenant = new Tenant();
        tenant.setCanonicalName("Default Tenant Test");
        tenant.setDomain("TEST");
        tenant = dds.getTenantDataStore().create(site.getId(), tenant, superUser);

        organization = new Organization();
        organization.setCanonicalName("Default Org Test");
        organization = dds.getOrganizationDataStore().create(tenant.getId(), organization, superUser);
        dds.getUserDataStore().addOrganizationToUser(organization, superUser);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                tenant.getDomain(), organization.getCanonicalName(), "TestUser", "ThisIsATestPassword123"
        );

        dds.getUserDataStore().addAuthenticationTokenToUser(superUser, token);
    }

    @AfterClass
    public static void tearDownAfterClass() {

        if (null != dds)
            dds.disconnect();
    }
}
