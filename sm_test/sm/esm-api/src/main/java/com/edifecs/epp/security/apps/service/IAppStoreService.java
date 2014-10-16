package com.edifecs.epp.security.apps.service;

import com.edifecs.epp.security.apps.handler.IAppStoreHandler;
import com.edifecs.servicemanager.annotations.Handler;
import com.edifecs.servicemanager.annotations.Service;

/**
 * Created by sandeep.kath on 6/25/2014.
 */

@Service(
        name = "appstore-service",
        version = "1.0",
        description = "appstore-service"
)
public interface IAppStoreService {

    @Handler
    IAppStoreHandler getAppStoreHandler() throws Exception;
}
