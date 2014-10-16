package com.edifecs.epp.security.service.handler;

import com.edifecs.epp.isc.exception.HandlerConfigurationException;
import com.edifecs.epp.security.data.Permission;
import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.exception.SecurityManagerException;
import com.edifecs.epp.security.jpa.helper.DatabaseDataStore;
import com.edifecs.epp.security.service.SecurityContext;
import com.edifecs.epp.security.service.handler.rest.PermissionHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PermissionHandlerTest extends AbstractHandlerTest {

	private static PermissionHandler permissionHandler;

	@Before
	public void before() throws ItemAlreadyExistsException,
			HandlerConfigurationException, SecurityManagerException {
		SecurityContext sc = new SecurityContext();
		sc.initDataStore(new DatabaseDataStore());
		sc.initManager(new com.edifecs.epp.security.remote.SecurityManager(
				null, commandCommunicator));
		permissionHandler = new PermissionHandler(sc);
		permissionHandler.initialize(commandCommunicator, commandCommunicator);
	}

	@Test
	public void createPermission() throws Exception {
		Permission permission = new Permission();
		permission.setCanonicalName("Name");
		permission.setCategoryCanonicalName("Category");
		permission.setProductCanonicalName("Product");
		permission.setSubTypeCanonicalName("SubType");
		permission.setTypeCanonicalName("Type");

		permission = permissionHandler.createPermission(permission);

		Assert.assertNotNull(permissionHandler.getPermissionById(permission
				.getId()));
	}
}
