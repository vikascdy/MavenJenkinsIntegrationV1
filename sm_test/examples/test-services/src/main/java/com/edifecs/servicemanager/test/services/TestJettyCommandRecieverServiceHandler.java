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

import java.util.ArrayList;
import java.util.List;

import com.edifecs.epp.isc.core.command.AbstractCommandHandler;
import com.edifecs.servicemanager.test.models.Student;

public class TestJettyCommandRecieverServiceHandler extends
		AbstractCommandHandler implements ITestJettyCommandRecieverServiceHandler {

	private static List<Student> students = new ArrayList<Student>();

	public boolean testCommand() {
		return true;
	}

	public String testCommand(String id, String cName) {
		String str = "Hello, " + cName + "{" + id + "} PUT is working for you.";
		return str;
	}

	public boolean addStudent(Student student) {
		if (null != students) {
			students.add(student);
			return true;
		} else {
			return false;
		}
	}

	public boolean delStudent(long id) {
		if (null != students) {
			for (Student s : students) {
				if (s.getId() == id) {
					students.remove(s);
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public List<Student> list() {
		if (null == students) {
			students = new ArrayList<Student>();
		}
		for (Student s : students) {
			System.out.println("###########" + s);
		}
		return students;
	}

}
