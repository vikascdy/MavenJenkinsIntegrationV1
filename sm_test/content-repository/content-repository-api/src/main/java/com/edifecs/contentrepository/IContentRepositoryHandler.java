// -----------------------------------------------------------------------------
// Copyright (c) Edifecs Inc. All Rights Reserved.
//
// This software is the confidential and proprietary information of Edifecs Inc.
// ("Confidential Information").  You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the license
// agreement you entered into with Edifecs.
//
// EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
// SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
// NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
// LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
// ITS DERIVATIVES.
// -----------------------------------------------------------------------------
package com.edifecs.contentrepository;

import com.edifecs.contentrepository.api.ContentNode;
import com.edifecs.contentrepository.api.FileVersion;
import com.edifecs.contentrepository.api.exception.ContentRepositoryException;
import com.edifecs.epp.isc.annotations.*;
import com.edifecs.epp.security.data.Tenant;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

@CommandHandler
public interface IContentRepositoryHandler {

    @SyncCommand(name = "addFile")
    @RequiresPermissions("platform:repository:admin:file:create")
    public String addFile(
            @Arg(name = "path", required = true, description = "Directory to place file") String path,
            @Arg(name = "filename", required = true, description = "Name of the file to write") String filename,
            @StreamArg(name = "inputStream", description = "File stream to store") InputStream inputStream
            ) throws ContentRepositoryException;

    @SyncCommand(name = "copyNode")
    @RequiresPermissions("platform:repository:admin:file:copy")
    public void copyNode(
            @Arg(name = "srcPath", required = true, description = "Directory to place file") String srcPath,
            @Arg(name = "destPath", required = true, description = "Name of the file to write") String destPath
            ) throws ContentRepositoryException;

    @SyncCommand(name = "createFolder")
    @RequiresPermissions("platform:repository:admin:folder:create")
    public void createFolder(
            @Arg(name = "path", required = true, description = "Directory to place file") String path
            ) throws ContentRepositoryException;

    @SyncCommand(name = "deleteFile")
    @RequiresPermissions("platform:repository:admin:file:delete")
    public void deleteFile(
            @Arg(name = "path", required = true, description = "Directory to place file") String path,
            @Arg(name = "filename", required = true, description = "Name of the file to write") String filename
            ) throws ContentRepositoryException;

    @SyncCommand(name = "deleteFolder")
    @RequiresPermissions("platform:repository:admin:folder:delete")
    public void deleteFolder(
            @Arg(name = "path", required = true, description = "Directory to place file") String path
            ) throws ContentRepositoryException;

    @SyncCommand(name = "getFile")
    @RequiresPermissions("platform:repository:user:file:view")
    public InputStream getFile(
            @Arg(name = "path", required = true, description = "Directory of the file") String path,
            @Arg(name = "filename", required = true, description = "Name of the file to get") String filename,
            @Arg(name = "version", required = false, description = "Version of the file to get") String version
            ) throws ContentRepositoryException;

    @SyncCommand(name = "getHistory")
    @RequiresPermissions("platform:repository:user:history:view")
    public ArrayList<FileVersion> getHistory(
            @Arg(name = "path", required = true, description = "Directory to place file") String path
            ) throws ContentRepositoryException;

    @SyncCommand(name = "getNode")
    @RequiresPermissions("platform:repository:user:file:view")
    public ContentNode getNode(
            @Arg(name = "path", required = true, description = "Directory to place file") String path
            ) throws ContentRepositoryException;

    @SyncCommand(name = "getProperties")
    @RequiresPermissions("platform:repository:user:file:view")
    public HashMap<String, String> getProperties(
            @Arg(name = "path", required = true, description = "Directory to place file") String path
            ) throws ContentRepositoryException;

    @SyncCommand(name = "getStatistics")
    @RequiresPermissions("platform:repository:user:reporting:view")
    public String getStatistics() throws ContentRepositoryException;

    @SyncCommand(name = "moveNode")
    @RequiresPermissions("platform:repository:admin:file:create")
    public void moveNode(
            @Arg(name = "srcPath", required = true, description = "Directory to place file") String srcPath,
            @Arg(name = "destPath", required = true, description = "Name of the file to write") String destPath
            ) throws ContentRepositoryException;

    @SyncCommand(name = "updateFile")
    @RequiresPermissions("platform:repository:admin:file:edit")
    public String updateFile(
            @Arg(name = "path", required = true, description = "Directory to place file") String path,
            @Arg(name = "filename", required = true, description = "Name of the file to write") String filename,
            @StreamArg(name = "inputStream", description = "File stream to store") InputStream inputStream
            ) throws ContentRepositoryException;

    @SyncCommand(name = "viewFolder")
    @RequiresPermissions("platform:repository:user:folder:view")
    public ArrayList<ContentNode> viewFolder(
            @Arg(name = "path", required = true, description = "Directory to place file") String path
            ) throws ContentRepositoryException;

    @SyncCommand
    @RequiresPermissions("platform:repository:admin:repository:create")
    public void setupTenantRepository(
            @Arg(name = "repositoryPath", required = true, description = "Directory containing xml config file") String repositoryPath,
            @Arg(name = "tenant", required = true, description = "Tenant") Tenant tenant)
            throws ContentRepositoryException;

    @SyncCommand
    @RequiresPermissions("platform:repository:admin:user:create")
    public void addUserToTenantRepository(
            @Arg(name = "tenant", required = true, description = "Tenant") Tenant tenant,
            @Arg(name = "username", required = true, description = "username") String username,
            @Arg(name = "password", required = true, description = "will be set to same as username") String password,
            @Arg(name = "admin", required = false, description = "is admin user") boolean admin)
            throws ContentRepositoryException;

    @SyncCommand(name = "isAvailable")
    public boolean isAvailable() throws ContentRepositoryException;

    @SyncCommand
    @RequiresPermissions("platform:repository:user:file:share")
    public String shareContentWithUser(
            @Arg(name = "path", required = true, description = "path") String path,
            @Arg(name = "filename", required = true, description = "filename") String filename,
            @Arg(name = "username", required = true, description = "username") String username)
            throws ContentRepositoryException;
}
