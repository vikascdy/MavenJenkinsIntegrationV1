package com.edifecs.epp.security.jpa.helper;

import com.edifecs.epp.security.data.Role;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class RoleDataStoreTest extends AbstractDataStoreTest {

    private Role role = null;

    @Before
    public void setUp() throws Exception {
        role = new Role();
    }

    @Test
    public void testCreateRole() {
        try {
            role.setCanonicalName(getClass().getSimpleName() + "Role 1");
            assertNotNull(dds.getRoleDataStore().create(tenant.getId(), role, superUser));
            System.out.println("###################### Role "
                    + dds.getRoleDataStore().getRange(0, 10).size());
            assertTrue(dds.getRoleDataStore().getRange(0, 10).size() > 0);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());

        }
    }

    @Test
    public void testGetRoleById() {
        try {
            Role role = new Role();
            role.setCanonicalName(getClass().getSimpleName() + "Role 3");
            role = dds.getRoleDataStore().getById(dds.getRoleDataStore().create(tenant.getId(), role, superUser).getId());
            System.out.println("############### role Name:"
                    + role.getCanonicalName());
            assertTrue(null != role);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetRoles() {
        try {
            Role role = new Role();
            role.setCanonicalName(getClass().getSimpleName() + "Role 4");
            dds.getRoleDataStore().create(tenant.getId(), role, superUser);
            List<Role> roles = (List<Role>) dds.getRoleDataStore().getRange(0,
                    10);
            assertTrue(roles.size() > 0);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
