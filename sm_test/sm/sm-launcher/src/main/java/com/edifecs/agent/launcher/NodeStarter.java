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

import java.lang.reflect.Method;
import com.edifecs.agent.launcher.classloader.SMClassLoader;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Main class to launch additional Nodes from the Agent within a separate JVM.
 * 
 * @author willclem
 */
public final class NodeStarter {

    /**
     * @param args
     *            -nodeName &lt;nodeName&gt; : <B>Required</B> Launches the
     *            configuration based on the given nodeName
     */
    public static void main(final String[] args) {
        SMClassLoader classLoader = new SMClassLoader(ClassLoader.getSystemClassLoader());
        classLoader.addClasspath(LauncherSystemVariables.SERVICE_MANAGER_ROOT_PATH + "bin/system");
        classLoader.addClasspath(LauncherSystemVariables.CONFIGURATION_PATH);
        classLoader.addClasspath(LauncherSystemVariables.LIB_COMMON_DIR);
        classLoader.addClasspath(LauncherSystemVariables.LIB_LAUNCHER_DIR);
        classLoader.addClasspath(LauncherSystemVariables.LIB_NATIVE_DIR);
        classLoader.addClasspath(LauncherSystemVariables.LIB_NODE_DIR);
        classLoader.addClasspath(LauncherSystemVariables.LIB_JDBC_DIR);

        // Pull out the startup properties from the command line arguments
        for (String arg : args) {
            if (arg.startsWith("-nodeName=")) {
                final String nodeName = arg.substring("-nodeName=".length());
                System.setProperty(LauncherSystemVariables.NODE_NAME_KEY, nodeName.trim());
            } else if (arg.startsWith("-nodeConfig=")) {
                final String nodeConfig = arg.substring("-nodeConfig=".length());
                System.setProperty(LauncherSystemVariables.NODE_CONFIG_JSON_KEY, nodeConfig.trim());
            } else if (arg.toLowerCase().contains("-nocolor")) {
                System.setProperty(LauncherSystemVariables.COLOR_DISABLED_KEY, "true");
            }
        }
        
        try {
            Class<?> clazz = Class.forName("com.edifecs.servicemanager.node.NodeLauncher", true, classLoader);
            Method method = clazz.getMethod("startNode", String[].class);
            Object instance = clazz.newInstance();
            method.invoke(instance, (Object) args);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * NodeStarter should never be instantiated.
     */
    private NodeStarter() {
        // Do Nothing
    }
}
