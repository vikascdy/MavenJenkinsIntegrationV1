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

package com.edifecs.servicemanager.test.services;

import com.edifecs.servicemanager.annotations.Handler;
import com.edifecs.servicemanager.annotations.Property;
import com.edifecs.servicemanager.annotations.Property.PropertyType;
import com.edifecs.servicemanager.annotations.Service;

/**
 * Service designed to run Chat Application in SM. 
 * The chat messages are handled through <code>TestChatServiceHandler</code>.
 *
 * @author willclem
 */
@Service(
        name = "Test Chat Service",
        version = "1.0",
        description = "Chat Application",
        properties = { @Property(name = "client_name", propertyType = PropertyType.STRING, description = "Provide your name", defaultValue = "Client", required = true),
                @Property(name = "max_width", propertyType = PropertyType.LONG, description = "Max Width of the Chat Window", defaultValue = "200"),
                @Property(name = "max_height", propertyType = PropertyType.LONG, description = "Max Height of the Chat Window", defaultValue = "200"),
                @Property(name = "start_width", propertyType = PropertyType.LONG, description = "Width of the Chat Window", defaultValue = "40"),
                @Property(name = "start_height", propertyType = PropertyType.LONG, description = "Height of the Chat Window", defaultValue = "18")})
public interface ITestChatService {

    @Handler
    ITestChatServiceHandler getTestChatServiceHandler();

}

