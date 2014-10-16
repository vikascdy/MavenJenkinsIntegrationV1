package com.edifecs.servicemanager.agent;

import com.edifecs.core.configuration.helper.SystemVariables;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

public class AgentLauncher {
    private Logger logger = LoggerFactory.getLogger(AgentLauncher.class);

    private AgentService agentService;
    
    public void startAgent(final String[] args) throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {

        // Load the Log4J.properties file
        PropertyConfigurator.configure(SystemVariables.CONFIGURATION_PATH + "log4j.properties");

    	// TODO: Implement optional input args
        System.setProperty(SystemVariables.NODE_NAME_KEY, "Agent");


        logger.info("Starting Edifecs Agent...");


        agentService = new AgentService(args);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    agentService.shutdown();
                } catch (Throwable e) {
                    logger.error("Fatal Error Shutting down, Force Closing. There may be rouge processes left running");
                }
            }
        });

        try {
        	agentService.start();
        	
            logger.info("Agent Started");
            waitTillInput();
        } catch (Exception e) {
            logger.error("Fatal error found in agent. Shutting down.", e);
            System.exit(1);
        }

        System.exit(0);
    }

    public void waitTillInput() throws InterruptedException {
        while (agentService.isRunning()) {
            Thread.sleep(1000);
        }
    }

}
