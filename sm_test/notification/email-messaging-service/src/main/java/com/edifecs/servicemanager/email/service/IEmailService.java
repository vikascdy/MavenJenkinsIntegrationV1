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

package com.edifecs.servicemanager.email.service;

import com.edifecs.servicemanager.annotations.Handler;
import com.edifecs.servicemanager.annotations.Resource;
import com.edifecs.servicemanager.annotations.Service;

/**
 * Service designed to test all types of annotations.
 * 
 * @author abhising
 */
@Service(
	name = "Email Service", 
	version = "1.0", 
	description = "Messaging service for sending emails",
	resources = {
		@Resource(name = "Email Service SMTP Server", type = "SMTP Server", unique = false)
	}
)
public interface IEmailService {

    @Handler
    IEmailCommandHandler getEmailCommandHandler();
}
