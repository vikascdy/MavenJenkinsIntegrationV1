package com.edifecs.epp.security.service.handler;

import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.data.token.LdapAuthenticationToken;
import com.edifecs.epp.security.data.token.UsernamePasswordAuthenticationToken;
import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.jpa.helper.DatabaseDataStore;
import com.edifecs.epp.security.service.SecurityContext;
import com.edifecs.epp.security.service.handler.rest.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertNotNull;

public class UserHandlerTest extends AbstractHandlerTest {

    private static UserHandler userHandler;
    private static RoleHandler roleHandler;
    private static PermissionHandler permissionHandler;
    private static OrganizationHandler organizationHandler;
    private static TenantHandler tenantHandler;

    @Before
    public void before() throws Exception {
        SecurityContext sc = new SecurityContext();
        sc.initDataStore(new DatabaseDataStore());
        sc.initManager(new com.edifecs.epp.security.remote.SecurityManager(null, commandCommunicator));
        userHandler = new UserHandler(sc);
        userHandler.initialize(commandCommunicator, commandCommunicator);

        roleHandler = new RoleHandler(sc);
        roleHandler.initialize(commandCommunicator, commandCommunicator);

        permissionHandler = new PermissionHandler(sc);
        permissionHandler.initialize(commandCommunicator, commandCommunicator);

        organizationHandler = new OrganizationHandler(sc);
        organizationHandler.initialize(commandCommunicator, commandCommunicator);

        tenantHandler = new TenantHandler(sc);
        tenantHandler.initialize(commandCommunicator, commandCommunicator);

    }

    @Test
    public void createUser() throws Exception {
        Tenant t = new Tenant();
        t.setCanonicalName("t1444");
        t = tenantHandler.createTenant(t);
        Organization org = new Organization();
        org.setCanonicalName("Org User Handler Test 5");
        org = organizationHandler.createOrganization(org);
        User user = new User();
        user.setActive(true);
        Contact contact = new Contact();
        contact.setFirstName("Test User 1");
        user.setContact(contact);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                SystemVariables.DEFAULT_TENANT_NAME, SystemVariables.DEFAULT_ORG_NAME,
                "admin12317", "ThereisapasswordI1231141");
        user = userHandler.createUser(user, token, org.getId());
        Assert.assertFalse(user.isDeleted());
        user.setActive(false);

