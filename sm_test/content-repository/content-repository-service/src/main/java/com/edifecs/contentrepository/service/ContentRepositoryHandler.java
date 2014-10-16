// -----------------------------------------------------------------------------
//  Copyright (c) Edifecs Inc. All Rights Reserved.
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
package com.edifecs.contentrepository.service;

import com.edifecs.contentrepository.IContentRepositoryHandler;
import com.edifecs.contentrepository.api.ContentNode;
import com.edifecs.contentrepository.api.FileVersion;
import com.edifecs.contentrepository.api.exception.ContentRepositoryException;
import com.edifecs.epp.isc.core.command.AbstractCommandHandler;
import com.edifecs.epp.security.data.Tenant;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ContentRepositoryHandler extends AbstractCommandHandler
        implements IContentRepositoryHandler {


    public ContentRepositoryHandler() {
    }

    @Override
    public String addFile(String path, String filename, InputStream inputStream)
            throws ContentRepositoryException {

        ContentRepositoryService.getLocalContentRepository().addFile(path, filename, inputStream);

        return ContentRepositoryService.getLocalContentRepository().getNode(path + filename).getVersion();
    }

    @Override
    public void copyNode(String srcPath, String destPath) throws ContentRepositoryException {

        ContentRepositoryService.getLocalContentRepository().copyNode(srcPath, destPath);
    }

    @Override
    public void createFolder(String path) throws ContentRepositoryException {

        ContentRepositoryService.getLocalContentRepository().createFolder(path);
    }

    @Override
    public void deleteFile(String path, String filename) throws ContentRepositoryException {

        ContentRepositoryService.getLocalContentRepository().deleteFile(path, filename);
    }

    @Override
    public void deleteFolder(String path) throws ContentRepositoryException {

        ContentRepositoryService.getLocalContentRepository().deleteFolder(path);
    }

    @Override
    public InputStream getFile(String path, String filename, String version)
            throws ContentRepositoryException {

        if (version != null) {
            return ContentRepositoryService.getLocalContentRepository().getFile(path, filename, version);
        } else {
            return ContentRepositoryService.getLocalContentRepository().getFile(path, filename);
        }
    }

    @Override
    public ArrayList<FileVersion> getHistory(String path) throws ContentRepositoryException {

        return new ArrayList<FileVersion>(ContentRepositoryService.getLocalContentRepository().getHistory(path));
    }

    @Override
    public ContentNode getNode(String path) throws ContentRepositoryException {
        return ContentRepositoryService.getLocalContentRepository().getNode(path);
    }

    @Override
    public HashMap<String, String> getProperties(String path) throws ContentRepositoryException {
        return new HashMap<String, String>(ContentRepositoryService.getLocalContentRepository().getProperties(path));
    }

    @Override
    public String getStatistics() throws ContentRepositoryException {
        return ContentRepositoryService.getLocalContentRepository().getStatistics();
    }

    @Override
    public void moveNode(String srcPath, String destPath) throws ContentRepositoryException {

        ContentRepositoryService.getLocalContentRepository().moveNode(srcPath, destPath);
    }

    @Override
    public String updateFile(String path, String filename, InputStream inputStream)
            throws ContentRepositoryException {
        ContentRepositoryService.getLocalContentRepository().updateFile(path, filename, inputStream);

        return ContentRepositoryService.getLocalContentRepository().getNode(path + filename).getVersion();
    }

    @Override
    public ArrayList<ContentNode> viewFolder(String path) throws ContentRepositoryException {

        return new ArrayList<ContentNode>(ContentRepositoryService.getLocalContentRepository().viewFolder(path));
    }

    @Override
    public void setupTenantRepository(String repositoryPath, Tenant tenant)
            throws ContentRepositoryException {
        ContentRepositoryService.getLocalContentRepository().setupTenantRepository(repositoryPath, tenant);
    }

    @Override
    public void addUserToTenantRepository(Tenant tenant, String username, String password, boolean admin)
            throws ContentRepositoryException {
        ContentRepositoryService.getLocalContentRepository()
                .addUserToTenantRepository(tenant, username, password, admin);
    }

    @Override
    public boolean isAvailable() throws ContentRepositoryException {
        return true;
    }

    @Override
    public String shareContentWithUser(String path, String filename, String username)
            throws ContentRepositoryException {
        return ContentRepositoryService.getLocalContentRepository()
                .shareContentWithUser(path, filename, username);
    }
}
