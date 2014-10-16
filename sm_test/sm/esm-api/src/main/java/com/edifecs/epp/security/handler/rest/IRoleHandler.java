package com.edifecs.epp.security.handler.rest;

import com.edifecs.epp.isc.annotations.*;
import com.edifecs.epp.isc.command.IRestCommandHandler;
import com.edifecs.epp.isc.core.command.Pagination;
import com.edifecs.epp.security.data.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

@CommandHandler(
        namespace = "role",
        description = "Contains methods that can be used to get back information about Roles")
public interface IRoleHandler extends IRestCommandHandler<Role> {

    @RequiresPermissions("platform:security:administrative:role:view")
    @Override
    public Role get(String url) throws Exception;

    @RequiresPermissions("platform:security:administrative:role:view")
    @Override
    public Collection<Role> list(Pagination pg) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:role:view")
    public PaginatedList<Role> getRolesForTenant(
            @Arg(name = "id", required = true) Long id,
            @Arg(name = "startRecord", required = true) long startRecord,
            @Arg(name = "recordCount", required = true) long recordCount)
            throws Exception;

    @RequiresPermissions("platform:security:administrative:role:create")
    @Override
    public Role post(Role role) throws Exception;

    @RequiresPermissions("platform:security:administrative:role:edit")
    @Override
    public Role put(String url, Role role) throws Exception;

    @RequiresPermissions("platform:security:administrative:role:delete")
    @Override
    public void delete(String url) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:role:create")
    public Role createRole(@Arg(name = "role", required = true) Role role)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:role:create")
    public Role createRoleForTenant(
            @Arg(name = "tenant", required = true) Tenant tenant,
            @Arg(name = "role", required = true) Role role)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:role:delete")
    public boolean deleteRole(@Arg(name = "id", required = true) Long id)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:role:delete")
    public boolean deleteRoles(@Arg(name = "ids", required = true) ArrayList<Long> ids)
            throws Exception;
    
    @SyncCommand
    @RequiresPermissions("platform:security:administrative:role:view")
    public Role getRoleById(@Arg(name = "id", required = true) Long id)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:role:view")
    public PaginatedList<Role> getRoles(
            @Arg(name = "startRecord", required = true) long startRecord,
            @Arg(name = "recordCount", required = true) long recordCount)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:role:edit")
    public Role updateRole(@Arg(name = "role", required = true) Role role)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:edit")
    public void addChildRoleToRole(
            @Arg(name = "role", required = true) Role role,
            @Arg(name = "parentRole", required = true) Role parentRole)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:edit")
    public void addChildRolesToRole(
            @Arg(name = "roles", required = true) ArrayList<Role> roles,
            @Arg(name = "parentRole", required = true) Role parentRole)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:edit")
    public void removeChildRolesFromRole(
            @Arg(name = "roles", required = true) ArrayList<Role> roles,
            @Arg(name = "parentRole", required = true) Role parentRole)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:edit")
    public void addRoleToUser(@Arg(name = "role", required = true) Role role,
                              @Arg(name = "user", required = true) User user) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:edit")
    public void addRolesToUser(
            @Arg(name = "roles", required = true) ArrayList<Role> roles,
            @Arg(name = "user", required = true) User user) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:edit")
    public void removeRolesFromUser(
            @Arg(name = "roles", required = true) ArrayList<Role> roles,
            @Arg(name = "user", required = true) User user) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:edit")
    public void removeRoleFromUser(
            @Arg(name = "role", required = true) Role role,
            @Arg(name = "user", required = true) User user) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:view")
    public PaginatedList<Role> getRolesForUser(
            @Arg(name = "userId", required = true) long userId,
            @Arg(name = "startRecord", required = true) long startRecord,
            @Arg(name = "recordCount", required = true) long recordCount)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:view")
    public PaginatedList<Role> getChildRolesForRole(
            @Arg(name = "role", required = true) Role role,
            @Arg(name = "startRecord", required = true) long startRecord,
            @Arg(name = "recordCount", required = true) long recordCount)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:group:edit")
    public void addRoleToGroup(@Arg(name = "role", required = true) Role role,
                               @Arg(name = "group", required = true) UserGroup group)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:group:edit")
    public void addRolesToGroup(
            @Arg(name = "roles", required = true) ArrayList<Role> roles,
            @Arg(name = "group", required = true) UserGroup group)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:group:edit")
    public void removeRolesFromGroup(
            @Arg(name = "roles", required = true) ArrayList<Role> roles,
            @Arg(name = "group", required = true) UserGroup group)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:group:edit")
    public void removeRoleFromGroup(
            @Arg(name = "role", required = true) Role role,
            @Arg(name = "group", required = true) UserGroup group)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:role:view")
    public Collection<Role> getRolesForGroup(
            @Arg(name = "groupId", required = true) Long groupId,
            @Arg(name = "startRecord", required = true) long startRecord,
            @Arg(name = "recordCount", required = true) long recordCount)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:role:view")
    public PaginatedList<Role> getRolesForOrganization(
            @Arg(name = "organizationId", required = true) Long organizationId,
            @Arg(name = "startRecord", required = true) long startRecord,
            @Arg(name = "recordCount", required = true) long recordCount)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:role:view")
    public Role getRoleByRoleName(
            @Arg(name = "roleName", required = true) String roleName)
            throws Exception;


	@SyncCommand
    @RequiresPermissions("platform:security:administrative:user:import")
    public String importRoleFromJson(
            @SuppressWarnings("deprecation") @StreamArg(name = "inputStream") InputStream inputStream) 
            		throws Exception;
	
	@SyncCommand
    @RequiresPermissions("platform:security:administrative:user:import")
    public String validateImportRoles(
            @SuppressWarnings("deprecation") @StreamArg(name = "inputStream") InputStream inputStream) 
            		throws Exception;
}