        System.out.println("updated " + userHandler.updateUser(user));
        System.out.println(userHandler.getUserById(user.getId()).isActive());

    }

    @Test
    public void createUsersForSameTenantFail() throws Exception {
        Tenant t = new Tenant();
        t.setCanonicalName("t1678");
        t = tenantHandler.createTenant(t);
        Organization org = new Organization();
        org.setCanonicalName("Org User Handler Test 2");
        org = organizationHandler.createOrganization(org);
        User user = new User();
        user.setActive(true);
        Contact contact = new Contact();
        contact.setFirstName("Test User 1");
        user.setContact(contact);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                SystemVariables.DEFAULT_TENANT_NAME, SystemVariables.DEFAULT_ORG_NAME,
                "admin123189", "ThereisapasswordI1231141");
        user = userHandler.createUser(user, token, org.getId());
        try {
            user.setId(null);
            user = userHandler.createUser(user, token, org.getId());
            Assert.fail();
        } catch (ItemAlreadyExistsException e) {
        }
    }

    @Test
    public void createLdapUser() throws Exception {
        Tenant t = new Tenant();
        t.setCanonicalName("t13425");
        t.setDomain("test");
        t = tenantHandler.createTenant(t);
        Organization org = new Organization();
        org.setCanonicalName("Org User Handler Test 6");
        org = organizationHandler.createOrganization(org);
        User user = new User();
        user.setActive(true);
        Contact contact = new Contact();
        contact.setFirstName("Test User 1");
        user.setContact(contact);

        LdapAuthenticationToken token = new LdapAuthenticationToken(
                t.getDomain(),
                SystemVariables.DEFAULT_ORG_NAME,
                "testUser");
        user = userHandler.createLdapUser(user, token, org.getId());
        Assert.assertFalse(user.isDeleted());
        user.setActive(false);

        System.out.println("updated " + userHandler.updateUser(user));
        System.out.println(userHandler.getUserById(user.getId()).isActive());

    }

    @Test
    public void createCertificateUser() throws Exception {
        Tenant t = new Tenant();
        t.setCanonicalName("t11");
        t.setDomain("test1");
        t = tenantHandler.createTenant(t);
        Organization org = new Organization();
        org.setCanonicalName("org11");
        org = organizationHandler.createOrganization(org);
        User user = new User();
        user.setActive(true);
        Contact contact = new Contact();
        contact.setFirstName("Test User 11");
        user.setContact(contact);

        user = userHandler.createCertificateUser(user, t.getDomain(), org.getCanonicalName(),
                "certificate will go here", org.getId(),"TestUsername1");
        Assert.assertFalse(user.isDeleted());
        user.setActive(false);

        System.out.println("updated " + userHandler.updateUser(user));
        System.out.println(userHandler.getUserById(user.getId()).isActive());

    }

    @Test
    public void deleteUser() throws Exception {
        Tenant t = new Tenant();
        t.setCanonicalName("t232");
        t = tenantHandler.createTenant(t);
        Organization org = new Organization();
        org.setCanonicalName("org2");
        org = organizationHandler.createOrganizationForTenant(t, org);
        User user = new User();
        user.setActive(true);
        Contact contact = new Contact();
        contact.setFirstName("Test User 11");
        user.setContact(contact);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                SystemVariables.DEFAULT_TENANT_NAME, SystemVariables.DEFAULT_ORG_NAME,
                "admin123157", "ThereisapasswordI12311451");
        user = userHandler.createUser(user, token, org.getId());
        Assert.assertFalse(user.isDeleted());
        user.setActive(false);

        System.out.println("updated " + userHandler.updateUser(user));
        System.out.println(userHandler.getUserById(user.getId()).isActive());

        Assert.assertNotNull(userHandler.getUserById(user.getId()));
        userHandler.deleteUser(user.getId());
        Assert.assertNull(userHandler.getUserById(user.getId()));
    }

    @Test
    public void testGetUsersForPermission() throws Exception {
        Role role = new Role();
        role.setCanonicalName("Role New 1");
        role.setDescription("Role Description 1");
        role = roleHandler.createRole(role);
        Permission permission = new Permission();
        permission.setCanonicalName("Permission 1");
        permission.setCategoryCanonicalName("Category 1");
        permission.setProductCanonicalName("Product 1");
        permission.setSubTypeCanonicalName("SubType 1");
        permission.setTypeCanonicalName("Type 1");
        permission = permissionHandler.createPermission(permission);
        permissionHandler.addPermissionToRole(permission, role);

        Tenant t = new Tenant();
        t.setCanonicalName("t1456");
        t = tenantHandler.createTenant(t);
        Organization org = new Organization();
        org.setCanonicalName("Org User Handler Test 1");
        org = organizationHandler.createOrganization(org);
        User user = new User();
        user.setActive(true);
        Contact contact = new Contact();
        contact.setFirstName("Test User 1");
        user.setContact(contact);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                SystemVariables.DEFAULT_TENANT_NAME, SystemVariables.DEFAULT_ORG_NAME,
                "admin1231", "ThereisapasswordI1231141");

        user = userHandler.createUser(user, token, org.getId());
        roleHandler.addRoleToUser(role, user);
        String permissionString = "Product 1:Category 1:Type 1:SubType 1:Permission 1";
        Collection<User> users = userHandler.getUsersForPermission(
                permissionString, 0L, 10L).getResultList();
        Assert.assertTrue(users.size() == 1);
        for (User u : users) {
            Assert.assertTrue(u.equals(user));
        }
        Assert.assertTrue(userHandler.getUsersForPermission(
                "Product 1:Category 1:Type 1:SubType 1:", 0, 10).getTotal() == 0);
    }

    @Test
    public void addRolesToUser() throws Exception {

        Tenant t = new Tenant();
        t.setCanonicalName("t431");
        t = tenantHandler.createTenant(t);
        Organization org = new Organization();
        org.setCanonicalName("Org User Handler Test 3");
        org = organizationHandler.createOrganization(org);
        User user = new User();
        user.setActive(true);
        Contact contact = new Contact();
        contact.setFirstName("Test User 1");
        user.setContact(contact);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                SystemVariables.DEFAULT_TENANT_NAME, SystemVariables.DEFAULT_ORG_NAME,
                "admin12314", "ThereisapasswordI1231141");
        user = userHandler.createUser(user, token, org.getId());

        Role r = new Role();
        r.setCanonicalName("test role __ 2");
        r = roleHandler.createRole(r);
        roleHandler.addRoleToUser(r, user);
        ArrayList<Role> roles = new ArrayList<Role>();
        roles.add(r);

        roleHandler.addRolesToUser(roles, user);
        Assert.assertEquals(1, roleHandler.getRolesForUser(user.getId(), 0, 10)
                .getTotal());

    }

    @Test
    public void addUserToOrg() throws Exception {
        Tenant tenant = new Tenant();
        tenant.setCanonicalName("tenant 1345");
        tenant = tenantHandler.createTenant(tenant);
        assertNotNull(tenant.getId());

        Organization org = new Organization();
        org.setCanonicalName("Org 11");
        org = organizationHandler.createOrganization(org);
        assertNotNull(org.getId());
        assertNotNull(organizationHandler.getOrganizationById(org.getId()));

        User user = new User();
        user.setActive(true);
        Contact contact = new Contact();
        contact.setFirstName("Test User 2");
        user.setContact(contact);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                SystemVariables.DEFAULT_TENANT_NAME, SystemVariables.DEFAULT_ORG_NAME,
                "test", "test");
        user = userHandler.createUser(user, token, org.getId());
        Assert.assertEquals(1,
                userHandler.getUsersForOrganization(org.getId(), 0, 25)
                        .getTotal()
        );

        assertNotNull(userHandler.getCredentialForUser(user.getId()));
        Assert.assertEquals(1, userHandler.getCredentialForUser(user.getId())
                .size());

        userHandler.deleteUser(user.getId());
        for (User u : userHandler.getUsersForOrganization(org.getId(), 0, 25)
                .getResultList()) {
            if (u.getId() == user.getId())
                Assert.assertFalse(true);
        }
    }

    @Test
    public void testRegexPattern() {
        String regex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        String seed = "test@test.com";
        Assert.assertTrue(userHandler.matchPattern(regex, seed));

        seed = seed.replace("@", " ");
        Assert.assertFalse(userHandler.matchPattern(regex, seed));
    }
    
    
   @Test
    public void testImportUsersFromJson() throws Exception {
	   
    	String userList = userHandler.importUsersJson(UserGroupHandlerTest.class.getResourceAsStream("/user_import_json_file.json"));
    	System.out.println(userList);
    }
}
