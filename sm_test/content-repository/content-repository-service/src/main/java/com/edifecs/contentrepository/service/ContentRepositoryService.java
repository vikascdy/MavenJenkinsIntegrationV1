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

import com.edifecs.contentrepository.IContentLibraryHandler;
import com.edifecs.contentrepository.IContentRepositoryHandler;
import com.edifecs.contentrepository.IContentRepositoryService;
import com.edifecs.contentrepository.api.IContentLibrary;
import com.edifecs.contentrepository.api.IContentRepository;
import com.edifecs.contentrepository.api.exception.ContentRepositoryException;
import com.edifecs.contentrepository.jackrabbit.ContentLibrary;
import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.isc.Address;
import com.edifecs.epp.security.data.Tenant;
import com.edifecs.epp.security.data.User;
import com.edifecs.servicemanager.api.AbstractService;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * ServiceManager Service allows the developer to push or pull content from the
 * Content repository using our messaging API.
 *
 * @author willclem
 */
public class ContentRepositoryService extends AbstractService implements IContentRepositoryService {
    private static IContentLibrary contentLibrary;

    public ContentRepositoryService() {
        super();
    }

    public static IContentLibrary getContentLibrary() {
        return contentLibrary;
    }

    public static IContentRepository getLocalContentRepository() {
        return contentLibrary;
    }

    /**
     * Gets the Content Repository Interface.
     *
     * @return
     * @throws ContentRepositoryException
     */

    @Override
    public void start() throws Exception {
        getLogger().info("Starting Content Repository Service...");
        if (contentLibrary == null) {
            ContentRepositoryService.contentLibrary = new ContentLibrary(getSecurityManager());
        }

        setupRepositoryForDefaultTenant();

        getLogger().info("Started Content Repository Service");
    }

    @Override
    public Properties getTestProperties() throws Exception {
        Properties properties = new Properties();
        properties.put("Path", this.getClass().getResource("/repository/").getPath());

        return properties;
    }

    private void setupRepositoryForDefaultTenant() throws Exception {
        String path = getProperties().getProperty("Path");
        if (path == null || path.equals("")) {
            path = SystemVariables.CONTENT_REPOSITORY_DEFAULT_DATA_DIRECTORY;
        }

        File file = new File(path);
        file.mkdirs();

        Address add = getCommandCommunicator().getAddressRegistry()
                .getAddressForServiceTypeName(SystemVariables.SECURITY_SERVICE_TYPE_NAME);
        Map<String, Serializable> args = new HashMap<String, Serializable>();
        args.put("canonicalName", SystemVariables.DEFAULT_TENANT_NAME);
        Tenant tenant = (Tenant) getCommandCommunicator().sendSyncMessage(add, "tenant.getTenantByName", args);

        args.clear();
        args.put("username", "admin");
        User admin = (User) getCommandCommunicator().sendSyncMessage(add, "user.getUserByUsername", args);
        contentLibrary.setupTenantRepository(path, tenant);
        contentLibrary.addUserToTenantRepository(tenant, admin.getUsername(), admin.getUsername(), true);
    }

    @Override
    public void stop() {
        if (contentLibrary != null) {
            try {
                contentLibrary.shutdown();
            } catch (Exception e) {
                getLogger().error("error shutting down content repository", e);
            }
            getLogger().info("Content Repository Service Stopped");
            contentLibrary = null;
        }
    }

    @Override
    public IContentRepositoryHandler getContentRepositoryHandler() {
        return new ContentRepositoryHandler();
    }

    @Override
    public IContentLibraryHandler getContentLibraryHandler() {
        return new ContentLibraryHandler();
    }
}