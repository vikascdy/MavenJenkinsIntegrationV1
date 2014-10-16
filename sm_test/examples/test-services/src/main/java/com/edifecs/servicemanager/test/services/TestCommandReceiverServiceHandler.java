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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edifecs.epp.isc.Address;
import com.edifecs.epp.isc.exception.MessageException;
import com.edifecs.epp.isc.core.command.AbstractCommandHandler;

public class TestCommandReceiverServiceHandler extends AbstractCommandHandler implements ITestCommandReceiverServiceHandler {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public boolean testCommand() {
		return true;
	}

	public String downloadAsString(String path) {
		try {
			FileInputStream fs = new FileInputStream(new File(path));
			InputStream is = fs;
			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer, "UTF-8");

			is.close();

			return writer.toString();
		} catch (FileNotFoundException e) {
			return e.toString();
		} catch (IOException e) {
			return e.toString();
		}
	}

	public InputStream downloadAsStream(String path) {
		try {
			logger.debug("#####HERE########");
			FileInputStream fs = new FileInputStream(new File(path));
			logger.debug("#####" + fs.toString() + " " + fs.available());
			InputStream is = fs;
			logger.debug("#####HERE 2########" + is.toString());
			return is;
		} catch (IOException e) {
			logger.debug("#####" + e.toString());
			return null;
		}
	}

	public Serializable testSecurityUserSessionPassCommand() throws Exception {

		if (getCommandCommunicator().getSecurityManager().getSessionManager().getCurrentSession() == null) {
			throw new MessageException("No User Session Found");
		}

		logger.error("The Current Session is: {}", getCommandCommunicator()
		        .getSecurityManager().getSessionManager().getCurrentSession()
		        .getSessionId());

		// Send Message to other nodes

		List<Address> addresses = getCommandCommunicator().getAddressRegistry()
		        .getAddressesForServiceTypeName(
		                "Test Command Receiver");

		getCommandCommunicator()
		        .sendSyncMessage(addresses,
		                "testSecurityUserSessionCommand");

		return getCommandCommunicator().getSecurityManager().getSessionManager()
		        .getCurrentSession().getSessionId();
	}

	public Serializable testSecurityUserSessionCommand()
	        throws MessageException {

		if (getCommandCommunicator().getSecurityManager().getSessionManager().getCurrentSession() == null) {
			throw new MessageException("No User Session Found");
		}

		logger.error("The Current Session is: {}", getCommandCommunicator()
		        .getSecurityManager().getSessionManager().getCurrentSession()
		        .getSessionId());

		return getCommandCommunicator().getSecurityManager().getSessionManager()
		        .getCurrentSession().getSessionId();
	}

	public boolean testPermissionRequired() {
		return true;
	}

	public boolean testSessionRequired() {
		return true;
	}

	public boolean testNullSession() {
		return true;
	}

}
