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

import com.edifecs.core.configuration.Configuration;
import com.edifecs.core.configuration.configuration.Node;
import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.core.configuration.helper.TypesafeConfigKeys;
import com.edifecs.core.memtracer.MemoryTracer;
import com.edifecs.core.memtracer.NodeNotFoundException;
import com.edifecs.core.memtracer.ProcessDetector;
import com.edifecs.core.memtracer.ServerMemoryTracer;
import com.edifecs.epp.isc.Address;
import com.edifecs.servicemanager.launcher.service.LauncherService;
import com.typesafe.config.Config;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * This class contains the main logic around the Agent.
 *
 * @author willclem
 */
public final class AgentService extends LauncherService {
    private static final Logger logger = LoggerFactory.getLogger(AgentService.class);

    /**
     * Holds the threads for all running external Node processes.
     */
    private final Map<String, NodeLauncherThread> nodeThreads = new ConcurrentHashMap<String, NodeLauncherThread>();

    private ServerMemoryTracer tracer;

    private Config config;

    private String[] args;

    public AgentService(String[] args) {
    	super(args);
        this.args = args;
    }

    /**
     * Start the Agent.
     *
     * @throws Exception
     */
    public void start() throws Exception {
        logger.debug("Starting Agent");

        try {
            // Detect other Agent processes.
            if (ProcessDetector.isOtherAgentRunning()) {
                logger.error("Another agent process is still running! Cannot start the Agent.");
                if (getCommandCommunicator() != null) {
                    getCommandCommunicator().disconnect();
                    setCommandCommunicator(null);
                }
                System.exit(1);

                return;
            }

            // Detect and kill unmanaged Node processes.
            ProcessDetector.terminateAllNodes();

        } catch (SigarException | UnsatisfiedLinkError ex) {
			getLogger().error(
					"A Sigar error occurred, preventing the agent from detecting other Agent processes.", ex);
		}

        // Load Configuration Property File
        logger.debug("Attempting to load configuration properties file...");

        loadConfigurationProperties();

        // Start the CPU/Memory tracer.
        startServerMemoryTracer();

        loadConfigurations();
        connectToCluster(Configuration.getHostname());

        if (Boolean.valueOf(System.getProperty("LAUNCH_NODES_KEY"))) {
            // Launch Core Node if there is one set for this Agent
            File coreConfigFile = new File(SystemVariables.CONFIGURATION_PATH + SystemVariables.CORE_CONFIGURATION_FILE);
            if (coreConfigFile.exists()) {
                launchConfiguredNode(configuration.getNode("Core"));
            }
        }

        // Wait for Core to start completely. This includes both Security
        // and ContentRepository
        waitForRequiredServices();

        // Connect Agents Command Handler
        AgentCommandHandler handler = new AgentCommandHandler(this);
        getCommandCommunicator().registerCommandHandler(getAddress(), handler);

        File file = new File(SystemVariables.CONFIGURATION_PATH + SystemVariables.CONFIGURATION_FILE);

        // FIXME: This needs to be updated to pull from the configuration registry or ECM
        // Try to download new Configuration.xml file from the CR
//        try {
//            // TODO: Need to check to see if we need to download the Configuration.xml file or not.
//            InputStream inputStream = getContentRepository().getFile(SystemVariables.CONTENT_REPOSITORY_CONFIGURATION_DIRECTORY, "Configuration.xml");
//
//            IOUtils.copy(inputStream, new FileOutputStream(file));
//        } catch (Exception e) {
//            logger.debug("No configuration found in the content repository.");
//        }

        try {
            if (Boolean.valueOf(System.getProperty("LAUNCH_NODES_KEY"))) {
                launchConfiguredNodes(configuration);
            }
        } catch (Exception e) {
            logger.info("No valid configuration.xml file found");
        }
    }

    /**
     * Attempts to gracefully stop all running threads. If the thread is still
     * running after the timeout, it will force kill all processes.<br/>
     * <br/>
     * This does not shut down the CORE Node within the cluster.
     *
     * @throws Exception
     *             Thrown if there is any issue stopping a process.
     */
    public void stopNodes(List<NodeLauncherThread> nodeThreadsToStop) throws InterruptedException {
        // Wait the configured shutdown timeout value
        Long startTime = new Date().getTime();
        Long timeout = getCommandCommunicator().getConfig().getDuration(TypesafeConfigKeys.SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS);
        boolean success = false;
        while(((new Date().getTime() - startTime) < timeout) && !success) {
            success = true;
            // If one thread is alive, set success == false
            for (NodeLauncherThread thread : nodeThreadsToStop) {
                logger.debug("Waiting for '{}' node to stop", thread.getNode().getName());
                success = success && !thread.isRunning();
                thread.sleep(1000L);
            }
        }

        // Force kill process if needed
        for (NodeLauncherThread nodeThread : nodeThreadsToStop) {
            if(nodeThread.isRunning()) {
                nodeThread.killProcess();
            }
        }
    }

