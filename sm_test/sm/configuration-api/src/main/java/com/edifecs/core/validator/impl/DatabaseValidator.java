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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.edifecs.core.validator.ResourceValidator;

public class DatabaseValidator implements ResourceValidator {

    private Properties properties;

    public DatabaseValidator(Properties properties) {
        this.properties = properties;
    }

    @Override
    public boolean isResourceValid() {
        // See your driver documentation for the proper format of this string :
        String connectionString = properties.getProperty("connectionString");
        // Provided by your driver documentation. In this case, a MySql driver
        // is used :
        String driverName = properties.getProperty("driverName");
        String userName = properties.getProperty("userName");
        String password = properties.getProperty("password");
        boolean result = false;
        Connection connection = null;

        try {
            Class.forName(driverName).newInstance();
            connection = DriverManager.getConnection(connectionString, userName, password);
            if (connection != null) {
                result = true;
            }
        } catch (SQLException e) {
            // log error.
            e.printStackTrace();
        } catch (InstantiationException e) {
            // log error.
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // log error.
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // log error.
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}
