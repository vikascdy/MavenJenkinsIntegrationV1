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
package com.edifecs.agent.launcher;

import java.io.File;

/**
 * Duplicate System Variables as
 * com.edifecs.core.configuration.helper.SystemVariables.
 * 
 * @author willclem
 */
public final class LauncherSystemVariables {

    /**
     * Working Directory path (edifecs/bin). This value is retrieved by
     * System.getProperty("user.dir").
     */
    public static final String WORKING_DIRECTORY = System.getProperty("user.dir");

    /**
     * Service Manager Root directory.
     */
    public static final String SERVICE_MANAGER_ROOT_PATH = new File(WORKING_DIRECTORY).getParent().toString()
            + File.separator;

    /**
     * Path to the platform path.
     */
    public static final String PLATFORM_PATH = SERVICE_MANAGER_ROOT_PATH + "platform" + File.separator;;

    /**
     * Path to the core JDBC folder where all bundles are located.
     */
    public static final String LIB_JDBC_DIR = PLATFORM_PATH + "core" + File.separator + "lib" + File.separator + "jdbc"
            + File.separator;

    /**
     * Path to the core Common folder where all bundles are located.
     */
    public static final String LIB_COMMON_DIR = PLATFORM_PATH + "core" + File.separator + "lib" + File.separator
            + "common" + File.separator;

    /**
     * Path to the core Native folder where all bundles are located.
     */
    public static final String LIB_NATIVE_DIR = PLATFORM_PATH + "core" + File.separator + "lib" + File.separator
            + "native" + File.separator;

    /**
     * Path to the core Tomcat folder where all bundles are located.
     */
    public static final String LIB_NODE_DIR = PLATFORM_PATH + "core" + File.separator + "lib" + File.separator
            + "sm-container" + File.separator;

    /**
     * Path to the core Tomcat folder where all bundles are located.
     */
    public static final String LIB_LAUNCHER_DIR = PLATFORM_PATH + "core" + File.separator + "lib" + File.separator
            + "sm-launcher" + File.separator;

    // System Variable Keys
    /**
     * System Properties Key value that identifies the property that holds the
     * name of the launched Node.
     */
    public static final String NODE_NAME_KEY = "NODE_NAME_KEY";

    public static final String NODE_CONFIG_JSON_KEY = "NODE_CONFIG_JSON_KEY";

    public static final String CONFIG_JSON_KEY = "CONFIG_JSON_KEY";

    /**
     * System Properties Key value that is non-null if ANSI color should be
     * disabled.
     */
    public static final String COLOR_DISABLED_KEY = "COLOR_DISABLED_KEY";

    public static final String CONFIGURATION_PATH = SERVICE_MANAGER_ROOT_PATH + "conf" + File.separator;

    public static final String LAUNCH_NODES_KEY = "LAUNCH_NODES_KEY";

    private LauncherSystemVariables() {
        // Left Blank as this is a Utility Class
    }

}
