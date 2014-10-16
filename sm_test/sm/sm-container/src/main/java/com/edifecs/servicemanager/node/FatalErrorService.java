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
package com.edifecs.servicemanager.node;

import com.edifecs.epp.isc.exception.ServiceException;
import com.edifecs.servicemanager.api.AbstractService;

/**
 * This is a placeholder service for use in the service registry if there is a
 * situation where a fatal error occurs on initialization of the service and no
 * code is loaded.
 * 
 * @author willclem
 */
public class FatalErrorService extends AbstractService implements IFatalErrorService {

    @Override
    public void start() throws Exception {
    	// Throw Exception
        throw new ServiceException(getMessage());
    }

    @Override
    public void stop() throws Exception {
        // Do Nothing
    }
}
