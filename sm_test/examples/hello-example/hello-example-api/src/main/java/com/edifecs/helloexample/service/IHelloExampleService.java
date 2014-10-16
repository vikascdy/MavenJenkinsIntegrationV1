// -----------------------------------------------------------------------------
// Copyright (c) Edifecs Inc. All Rights Reserved.
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

import com.edifecs.helloexample.handler.IHelloExampleHandler;
import com.edifecs.servicemanager.annotations.Handler;
import com.edifecs.servicemanager.annotations.Property;
import com.edifecs.servicemanager.annotations.Property.PropertyType;
import com.edifecs.servicemanager.annotations.Service;

@Service(
	name = "hello-example-service",
	version = "1.0.0.0",
	description = "Hello Example Service", 
	properties = {@Property(name = "name", propertyType = PropertyType.STRING, description = "" , defaultValue = "world", required = true)}
)
public interface IHelloExampleService {

    @Handler
    IHelloExampleHandler getHelloExampleCommandHandler();
}
