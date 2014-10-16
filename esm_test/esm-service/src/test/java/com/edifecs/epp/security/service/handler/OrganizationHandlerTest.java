package com.edifecs.epp.security.service.handler;

import com.edifecs.epp.security.data.Organization;
import com.edifecs.epp.security.data.Role;
import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.jpa.helper.DatabaseDataStore;
import com.edifecs.epp.security.service.SecurityContext;
import com.edifecs.epp.security.service.handler.rest.OrganizationHandler;
import com.edifecs.epp.security.service.handler.rest.RoleHandler;
import com.edifecs.epp.security.service.handler.rest.TenantHandler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;



public class OrganizationHandlerTest extends AbstractHandlerTest {

    private OrganizationHandler organizationHandler;
    private RoleHandler roleHandler;
    private TenantHandler tenantHandler;

    @Before
    public void before() throws Exception {
        SecurityContext sc = new SecurityContext();
        sc.initDataStore(new DatabaseDataStore());
        sc.initManager(new com.edifecs.epp.security.remote.SecurityManager(null, commandCommunicator));
        organizationHandler = new OrganizationHandler(sc);
        organizationHandler.initialize(commandCommunicator, commandCommunicator);
        roleHandler = new RoleHandler(sc);
        roleHandler.initialize(commandCommunicator, commandCommunicator);
        
        tenantHandler = new TenantHandler(sc);
        tenantHandler.initialize(commandCommunicator, commandCommunicator);
    }

    @Test
    public void testCreateOrganization() throws Exception {
        Organization org = new Organization();
        org.setCanonicalName("ABC");
        org = organizationHandler.createOrganization(org);
        assertNotNull(org.getId());
        assertNotNull(organizationHandler.getOrganizationById(org.getId()));
    }

    @Test
    public void testGetChildOrganizationsById() throws Exception {

        Organization org = new Organization();
        org.setCanonicalName("Org Handler Test 1");
        org = organizationHandler.createOrganization(org);
        Organization childOrg1 = new Organization();

        childOrg1.setCanonicalName("Child Org 1");
        childOrg1 = organizationHandler.createOrganization(childOrg1);
        organizationHandler.addChildOrganization(org.getId(), childOrg1.getId());

        Organization childOrg2 = new Organization();
        childOrg2.setCanonicalName("Child Org 2");
        childOrg2 = organizationHandler.createOrganization(childOrg2);
        organizationHandler.addChildOrganization(org.getId(), childOrg2.getId());

        Collection<Organization> childOrgs = organizationHandler.getChildOrganizationsById(org.getId());
        assertNotNull(childOrgs);
        assertTrue(childOrgs.size() == 2);
        assertTrue(childOrgs.contains(childOrg1));
        assertTrue(childOrgs.contains(childOrg2));

        organizationHandler.removeChildOrganization(org.getId(), childOrg2.getId());
        childOrgs = organizationHandler.getChildOrganizationsById(org.getId());
        assertNotNull(childOrgs);
        assertTrue(childOrgs.size() == 1);
        assertTrue(childOrgs.contains(childOrg1));
        assertTrue(!childOrgs.contains(childOrg2));
    }

    @Test
    public void addRolesToOrg() throws Exception {
        Role role = new Role();
        role.setCanonicalName("Role Name");
        role.setDescription("Role Description");
        role = roleHandler.createRole(role);
        Assert.assertNotNull(roleHandler.getRoleById(role.getId()));

        Organization org = new Organization();
        org.setCanonicalName("Org 1");
        org = organizationHandler.createOrganization(org);
        assertNotNull(org.getId());
        assertNotNull(organizationHandler.getOrganizationById(org.getId()));

        organizationHandler.addRoleToOrganization(org.getId(), role.getId());
        assertEquals(1, roleHandler.getRolesForOrganization(org.getId(), 0, 10).getTotal());
    }

    @Test(expected = ItemAlreadyExistsException.class)
    public void testSingleParent() throws Exception {

        Organization parent_org = new Organization();
        parent_org.setCanonicalName("Parent 1");
        parent_org = organizationHandler.createOrganization(parent_org);

        Organization org = new Organization();
        org.setCanonicalName("Parent 2");
        org = organizationHandler.createOrganization(org);
        Organization childOrg1 = new Organization();

        childOrg1.setCanonicalName("Child Org 11");
        childOrg1 = organizationHandler.createOrganization(childOrg1);

        organizationHandler.addChildOrganization(org.getId(), childOrg1.getId());
        organizationHandler.addChildOrganization(parent_org.getId(), childOrg1.getId());
    }
    
    
    @Test
    public void testImportOrganizationFromJson() throws Exception {
	   
    	String OrgList = organizationHandler.importOrganizationFromJson(OrganizationHandlerTest.class.getResourceAsStream("/test_org_import.json"));
    	System.out.println(OrgList);
    }

}
