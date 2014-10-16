package com.edifecs.agent.stopper;

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

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

import com.edifecs.core.configuration.helper.TypesafeConfigKeys;
import com.edifecs.epp.isc.CommandCommunicator;
import com.edifecs.epp.security.data.token.CertificateAuthenticationToken;
import com.edifecs.epp.security.utils.JKSKeyStoreManager;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.log4j.PropertyConfigurator;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.ptql.ProcessQuery;
import org.hyperic.sigar.ptql.ProcessQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edifecs.core.configuration.Configuration;
import com.edifecs.core.configuration.configuration.Node;
import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder;
import com.edifecs.epp.isc.Address;

public class AgentStopper {
	private static final Logger logger = LoggerFactory
			.getLogger(AgentStopper.class);

	private static CommandCommunicator commandCommunicator;

	private static String serverName;

	private static String clusterName;

	private static Configuration configuration = new Configuration();

	private static boolean forced = false;

	private static void connectToCluster() throws Exception {

		// load configuration
		logger.debug("Loading Configuration...");

        configuration = configuration.loadConfiguration(SystemVariables.SERVICE_MANAGER_ROOT_PATH);

        if (configuration.getServer() == null) {
            logger.error("Configuration.xml file is missing, Terminating nodes and agents forcefully... ");
            terminateAllNodes();
            terminateAgent();
            System.exit(0);
        }

		logger.debug("Configuration loaded : {}", configuration.toString());
		Config config = ConfigFactory.parseFile(new File(SystemVariables.CONFIGURATION_PROPERTIES));

		clusterName = config.getString(TypesafeConfigKeys.CLUSTER_NAME);

		serverName = configuration.getServer().getName();

		Address address = new Address("__AGENT_STOPPER__");

		logger.debug("Address : {}", address.toString());

		logger.debug("Attempting to join Cluster with:");
		logger.debug("    Cluster Name: {}", clusterName);
		logger.debug("    Server Name: {}", serverName);

		CommandCommunicatorBuilder builder = new CommandCommunicatorBuilder(config);
		builder.setAddress(address);

		commandCommunicator = builder.initialize();
		commandCommunicator.connect();

		byte[] cert = new JKSKeyStoreManager(
				SystemVariables.SECURITY_CERTIFICATE_FILE)
				.getRSAEncodedKey("security-system");
		CertificateAuthenticationToken token = new CertificateAuthenticationToken(
                SystemVariables.DEFAULT_SITE_NAME,
                SystemVariables.DEFAULT_TENANT_NAME,
                cert, SystemVariables.DEFAULT_SYSTEM_USER);
		
		try {
			commandCommunicator.getSecurityManager().getAuthenticationManager().loginToken(token);
		} catch (Exception e) {
			logger.debug("No Security Service Found in Cluster. Switching to force close only", e);
            forced = true;
		}

		logger.debug("Successfully connected to cluster.");
		
	}

	public static void main(String[] args) {

        // Load the Log4J.properties file
        PropertyConfigurator.configure(SystemVariables.CONFIGURATION_PATH + "log4j.properties");

		for (String arg : args) {
			if (arg.toLowerCase().contains("-f") || arg.toLowerCase().contains("/f")) {
				forced = true;
				logger.debug("Attempting to stop Agent forced...");
			}
		}
		
		if (!forced) {
			logger.debug("Attempting to stop Agent gracefully...");
		}
		
		// Set Sigars LIB Path
		System.setProperty("org.hyperic.sigar.path", SystemVariables.NATIVE_LIB_PATH);

		try {
			// connect to cluster
			connectToCluster();

			
			// call stopAgent Command from Agent Service
			logger.debug("Sending stopServer to Agent...");
			try {
				Address agentAddress = new Address(serverName);
				Boolean agentResBoolean = (Boolean) commandCommunicator
						.sendSyncMessage(agentAddress, "stopServer");

				logger.debug("Agent Stopped : {}", agentResBoolean);
				
				//TODO: Wait to make sure it stops properly
			} catch (Exception e) {
				logger.error("Unable to gracefully shutdown Agent", e);
			}
			
			// TODO: Also scan the address registry for anything running on the local machine
			
			if (!forced) {
				System.out.println("Problem occured while shutting down Agent. \r\n Do you want to force close the Agent?(Y/N)");
				Scanner scanner = new Scanner(System.in);

				if (scanner.next().equalsIgnoreCase("y") || scanner.next().equalsIgnoreCase("yes")) {
					System.out.println("force closing Agent...");
					forced = true;
				} else {
					System.out.println("Aborting shutdown, processes must be manually shutdown.");
					System.exit(0);
				}
				
				scanner.close();
			}
				
			if (forced) {
				terminateAgent();
			}

			// call stopNode Command from Node Service for all Nodes
			for (Node node : configuration.getServer()
					.getNodes()) {
				try {
					logger.debug("sending stopNode for node : {}",
							node.getName());

					Address nodeAddress = new Address(serverName,
							node.getName());
					Boolean resBoolean = (Boolean) commandCommunicator
							.sendSyncMessage(nodeAddress, "stopNode");

					logger.debug("Node :{}, Stopped : {}", node.getName(),
							resBoolean);
				} catch (Exception e) {
					logger.error("Unable to gracefully shutdown Node: {}", node.getName());
				}
			}
			
			// TODO: Also scan the address registry for anything running on the local machine
			
			if (forced) {
				System.out.println("force closing all Nodes...");
				terminateAllNodes();
			}
			
			// TODO : this should be called in stopServer command, awaiting TODO
			// completion.
			System.exit(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public static void terminateAllNodes() {
        try {
            Sigar sigar = new Sigar();
            ProcessQuery query = new ProcessQueryFactory()
                    .getQuery("State.Name.eq=java,"
                            + "Args.*.re=com\\.edifecs\\.agent\\.launcher\\.NodeStarter");
            long[] pids = query.find(sigar);
            if (pids.length > 0) {
                logger.warn("Detected " + pids.length + " unmanaged nodes running!");
                logger.warn("Killing unmanaged nodes.");
                for (long pid : pids) {
                    logger.error("Force killed process: {}", pid);
                    sigar.kill(pid, -9); // Apparently, -9 is the
                    // "magic number" to force-kill
                    // a process.
                }
            }
        } catch (SigarException ex) {
            logger.error("A Sigar error occurred, preventing the agent from detecting still-running nodes.", ex);
        }
    }

	private static void terminateAgent() {
		try {
			logger.debug("Force closing Agents");
			Sigar sigar = new Sigar();
			ProcessQuery query = new ProcessQueryFactory()
					.getQuery("State.Name.re=^java[w]?$,"
							+ "Args.*.re=com\\.edifecs\\.agent\\.launcher\\.AgentStarter");

			long[] pids = query.find(sigar);
			logger.debug("Sigar Query, PID's : {}", Arrays.toString(pids));

			if (pids.length > 0) {
				logger.warn("Detected {} unmanaged agents running!", pids.length);
				logger.warn("Killing unmanaged agents.");
				for (long pid : pids) {
					logger.debug("SIGAR killing Agent process : {} - {}", pid, sigar.getProcArgs(pid));
					sigar.kill(pid, Sigar.getSigNum("KILL")); // Apparently, -9 is the "magic number" to force-kill a process.
				}
			}
		} catch (SigarException ex) {
			throw new RuntimeException(
					"A Sigar error occurred, preventing force shutting down the Agent: "
							+ ex.getMessage());
		}
	}
}
