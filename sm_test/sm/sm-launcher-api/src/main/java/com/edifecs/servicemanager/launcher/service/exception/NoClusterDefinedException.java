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
package com.edifecs.servicemanager.launcher.service.exception;

import com.edifecs.core.configuration.helper.PropertiesException;

public class NoClusterDefinedException extends PropertiesException {
    private static final long serialVersionUID = 1L;

    public NoClusterDefinedException() {
    	super("No isc.cluster.name property found in application.conf file.");
    }

    public NoClusterDefinedException(Exception e) {
        super(e);
    }

    public NoClusterDefinedException(String msg) {
        super(msg);
    }

    public NoClusterDefinedException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

}
