package com.edifecs.epp.security.apps.provider;

import com.edifecs.epp.packaging.manifest.Manifest;
import com.edifecs.epp.security.apps.model.App;
import com.edifecs.epp.flexfields.model.FlexGroup;
import com.edifecs.epp.security.apps.model.AppStatus;

import java.util.List;
import java.util.Map;

/**
 * Created by sandeep.kath on 6/24/2014.
 */
public interface IAppStore {

    //TODO: Review and add more methods after ECM, EAS integration */

    App[] getInstalledApps(long tenantId) throws Exception;

    App[] getAvailableApps(long tenantId) throws Exception;

    App[] getAppsCatalog(long startRecord, long recordCount) throws Exception;

    String getAppConfiguration(String appId) throws Exception;

    Map<String, List<FlexGroup>> getTenantAppConfigurations(String tenantName) throws Exception;

    Map<String, Manifest> getAppManifestMap();

    Map<String, String> getAppManifestString();

    boolean sendInstallAppRequest(String appName, Long tenantId) throws Exception;

    AppStatus getAppStatus(String appName, Long tenantId) throws Exception;

}
