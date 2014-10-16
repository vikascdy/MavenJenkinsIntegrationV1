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

package com.edifecs.helloexample.service;

import com.edifecs.helloexample.handler.HelloExampleHandler;
import com.edifecs.helloexample.handler.IHelloExampleHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edifecs.servicemanager.api.AbstractService;

public class HelloExampleService extends AbstractService implements IHelloExampleService {
	
	private Logger logger = LoggerFactory.getLogger(HelloExampleService.class);

	@Override
	public void start() throws Exception {
		logger.info("hello example service started.");
	}

	@Override
	public void stop() throws Exception {
		//TODO
		logger.info("hello example service stopped.");
	}

    @Override
    public IHelloExampleHandler getHelloExampleCommandHandler() {
        String greetee = getProperties().getProperty("name");
        return new HelloExampleHandler(greetee);
    }
}
