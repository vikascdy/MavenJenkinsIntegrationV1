package com.edifecs.epp.security.handler.rest;

import com.edifecs.epp.isc.annotations.*;
import com.edifecs.epp.isc.command.IRestCommandHandler;
import com.edifecs.epp.isc.core.command.Pagination;
import com.edifecs.epp.security.data.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

@CommandHandler(
        namespace = "group",
        description = "Contains methods that can be used to get back information about Groups")
public interface IUserGroupHandler extends IRestCommandHandler<UserGroup> {

    @RequiresPermissions("platform:security:administrative:group:view")
    @Override
    public UserGroup get(String url) throws Exception;

    @RequiresPermissions("platform:security:administrative:group:view")
    @Override
    public Collection<UserGroup> list(Pagination pg) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:group:view")
    public PaginatedList<UserGroup> getGroupsForTenant(
            @Arg(name = "id", required = true) Long id,
            @Arg(name = "startRecord", required = true) long startRecord,
            @Arg(name = "recordCount", required = true) long recordCount)
            throws Exception;

    @RequiresPermissions("platform:security:administrative:group:create")
    @Override
    public UserGroup post(UserGroup group) throws Exception;

    @RequiresPermissions("platform:security:administrative:group:edit")
    @Override
    public UserGroup put(String url, UserGroup group) throws Exception;

    @RequiresPermissions("platform:security:administrative:group:delete")
    @Override
    public void delete(String url) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:group:create")
    public UserGroup createGroup(
            @Arg(name = "group", required = true) UserGroup group)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:group:create")
    UserGroup createGroupForTenant(
            @Arg(name = "tenant", required = true) Tenant t,
            @Arg(name = "group", required = true) UserGroup g) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:group:delete")
    public boolean deleteGroup(@Arg(name = "id", required = true) Long id)
            throws Exception;
    
    @SyncCommand
    @RequiresPermissions("platform:security:administrative:group:delete")
    public boolean deleteGroups(@Arg(name = "ids", required = true) ArrayList<Long> ids)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:group:view")
    public PaginatedList<UserGroup> getGroups(
            @Arg(name = "startRecord", required = true) long startRecord,
            @Arg(name = "recordCount", required = true) long recordCount)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:group:view")
    public Collection<UserGroup> getChildGroupsForGroup(
            @Arg(name = "group", required = true) UserGroup group)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:group:view")
    public UserGroup getGroupById(@Arg(name = "id", required = true) Long id)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:group:edit")
    public UserGroup updateGroup(
            @Arg(name = "group", required = true) UserGroup group)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:edit")
    public void addGroupToUser(
            @Arg(name = "group", required = true) UserGroup group,
            @Arg(name = "user", required = true) User user) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:group:edit")
    public void addChildGroupsToGroup(
            @Arg(name = "group", required = true) UserGroup group,
            @Arg(name = "parentgGroup", required = true) UserGroup parentGroup)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:edit")
    public void addGroupsToUser(
            @Arg(name = "groups", required = true) ArrayList<UserGroup> groups,
            @Arg(name = "user", required = true) User user) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:edit")
    public void addUsersToGroup(
            @Arg(name = "group", required = true) UserGroup group,
            @Arg(name = "users", required = true) ArrayList<User> users)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:edit")
    public void addOrganizationToGroup(
            @Arg(name = "group", required = true) UserGroup group,
            @Arg(name = "organization", required = true) Organization organization)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:edit")
    public void addOrganizationsToGroup(
            @Arg(name = "group", required = true) UserGroup group,
            @Arg(name = "organizations", required = true) ArrayList<Organization> organizations)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:edit")
    public void removeOrganizationsFromGroup(
            @Arg(name = "group", required = true) UserGroup group,
            @Arg(name = "organizations", required = true) ArrayList<Organization> organizations)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:edit")
    public void removeOrganizationFromGroup(
            @Arg(name = "group", required = true) UserGroup group,
            @Arg(name = "organization", required = true) Organization organization)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:edit")
    public void removeUsersFromGroup(
            @Arg(name = "group", required = true) UserGroup group,
            @Arg(name = "users", required = true) ArrayList<User> users)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:edit")
    public void removeGroupsFromUser(
            @Arg(name = "groups", required = true) ArrayList<UserGroup> groups,
            @Arg(name = "user", required = true) User user) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:edit")
    public void removeGroupFromUser(
            @Arg(name = "group", required = true) UserGroup group,
            @Arg(name = "user", required = true) User user) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:view")
    public PaginatedList<UserGroup> getGroupsForUser(
            @Arg(name = "userId", required = true) long userId,
            @Arg(name = "startRecord", required = true) long startRecord,
            @Arg(name = "recordCount", required = true) long recordCount)
            throws Exception;

	 @SuppressWarnings("deprecation")
		@SyncCommand
	    @RequiresPermissions("platform:security:administrative:user:import")
	    public String importGroupsFromJson(
	            @StreamArg(name = "inputStream") InputStream inputStream) throws Exception;
	 
	 @SuppressWarnings("deprecation")
		@SyncCommand
	    @RequiresPermissions("platform:security:administrative:user:import")
	    public String validateImportGroups(
	            @StreamArg(name = "inputStream") InputStream inputStream) throws Exception;

}
