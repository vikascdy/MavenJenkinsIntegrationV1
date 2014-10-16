package com.edifecs.epp.security.jpa.helper;

import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.data.token.PasswordResetToken;
import com.edifecs.epp.security.data.token.UsernamePasswordAuthenticationToken;
import com.edifecs.epp.security.datastore.*;
import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.exception.ItemNotFoundException;
import com.edifecs.epp.security.exception.SecurityDataException;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserDataStoreTest extends AbstractDataStoreTest {

    private IUserDataStore userDataStore = null;
    private IPermissionDataStore permissionDataStore = null;
    private IRoleDataStore roleDataStore = null;
    private IOrganizationDataStore organizationDataStore = null;
    private IUserGroupDataStore userGroupDataStore = null;
    private User user;

    @Before
    public void setUp() throws Exception {
        userDataStore = dds.getUserDataStore();
        permissionDataStore = dds.getPermissionDataStore();
        roleDataStore = dds.getRoleDataStore();
        organizationDataStore = dds.getOrganizationDataStore();
        userGroupDataStore = dds.getUserGroupDataStore();

        // Region Api Object
        Region region = new Region();
        region.setCanonicalName("XYZ");

        // Country Api Object
        Country country = new Country();
        country.setCanonicalName("USA");

        // Address Api Object
        Address address = new Address();
        address.setAlternativeExtension("123");
        address.setAlternativePhone("123456");
        address.setCity("ABC");
        address.setExtension("123");
        address.setFax("12345");
        address.setPortalCode("123");
        address.setStreetAddress1("ABC1");
        address.setStreetAddress2("ABC2");
        address.setStreetAddress3("ABC3");
        address.setCountry(country);
        address.setRegion(region);

        List<Address> addresses = new ArrayList<Address>();
        addresses.add(address);
        addresses.add(address);

        // language Api Object
        Language language = new Language();
        language.setCanonicalName("English");

        // TimeZone Api Object
        TimeZone tZone = new TimeZone();
        tZone.setCanonicalName("Pacific");

        // Contact Api Object
        Contact contact = new Contact();
        contact.setEmailAddress("a@a.com");
        contact.setFirstName("Tom");
        contact.setLastName("Harris");
        contact.setMiddleName("Chriss");
        contact.setSalutation("Mr.");
        contact.setPreferredLanguage(language);
        contact.setPreferredTimezone(tZone);
        contact.addAddress(address);

        // User Api Object
        user = new User();
        user.setDeleted(false);
        user.setActive(true);
        user.setLastLoginDateTime(new Date());
        user.setContact(contact);
        user.setHumanUser(true);
    }

    @Test
    public void testCreateUser() throws ItemAlreadyExistsException, SecurityDataException, ItemNotFoundException {


        UserGroup group = new UserGroup();
        group.setCanonicalName(getClass().getSimpleName() + "user group 1");
        UserGroup g = userGroupDataStore.create(tenant.getId(), group, superUser);
        userGroupDataStore.addOrganizationToUserGroup(organization, g);

        UsernamePasswordAuthenticationToken tok = new UsernamePasswordAuthenticationToken(
                tenant.getDomain(), organization.getCanonicalName(), "a_a", "a_a");

        User user1 = userDataStore.create(organization.getId(), user, tok, superUser);

        PasswordResetToken token = new PasswordResetToken();
        token.setDateGenerated(new Date());
        token.setExpiryDate(new Date(System.currentTimeMillis() + 5 * 60 * 1000));

        token.setToken("test");
        user.setId(user1.getId());
        userDataStore.addAuthenticationTokenToUser(user, token);

        assertTrue(userDataStore.getRange(0, 10).size() > 0);

        Role r = new Role();
        r.setCanonicalName(getClass().getSimpleName() + "Test Role");
        roleDataStore.create(tenant.getId(), r, superUser);
        r = roleDataStore.getById(1);
        roleDataStore.addRoleToUser(user1, r);

        System.out.println("users for role " + userDataStore.getUsersForRole(1).size());
        System.out.println("### " + userDataStore.getById(user1.getId()));
        System.out.println("users by group " + userDataStore.getUsersForGroup(g.getId()).size());
        System.out.println("Users by org" + userDataStore.getUsersForOrganization(organization.getId(), 0, 10));
        System.out.println("search by name" + userDataStore.searchUsersByName("a_a", 0, 100));
        System.out.println("search by role " + userDataStore.searchUsersForRole("a_a", r.getId()).size());
    }

    @Test
    public void testGetUserById() {
        try {
            User user1 = userDataStore.getById(1);
            if (user1 == null) {
                user1 = userDataStore.create(organization.getId(), user, superUser);
            }

            assertTrue(user1.getId() != null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetUsers() {
        try {
            Collection<User> users = userDataStore.getRange(0, 20);
            if (users.size() == 0) {
                userDataStore.create(organization.getId(), user, superUser);
            }
            for (User user : userDataStore.getRange(0, 20)) {
                System.out.println(user.toString() + "\r\n");
            }
            assertTrue(userDataStore.getRange(0, 20).size() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetUsersForPermission() {
        try {
            User user1 = userDataStore.getById(1);
            Permission permission1 = permissionDataStore.getById(1);
            if (user1 == null) {
                user1 = userDataStore.create(organization.getId(), user, superUser);
            }
            if (permission1 == null) {
                Permission permission = new Permission();
                permission.setCanonicalName(getClass().getSimpleName() + "Permission 1");
                permission.setCategoryCanonicalName("category 1");
                permission.setProductCanonicalName("product 1");
                permission.setTypeCanonicalName("type 1");
                permission.setSubTypeCanonicalName("sub type 1");
                permission1 = permissionDataStore.create(permission, superUser);
            }
            if (userDataStore.getUsersForPermission(permission1).size() == 0) {
                Role role = new Role();
                role.setCanonicalName(getClass().getSimpleName() + "Role 5");
                Role role1 = roleDataStore.create(tenant.getId(), role, superUser);
                List<User> users1 = userDataStore
                        .getUsersForRole(role1.getId());
                if (users1.size() == 0) {
                    roleDataStore.addRoleToUser(user1, role1);
                }
                assertTrue(userDataStore.getUsersForRole(role1.getId()).size() > 0);

                Collection<Permission> permissions1 = permissionDataStore
                        .getPermissionsForRole(role1);
                if (permissions1.size() == 0) {
                    permissionDataStore.addPermissionToRole(role1, permission1);
                }
                assertTrue(permissionDataStore.getPermissionsForRole(role1).size() > 0);
            }
            assertTrue(userDataStore.getUsersForPermission(permission1).size() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
