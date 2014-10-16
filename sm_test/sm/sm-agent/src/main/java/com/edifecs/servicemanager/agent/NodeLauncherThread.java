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
package com.edifecs.servicemanager.agent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edifecs.core.configuration.configuration.Node;
import com.edifecs.core.configuration.configuration.Property;
import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.core.memtracer.NodeMemoryTracer;
import com.edifecs.core.memtracer.NodeNotFoundException;

/**
 * Thread to launch and maintain status for a running Node Process.
 * 
 * @author willclem
 */
public class NodeLauncherThread extends Thread {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * Holds the java.home System Property.
     */
    private static final String JAVA_HOME_SYSTEM_VARIABLE = System.getProperty("java.home");

    /**
     * Holds the os.name System Property.
     */
    private static final String OS_NAME_SYSTEM_VARIABLE = System.getProperty("os.name");

    /**
     * Holds the Path to the machines Java executable.
     */
    private String javawBin = JAVA_HOME_SYSTEM_VARIABLE + File.separator + "bin" + File.separator + "java";

    /**
     * Holds the reference to the running Process for this Node.
     */
    private Process process;

    private Node node;

    private NodeMemoryTracer tracer;

    private String jvmOptions;

    private String[] args;

    /**
     * Thread that creates and launches a new JVM for the configured Node.
     * 
     * @param node Node object holding all configurations for the Node to start
     */
    public NodeLauncherThread(final Node node, String jvmOptions, String[] args) {
        this.node = node;
        this.jvmOptions = jvmOptions;
        this.args = args;
        if (OS_NAME_SYSTEM_VARIABLE.toLowerCase().contains("win")) {
            javawBin += ".exe";
        }
    }

    @Override
    public final void run() {
        List<String> cmd = new ArrayList<String>();

        cmd.add(javawBin);

        List<String> properties = new ArrayList<String>();

        if (node.getJvmOptions() != null) {
        	for (Property property : node.getJvmOptions().getProperties()) {
        		properties.add(property.getValue());
        	}
        }

        // Gets the default JVM options from the config.properties file.
        if (jvmOptions != null) {
        	List<String> properties2 = new ArrayList<String>();
        	for (String property : jvmOptions.split(";")) {
        		properties2.add(property);
        	}
        	
        	properties = mergeJVMOptions(properties, properties2);
        }

        for (String property : properties) {
            cmd.add(property);
        }

        cmd.add("-cp");

		String pathSeparator = System.getProperties().getProperty("path.separator");

		// This is a hack to load Tomcat into the system class loader.
		String classpath = System.getProperty("java.class.path") + pathSeparator + SystemVariables.LIB_TOMCAT_DIR + "*";

		cmd.add(classpath);

        cmd.add("com.edifecs.agent.launcher.NodeStarter");
        cmd.add("-nodeName=" + node.getName());
        Collections.addAll(cmd, args);
        if (System.getProperty(SystemVariables.COLOR_DISABLED_KEY) != null)
            cmd.add("-nocolor");

        try {

            logger.info("Launching Node {}...", node.getName());
            if (logger.isDebugEnabled())
                for (String c : cmd) logger.debug(c);

            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.directory(new File(SystemVariables.WORKING_DIRECTORY));

            process = pb.start();

            try {
                tracer = new NodeMemoryTracer(node.getName());
                new Thread(tracer).start();
            } catch (NodeNotFoundException ex) {
                logger.error(ex.getMessage(), ex);
            }

            // any output?
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), true);
            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), false);

            // kick them off
            errorGobbler.start();
            outputGobbler.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

        super.run();
    }

    /**
     * Kills the running process forcefully. This should only be done as a last
     * resort.
     */
    public void killProcess() throws InterruptedException {
        logger.error("Force killing node: {}", node.getName());
        if (tracer != null) {
            tracer.stop();
        }
        process.destroy();
    }

    public NodeMemoryTracer getTracer() {
        return tracer;
    }

    public boolean isRunning() {
        try {
            process.exitValue();
            // TODO: Do something with the exit value to notify user that the
            // node failed to stop properly
            return false;
        } catch (IllegalThreadStateException e) {
            return true;
        }
    }
    
    protected List<String> mergeJVMOptions(List<String> a, List<String> b) {
    	List<String> properties = new ArrayList<String>();

		properties.addAll(a);

        // Gets the default JVM options from the config.properties file.
        for (String opt : b) {
            boolean found = false;
            for (String property : a) {
            	// Check for JVM Options with equal sign
            	if (opt.startsWith(property.split("=")[0])) {
                    found = true;
                    break;
                } else if (opt.startsWith(property.split("(\\d)")[0])) {
                	found = true;
                    break;
                }
            }
            if (!found) {
            	properties.add(opt);
            }
        }

        return properties;
    }

    public Node getNode() {
        return node;
    }
}
