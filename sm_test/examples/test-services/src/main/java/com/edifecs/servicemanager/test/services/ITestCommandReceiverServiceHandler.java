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

package com.edifecs.servicemanager.test.services;

import com.edifecs.epp.isc.annotations.*;
import com.edifecs.epp.isc.exception.MessageException;

import java.io.InputStream;
import java.io.Serializable;

@CommandHandler
@Akka(enabled = true)
public interface ITestCommandReceiverServiceHandler {

	@Command(name = "testCommand")
	@NullSessionAllowed
	public boolean testCommand();

	@Command(name = "downloadAsString")
	public String downloadAsString(@Arg(name = "path", required = true,
	        description = "File path") String path);

	@Command(name = "downloadAsStream")
	public InputStream downloadAsStream(@Arg(name = "path", required = true,
	        description = "File path") String path);

	@Command(name = "testSecurityUserSessionPassCommand")
	public Serializable testSecurityUserSessionPassCommand() throws Exception;

	@Command(name = "testSecurityUserSessionCommand")
	public Serializable testSecurityUserSessionCommand()
	        throws MessageException;

	@Command(name = "testPermissionRequired")
	@RequiresPermissions("testProduct1:testCategory1:testTypeName1:testSubTypeName1:testName1")
	public boolean testPermissionRequired();

	@Command(name = "testSessionRequired")
	@RequiresPermissions("testProduct2:testCategory2:testTypeName2:testSubTypeName2:testName2")
	public boolean testSessionRequired();

	@Command(name = "testNullSession")
	@NullSessionAllowed
	public boolean testNullSession();

}
