package com.edifecs.epp.security.apps;

import com.edifecs.epp.security.apps.provider.IAppStore;
import com.edifecs.epp.security.apps.provider.impl.LocalAppStoreProvider;

/**
 * Created by sandeep.kath on 6/24/2014.
 */
public class AppStoreFactory {
    public static final IAppStore createInstance(String providerName) throws Exception{
        if (providerName.compareToIgnoreCase("local") == 0) {
            IAppStore appStoreProvider = new LocalAppStoreProvider();
            return appStoreProvider;
        } else throw new IllegalArgumentException("Invalid App Store Provider");
    }
}
