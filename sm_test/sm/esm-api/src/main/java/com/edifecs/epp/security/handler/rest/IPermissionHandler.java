package com.edifecs.epp.security.handler.rest;

import com.edifecs.epp.isc.annotations.*;
import com.edifecs.epp.isc.command.IRestCommandHandler;
import com.edifecs.epp.isc.core.command.Pagination;
import com.edifecs.epp.security.data.PaginatedList;
import com.edifecs.epp.security.data.Permission;
import com.edifecs.epp.security.data.Role;
import com.edifecs.epp.security.data.User;

import java.util.ArrayList;
import java.util.Collection;

//TODO: Refactor the locations of these methods.
@CommandHandler(
        namespace = "permission",
        description = "Contains methods that can be used to get back information about Permissions")
public interface IPermissionHandler extends IRestCommandHandler<Permission> {

    @RequiresPermissions("platform:security:administrative:permission:view")
    @Override
    public Permission get(String url) throws Exception;

    @RequiresPermissions("platform:security:administrative:permission:view")
    @Override
    public Collection<Permission> list(Pagination pg) throws Exception;

    @RequiresPermissions("platform:security:administrative:permission:create")
    @Override
    public Permission post(Permission permission) throws Exception;

    @RequiresPermissions("platform:security:administrative:permission:edit")
    @Override
    public Permission put(String url, Permission permission) throws Exception;

    @RequiresPermissions("platform:security:administrative:permission:delete")
    @Override
    public void delete(String url) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:permission:create")
    public Permission createPermission(
            @Arg(name = "permission", required = true) Permission permission)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:permission:delete")
    public boolean deletePermission(@Arg(name = "id", required = true) Long id)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:permission:delete")
    public boolean deletePermissions(@Arg(name = "ids", required = true) ArrayList<Long> ids)
            throws Exception;

    
    @SyncCommand
    @RequiresPermissions("platform:security:administrative:permission:view")
    public Permission getPermissionById(
            @Arg(name = "id", required = true) Long id) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:permission:view")
    public PaginatedList<Permission> getPermissions(
            @Arg(name = "startRecord", required = true) long startRecord,
            @Arg(name = "recordCount", required = true) long recordCount)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:permission:view")
    public Collection<Permission> getAllPermissions() throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:permission:edit")
    public Permission updatePermission(
            @Arg(name = "permission", required = true) Permission permission)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:view")
    public PaginatedList<Permission> getPermissionsForUser(
            @Arg(name = "user", required = true) User user,
            @Arg(name = "startRecord", required = true) long startRecord,
            @Arg(name = "recordCount", required = true) long recordCount)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:user:view")
    public PaginatedList<Permission> getPermissionsForUserId(
            @Arg(name = "id", required = true) Long id,
            @Arg(name = "startRecord", required = true) long startRecord,
            @Arg(name = "recordCount", required = true) long recordCount)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:role:edit")
    public void addPermissionToRole(
            @Arg(name = "permission", required = true) Permission permission,
            @Arg(name = "role", required = true) Role role) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:role:edit")
    public void addPermissionsToRole(
            @Arg(name = "permissions", required = true) ArrayList<Permission> permissions,
            @Arg(name = "role", required = true) Role role) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:role:edit")
    public void removePermissionFromRole(
            @Arg(name = "permission", required = true) Permission permission,
            @Arg(name = "role", required = true) Role role) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:role:edit")
    public void removePermissionsFromRole(
            @Arg(name = "permissions", required = true) ArrayList<Permission> permissions,
            @Arg(name = "role", required = true) Role role) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:role:view")
    public PaginatedList<Permission> getPermissionsForRole(
            @Arg(name = "role", required = true) Role role,
            @Arg(name = "startRecord", required = true) long startRecord,
            @Arg(name = "recordCount", required = true) long recordCount)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:security:administrative:role:view")
    public PaginatedList<Permission> getPermissionsForRoleId(
            @Arg(name = "id", required = true) Long id,
            @Arg(name = "startRecord", required = true) long startRecord,
            @Arg(name = "recordCount", required = true) long recordCount)
            throws Exception;

}
