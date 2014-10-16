package com.edifecs.epp.security.service.handler;

import com.edifecs.epp.security.data.Role;
import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.jpa.helper.DatabaseDataStore;
import com.edifecs.epp.security.service.SecurityContext;
import com.edifecs.epp.security.service.handler.rest.RoleHandler;
import com.edifecs.epp.security.service.handler.rest.TenantHandler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class RoleHandlerTest extends AbstractHandlerTest {

    private static RoleHandler roleHandler;
    
    private static TenantHandler tenantHandler;

    @Before
    public void before() throws Exception {
        SecurityContext sc = new SecurityContext();
        sc.initDataStore(new DatabaseDataStore());
        sc.initManager(new com.edifecs.epp.security.remote.SecurityManager(null, commandCommunicator));
        roleHandler = new RoleHandler(sc);
        roleHandler.initialize(commandCommunicator, commandCommunicator);
        
        tenantHandler = new TenantHandler(sc);
        tenantHandler.initialize(commandCommunicator, commandCommunicator);
    }

    @Test
    public void createRole() throws Exception {
        Role role = new Role();
        role.setCanonicalName("Role Name _ 1");
        role.setDescription("Role Description");

        role = roleHandler.createRole(role);

        Assert.assertNotNull(roleHandler.getRoleById(role.getId()));
    }

    @Test(expected = ItemAlreadyExistsException.class)
    public void createDuplicateRoleInTenantFail() throws Exception {
        Role role = new Role();
        role.setCanonicalName("Role Name _ 2");
        role.setDescription("Role Description");

        role = roleHandler.createRole(role);
        role.setId(null);
        roleHandler.createRole(role);
    }

    
    @Test
    public void testImportRolesFromJson() throws Exception {
        
    	/*Collection<Role> userGroupList = roleHandler.importRoleFromJson(RoleHandlerTest.class.getResourceAsStream("/test_roles_import.json"));

    	for (Role users : userGroupList){
    		System.out.println(users.getCanonicalName());
    	}*/
    	
    	// TODO: Need to change the implementation so that the service layer would return json
    	String jsonResp = roleHandler.importRoleFromJson(RoleHandlerTest.class.getResourceAsStream("/test_roles_import.json"));
    	System.out.println(jsonResp);
    	
    }
    
}
