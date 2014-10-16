package com.edifecs.epp.security.service.handler;

import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.data.token.UsernamePasswordAuthenticationToken;
import com.edifecs.epp.security.jpa.helper.DatabaseDataStore;
import com.edifecs.epp.security.service.SecurityContext;
import com.edifecs.epp.security.service.handler.rest.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;



public class UserGroupHandlerTest extends AbstractHandlerTest {

    private static UserHandler userHandler;
    private static UserGroupHandler userGroupHandler;
    private static RoleHandler roleHandler;
    private static OrganizationHandler organizationHandler;
    private static TenantHandler tenantHandler;

    @Before
    public void before() throws Exception {
        SecurityContext sc = new SecurityContext();
        sc.initDataStore(new DatabaseDataStore());
        sc.initManager(new com.edifecs.epp.security.remote.SecurityManager(null, commandCommunicator));
        userHandler = new UserHandler(sc);
        userHandler.initialize(commandCommunicator, commandCommunicator);
        userGroupHandler = new UserGroupHandler(sc);
        userGroupHandler.initialize(commandCommunicator, commandCommunicator);
        roleHandler = new RoleHandler(sc);
        roleHandler.initialize(commandCommunicator, commandCommunicator);
        organizationHandler = new OrganizationHandler(sc);
        organizationHandler.initialize(commandCommunicator, commandCommunicator);
        tenantHandler = new TenantHandler(sc);
        tenantHandler.initialize(commandCommunicator, commandCommunicator);
    }

    @Test
    public void testDeleteGroup() throws Exception {
        Tenant t = new Tenant();
        t.setCanonicalName("tu122");
        t.setDomain("t1");
        t = tenantHandler.createTenant(t);
        Organization org = new Organization();
        org.setCanonicalName("org1");
        org = organizationHandler.createOrganization(org);
        User user = new User();
        user.setActive(true);
        Contact contact = new Contact();
        contact.setFirstName("Test User 1");
        user.setContact(contact);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                SystemVariables.DEFAULT_TENANT_NAME, SystemVariables.DEFAULT_ORG_NAME,
                "admin12318866677", "admin");

        user = userHandler.createUser(user, token, org.getId());

        Role r = new Role();
        r.setCanonicalName("test role 1");
        r = roleHandler.createRole(r);
        roleHandler.addRoleToUser(r, user);

        UserGroup g = new UserGroup();
        g.setCanonicalName(getClass().getSimpleName() + "test group 1");
        g = userGroupHandler.createGroup(g);
        roleHandler.addRoleToGroup(r, g);
        ArrayList<User> users = new ArrayList<>();
        users.add(user);
        userGroupHandler.addUsersToGroup(g, users);

        userGroupHandler.deleteGroup(g.getId());

        Assert.assertTrue(userGroupHandler.getGroupById(g.getId()) == null);
        Assert.assertTrue(roleHandler.getRoleById(r.getId()) != null);
        Assert.assertTrue(r.equals(roleHandler.getRoleById(r.getId())));
        Assert.assertTrue(userHandler.getUserById(user.getId()) != null);
        Assert.assertTrue(user.equals(userHandler.getUserById(user.getId())));
    }

    @Test
    public void addRolesToGroup() throws Exception {
        Tenant t = new Tenant();
        t.setCanonicalName("t123");
        t = tenantHandler.createTenant(t);
        Organization org = new Organization();
        org.setCanonicalName("Org User Group Handler 1");
        org = organizationHandler.createOrganization(org);
        User user = new User();
        user.setActive(true);
        Contact contact = new Contact();
        contact.setFirstName("Test User 1");
        user.setContact(contact);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                SystemVariables.DEFAULT_TENANT_NAME, SystemVariables.DEFAULT_ORG_NAME,
                "admin12314999", "ThereisapasswordI1231141");
        user = userHandler.createUser(user, token, org.getId());
        Role r = new Role();
        r.setCanonicalName("test role 2");
        r = roleHandler.createRole(r);
        roleHandler.addRoleToUser(r, user);
        ArrayList<Role> roles = new ArrayList<Role>();
        roles.add(r);

        UserGroup g = new UserGroup();
        g.setCanonicalName(getClass().getSimpleName() + "test group 2");
        g = userGroupHandler.createGroup(g);

        roleHandler.addRoleToGroup(r, g);
        ArrayList<User> users = new ArrayList<>();
        users.add(user);
        userGroupHandler.addUsersToGroup(g, users);
        roleHandler.addRolesToGroup(roles, g);

        Assert.assertEquals(1, roleHandler.getRolesForGroup(g.getId(), 0, 10).size());

    }

    @Test
    public void addOrgToGroup() throws Exception {

        UserGroup g = new UserGroup();
        g.setCanonicalName(getClass().getSimpleName() + "test group 3");
        g = userGroupHandler.createGroup(g);

        Organization org1 = new Organization();
        org1.setCanonicalName("Org 2");
        org1 = organizationHandler.createOrganization(org1);
        assertNotNull(org1.getId());
        assertNotNull(organizationHandler.getOrganizationById(org1.getId()));

        userGroupHandler.addOrganizationToGroup(g, org1);
        Assert.assertEquals(1,
                organizationHandler.getOrganizationsForGroup(g.getId(), 0, 10)
                        .getTotal()
        );
    }
    
    
   /* @Test
    public void testImportGroupsFromJson() throws Exception {
    	String userGroupList = userGroupHandler.importGroupsFromJson(UserGroupHandlerTest.class.getResourceAsStream("/test_groups_json.json"));
    	System.out.println(userGroupList);
    }*/

}
