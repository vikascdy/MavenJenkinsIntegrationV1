package com.edifecs.epp.flexfields.service;

import com.edifecs.epp.flexfields.handler.FlexFieldHandler;
import com.edifecs.epp.flexfields.handler.IFlexFieldHandler;

import com.edifecs.servicemanager.api.AbstractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sandeep.kath on 5/28/2014.
 */
public class FlexFieldService extends AbstractService implements IFlexFieldService {

    private Logger logger = LoggerFactory.getLogger(FlexFieldService.class);

    @Override
    public void start() throws Exception {
        logger.debug("Flex Field service started.");
    }

    @Override
    public void stop() throws Exception {
        logger.debug("Flex Field service stopped.");
    }


    @Override
    public IFlexFieldHandler getFlexFieldHandler() throws Exception {
        return new FlexFieldHandler(getResources().get("FlexField Database"));
    }
}
