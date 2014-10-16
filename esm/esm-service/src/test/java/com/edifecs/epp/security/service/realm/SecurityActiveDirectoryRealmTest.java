package com.edifecs.epp.security.service.realm;

import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.data.token.UsernamePasswordAuthenticationToken;
import com.edifecs.epp.security.jpa.helper.DatabaseDataStore;
import com.edifecs.epp.security.service.SecurityContext;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

public class SecurityActiveDirectoryRealmTest {

    private final String realmName = RealmType.ACTIVEDIRECTORY.getVal();
    private final String URL = "ldap://ecadc02.edifecs.local:389";
    private final String domainName = "";
    private final String userName = "XESUser01";

    // private final String URL = "ldap://corp.edifecs.com:389";
    // private final String domainName = "EDFX";
    // private final String userName = "ENG.PlatformUser1";
    // private final String password = "Hc2%jbS6";
    // private final String userSearchBase =
    // "OU=Edifecs,DC=corp,DC=edifecs,DC=com";
    // private final String userFilter =
    // "(&(objectClass=user)(sAMAccountName={0}))";
    // private final String groupSearchBase =
    // "ou=Edifecs,dc=corp,dc=edifecs,dc=com";
    // private final String groupFilter = "(&(objectClass=group)(member={0}))";
    // private final String userGroupName = "ENG.PlatformGroup01";
    // private final String subUserGroupName = "ENG.PlatformGroup02";
    private final String password = "10resUSEX";
    private final String userGroupName = "XESAdmins";
    private final String userSearchBase = "ou=ServiceAccounts,dc=edifecs,dc=local";
    private final String userFilter = "(&(objectClass=user)(sAMAccountName={0}))";
    private final String groupSearchBase = "ou=Groups,dc=edifecs,dc=local";
    private final String groupFilter = "(&(objectClass=group)(member={0}))";
    private Properties ldapProperties = null;
    private DatabaseDataStore dataStore = null;
    private SecurityActiveDirectoryRealm adRealm = null;

    @Before
    public void setUp() throws Exception {
        ldapProperties = new Properties();
        ldapProperties.put(SecurityActiveDirectoryRealm.URL, URL);
        ldapProperties.put(SecurityActiveDirectoryRealm.SYSTEM_USER, userName);
        ldapProperties.put(SecurityActiveDirectoryRealm.SYSTEM_PASS, password);
        ldapProperties
                .put(SecurityActiveDirectoryRealm.DOMAIN_NAME, domainName);
        ldapProperties.put(SecurityActiveDirectoryRealm.USER_SEARCH_BASE,
                userSearchBase);
        ldapProperties
                .put(SecurityActiveDirectoryRealm.USER_FILTER, userFilter);
        ldapProperties.put(SecurityActiveDirectoryRealm.GROUP_SEARCH_BASE,
                groupSearchBase);
        ldapProperties.put(SecurityActiveDirectoryRealm.GROUP_FILTER,
                groupFilter);
        dataStore = new DatabaseDataStore();
        adRealm = new SecurityActiveDirectoryRealm(dataStore, ldapProperties);
    }

    @After
    public void shutDown() {
        dataStore.disconnect();
    }

    @Test
    @Ignore
    public void testSuccessfulAuthentication() throws AuthenticationException {
        AuthenticationInfo authInfo = adRealm
                .getAuthenticationInfo(new UsernamePasswordAuthenticationToken(
                        SystemVariables.DEFAULT_TENANT_NAME, SystemVariables.DEFAULT_ORG_NAME, userName, password
                        .toCharArray()
                ));

        System.out.println("Expected successful Authentication!");
        assertNotNull(authInfo);
        assertNotNull(authInfo.getPrincipals());
        assertEquals(userName, authInfo.getPrincipals().getPrimaryPrincipal());
        assertNotNull(authInfo.getPrincipals().getRealmNames());
        assertTrue(authInfo.getPrincipals().getRealmNames().size() == 2);
        assertTrue(authInfo.getPrincipals().getRealmNames().contains(realmName));

        assertNotNull(dataStore.getUserDataStore().getUserByUsername(
                domainName, userName));
        assertNotNull(dataStore.getUserDataStore()
                .getUserByUsername(domainName, userName).getId());
    }

