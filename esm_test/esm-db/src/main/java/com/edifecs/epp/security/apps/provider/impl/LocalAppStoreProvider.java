package com.edifecs.epp.security.apps.provider.impl;

import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.security.apps.model.App;
import com.edifecs.epp.security.apps.model.AppComponent;
import com.edifecs.epp.security.apps.model.AppStatus;
import com.edifecs.epp.security.apps.provider.IAppStore;
import com.edifecs.epp.flexfields.model.FlexGroup;
import com.edifecs.epp.packaging.manifest.LogicalComponent;
import com.edifecs.epp.packaging.manifest.Manifest;

import org.apache.commons.io.IOUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;

import java.util.*;

/**
 * Created by sandeep.kath on 6/24/2014.
 */
public class LocalAppStoreProvider implements IAppStore {

    //TODO: For now App ID and App Name are same.

    private static Map<Long, Map<String, App>> appStore = new HashMap();

    private Map<String, Manifest> appManifestMap = new HashMap();
    private Map<String, String> appManifestString = new HashMap();


    public Map<String, Manifest> getAppManifestMap() {
        return appManifestMap;
    }

    public Map<String, String> getAppManifestString() {
        return appManifestString;
    }

    @Override
    public boolean sendInstallAppRequest(String appName, Long tenantId) throws Exception {
            Map<String, App> appsMap;
            if (!appStore.containsKey(tenantId)) {
                appsMap = new HashMap<>();
                appStore.put(tenantId, appsMap);
            } else {
                appsMap = appStore.get(tenantId);
            }
            appsMap.put(appName, getApp(appName));
            return true;
    }

    @Override
    public AppStatus getAppStatus(String appName, Long tenantId) throws Exception {
        return AppStatus.INSTALLED;
    }


    public LocalAppStoreProvider() throws Exception {
        getAppManifestFiles(new File(SystemVariables.SERVICE_MANAGER_ROOT_PATH));
    }

    //TODO: Dummy Imlementation to return Available apps
    @Override
    public App[] getInstalledApps(long tenantId) throws Exception {
        List<App> appList = new ArrayList<>();
        App cnc_app = new App();
        cnc_app.setName("CNC");
        cnc_app.setDescription("Collaboration and Connectivity");
        cnc_app.setVersion("1.0.1.M3");
        cnc_app.setDisplayVersion("1.0");

        cnc_app.setId("cnc");

        appList.add(cnc_app);

        App app = new App();
        app.setName("CollabT");
        app.setDescription("Collaboration Testing");
        app.setVersion("6.0.1.M4");
        app.setDisplayVersion("6.0");

        app.setId("collabt");

        appList.add(app);


        Map<String, App> tenantApps = appStore.get(tenantId);
        if (tenantApps != null) {
            for (Map.Entry<String, App> entry : tenantApps.entrySet()) {
                appList.add(entry.getValue());
            }
        }
        return appList.toArray(new App[appList.size()]);
    }


    @Override
    public App[] getAvailableApps(long tenantId) throws Exception {
        List<App> appList = new ArrayList<>();
        for (Map.Entry<String, Manifest> entry : appManifestMap.entrySet()) {
            App app = getApp(entry.getKey());
            appList.add(app);
        }
        Map<String, App> tenantApps = appStore.get(tenantId);
        if (tenantApps != null) {
            Iterator<App> iterator = appList.iterator();
            while (iterator.hasNext()) {
                if (tenantApps.containsKey(iterator.next().getId())) {
                    iterator.remove();
                }
            }
        }
        return appList.toArray(new App[appList.size()]);


    }

    private App getApp(String key) throws Exception{
        Manifest m = appManifestMap.get(key);
        if (m != null) {
            App app = new App();
            app.setName(m.displayName().get());
            app.setDescription(m.description().get());
            app.setVersion(m.version().fullVersion());
            app.setDisplayVersion(m.displayVersion().get());
            // temp
            app.setId(key);
            List<LogicalComponent> logicalComponents = m.getLogicalComponents();
            for (LogicalComponent logicalComponent : logicalComponents) {
                AppComponent appComponent = new AppComponent();
                appComponent.setName(logicalComponent.displayName().get());
                appComponent.setDescription(logicalComponent.description().get());
                app.addComponent(appComponent);
            }
            return app;
        } else
            throw new Exception("Application is not available to be installed.");
    }

    @Override
    public App[] getAppsCatalog(long startRecord, long recordCount) throws Exception {
        List apps1 = Arrays.asList(getInstalledApps(0)); //Mocked Data
        List apps2 = Arrays.asList(getAvailableApps(0)); //Mocked Data
        List merged = new ArrayList(apps1);
        merged.addAll(apps2);
        return (App[]) merged.toArray(new App[merged.size()]);
    }

    @Override
    public String getAppConfiguration(String appId) throws Exception {

        if (appManifestMap.containsKey(appId)) {

        } else {
            throw new Exception("Requested Application does not exist.");
        }

        return null;
    }

    @Override
    public Map<String, List<FlexGroup>> getTenantAppConfigurations(String tenantName) throws Exception {
        throw new NotImplementedException();

    }


    private void getAppManifestFiles(File directory) throws FileNotFoundException, IOException {
        search(directory);
    }

    private void search(File file) throws FileNotFoundException, IOException {
        if (file.isDirectory()) {
            if (file.canRead()) {
                for (File temp : file.listFiles()) {
                    if (temp.isDirectory()) {
                        search(temp);
                    } else {

                        if (SystemVariables.MANIFEST_FILE_EXTENSION.equals(temp.getName().toLowerCase())) {
                            InputStream fileInputStream = new FileInputStream(temp.getAbsolutePath());
                            String yaml = IOUtils.toString(fileInputStream, "UTF-8");
                            Manifest m = Manifest.fromYaml(yaml, "local_appstore").get();
                            appManifestMap.put(m.name(), m);
                            appManifestString.put(m.name(), yaml);
                        }
                    }
                }
            }
        }
    }


}
