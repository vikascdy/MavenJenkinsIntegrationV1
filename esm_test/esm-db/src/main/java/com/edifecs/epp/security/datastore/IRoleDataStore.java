package com.edifecs.epp.security.datastore;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.exception.SecurityDataException;
import com.edifecs.epp.security.jpa.util.TenantValidationHelper.RolesValidator;

public interface IRoleDataStore extends IBaseSlaveDataStore<Role> {

	Role getRoleByRoleName(String roleName);

	PaginatedList<Role> getRolesForUser(User user, long startRecord,
			long recordCount) throws SecurityDataException;

	PaginatedList<Role> getRolesForGroup(UserGroup group, long startRecord,
			long recordCount) throws SecurityDataException;

	Collection<Role> getRolesForUser(User user) throws SecurityDataException;

	Collection<Role> getRolesForGroup(UserGroup group)
			throws SecurityDataException;

	void addRoleToGroup(UserGroup group, Role role)
			throws SecurityDataException;

	void addRoleToUser(User user, Role role) throws SecurityDataException;

	void addRolesToUser(User user, Collection<Role> roles)
			throws SecurityDataException;

	void addRolesToGroup(UserGroup group, Collection<Role> roles)
			throws SecurityDataException;

	void removeRoleFromUser(User user, Role role) throws SecurityDataException;

	void removeRoleFromGroup(UserGroup group, Role role)
			throws SecurityDataException;

	void addChildRoleToRole(Role role, Role parentRole)
			throws SecurityDataException;

	PaginatedList<Role> getChildRolesForRole(Role role, long startRecord,
			long recordCount) throws SecurityDataException;

	Collection<Role> getChildRolesForRole(Role role)
			throws SecurityDataException;

	void removeChildRoleFromRole(Role role, Role parentRole)
			throws SecurityDataException;

	List<Role> queryRoles(String name, long startRecord, long maxRecords)
			throws SecurityDataException;

	PaginatedList<Role> getTransitiveRolesForUser(User user, long startRecord,
			long recordCount) throws SecurityDataException;

	Collection<Role> getTransitiveRolesForGroup(UserGroup group)
			throws SecurityDataException;

	Map<Class<?>, Collection<?>> getTransitiveRolesAndPermissionsForGroups(
			List<UserGroup> sMGroups) throws SecurityDataException;

	PaginatedList<Role> getRolesForTenant(Long id, long startRecord,
			long recordCount) throws SecurityDataException;

	PaginatedList<Role> getRolesForOrganization(Long organizationId,
			long startRecord, long recordCount) throws SecurityDataException;

	Role importRoles(Long tenantId, Role role)
			throws ItemAlreadyExistsException, SecurityDataException;
	
	RolesValidator ValidateRolesImport(Long tenantId, Role role);

}
