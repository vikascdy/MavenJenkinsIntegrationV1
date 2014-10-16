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

import com.edifecs.epp.isc.annotations.Akka;
import com.edifecs.epp.isc.annotations.Arg;
import com.edifecs.epp.isc.annotations.Command;
import com.edifecs.epp.isc.annotations.CommandHandler;
import com.edifecs.servicemanager.test.models.Student;

import java.util.List;

@CommandHandler
@Akka(enabled = true)
public interface ITestJettyCommandRecieverServiceHandler {

	@Command(name = "testCommand")
	public boolean testCommand();

	@Command(name = "testParamCommand")
	public String testCommand(
			@Arg(name = "id", required = true, description = "id") String id,
			@Arg(name = "cName", required = false, description = "common name") String cName);

	@Command(name = "addStudent")
	public boolean addStudent(
			@Arg(name = "student", required = true, description = "student") Student student);

	@Command(name = "delStudent")
	public boolean delStudent(
			@Arg(name = "id", required = true, description = "id") long id);

	@Command(name = "list")
	public List<Student> list();
}
