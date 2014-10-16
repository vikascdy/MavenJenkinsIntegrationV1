package com.edifecs.epp.security.service.handler;

import com.edifecs.epp.isc.Address;
import com.edifecs.epp.isc.annotations.Arg;
import com.edifecs.epp.isc.exception.ServiceTypeNotFoundException;
import com.edifecs.epp.security.IAuthorizationManager;
import com.edifecs.epp.security.apps.AppStoreFactory;
import com.edifecs.epp.security.apps.handler.IAppStoreHandler;
import com.edifecs.epp.security.apps.model.App;
import com.edifecs.epp.security.apps.model.AppStatus;
import com.edifecs.epp.security.apps.provider.IAppStore;
import com.edifecs.epp.flexfields.model.FlexGroup;
import com.edifecs.epp.isc.core.command.AbstractCommandHandler;

import java.io.Serializable;
import java.util.*;

import com.edifecs.epp.packaging.manifest.Manifest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sandeep.kath on 6/25/2014.
 */
// TODO: This needs to be split out into its own service
public class AppStoreHandler extends AbstractCommandHandler implements IAppStoreHandler {
    private final static String STORE_NAME = "local";
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String getAppStoreName() throws Exception {
        return STORE_NAME;
    }
    //TODO - Security Audit required for these command handlers.

    private void createAppConfigurationFromManifest() throws Exception {
        IAppStore appStore = AppStoreFactory.createInstance(STORE_NAME);
        for (Map.Entry<String, String> entry : appStore.getAppManifestString().entrySet()) {
            Map<String, Serializable> args = new HashMap();
            args.put("manifestString", entry.getValue().toString());
            Address serviceAddress = getCommandCommunicator().getAddressRegistry().getAddressForServiceTypeName("flexfields-service");
            Object response = getCommandCommunicator().sendSyncMessage(serviceAddress, "FlexField.parseAppManifest", args);
        }
    }

    @Override
    public App[] getInstalledApps(@Arg(name = "tenantId", required = true) long tenantId) throws Exception {
        IAppStore appStore = AppStoreFactory.createInstance(STORE_NAME);
        IAuthorizationManager authorizationManager = getCommandCommunicator().getSecurityManager().getAuthorizationManager();
        if (authorizationManager.isPermitted("platform:security:administrative:tenant:view")) {
            return appStore.getInstalledApps(tenantId);
        } else {
            throw new SecurityException("Unauthorized to access tenant app store.");
        }
    }

    @Override
    public App[] getAvailableApps(@Arg(name = "tenantId", required = true) long tenantId) throws Exception {
        IAppStore appStore = AppStoreFactory.createInstance(STORE_NAME);
        IAuthorizationManager authorizationManager = getCommandCommunicator().getSecurityManager().getAuthorizationManager();
        if (authorizationManager.isPermitted("platform:security:administrative:tenant:view")) {
            return appStore.getAvailableApps(tenantId);
        } else {
            throw new SecurityException("Unauthorized to access tenant app store.");
        }
    }

    @Override
    public List<FlexGroup> getAppConfiguration(@Arg(name = "appName", required = true) String appName, @Arg(name = "tenantId", required = true) long tenantId) throws Exception {
        HashMap<String, String> properties = new HashMap();
        Map<String, Serializable> args = new HashMap();
        properties.put("appName", appName);
        properties.put("componentName", "*");
        properties.put("entityName", "TenantEntity");
        properties.put("entityId", Long.toString(tenantId));
        args.put("contextMap", properties);
        Address serviceAddress = getCommandCommunicator().getAddressRegistry().getAddressForServiceTypeName("flexfields-service");
        Object response = getCommandCommunicator().sendSyncMessage(serviceAddress, "FlexField.getFields", args);
        return ((List<FlexGroup>) response);
    }

    @Override
    public boolean sendInstallAppRequest(@Arg(name = "appName", required = true) String appName, @Arg(name = "tenantId", required = true) long tenantId) throws Exception {
        IAppStore appStore = AppStoreFactory.createInstance(STORE_NAME);
        return appStore.sendInstallAppRequest(appName, tenantId);
    }

    @Override
    public AppStatus getAppStatus(@Arg(name = "appName", required = true) String appName, @Arg(name = "tenantId", required = true) long tenantId) throws Exception {
        IAppStore appStore = AppStoreFactory.createInstance(STORE_NAME);
        return appStore.getAppStatus(appName, tenantId);
    }


    @Override
    public Map<String, List<FlexGroup>> getTenantAppConfigurations() throws Exception {

        Map<String, List<FlexGroup>> responseMap = new HashMap();

        try {
            Address serviceAddress = getCommandCommunicator().getAddressRegistry().getAddressForServiceTypeName("flexfields-service");
            IAppStore appStore = AppStoreFactory.createInstance(STORE_NAME);
            String tenantName = getCommandCommunicator().getSecurityManager().getSubjectManager().getTenant().getCanonicalName();
            Long tenantId = getCommandCommunicator().getSecurityManager().getSubjectManager().getTenant().getId();
            for (Map.Entry<String, Manifest> entry : appStore.getAppManifestMap().entrySet()) {
                HashMap<String, String> properties = new HashMap();
                Map<String, Serializable> args = new HashMap();
                properties.put("tenantName", tenantName);
                properties.put("appName", entry.getKey());
                properties.put("componentName", "*");
                properties.put("entityName", "TenantEntity");
                properties.put("entityId", tenantId.toString());
                args.put("contextMap", properties);

                Object response = getCommandCommunicator().sendSyncMessage(serviceAddress, "FlexField.getFields", args);
                responseMap.put(entry.getKey(), (List<FlexGroup>) response);
            }
        } catch (ServiceTypeNotFoundException e) {
            logger.debug("Flex Field Service is not Initialized.");
        }
        return responseMap;
    }

    @Override
    public Object[] getAppCatalog(@Arg(name = "startRecord", required = true) long startRecord, @Arg(name = "recordCount", required = true) long recordCount) throws Exception {
        IAppStore appStore = AppStoreFactory.createInstance(STORE_NAME);
        IAuthorizationManager authorizationManager = getCommandCommunicator().getSecurityManager().getAuthorizationManager();
        if (authorizationManager.isPermitted("platform:security:administrative:site:view")) {
            return appStore.getAppsCatalog(startRecord, recordCount);
        } else {
            throw new SecurityException("Unauthorized to access app catalog.");
        }
    }

}
