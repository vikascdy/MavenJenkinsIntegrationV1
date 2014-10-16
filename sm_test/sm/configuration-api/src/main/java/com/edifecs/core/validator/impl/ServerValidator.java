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
package com.edifecs.core.validator.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import com.edifecs.core.validator.ResourceValidator;

public class ServerValidator implements ResourceValidator {

    private static final int TIMEOUT = 5000;

    private Properties       properties;

    public ServerValidator(Properties properties) {
        this.properties = properties;
    }

    @Override
    public boolean isResourceValid() {

        String ipAddress = properties.getProperty("ipAddress");
        String serverName = properties.getProperty("serverName");
        InetAddress address = null;
        try {
            if (ipAddress != null) {
                address = InetAddress.getByName(ipAddress);
                address = InetAddress.getLocalHost();
                address.getHostAddress();
            } else if (serverName != null) {
                address = InetAddress.getByName(serverName);
            }

            if (address != null) {
                return address.isReachable(TIMEOUT);
            } else {
                return false;
            }
        } catch (UnknownHostException e) {
            // logger.error(e);
            return false;
        } catch (IOException e) {
            // logger.error(e);
            return false;
        }
    }

}
