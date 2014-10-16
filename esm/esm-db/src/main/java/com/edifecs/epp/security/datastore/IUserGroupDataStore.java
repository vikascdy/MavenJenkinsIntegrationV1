package com.edifecs.epp.security.datastore;

import java.util.Collection;
import java.util.List;

import com.edifecs.epp.security.data.*;
import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
import com.edifecs.epp.security.exception.ItemNotFoundException;
import com.edifecs.epp.security.exception.SecurityDataException;
import com.edifecs.epp.security.jpa.util.TenantValidationHelper.GroupsValidator;

public interface IUserGroupDataStore extends IBaseSlaveDataStore<UserGroup> {

	PaginatedList<UserGroup> getTransitiveUserGroupsForUser(Long user,
			long startRecord, long recordCount) throws SecurityDataException;

	Collection<UserGroup> getUserGroupsForUser(User user)
			throws SecurityDataException;

    List<UserGroup> getTransitiveUserGroupsForUser(Long userId) throws SecurityDataException;

    void addUserToUserGroup(User user, UserGroup group)
			throws SecurityDataException;

	void removeUserFromUserGroup(User user, UserGroup group)
			throws SecurityDataException;

	void addOrganizationToUserGroup(Organization organization, UserGroup group)
			throws SecurityDataException;

	void removeOrganizationFromUserGroup(Organization organization,
			UserGroup group) throws SecurityDataException;

	UserGroup getUserGroupByUserGroupName(String groupName)
			throws ItemNotFoundException, SecurityDataException;

	List<UserGroup> queryGroups(String seed, long startRecord, long maxRecords)
			throws SecurityDataException;

	void addChildGroupToUserGroup(UserGroup group, UserGroup parentGroup)
			throws SecurityDataException;

	Collection<UserGroup> getChildGroupsForUserGroup(UserGroup group)
			throws SecurityDataException;

	PaginatedList<UserGroup> getGroupsForTenant(Long id, long startRecord,
			long recordCount) throws SecurityDataException;

	UserGroup importgroups(Long tenantId, UserGroup group)
			throws ItemAlreadyExistsException, SecurityDataException;
	
	GroupsValidator validateImportGroups(Long tenantId, UserGroup userGroup);
}
