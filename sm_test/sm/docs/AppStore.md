# EAS (Edifecs App Store)

The application store is a specific set of commands needed for the display of the application catalog, configuration,
and status of the applications.

For ECM integration, it communicates with ECM, which serves back the required data. ECM Will then query EAS as needed
for things like the available applications.

For SM only integration, it looks into the apps folder within SM and pulls back the list of applications available from
there.

## EAS Interface Class

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
        App[] getInstalledApps(@Arg(name = "startRecord", required = true) long startRecord,
                               @Arg(name = "recordCount", required = true) long recordCount,
                               @Arg(name = "tenantId", required = true) long tenantId) throws Exception;
    
        @SyncCommand
        App[] getAvailableApps(@Arg(name = "startRecord", required = true) long startRecord,
                               @Arg(name = "recordCount", required = true) long recordCount,
                               @Arg(name = "tenantId", required = true) long tenantId) throws Exception;
    
        @SyncCommand
        List<FlexGroup> getAppConfiguration(@Arg(name = "appName", required = true) String appName,
                                            @Arg(name = "tenantId", required = true) long tenantId) throws Exception;
    
        @SyncCommand
        boolean sendInstallAppRequest(@Arg(name = "appName", required = true) String appName, 
                                      @Arg(name = "tenantId", required = true) long tenantId) throws Exception;
    
        @SyncCommand
        AppStatus getAppStatus(@Arg(name = "appName", required = true) String appName,
                               @Arg(name = "tenantId", required = true) long tenantId) throws Exception;
    
    }
