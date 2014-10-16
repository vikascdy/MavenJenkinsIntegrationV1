package com.edifecs.epp.security.jpa.helper;

import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.datastore.IOrganizationDataStore;
import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.exception.ItemNotFoundException;
import com.edifecs.epp.security.exception.SecurityDataException;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class OrganizationDataStoreTest extends AbstractDataStoreTest {


    private IOrganizationDataStore orgDataStore = null;

    @Before
    public void setUp() throws Exception {
        orgDataStore = dds.getOrganizationDataStore();
    }

    @Test
    public void testCreateOrganization() {
        try {
            Organization org = new Organization();
            org.setCanonicalName(getClass().getSimpleName() + "Org");
            org = orgDataStore.create(tenant.getId(), org, superUser);
            assertNotNull(org);

            SecurityRealm se = new SecurityRealm();
            se.setEnabled(true);
            se.setName("TEST.ORG.LDAP");
            se.setRealmType(RealmType.LDAP);

            CustomProperty prop = new CustomProperty();
            prop.setName("userProvider");
            prop.setDescription("ldap url");
            prop.setRequired(true);
            prop.setDefaultVal("ldap://192.168.1.1:10389");
            se.getProperties().add(prop);

            orgDataStore.addRealmToOrganization(org.getId(), se);

            for (SecurityRealm sr : orgDataStore.getById(org.getId()).getSecurityRealms()) {
                System.out.println("realm : " + sr.getName());
                sr.setName("Updated");
                System.out.println("sr prop " + sr.getProperties().get(0).getPropertyId());
                System.out.println("updated " + orgDataStore.updateRealm(sr));
            }

            assertTrue(orgDataStore.getRange(0, 10).size() > 0);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testDeleteOrganization() {
        try {
            Organization org = new Organization();
            org.setCanonicalName(getClass().getSimpleName() + "Org 1");
            org = orgDataStore.create(tenant.getId(), org, superUser);
            orgDataStore.delete(org);
            assertNull(orgDataStore.getById(org.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetOrganizationById() {
        try {
            Organization org = new Organization();
            org.setCanonicalName(getClass().getSimpleName() + "Org 2");
            org = orgDataStore.create(tenant.getId(), org, superUser);
            Organization organization = orgDataStore.getById(org.getId());
            assertTrue(null != organization);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetOrganizations() {
        try {
            Organization org = new Organization();
            org.setCanonicalName(getClass().getSimpleName() + "Org 3");
            orgDataStore.create(tenant.getId(), org, superUser);
            List<Organization> organizations = (List<Organization>) orgDataStore.getRange(0, 10);
            assertTrue(organizations.size() > 0);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetTenantByOrganizationId() {
        try {
            Organization org = new Organization();
            org.setCanonicalName(getClass().getSimpleName() + "Org 4");
            Organization o = orgDataStore.create(tenant.getId(), org, superUser);
            Tenant tenant = orgDataStore.getTenantByOrganizationId(o.getId());
            assertNotNull(tenant);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetUserOrganizationByUserId() throws ItemAlreadyExistsException, SecurityDataException, ItemNotFoundException {
        Organization org = new Organization();
        org.setCanonicalName(getClass().getSimpleName() + "Org 5");
        org = orgDataStore.create(tenant.getId(), org, superUser);

        Organization o2 = orgDataStore.getUserOrganizationByUserId(superUser.getId());
        assertNotNull(o2);
    }

    @Test
    public void testGetChildOrganizationsById() {
        try {
            Organization org = new Organization();
            org.setCanonicalName(getClass().getSimpleName() + "Org 6");
            Organization o = orgDataStore.create(tenant.getId(), org, superUser);

            Organization co1 = new Organization();
            co1.setCanonicalName(getClass().getSimpleName() + "child Org 1");
            co1 = orgDataStore.create(tenant.getId(), co1, superUser);
            orgDataStore.addChildOrganization(o.getId(), co1);

            Organization co2 = new Organization();
            co2.setCanonicalName(getClass().getSimpleName() + "child org 2");
            co2 = orgDataStore.create(tenant.getId(), co2, superUser);
            orgDataStore.addChildOrganization(o.getId(), co2);

            Collection<Organization> childOrgs = orgDataStore.getChildOrganizationsById(o.getId());
            assertNotNull(childOrgs);
            assertTrue(childOrgs.size() == 2);
            assertTrue(childOrgs.contains(co1));
            assertTrue(childOrgs.contains(co2));
            Collection<Organization> childOrgs2 = orgDataStore.getChildOrganizationsById(co1.getId());
            assertNotNull(childOrgs2);
            assertTrue(childOrgs2.isEmpty());
            orgDataStore.removeChildOrganization(o.getId(), co2);
            childOrgs = orgDataStore.getChildOrganizationsById(o.getId());
            assertNotNull(childOrgs);
            assertTrue(childOrgs.size() == 1);
            assertTrue(childOrgs.contains(co1));
            assertTrue(!childOrgs.contains(co2));
            assertNull(orgDataStore.getById(co2.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetTransitiveChildOrganizationsById() {
        try {

            Organization org = new Organization();
            org.setCanonicalName(getClass().getSimpleName() + "Org 7");
            Organization o = orgDataStore.create(tenant.getId(), org, superUser);

            Organization co1 = new Organization();
            co1.setCanonicalName(getClass().getSimpleName() + "child Org 3");
            co1 = orgDataStore.create(tenant.getId(), co1, superUser);
            orgDataStore.addChildOrganization(o.getId(), co1);

            Organization cco1 = new Organization();
            cco1.setCanonicalName(getClass().getSimpleName() + "child of Child of org 3");
            cco1 = orgDataStore.create(tenant.getId(), cco1, superUser);
            orgDataStore.addChildOrganization(co1.getId(), cco1);

            Organization ccco1 = new Organization();
            ccco1.setCanonicalName(getClass().getSimpleName() + "cc child of Child of org 3");
            ccco1 = orgDataStore.create(tenant.getId(), ccco1, superUser);
            orgDataStore.addChildOrganization(cco1.getId(), ccco1);

            Organization co2 = new Organization();
            co2.setCanonicalName(getClass().getSimpleName() + "child org 4");
            co2 = orgDataStore.create(tenant.getId(), co2, superUser);
            orgDataStore.addChildOrganization(o.getId(), co2);

            OrganizationDetail orgDetail = orgDataStore
                    .getTransitiveChildOrganizationsForOrganization(o.getId());
            assertNotNull(orgDetail.getChildOrganizations());
            assertTrue(orgDetail.getChildOrganizations().size() == 2);

            orgDataStore.removeChildOrganization(o.getId(), co2);
            orgDetail = orgDataStore
                    .getTransitiveChildOrganizationsForOrganization(o.getId());

            // System.out.println(gson.toJson(orgDetail));
            assertNotNull(orgDetail.getChildOrganizations());
            assertTrue(orgDetail.getChildOrganizations().size() == 1);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
