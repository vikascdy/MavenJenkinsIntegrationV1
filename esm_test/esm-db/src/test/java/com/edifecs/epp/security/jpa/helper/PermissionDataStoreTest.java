package com.edifecs.epp.security.jpa.helper;

import com.edifecs.epp.security.data.Permission;
import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.exception.ItemNotFoundException;
import com.edifecs.epp.security.exception.SecurityDataException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class PermissionDataStoreTest extends AbstractDataStoreTest {

    private Permission permission = null;

    @Before
    public void setUp() throws Exception {
        permission = new Permission();
        permission.setProductCanonicalName("Product 1");
        permission.setCategoryCanonicalName("Category 1");
        permission.setTypeCanonicalName("Type 1");
        permission.setSubTypeCanonicalName("SubType 1");
        permission.setSortOrder(1L);
    }

    @Test
    public void testCreatePermission() {
        try {
            permission.setCanonicalName(getClass().getSimpleName() + "Perm 1");
            assertNotNull(dds.getPermissionDataStore().create(permission, superUser));
            System.out.println("###################### Perm "
                    + dds.getPermissionDataStore().getRange(0, 10).size());
            assertTrue(dds.getPermissionDataStore().getRange(0, 10).size() > 0);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());

        }
    }

    @Test
    public void testGetPermissionById() throws ItemAlreadyExistsException,
            SecurityDataException, ItemNotFoundException {
        permission.setCanonicalName(getClass().getSimpleName() + "Test 2");
        permission = dds.getPermissionDataStore().create(permission, superUser);
        permission = dds.getPermissionDataStore().getById(permission.getId());
        assertTrue(null != permission);
        System.out.println("############### permission Name:"
                + permission.getCanonicalName());
    }

    @Test
    public void testGetPermissions() throws ItemAlreadyExistsException,
            SecurityDataException {
        permission.setCanonicalName(getClass().getSimpleName() + "Test 3");
        dds.getPermissionDataStore().create(permission, superUser);
        List<Permission> permissions = (List<Permission>) dds
                .getPermissionDataStore().getRange(0, 10);
        assertTrue(permissions.size() > 0);
    }
}
