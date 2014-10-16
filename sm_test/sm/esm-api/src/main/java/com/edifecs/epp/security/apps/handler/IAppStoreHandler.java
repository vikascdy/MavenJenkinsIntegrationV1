package com.edifecs.epp.security.apps.handler;

import com.edifecs.epp.isc.annotations.Arg;
import com.edifecs.epp.security.apps.model.App;
import com.edifecs.epp.flexfields.model.FlexGroup;
import com.edifecs.epp.isc.annotations.CommandHandler;
import com.edifecs.epp.isc.annotations.SyncCommand;
import com.edifecs.epp.security.apps.model.AppStatus;

import java.util.List;
import java.util.Map;

/**
 * Created by sandeep.kath on 6/25/2014.
 */
@CommandHandler(namespace = "AppStore", description = "AppStore Handler")
public interface IAppStoreHandler {

    @SyncCommand
    public String getAppStoreName() throws Exception;

    @SyncCommand
    Map<String, List<FlexGroup>> getTenantAppConfigurations() throws Exception;

    @SyncCommand
    Object[] getAppCatalog(@Arg(name = "startRecord", required = true) long startRecord,
                           @Arg(name = "recordCount", required = true) long recordCount) throws Exception;

    @SyncCommand
    App[] getInstalledApps(@Arg(name = "tenantId", required = true) long tenantId) throws Exception;

    @SyncCommand
    App[] getAvailableApps(@Arg(name = "tenantId", required = true) long tenantId) throws Exception;

    @SyncCommand
    List<FlexGroup> getAppConfiguration(@Arg(name = "appName", required = true) String appName, @Arg(name = "tenantId", required = true) long tenantId) throws Exception;

    @SyncCommand
    boolean sendInstallAppRequest(@Arg(name = "appName", required = true) String appName,  @Arg(name = "tenantId", required = true) long tenantId) throws Exception;

    @SyncCommand
    AppStatus getAppStatus(@Arg(name = "appName", required = true) String appName,  @Arg(name = "tenantId", required = true) long tenantId) throws Exception;

}
