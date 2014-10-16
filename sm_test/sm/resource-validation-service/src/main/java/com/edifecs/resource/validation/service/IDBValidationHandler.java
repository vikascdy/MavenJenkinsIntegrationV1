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

package com.edifecs.resource.validation.service;

import com.edifecs.epp.isc.annotations.Arg;
import com.edifecs.epp.isc.annotations.Command;
import com.edifecs.epp.isc.annotations.RequiresPermissions;
import com.edifecs.resource.validation.service.helpers.DBConnection;
import com.edifecs.resource.validation.service.helpers.DBConnectionException;
import com.edifecs.resource.validation.service.helpers.DBConnectionStatus;

public interface IDBValidationHandler {

    @Command
    public boolean check();

    @Command
    @RequiresPermissions("platform:resource:sql:validate")
    public DBConnectionStatus validateMySQLServer(
            @Arg(name = "connection", required = true, description = "") DBConnection connection);

    @Command
    @RequiresPermissions("platform:resource:sql:validate")
    public DBConnectionStatus validateMSSQLServer(
            @Arg(name = "connection", required = true, description = "") DBConnection connection)
            throws DBConnectionException;

    @Command(name = "validateDB")
    @RequiresPermissions("platform:resource:sql:validate")
    public boolean validateDB(@Arg(name = "dbType", required = false, description = "") String dbType,
            @Arg(name = "serverType", required = false, description = "") String serverType,
            @Arg(name = "server", required = true, description = "") String server,
            @Arg(name = "port", required = true, description = "") int port,
            @Arg(name = "db", required = true, description = "") String dbname,
            @Arg(name = "user", required = true, description = "") String username,
            @Arg(name = "password", required = true, description = "") String password);

}