    public void stopAllNodes() throws Exception {
        // Stop all node memory tracer threads.
        List<NodeLauncherThread> nodeThreadsToStop = new ArrayList<>();
        for (NodeLauncherThread nodeThread : nodeThreads.values()) {
            if (!nodeThread.getNode().getName().equals(SystemVariables.CORE_NODE_NAME)) {
                nodeThreadsToStop.add(nodeThreads.remove(nodeThread.getNode().getName()));
                if(nodeThread.getTracer() != null) {
                    nodeThread.getTracer().stop();
                }
            }
        }

    	// Shutdown all non Core Nodes
    	stopNodes(nodeThreadsToStop);

    	// Shutdown the Core Node
        List<NodeLauncherThread> coreNodeThreadsToStop = new ArrayList<>();
        for (NodeLauncherThread nodeThread : nodeThreads.values()) {
            if (nodeThread.getNode().getName().equals(SystemVariables.CORE_NODE_NAME)) {
                coreNodeThreadsToStop.add(nodeThreads.remove(nodeThread.getNode().getName()));
                if (nodeThread.getTracer() != null) {
                    nodeThread.getTracer().stop();
                }
            }
        }
        stopNodes(coreNodeThreadsToStop);

        // Force Kill all Nodes through Sigar that were not started as part of this agent
        ProcessDetector.terminateAllNodes();
    }

    private void sendShutdownCommand(Address address) {
        logger.debug("Stopping Node: " + address.toString());

        try {
            Boolean response = (Boolean) getCommandCommunicator().sendSyncMessage(address, "discardNode");

            if(!response) {
                logger.error("Error sending shutdown command to {}", address.toString());
            } else {
                long shutdownTimeout = getCommandCommunicator().getConfig().getDuration(
                        TypesafeConfigKeys.SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS);
                for (int timeout = 0;
                     timeout < shutdownTimeout && isNodesRunning();
                     timeout += THREAD_SLEEP_DURATION) {
                    Thread.sleep(THREAD_SLEEP_DURATION);
                }

                if (isNodesRunning()) {
                    // Not all Nodes responded with shutdown accepted response.
                    logger.error("Node {} did not respond to the shutdown command.", address.toString());
                }
            }
        } catch (Exception e) {
            logger.error("Error sending shutdown command to " + address.toString(), e);
        }
    }

    /**
     * This method is used to determine if there is a node still running or not.
     *
     * @return
     */
    private boolean isNodesRunning() {
        for (Entry<String, NodeLauncherThread> entry : nodeThreads.entrySet()) {
            if (entry.getValue() != null && entry.getValue().isRunning()
                    && (entry.getValue().getTracer() == null
                    || !SystemVariables.CORE_NODE_NAME.equals(
                            entry.getValue().getTracer().getNodeName()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Attempts to gracefully stop all running threads. If the thread is still
     * running after the timeout, it will force kill all processes.
     * @throws Exception
     *             Thrown if there is any issue stopping a process.
     */
    public void shutdown() throws Exception {
        logger.info("Stopping Edifecs Agent...");

        preShutdown();

        stopAllNodes();

        // TODO: This can cause timeout exceptions for message passing and is the cause of shutdown failures
//        if (getCommandCommunicator() != null && getCommandCommunicator().isConnected()) {
//            getCommandCommunicator().disconnect();
//        }

        if (getTracer() != null && getTracer().isRunning()) {
            getTracer().stop();
        }

        postShutdown();

        running = false;

        logger.info("Edifecs Agent Stopped");
    }

    /**
     * Launches all nodes within the given server.
     *
     * @param configuration
     *            Uses Server to spawn all the nodes within it
     * @throws InterruptedException
     */
    private void launchConfiguredNodes(final Configuration configuration) throws InterruptedException {
        for (Node node : configuration.getNodes()) {
            if(!node.getName().equals("Core")) {
                launchConfiguredNode(node);
            }
        }
    }

    private void launchConfiguredNode(Node node) throws InterruptedException {
        Config config = getCommandCommunicator().getConfig();
        String jvmOptions = config.getString(
            TypesafeConfigKeys.DEFAULT_NODE_JVM_OPTS);

        NodeLauncherThread nodeThread = new NodeLauncherThread(node, jvmOptions, args);
        nodeThreads.put(node.getName(), nodeThread);
        nodeThread.start();
        boolean started = false;
        while (!started && isRunning()) {
            if (getCommandCommunicator().getAddressRegistry().getAddressForNodeName(node.getName()) != null) {
                started = true;
            }
            Thread.sleep(100);
            // TODO: Handle startup failure case
        }
    }

    /*
     *
     * Methods to remove
     */

    @Deprecated
    public MemoryTracer getTracerForNode(String nodeName) throws NodeNotFoundException {
        NodeLauncherThread thread = nodeThreads.get(nodeName);
        if (thread == null) {
            throw new NodeNotFoundException("No node named '" + nodeName + "' is currently running.");
        }
        return thread.getTracer();
    }

    public ServerMemoryTracer getTracer() {
        return tracer;
    }

    protected void startServerMemoryTracer() {
        tracer = new ServerMemoryTracer();
        new Thread(tracer).start();
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