    @Test(expected = AuthenticationException.class)
    public void testFailedAuthentication() {
        System.out.println("Expected failed Authentication!");
        adRealm.getAuthenticationInfo(new UsernamePasswordAuthenticationToken(
                SystemVariables.DEFAULT_TENANT_NAME, SystemVariables.DEFAULT_ORG_NAME, "wrongName",
                "wrongPassword".toCharArray()));
    }

    @Test
    @Ignore
    public void testSuccessfulAuthorization() throws AuthenticationException {
        try {
            createSMEntities();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        String user = "".equals(domainName) ? userName : domainName + "\\"
                + userName;
        AuthenticationInfo authenInfo = adRealm
                .getAuthenticationInfo(new UsernamePasswordAuthenticationToken(
                        SystemVariables.DEFAULT_TENANT_NAME, SystemVariables.DEFAULT_ORG_NAME, user, password
                        .toCharArray()
                ));
        AuthorizationInfo authorInfo = adRealm
                .doGetAuthorizationInfo(authenInfo);

        System.out.println("Expected successful Authorization!");
        assertNotNull(authorInfo);
        assertTrue(authorInfo.getRoles().size() == 2);
        assertTrue(authorInfo.getRoles().contains("role 1"));
        assertTrue(authorInfo.getRoles().contains("sub role 1"));
        assertTrue(authorInfo.getStringPermissions().size() == 2);
        assertTrue(authorInfo.getStringPermissions().contains(
                "product 1:category 1:type 1:sub type 1:permission 1"));
        assertTrue(authorInfo.getStringPermissions().contains(
                "product 2:category 2:type 2:sub type 2:permission 2"));
    }

    private void createSMEntities() throws Exception {
        Site site = new Site();
        dataStore.getSiteDataStore().create(site, SecurityContext.getCurrentUser());
        Tenant tenant = new Tenant();
        dataStore.getTenantDataStore().create(site.getId(), tenant, SecurityContext.getCurrentUser());

        // Create Group
        UserGroup g = new UserGroup();
        g.setCanonicalName(userGroupName);
        g = dataStore.getUserGroupDataStore().create(site.getId(), g, SecurityContext.getCurrentUser());

        Role r1 = new Role();
        r1.setCanonicalName("role 1");
        r1 = dataStore.getRoleDataStore().create(site.getId(), r1, SecurityContext.getCurrentUser());
        Role sr1 = new Role();
        sr1.setCanonicalName("sub role 1");
        sr1 = dataStore.getRoleDataStore().create(site.getId(), sr1, SecurityContext.getCurrentUser());
        dataStore.getRoleDataStore().addChildRoleToRole(sr1, r1);
        Role r2 = new Role();
        r2.setCanonicalName("role 2");
        r2 = dataStore.getRoleDataStore().create(site.getId(), r2, SecurityContext.getCurrentUser());

        Permission p1 = createPermission(1);
        Permission p2 = createPermission(2);
        Permission p3 = createPermission(3);

        dataStore.getPermissionDataStore().addPermissionToRole(r1, p1);
        dataStore.getPermissionDataStore().addPermissionToRole(sr1, p2);
        dataStore.getPermissionDataStore().addPermissionToRole(r2, p3);
        dataStore.getRoleDataStore().addRoleToGroup(g, r1);
    }

    private Permission createPermission(int i) throws Exception {
        Permission p = new Permission();
        p.setCanonicalName("permission " + i);
        p.setCategoryCanonicalName("category " + i);
        p.setProductCanonicalName("product " + i);
        p.setTypeCanonicalName("type " + i);
        p.setSubTypeCanonicalName("sub type " + i);
        p = dataStore.getPermissionDataStore().create(p, SecurityContext.getCurrentUser());
        return p;
    }

}
