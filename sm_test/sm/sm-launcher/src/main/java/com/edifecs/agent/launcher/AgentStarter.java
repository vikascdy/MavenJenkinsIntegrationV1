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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.edifecs.agent.launcher.classloader.SMClassLoader;

/**
 * Main class for launching the Agent.
 * 
 * @author willclem
 */
public final class AgentStarter {

    /**
     * @param args
     *            Currently Accepts no input parameters.
     */
    public static void main(final String[] args) {
        SMClassLoader classLoader = new SMClassLoader(ClassLoader.getSystemClassLoader());
        classLoader.addClasspath(LauncherSystemVariables.SERVICE_MANAGER_ROOT_PATH + "bin/system");
        classLoader.addClasspath(LauncherSystemVariables.LIB_NODE_DIR);
        classLoader.addClasspath(LauncherSystemVariables.LIB_COMMON_DIR);
        classLoader.addClasspath(LauncherSystemVariables.LIB_LAUNCHER_DIR);
        classLoader.addClasspath(LauncherSystemVariables.LIB_NATIVE_DIR);
        classLoader.addClasspath(LauncherSystemVariables.CONFIGURATION_PATH);
        
        System.setProperty(LauncherSystemVariables.LAUNCH_NODES_KEY, "true");

        for (String arg : args) {
            if (arg.contains("-launchNodes=")) {
                String launchNodes = arg.replace("-launchNodes=", "");
                if (launchNodes != null) {
                    System.setProperty(LauncherSystemVariables.LAUNCH_NODES_KEY, launchNodes.trim());
                }
            }
            
            else if (arg.toLowerCase().contains("-nocolor")) {
                System.setProperty(LauncherSystemVariables.COLOR_DISABLED_KEY, "true");
            }
        }
        
        try {
            Class<?> clazz = Class.forName("com.edifecs.servicemanager.agent.AgentLauncher", true, classLoader);
            Method method = clazz.getMethod("startAgent", String[].class);
            Object instance = clazz.newInstance();
            method.invoke(instance, (Object) args);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * AgentStarter should never be instantiated.
     */
    private AgentStarter() {
        // Do Nothing
    }
}
