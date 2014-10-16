package com.edifecs.epp.security.jpa.helper;

import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.data.token.UsernamePasswordAuthenticationToken;
import com.edifecs.epp.security.datastore.IUserGroupDataStore;
import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.exception.ItemNotFoundException;
import com.edifecs.epp.security.exception.SecurityDataException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class UserGroupDataStoreTest extends AbstractDataStoreTest {

    private UserGroup group = null;
    private IUserGroupDataStore userGroupDataStore = null;

    @Before
    public void setUp() throws Exception {
        userGroupDataStore = dds.getUserGroupDataStore();
        group = new UserGroup();
        // group.setTenant(tenant);
        group.setMaximumUsers(new Long(10));
    }

    @Test
    public void testCreateGroup() {
        try {
            group.setCanonicalName(getClass().getSimpleName() + "Group 1");
            assertNotNull(userGroupDataStore.create(tenant.getId(), group, superUser));
            assertTrue(userGroupDataStore.getRange(0, 10).size() > 0);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetGroups() {
        try {
            group.setCanonicalName(getClass().getSimpleName() + "Group 2");
            userGroupDataStore.create(tenant.getId(), group, superUser);
            List<UserGroup> groups = (List<UserGroup>) userGroupDataStore.getRange(0, 10);
            assertTrue(groups.size() > 0);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetGroupById() throws Exception {
        try {
            group.setCanonicalName(getClass().getSimpleName() + "Group 3");
            UserGroup newGroup = userGroupDataStore.create(tenant.getId(), group, superUser);
            newGroup = userGroupDataStore.getById(newGroup.getId());

            System.out.println("############### Grp Name:" + newGroup.getCanonicalName());
            assertTrue(null != newGroup);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testDeleteGroup() throws ItemAlreadyExistsException, SecurityDataException, ItemNotFoundException {
        Tenant t = new Tenant();
        t.setCanonicalName(getClass().getSimpleName() + "T1");
        t.setDomain("testDeleteGroupTenant");
        t = dds.getTenantDataStore().create(site.getId(), t, superUser);

        Organization org = new Organization();
        org.setCanonicalName(getClass().getSimpleName() + "Org1");
        org = dds.getOrganizationDataStore().create(t.getId(), org, superUser);

        UserGroup g = new UserGroup();
        g.setCanonicalName(getClass().getSimpleName() + "test group 1");
        g = dds.getUserGroupDataStore().create(t.getId(), g, superUser);

        Role r = new Role();
        r.setCanonicalName(getClass().getSimpleName() + "test role 1");
        r = dds.getRoleDataStore().create(t.getId(), r, superUser);

        User u = new User();
        u.setActive(true);
        Contact contact = new Contact();
        contact.setFirstName("test user 1");
        u.setContact(contact);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                t.getDomain(), org.getCanonicalName(), "admin12344", "pass123455");

        u = dds.getUserDataStore().create(org.getId(), u, token, superUser);

        System.out.println("###################### Group "
                + dds.getUserGroupDataStore().getRange(0, 10).size());

        dds.getRoleDataStore().addRoleToUser(u, r);
        dds.getRoleDataStore().addRoleToGroup(g, r);
        userGroupDataStore.addUserToUserGroup(u, g);
        userGroupDataStore.delete(g);

        assertTrue(userGroupDataStore.getById(g.getId()) == null);
        assertTrue(dds.getRoleDataStore().getById(r.getId()) != null);
        assertTrue(r.equals(dds.getRoleDataStore().getById(r.getId())));
        assertTrue(dds.getUserDataStore().getById(u.getId()) != null);
        assertTrue(u.equals(dds.getUserDataStore().getById(u.getId())));
    }

}
