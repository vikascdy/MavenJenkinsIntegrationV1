package com.edifecs.epp.security.service.realm;

import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.data.token.UsernamePasswordAuthenticationToken;
import com.edifecs.epp.security.jpa.helper.DatabaseDataStore;
import com.edifecs.epp.security.service.SecurityContext;
import com.edifecs.epp.security.service.handler.AbstractHandlerTest;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.naming.ConfigurationException;
import java.util.Properties;

import static org.junit.Assert.*;

public class SecurityLdapRealmTest extends AbstractHandlerTest  {

    private static final String URL = "ldap://corp.edifecs.com:389";

    private static final String USERNAME = "EDFX\\ENG.PlatformUser1";
    private static final String PASSWORD = "Hc2%jbS6";

    private static final String USER_SEARCH_BASE = "OU=Services,OU=ENG,OU=Service Accounts,OU=Edifecs,DC=corp,DC=edifecs,DC=com";
    private static final String USER_FILTER = "(&(objectClass=user)(sAMAccountName={0}))";
    private static final String GROUP_SEARCH_BASE = "OU=Security Groups,OU=Edifecs,DC=corp,DC=edifecs,DC=com";
    private static final String GROUP_FILTER = "(&(objectClass=group)(member={0}))";
    private static final String TENANT = "LDAPSampleTEN";
    private static final String ORG = "LDAPSampleOrg";
    private static final String REALM_NAME = RealmType.LDAP.getVal();
    private static DatabaseDataStore database = null;
    private final String userGroupName = "ENG.PlatformGroup07";
    private final String subUserGroupName = "ENG.PlatformGroup08";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        database = new DatabaseDataStore();

        Site s = new Site();
        s = database.getSiteDataStore().getSite();

        Tenant t = new Tenant();
        t.setCanonicalName(SystemVariables.DEFAULT_SITE_NAME);
        t.setDomain(TENANT);
        t = database.getTenantDataStore().create(s.getId(), t, null);

