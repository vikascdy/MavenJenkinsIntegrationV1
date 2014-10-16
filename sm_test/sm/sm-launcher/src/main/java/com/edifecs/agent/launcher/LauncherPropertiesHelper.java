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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

import java.util.Properties;

public final class LauncherPropertiesHelper {

    public static final String CONFIGURATION_PROPERTIES_CLUSTER_NAME = "cluster.name";
    public static final String CONFIGURATION_PROPERTIES_DEFAULT_NODE_JVM_OPTS = "default.node.jvm.opts";
    
    public static final String CONFIGURATION_PROPERTIES_SHUTDOWN_TIMEOUT_VALUE = "sm.shutdown.timeout";
    
    private LauncherPropertiesHelper() { }
    
    public static Properties loadConfigProperties(String configPropertiesPath)throws IOException {

        Properties properties = new Properties();
        
        // Load default config.properties file
        File file = new File(configPropertiesPath);
        FileInputStream fis = new FileInputStream(file);
        properties.putAll(loadConfigProperties(fis));
        
        // Load the config.properties.<hostname> file if it exists
        file = new File(configPropertiesPath + "." + InetAddress.getLocalHost().getHostName().toLowerCase());
        if (file.exists()) {
            fis = new FileInputStream(file);
            properties.putAll(loadConfigProperties(fis));
        }
        return properties;
    }

    public static Properties loadConfigProperties(InputStream configPropertiesStream) throws IOException {

        Properties configProperties = new Properties();

        configProperties.load(configPropertiesStream);

        return configProperties;
    }

    /**
     * Retrieves the value cluster.name from the config.propeties file.
     * 
     * @return Default Node JVM Opts
     */
    public static String getClusterName(Properties configProperties) {
        return configProperties.getProperty(CONFIGURATION_PROPERTIES_CLUSTER_NAME);
    }

    /**
     * Retrieves the value default.node.jvm.opts from the config.propeties file.
     * 
     * @return Default Node JVM Opts
     */
    public static String getDefaultNodeJVMOpts(Properties configProperties) {
        return configProperties.getProperty(CONFIGURATION_PROPERTIES_DEFAULT_NODE_JVM_OPTS);
    }

    public static String getShutdownTime(Properties configProperties) {
        return configProperties.getProperty(CONFIGURATION_PROPERTIES_SHUTDOWN_TIMEOUT_VALUE);
    }

}
