package com.edifecs.epp.security.jpa.helper;

import com.edifecs.epp.security.data.Organization;
import com.edifecs.epp.security.data.Tenant;
import com.edifecs.epp.security.datastore.IOrganizationDataStore;
import com.edifecs.epp.security.datastore.ITenantDataStore;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TenantDataStoreTest extends AbstractDataStoreTest {

    private ITenantDataStore tenantDataStore = null;
    private IOrganizationDataStore orgDataStore = null;

    @Before
    public void setUp() throws Exception {
        tenantDataStore = dds.getTenantDataStore();
        orgDataStore = dds.getOrganizationDataStore();
    }

    @Test
    public void testCreate() {
        try {
            Tenant tenant = new Tenant();
            tenant.setCanonicalName(getClass().getSimpleName() + ":Tenant 1");
            Tenant t = tenantDataStore.create(site.getId(), tenant, superUser);
            t.setDomain("Test");
            assertNotNull(t.getId());
            assertNotNull(tenantDataStore.getById(t.getId()));
            assertEquals(getClass().getSimpleName() + ":Tenant 1", t.getCanonicalName());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdate() {
        try {
            Tenant t = new Tenant();
            t.setCanonicalName(getClass().getSimpleName() + ":Tenant 2");
            t = tenantDataStore.create(site.getId(), t, superUser);
            t.setCanonicalName(getClass().getSimpleName() + ":Tenant 2 updated");
            tenantDataStore.update(site.getId(), t, superUser);

            assertNotNull(tenantDataStore.getById(t.getId()));
            assertEquals(getClass().getSimpleName() + ":Tenant 2 updated", tenantDataStore.getById(t.getId())
                    .getCanonicalName());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testDelete() {
        try {
            Tenant t = new Tenant();
            t.setCanonicalName(getClass().getSimpleName() + ":Tenant 3");
            t = tenantDataStore.create(site.getId(), t, superUser);
            tenantDataStore.delete(t);
            assertNull(tenantDataStore.getById(t.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testAddOrganizationToTenant() {
        try {
            Organization o = new Organization();
            o.setCanonicalName(getClass().getSimpleName() + ":Org 1");
            o = orgDataStore.create(tenant.getId(), o, superUser);
            assertNotNull(orgDataStore.getTenantByOrganizationId(o.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
