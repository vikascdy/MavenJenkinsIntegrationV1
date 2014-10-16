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

import com.edifecs.epp.isc.annotations.Akka;
import com.edifecs.epp.isc.annotations.Arg;
import com.edifecs.epp.isc.annotations.Command;
import com.edifecs.epp.isc.annotations.Rest;

import java.util.Properties;

@Akka(enabled = true)
@Rest(enabled = true)
public interface IEmailCommandHandler {

	@Command(name = "mail.sendmail")
	public boolean sendMail(
			@Arg(name = "to", description = "reciever", required = true) String to,
			@Arg(name = "from", description = "sender", required = true) String from,
			@Arg(name = "subject", description = "mail subject", required = true) String subject,
			@Arg(name = "msg", description = "mail message", required = true) String msg);

	public String getUsername();

	public void setUsername(String username);

	public String getPassword();

	public void setPassword(String password);

	public Properties getProps();

	public void setProps(Properties props);
}
