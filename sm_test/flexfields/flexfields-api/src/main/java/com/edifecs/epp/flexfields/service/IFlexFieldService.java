
package com.edifecs.epp.flexfields.service;

import com.edifecs.epp.flexfields.handler.IFlexFieldHandler;
import com.edifecs.servicemanager.annotations.Handler;
import com.edifecs.servicemanager.annotations.Service;

@Service(
        name = "flexfields-service",
        version = "1.0",
        description = "flexfield-service"
)
public interface IFlexFieldService {

    @Handler
    IFlexFieldHandler getFlexFieldHandler() throws Exception;
}