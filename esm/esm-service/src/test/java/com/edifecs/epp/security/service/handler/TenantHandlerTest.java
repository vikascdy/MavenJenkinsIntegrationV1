package com.edifecs.epp.security.service.handler;

import com.edifecs.epp.isc.CommandCommunicator;
import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.jpa.helper.DatabaseDataStore;
import com.edifecs.epp.security.remote.SecurityManager;
import com.edifecs.epp.security.service.SecurityContext;
import com.edifecs.epp.security.service.handler.rest.*;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import sun.misc.BASE64Encoder;

import java.io.InputStream;

import static org.junit.Assert.*;

public class TenantHandlerTest extends AbstractHandlerTest {

    private static TenantHandler tenantHandler;
    private static OrganizationHandler organizationHandler;
    private static RoleHandler roleHandler;
    private static UserGroupHandler userGroupHandler;
    private static SiteHandler siteHandler;

    @Before
    public void before() throws Exception {
        SecurityContext sc = new SecurityContext();
        sc.initDataStore(new DatabaseDataStore());
        sc.initManager(new SecurityManager(null, commandCommunicator));
        tenantHandler = new TenantHandler(sc);
        tenantHandler.initialize(commandCommunicator, CommandCommunicator.getInstance());
        organizationHandler = new OrganizationHandler(sc);
        organizationHandler.initialize(commandCommunicator, CommandCommunicator.getInstance());
        siteHandler = new SiteHandler(sc);
        siteHandler.initialize(commandCommunicator, CommandCommunicator.getInstance());
        roleHandler = new RoleHandler(sc);
        roleHandler.initialize(commandCommunicator, CommandCommunicator.getInstance());
        userGroupHandler = new UserGroupHandler(sc);
        userGroupHandler.initialize(commandCommunicator, CommandCommunicator.getInstance());
    }

    @Test
    public void testUpdateTenant() throws Exception {
        Tenant t = new Tenant();
        t.setCanonicalName("t198");
        Organization o = new Organization();
        o.setCanonicalName("o1");

        t = tenantHandler.createTenant(t);
        t.setCanonicalName("t198 updated");
        t = tenantHandler.updateTenant(t);
        assertNotNull(tenantHandler.getTenantById(t.getId()));
        assertEquals("t198 updated", tenantHandler.getTenantById(t.getId())
                .getCanonicalName());
    }

    @Test
    public void testDeleteTenant() throws Exception {
        Tenant t = new Tenant();
        t.setCanonicalName("t1111123");
        Organization o = new Organization();
        o.setCanonicalName("o1");

        t = tenantHandler.createTenant(t);
        tenantHandler.deleteTenant(t.getId());
        assertNull(tenantHandler.getTenantById(t.getId()));
    }

    @Test
    public void testAddGetOrganizationToTenant() throws Exception {
        Tenant t = new Tenant();
        t.setCanonicalName("t22");
        Organization o = new Organization();
        o.setCanonicalName("o2");

        t = tenantHandler.createTenant(t);
        o = organizationHandler.createOrganizationForTenant(t, o);

        assertNotNull(organizationHandler.getOrganizationsForTenant(t.getId(),
                0, 10));
        assertEquals(1,
                organizationHandler.getOrganizationsForTenant(t.getId(), 0, 10)
                        .getTotal()
        );
    }

    @Test
    public void testAddGetRolesToTenant() throws Exception {
        Tenant t = new Tenant();
        t.setCanonicalName("t23");
        Role r = new Role();
        r.setCanonicalName("Role 1");
        r.setDescription("testing");

        t = tenantHandler.createTenant(t);
        r = roleHandler.createRoleForTenant(t, r);

        assertNotNull(roleHandler.getRolesForTenant(t.getId(), 0, 10));
        assertEquals(1, roleHandler.getRolesForTenant(t.getId(), 0, 10)
                .getTotal());
    }

    @Test
    public void testAddGetGroupsToTenant() throws Exception {
        Tenant t = new Tenant();
        t.setCanonicalName("t23333");
        UserGroup g = new UserGroup();
        g.setCanonicalName("Group 1");
        g.setDescription("test group");

        t = tenantHandler.createTenant(t);
        g = userGroupHandler.createGroupForTenant(t, g);

        assertNotNull(userGroupHandler.getGroupsForTenant(t.getId(), 0, 10));
        assertEquals(1, userGroupHandler.getGroupsForTenant(t.getId(), 0, 10)
                .getTotal());
    }

    @Test
    public void testUpdateTenantPasswordPolicy() throws Exception {
        Tenant t = new Tenant();
        t.setCanonicalName("t4");
        PasswordPolicy pp = new PasswordPolicy();
        pp.setChangePasswdAtFirstLogin(true);
        pp.setPasswdAge(50);
        t.setPasswordPolicy(pp);
        t = tenantHandler.createTenant(t);
        pp = t.getPasswordPolicy();
        assertNotNull(pp);

        pp.setPasswdAge(200);
        Tenant upT = tenantHandler.updateTenantPasswordPolicy(t.getId(), pp);
        assertNotNull(upT);
        assertEquals(200, upT.getPasswordPolicy().getPasswdAge());
    }

    @Test
    public void testUpdateTenantLogo() throws Exception {
        Tenant t = new Tenant();
        t.setCanonicalName("tenantWithLogo");
        t = tenantHandler.createTenant(t);

        InputStream logo = getClass().getResourceAsStream("/logo.jpg");

        BASE64Encoder encoder=new BASE64Encoder();
        String data=IOUtils.toString(logo, "UTF-8");
        assertNotNull(logo);

        Boolean resp = tenantHandler.updateTenantLogo(t.getId(), data);
        assertTrue(resp);
        assertNotNull(tenantHandler.getTenantLogo(t.getCanonicalName()));
    }

    
    @Test
    public void testImportTenantFromJson() throws Exception {
    	String OrgList = tenantHandler.importTenantFromJson(TenantHandlerTest.class.getResourceAsStream("/tenant_import_json.json"));
    	System.out.println(OrgList);
    }
}
