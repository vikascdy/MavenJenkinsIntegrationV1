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
package com.edifecs.contentrepository.service.helper;

import org.apache.jackrabbit.api.security.user.User;

import com.edifecs.contentrepository.api.model.AuthLevel;
import com.edifecs.contentrepository.api.model.UserEntry;

public final class JCRConverter {

    private JCRConverter() {
    }

    public static UserEntry convertJcrUserToApiUser(User jackrabbitUser) throws Exception {
        return new UserEntry(
                jackrabbitUser.getProperty("username")[0].getString(),
                jackrabbitUser.getProperty("password")[0].getString(),
                jackrabbitUser.getProperty("firstName")[0].getString(),
                jackrabbitUser.getProperty("lastName")[0].getString(),
                jackrabbitUser.getProperty("email")[0].getString(),
                AuthLevel.valueOf(jackrabbitUser.getProperty("level")[0].getString()),
                jackrabbitUser.getProperty("userkey")[0].getString(),
                jackrabbitUser.getProperty("confirmed")[0].getBoolean());
    }

}
