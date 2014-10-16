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

import com.edifecs.servicemanager.annotations.Service;
import com.edifecs.servicemanager.annotations.ServiceDependency;
import com.edifecs.servicemanager.api.AbstractService;

/**
 * Service designed to test the large file transfer mechanism. The service
 * sends large files via stream to the <CODE> TestLargeFileReceiverService </CODE>.
 * 
 * @author ashipras
 */
@Service(
        name = "Test Large File Sender",
        version = "1.0",
        description = "Sends Large Files",    
        services = {@ServiceDependency (name = "Test Large File Receiver", typeName = "Test Command Receiver", version = "1.0", unique = false)}
)
public class TestLargeFileSenderService extends AbstractService {

	@Override
	public void start() throws Exception {
		// TODO Auto-generated method stub		   
        getLogger().debug("{}: Service Successfully Started", this.getClass());
    }

    @Override
    public void stop() throws Exception {
        getLogger().debug("{}: Service Successfully Stopped", this.getClass());
    }    
}