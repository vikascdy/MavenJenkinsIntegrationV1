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
package com.edifecs.epp.isc;

import java.io.IOException;
import java.util.Properties;

import com.edifecs.epp.isc.annotations.*;
import com.edifecs.epp.isc.core.ServiceInformation;
import com.edifecs.epp.isc.core.command.AbstractCommandHandler;

//FIXME: Review these commands as not all should be marked as NullSessionAllowed, or exposed through rest
@CommandHandler
@NullSessionAllowed
public class BaseMessageHandler extends AbstractCommandHandler {

    @SyncCommand(name = "getServicesInformation")
    public IServerServiceRegistry getServicesInformation() {
        IServerServiceRegistry reg = getCommandCommunicator().getAddressRegistry().getAllServiceStatusForAddress(
                getCommandCommunicator().getAddress());
        if (reg != null) {
            getLogger().debug("Returned locally running services: {}", reg.getAllServices());
        } else {
            getLogger().debug("No locally running services to return status on.");
        }
        return reg;
    }

    @SyncCommand(name = "updateServiceStatus")
    public boolean updateServiceStatus(
            @Arg(name = "address", description = "", required = true) Address address,
            @Arg(name = "serviceName", description = "", required = true) String serviceName,
            @Arg(name = "serviceInformation", description = "", required = true) ServiceInformation serviceInformation) {

        getCommandCommunicator().getAddressRegistry().updateLocalServiceInformation(address,
                serviceName, serviceInformation);

        return true;
    }
    
	@SyncCommand(name = "version")
	public String version() throws IOException {
		Properties properties = new Properties();
		properties.load(this.getClass().getResourceAsStream("/src/main/resources/buildNumber.properties"));
		
		return "\"" + properties.getProperty("app.version") + "\"";
	}
	
	@SyncCommand(name = "buildNumber")
	public String buildNumber() throws IOException {
		Properties properties = new Properties();
		properties.load(this.getClass().getResourceAsStream("/src/main/resources/buildNumber.properties"));
		
		return "\"" + properties.getProperty("app.buildNumber") + "\"";
	}

}
