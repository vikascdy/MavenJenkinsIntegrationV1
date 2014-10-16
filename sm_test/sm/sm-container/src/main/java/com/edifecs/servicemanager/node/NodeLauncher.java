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
package com.edifecs.servicemanager.node;

import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.servicemanager.log.NodeConsoleLayout;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Main class to launch additional Nodes from the Agent within a separate JVM.
 * 
 * @author willclem
 */
public final class NodeLauncher {
    private Logger logger = LoggerFactory.getLogger(NodeLauncher.class);

    private static NodeService nodeService;

    /**
     * @param args
     *            -nodeName &lt;nodeName&gt; : <B>Required</B> Launches the
     *            configuration based on the given nodeName
     * @throws Exception
     */
    public void startNode(final String[] args) {
        // TODO: Check to see if the working directory is valid

        // Load the Log4J.properties file
        PropertyConfigurator.configure(SystemVariables.CONFIGURATION_PATH + "log4j.properties");

        // Register a shutdown hook to gracefully shutdown the JVM
        Hook hook = new Hook();
        Runtime.getRuntime().addShutdownHook(hook);

        // Parse a JSON config, if necessary.
        if (System.getProperty(SystemVariables.NODE_CONFIG_JSON_KEY) != null) {
            final JsonParser parser = new JsonParser();
            final JsonElement nodeJson = parser.parse(System.getProperty(SystemVariables.NODE_CONFIG_JSON_KEY));
            if (nodeJson.isJsonObject()) {
                try {
                    System.setProperty(SystemVariables.NODE_NAME_KEY,
                         ((JsonObject)nodeJson).get("name").getAsString());
                } catch (NullPointerException ex) {
                    throw new IllegalArgumentException("nodeConfig must contain a 'name' property.");
                }
                final String fullConfig = "{\"clusters\":[{\"servers\":[{\"nodes\":[" + nodeJson + "]}]}]}";
                System.setProperty(SystemVariables.CONFIG_JSON_KEY, fullConfig);
                System.clearProperty(SystemVariables.NODE_CONFIG_JSON_KEY);
            } else {
                throw new IllegalArgumentException("nodeConfig must be a JSON object.");
            }
        }

        // Register the node's name with the logging system.
        final String nodeName = System.getProperty(SystemVariables.NODE_NAME_KEY);
        NodeConsoleLayout.nodeName = nodeName;
        if (nodeName == null) {
        	logger.error("No nodename specified while launching node. Nodename is required. If" +
                " running from the command line, please add the argument '-nodeName=<nodename>'.");
        	System.exit(1);
		}

        try {
            nodeService = new NodeService(args);
            nodeService.start();

            waitTillInput();
        } catch (Exception e) {
            logger.error("Fatal error found in node. Shutting down.", e);
            System.exit(1);
        }
    }

    public void waitTillInput() throws InterruptedException {
        while (nodeService.isRunning()) {
            Thread.sleep(1000);
        }
    }

    /**
     * Thread called when the application shuts down.
     */
    private static class Hook extends Thread {
        public void run() {
        	if(nodeService != null) {
        		nodeService.shutdown();
        	}
            try {
                System.in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
