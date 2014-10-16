package com.edifecs.epp.security.datastore;

import java.util.Collection;
import java.util.List;

import com.edifecs.epp.security.data.PaginatedList;
import com.edifecs.epp.security.data.Permission;
import com.edifecs.epp.security.data.Role;
import com.edifecs.epp.security.jpa.entity.PermissionEntity;
import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.exception.SecurityDataException;

public interface IPermissionDataStore extends IBaseOwnerDataStore<Permission> {

	List<Permission> getGroupedPermissions() throws SecurityDataException;

	PaginatedList<Permission> getTransitivePermissionsForUser(Long userId,
			long startRecord, long recordCount) throws SecurityDataException;

	PaginatedList<Permission> getPermissionsForRole(Role role,
			long startRecord, long recordCount) throws SecurityDataException;

	Collection<Permission> getTransitivePermissionsForUser(Long userId)
			throws SecurityDataException;

	Collection<Permission> getPermissionsForRole(Role role)
			throws SecurityDataException;

	void addPermissionToRole(Role role, Permission permission)
			throws SecurityDataException;

	void addPermissionsToRole(Role role, List<Permission> permissions)
			throws SecurityDataException;

	void removePermissionFromRole(Role role, Permission permission)
			throws SecurityDataException;

	void removePermissionsFromRole(Role role, Collection<Permission> permissions)
			throws SecurityDataException;

	Collection<Permission> getTransitivePermissionsForRole(Role role)
			throws SecurityDataException;

    Permission getPermission(Permission permission) throws SecurityDataException;

	PermissionEntity importPermissions(PermissionEntity permEntity)
			throws ItemAlreadyExistsException, SecurityDataException;
}
