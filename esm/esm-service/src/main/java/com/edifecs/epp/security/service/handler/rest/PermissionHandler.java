package com.edifecs.epp.security.service.handler.rest;

import com.edifecs.epp.isc.core.command.Pagination;
import com.edifecs.epp.security.data.PaginatedList;
import com.edifecs.epp.security.data.Permission;
import com.edifecs.epp.security.data.Role;
import com.edifecs.epp.security.data.User;
import com.edifecs.epp.security.handler.rest.IPermissionHandler;
import com.edifecs.epp.security.service.SecurityContext;

import java.util.ArrayList;
import java.util.Collection;

public class PermissionHandler extends AbstractSecurityRestHandler<Permission>
        implements IPermissionHandler {

    public PermissionHandler(SecurityContext context) {
        super(context, Permission.class);
    }

    @Override
    public Permission get(String url) throws Exception {
        final long id = idFromUrl(url);
        return sc.dataStore().getPermissionDataStore().getById(id);
    }

    @Override
    public Collection<Permission> list(Pagination pg) throws Exception {
        final Collection<Permission> permissions;
        if (pg.limit() == 0)
            permissions = sc.dataStore().getPermissionDataStore().getAll();
        else
            permissions = sc.dataStore().getPermissionDataStore()
                    .getRange(pg.start(), pg.limit());
        return sort(permissions, pg.sorters());
    }

    @Override
    public Permission post(Permission permission) throws Exception {
        return createPermission(permission);
    }

    @Override
    public Permission put(String url, Permission permission) throws Exception {
        final long id = idFromUrl(url);
        if (permission.getId() != id)
            permission.setId(id);
        return updatePermission(permission);
    }

    @Override
    public void delete(String url) throws Exception {
        final long id = idFromUrl(url);
        final Permission permission = new Permission();
        permission.setId(id);
        sc.dataStore().getPermissionDataStore().delete(permission);
    }

    public Permission createPermission(Permission permission) throws Exception {
        Permission perm = sc.dataStore().getPermissionDataStore()
                .create(permission, getSecurityManager().getSubjectManager().getUser());
        return perm;
    }

    public boolean deletePermission(Long id) throws Exception {
        final Permission p = new Permission();
        p.setId(id);
        sc.dataStore().getPermissionDataStore().delete(p);
        getLogger().info("Permission deleted : {}", id);
        return true;
    }

    public Permission getPermissionById(Long id) throws Exception {
        return sc.dataStore().getPermissionDataStore().getById(id);
    }

    public PaginatedList<Permission> getPermissions(long startRecord,
                                                    long recordCount) throws Exception {
        return sc.dataStore().getPermissionDataStore()
                .getPaginatedRange(startRecord, recordCount);
    }

    public Collection<Permission> getAllPermissions() throws Exception {
        return sc.dataStore().getPermissionDataStore().getGroupedPermissions();
    }

    public Permission updatePermission(Permission permission) throws Exception {
        Permission perm = sc.dataStore().getPermissionDataStore()
                .update(permission, getSecurityManager().getSubjectManager().getUser());
        return perm;
    }

    public PaginatedList<Permission> getPermissionsForUser(User user,
                                                           long startRecord, long recordCount) throws Exception {
        return sc
                .dataStore()
                .getPermissionDataStore()
                .getTransitivePermissionsForUser(user.getId(), startRecord,
                        recordCount);
    }

    public PaginatedList<Permission> getPermissionsForUserId(Long id,
                                                             long startRecord, long recordCount) throws Exception {
        User user = new User();
        user.setId(id);
        return sc
                .dataStore()
                .getPermissionDataStore()
                .getTransitivePermissionsForUser(user.getId(), startRecord,
                        recordCount);
    }

    public void addPermissionToRole(Permission permission, Role role)
            throws Exception {
        sc.dataStore().getPermissionDataStore()
                .addPermissionToRole(role, permission);
        getLogger().info("permission :{}, added to role : {}",
                permission.getCanonicalName(), role.getCanonicalName());
    }

    public void addPermissionsToRole(ArrayList<Permission> permissions,
                                     Role role) throws Exception {
        sc.dataStore().getPermissionDataStore()
                .addPermissionsToRole(role, permissions);
        getLogger().info("{} permissions , added to role : {}",
                permissions.size(), role.getCanonicalName());
    }

    public void removePermissionFromRole(Permission permission, Role role)
            throws Exception {
        sc.dataStore().getPermissionDataStore()
                .removePermissionFromRole(role, permission);
        getLogger().info("permission :{}, removed from role : {}",
                permission.getCanonicalName(), role.getCanonicalName());
    }

    public void removePermissionsFromRole(ArrayList<Permission> permissions,
                                          Role role) throws Exception {
        sc.dataStore().getPermissionDataStore()
                .removePermissionsFromRole(role, permissions);
        getLogger().info("{} permissions , removed from role : {}",
                permissions.size(), role.getCanonicalName());
    }

    public PaginatedList<Permission> getPermissionsForRole(Role role,
                                                           long startRecord, long recordCount) throws Exception {
        return sc.dataStore().getPermissionDataStore()
                .getPermissionsForRole(role, startRecord, recordCount);
    }

    public PaginatedList<Permission> getPermissionsForRoleId(Long id,
                                                             long startRecord, long recordCount) throws Exception {
        Role role = new Role();
        role.setId(id);
        return sc.dataStore().getPermissionDataStore()
                .getPermissionsForRole(role, startRecord, recordCount);
    }

	@Override
	public boolean deletePermissions(ArrayList<Long> ids) throws Exception {
		boolean flag = true;
		for (Long id : ids) {
			deletePermission(id);
		}
		return flag;
	}
}