        Organization org = new Organization();
        org.setCanonicalName(ORG);
        org.setDescription("Default Organization");
        org = database.getOrganizationDataStore().create(t.getId(), org, null);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        database.disconnect();
    }

    @Test
    public void testLdapConnection() throws Exception {
        Properties ldapProperties = new Properties();
        ldapProperties.put(SecurityLdapRealm.URL, URL);
        ldapProperties.put(SecurityLdapRealm.SYSTEM_USER, USERNAME);
        ldapProperties.put(SecurityLdapRealm.SYSTEM_PASS, PASSWORD);

        ldapProperties.put(SecurityLdapRealm.USER_SEARCH_BASE, USER_SEARCH_BASE);
        ldapProperties.put(SecurityLdapRealm.USER_FILTER, USER_FILTER);
        ldapProperties.put(SecurityLdapRealm.GROUP_SEARCH_BASE, GROUP_SEARCH_BASE);
        ldapProperties.put(SecurityLdapRealm.GROUP_FILTER, GROUP_FILTER);

        ldapProperties.put(SecurityLdapRealm.USER_ORGANIZATION, ORG);
        ldapProperties.put(SecurityLdapRealm.USER_TENANT, TENANT);

        SecurityLdapRealm ldapRealm = new SecurityLdapRealm(database, ldapProperties);

        boolean result = ldapRealm.testLdapConnection(USERNAME, PASSWORD);

        assertTrue(result);
    }

    @Test
    public void testCreateLdapUser() throws Exception {
        Properties ldapProperties = new Properties();
        ldapProperties.put(SecurityLdapRealm.URL, URL);
        ldapProperties.put(SecurityLdapRealm.SYSTEM_USER, USERNAME);
        ldapProperties.put(SecurityLdapRealm.SYSTEM_PASS, PASSWORD);

        ldapProperties.put(SecurityLdapRealm.USER_SEARCH_BASE, USER_SEARCH_BASE);
        ldapProperties.put(SecurityLdapRealm.USER_FILTER, USER_FILTER);
        ldapProperties.put(SecurityLdapRealm.GROUP_SEARCH_BASE, GROUP_SEARCH_BASE);
        ldapProperties.put(SecurityLdapRealm.GROUP_FILTER, GROUP_FILTER);

        ldapProperties.put(SecurityLdapRealm.USER_ORGANIZATION, ORG);
        ldapProperties.put(SecurityLdapRealm.USER_TENANT, TENANT);

        SecurityLdapRealm ldapRealm = new SecurityLdapRealm(database, ldapProperties);

        User user = ldapRealm.createLdapUser(USERNAME);

        assertNotNull(user);
        assertNotNull(user.getContact());
        System.out.println(" First Name : " + user.getContact().getFirstName());
    }

    @Test(expected = ConfigurationException.class)
    public void testLdapConnectionAnonymous() throws Exception {
        Properties ldapProperties = new Properties();
        ldapProperties.put(SecurityLdapRealm.URL, URL);
        ldapProperties.put(SecurityLdapRealm.SYSTEM_USER, "");
        ldapProperties.put(SecurityLdapRealm.SYSTEM_PASS, "");

        ldapProperties.put(SecurityLdapRealm.USER_SEARCH_BASE, USER_SEARCH_BASE);
        ldapProperties.put(SecurityLdapRealm.USER_FILTER, USER_FILTER);
        ldapProperties.put(SecurityLdapRealm.GROUP_SEARCH_BASE, GROUP_SEARCH_BASE);
        ldapProperties.put(SecurityLdapRealm.GROUP_FILTER, GROUP_FILTER);

        ldapProperties.put(SecurityLdapRealm.USER_ORGANIZATION, ORG);
        ldapProperties.put(SecurityLdapRealm.USER_TENANT, TENANT);

        SecurityLdapRealm ldapRealm = new SecurityLdapRealm(database, ldapProperties);

        boolean result = ldapRealm.testLdapConnection(USERNAME, PASSWORD);

        assertTrue(result);
    }

    @Test(expected = ConfigurationException.class)
    public void testLdapConnectionUserFilterFail() throws Exception {
        Properties ldapProperties = new Properties();
        ldapProperties.put(SecurityLdapRealm.URL, URL);
        ldapProperties.put(SecurityLdapRealm.SYSTEM_USER, USERNAME);
        ldapProperties.put(SecurityLdapRealm.SYSTEM_PASS, PASSWORD);

        ldapProperties.put(SecurityLdapRealm.USER_SEARCH_BASE, USER_SEARCH_BASE);
        ldapProperties.put(SecurityLdapRealm.USER_FILTER, "USER_FILTER");
        ldapProperties.put(SecurityLdapRealm.GROUP_SEARCH_BASE, GROUP_SEARCH_BASE);
        ldapProperties.put(SecurityLdapRealm.GROUP_FILTER, GROUP_FILTER);

        ldapProperties.put(SecurityLdapRealm.USER_ORGANIZATION, ORG);
        ldapProperties.put(SecurityLdapRealm.USER_TENANT, TENANT);

        SecurityLdapRealm ldapRealm = new SecurityLdapRealm(database, ldapProperties);

        ldapRealm.testLdapConnection(USERNAME, PASSWORD);
    }

    @Test(expected = ConfigurationException.class)
    public void testLdapConnectionGroupFilterFail() throws Exception {
        Properties ldapProperties = new Properties();
        ldapProperties.put(SecurityLdapRealm.URL, URL);
        ldapProperties.put(SecurityLdapRealm.SYSTEM_USER, USERNAME);
        ldapProperties.put(SecurityLdapRealm.SYSTEM_PASS, PASSWORD);

        ldapProperties.put(SecurityLdapRealm.USER_SEARCH_BASE, USER_SEARCH_BASE);
        ldapProperties.put(SecurityLdapRealm.USER_FILTER, USER_FILTER);
        ldapProperties.put(SecurityLdapRealm.GROUP_SEARCH_BASE, GROUP_SEARCH_BASE);
        ldapProperties.put(SecurityLdapRealm.GROUP_FILTER, "GROUP_FILTER");

        ldapProperties.put(SecurityLdapRealm.USER_ORGANIZATION, ORG);
        ldapProperties.put(SecurityLdapRealm.USER_TENANT, TENANT);

        SecurityLdapRealm ldapRealm = new SecurityLdapRealm(database, ldapProperties);

        ldapRealm.testLdapConnection(USERNAME, PASSWORD);
    }

    @Test(expected = ConfigurationException.class)
    public void testLdapConnectionAuthFail() throws Exception {
        Properties ldapProperties = new Properties();
        ldapProperties.put(SecurityLdapRealm.URL, URL);
        ldapProperties.put(SecurityLdapRealm.SYSTEM_USER, USERNAME);
        ldapProperties.put(SecurityLdapRealm.SYSTEM_PASS, PASSWORD);

        ldapProperties.put(SecurityLdapRealm.USER_SEARCH_BASE, USER_SEARCH_BASE);
        ldapProperties.put(SecurityLdapRealm.USER_FILTER, USER_FILTER);
        ldapProperties.put(SecurityLdapRealm.GROUP_SEARCH_BASE, GROUP_SEARCH_BASE);
        ldapProperties.put(SecurityLdapRealm.GROUP_FILTER, GROUP_FILTER);

        ldapProperties.put(SecurityLdapRealm.USER_ORGANIZATION, ORG);
        ldapProperties.put(SecurityLdapRealm.USER_TENANT, TENANT);

        SecurityLdapRealm ldapRealm = new SecurityLdapRealm(database, ldapProperties);

        ldapRealm.testLdapConnection(USERNAME, "PASSWORD");
    }

    @Test(expected = ConfigurationException.class)
    public void testLdapConnectionUserSrchBaseFail() throws Exception {
        Properties ldapProperties = new Properties();
        ldapProperties.put(SecurityLdapRealm.URL, URL);
        ldapProperties.put(SecurityLdapRealm.SYSTEM_USER, USERNAME);
        ldapProperties.put(SecurityLdapRealm.SYSTEM_PASS, PASSWORD);

        ldapProperties.put(SecurityLdapRealm.USER_SEARCH_BASE, "USER_SEARCH_BASE");
        ldapProperties.put(SecurityLdapRealm.USER_FILTER, USER_FILTER);
        ldapProperties.put(SecurityLdapRealm.GROUP_SEARCH_BASE, GROUP_SEARCH_BASE);
        ldapProperties.put(SecurityLdapRealm.GROUP_FILTER, GROUP_FILTER);

        ldapProperties.put(SecurityLdapRealm.USER_ORGANIZATION, ORG);
        ldapProperties.put(SecurityLdapRealm.USER_TENANT, TENANT);

        SecurityLdapRealm ldapRealm = new SecurityLdapRealm(database,
                ldapProperties);

        ldapRealm.testLdapConnection(USERNAME, "PASSWORD");
    }

    @Test
    @Ignore
    public void testSuccessfulAuthentication() throws Exception {
        Properties ldapProperties = new Properties();
        ldapProperties.put(SecurityLdapRealm.URL, URL);
        ldapProperties.put(SecurityLdapRealm.SYSTEM_USER, USERNAME);
        ldapProperties.put(SecurityLdapRealm.SYSTEM_PASS, PASSWORD);

        ldapProperties.put(SecurityLdapRealm.USER_SEARCH_BASE, USER_SEARCH_BASE);
        ldapProperties.put(SecurityLdapRealm.USER_FILTER, USER_FILTER);
        ldapProperties.put(SecurityLdapRealm.GROUP_SEARCH_BASE, GROUP_SEARCH_BASE);
        ldapProperties.put(SecurityLdapRealm.GROUP_FILTER, GROUP_FILTER);

        ldapProperties.put(SecurityLdapRealm.USER_ORGANIZATION, ORG);
        ldapProperties.put(SecurityLdapRealm.USER_TENANT, TENANT);

        SecurityLdapRealm ldapRealm = new SecurityLdapRealm(database, ldapProperties);

        AuthenticationInfo authInfo = ldapRealm
                .getAuthenticationInfo(new UsernamePasswordAuthenticationToken(
                        SystemVariables.DEFAULT_TENANT_NAME, SystemVariables.DEFAULT_ORG_NAME,
                        USERNAME, PASSWORD));

        // Check Realm Name Reference Works Properly
        assertTrue(authInfo.getPrincipals().fromRealm(REALM_NAME).size() == 1);

        // Check to see if the User Principle is Long
        assertTrue(authInfo.getPrincipals().fromRealm(REALM_NAME).toArray()[0] instanceof String);
    }

    @Test
    @Ignore
    public void testAuthorization() throws Exception {
        Properties ldapProperties = new Properties();
        ldapProperties.put(SecurityLdapRealm.URL, URL);
        ldapProperties.put(SecurityLdapRealm.SYSTEM_USER, USERNAME);
        ldapProperties.put(SecurityLdapRealm.SYSTEM_PASS, PASSWORD);

        ldapProperties.put(SecurityLdapRealm.USER_SEARCH_BASE, USER_SEARCH_BASE);
        ldapProperties.put(SecurityLdapRealm.USER_FILTER, USER_FILTER);
        ldapProperties.put(SecurityLdapRealm.GROUP_SEARCH_BASE, GROUP_SEARCH_BASE);
        ldapProperties.put(SecurityLdapRealm.GROUP_FILTER, GROUP_FILTER);

        ldapProperties.put(SecurityLdapRealm.USER_ORGANIZATION, ORG);
        ldapProperties.put(SecurityLdapRealm.USER_TENANT, TENANT);

        SecurityLdapRealm ldapRealm = new SecurityLdapRealm(database, ldapProperties);

        AuthenticationInfo authInfo = ldapRealm
                .getAuthenticationInfo(new UsernamePasswordAuthenticationToken(
                        SystemVariables.DEFAULT_TENANT_NAME, SystemVariables.DEFAULT_ORG_NAME,
                        USERNAME, PASSWORD));

        assertTrue(authInfo.getPrincipals().oneByType(Long.class) instanceof Long);

        // Check Realm Name Reference Works Properly
        assertTrue(authInfo.getPrincipals().fromRealm(REALM_NAME).size() == 1);

        // Check to see if the User Principle is Long
        assertTrue(authInfo.getPrincipals().fromRealm(REALM_NAME).toArray()[0] instanceof String);

        try {
            createSMEntities();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        AuthorizationInfo authorInfo = ldapRealm
                .doGetAuthorizationInfo(authInfo.getPrincipals());

        assertNotNull(authorInfo);
        assertTrue(authorInfo.getRoles().size() == 2);
        assertTrue(authorInfo.getRoles().contains("ldap testing role 1"));
        assertTrue(authorInfo.getRoles().contains("ldap testing sub role 1"));
        assertTrue(authorInfo.getStringPermissions().size() == 2);
        assertTrue(authorInfo
                .getStringPermissions()
                .contains(
                        "product 1:category 1:type 1:sub type 1:ldap testing permission 1"));
        assertTrue(authorInfo
                .getStringPermissions()
                .contains(
                        "product 2:category 2:type 2:sub type 2:ldap testing permission 2"));
    }

    @Test(expected = AuthenticationException.class)
    public void testPasswordIncorrectFailure() throws Exception {
        Properties ldapProperties = new Properties();
        ldapProperties.put(SecurityLdapRealm.URL, URL);

        ldapProperties.put(SecurityLdapRealm.USER_ORGANIZATION, ORG);
        ldapProperties.put(SecurityLdapRealm.USER_TENANT, TENANT);

        SecurityLdapRealm ldapRealm = new SecurityLdapRealm(database, ldapProperties);

        AuthenticationInfo authInfo = ldapRealm
                .doGetAuthenticationInfo(new UsernamePasswordAuthenticationToken(
                        SystemVariables.DEFAULT_TENANT_NAME, SystemVariables.DEFAULT_ORG_NAME,
                        USERNAME, PASSWORD + "WRONG"));
    }

    @Test(expected = AuthenticationException.class)
    public void testUsernameIncorrectFailure() throws Exception {
        Properties ldapProperties = new Properties();
        ldapProperties.put(SecurityLdapRealm.URL, URL);

        ldapProperties.put(SecurityLdapRealm.USER_ORGANIZATION, ORG);
        ldapProperties.put(SecurityLdapRealm.USER_TENANT, TENANT);

        SecurityLdapRealm ldapRealm = new SecurityLdapRealm(database, ldapProperties);

        AuthenticationInfo authInfo = ldapRealm
                .doGetAuthenticationInfo(new UsernamePasswordAuthenticationToken(
                        SystemVariables.DEFAULT_TENANT_NAME, SystemVariables.DEFAULT_ORG_NAME,
                        USERNAME + "WRONG", PASSWORD));
    }

    @Ignore
    public void systemAccountTest() {
        // LdapContext context = ldapRealm.getContextFactory()
        // .getSystemLdapContext();

        // String seString = "";
        // NamingEnumeration<SearchResult> renum = context.search(
        // "ou=users,ou=system", "(&(objectClass=*)(cn={0}))",
        // searchArguments, controls);
        // // locate this user's record
        // SearchControls controls = new SearchControls();
        // controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        //
        // String searchFilter = "(&(objectClass=*)(member={0}))";
        // String searchBase = "";
        // Object[] searchArguments = new Object[] { ("Andy Singh") };
        //
        // String seString = "";
        // NamingEnumeration<SearchResult> renum = context.search(
        // "ou=users,ou=system", "(&(objectClass=*)(cn={0}))",
        // searchArguments, controls);

        // // NamingEnumeration<SearchResult> renum =
        // // context.search(searchBase,
        // // searchFilter, searchArguments, controls);
        //
        // if (!renum.hasMore()) {
        // System.out.println("Cannot locate user information for "+
        // principalName);
        // System.exit(1);
        // }
        // while (renum.hasMore()) {
        // SearchResult result = renum.next();
        // Attribute grp = result.getAttributes().get("CN");
        // if (null != grp)
        // System.out.println(grp.get().toString());
        // }
        // context.close();
    }

    private void createSMEntities() throws Exception {
        DatabaseDataStore dataStore = database;

        Site site = new Site();
        dataStore.getSiteDataStore().create(site, SecurityContext.getCurrentUser());
        Tenant tenant = new Tenant();
        dataStore.getTenantDataStore().create(site.getId(), tenant, SecurityContext.getCurrentUser());

        // Create Group
        UserGroup g = new UserGroup();
        g.setCanonicalName(userGroupName);
        g = dataStore.getUserGroupDataStore().create(tenant.getId(), g, SecurityContext.getCurrentUser());

        // Create Sub Group
        UserGroup sg = new UserGroup();
        sg.setCanonicalName(subUserGroupName);
        sg = dataStore.getUserGroupDataStore().create(tenant.getId(), sg, SecurityContext.getCurrentUser());
        dataStore.getUserGroupDataStore().addChildGroupToUserGroup(sg, g);

        Role r1 = new Role();
        r1.setCanonicalName("ldap testing role 1");
        r1 = dataStore.getRoleDataStore().create(tenant.getId(), r1, SecurityContext.getCurrentUser());
        Role sr1 = new Role();
        sr1.setCanonicalName("ldap testing sub role 1");
        sr1 = dataStore.getRoleDataStore().create(tenant.getId(), sr1, SecurityContext.getCurrentUser());
        dataStore.getRoleDataStore().addChildRoleToRole(sr1, r1);
        Role r2 = new Role();
        r2.setCanonicalName("ldap testing role 2");
        r2 = dataStore.getRoleDataStore().create(tenant.getId(), r2, SecurityContext.getCurrentUser());

        Permission p1 = createPermission(dataStore, 1);
        Permission p2 = createPermission(dataStore, 2);
        Permission p3 = createPermission(dataStore, 3);

        dataStore.getPermissionDataStore().addPermissionToRole(r1, p1);
        dataStore.getPermissionDataStore().addPermissionToRole(sr1, p2);
        dataStore.getPermissionDataStore().addPermissionToRole(r2, p3);
        dataStore.getRoleDataStore().addRoleToGroup(g, r1);
        dataStore.getRoleDataStore().addRoleToGroup(sg, r2);
    }

    private Permission createPermission(DatabaseDataStore dataStore, int i)
            throws Exception {
        Permission p = new Permission();
        p.setCanonicalName("ldap testing permission " + i);
        p.setCategoryCanonicalName("category " + i);
        p.setProductCanonicalName("product " + i);
        p.setTypeCanonicalName("type " + i);
        p.setSubTypeCanonicalName("sub type " + i);
        p = dataStore.getPermissionDataStore().create(p, SecurityContext.getCurrentUser());
        return p;
    }

}
