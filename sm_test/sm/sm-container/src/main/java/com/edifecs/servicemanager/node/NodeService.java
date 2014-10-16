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

import static com.edifecs.servicemanager.api.ServiceAnnotationProcessor.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.edifecs.core.configuration.Cartridge;
import com.edifecs.core.configuration.Configuration;
import com.edifecs.core.configuration.helper.TypesafeConfigKeys;
import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder;
import com.edifecs.epp.packaging.manifest.PhysicalComponent;
import com.edifecs.epp.packaging.manifest.ServiceScheduling;
import com.edifecs.servicemanager.api.ServiceRef;
import com.edifecs.servicemanager.api.ServiceRegistry;
import com.edifecs.servicemanager.node.exception.CartridgeInstallationFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edifecs.agent.launcher.classloader.SMClassLoader;
import com.edifecs.core.configuration.configuration.Service;
import com.edifecs.core.configuration.helper.PropertiesException;
import com.edifecs.core.configuration.helper.SystemVariables;

import com.edifecs.epp.isc.Address;
import com.edifecs.epp.isc.core.LogFile;
import com.edifecs.epp.isc.core.ServiceStatus;
import com.edifecs.epp.isc.exception.ServiceException;
import com.edifecs.servicemanager.api.AbstractService;
import com.edifecs.servicemanager.launcher.service.LauncherService;
import com.edifecs.servicemanager.node.threads.ServiceStartThread;
import com.edifecs.servicemanager.node.threads.StopNodeThread;

/**
 * Contains all major methods for starting and controlling a ServiceManager
 * Node.
 *
 * @author willclem
 */
public class NodeService extends LauncherService {
    private static final Logger logger = LoggerFactory.getLogger(NodeService.class);

    private boolean coreService = false;

    public NodeService(String[] args) {
        super(args);
    }

    public NodeService() {
        super();
    }

    public void start(String nodeName, InputStream configProperties) {
        try {
            getLogger().info("Starting Empty Node");

            loadConfigurationProperties(configProperties);

            // Connect to the Cluster
            String serverName = Configuration.getHostname();

            CommandCommunicatorBuilder builder = new CommandCommunicatorBuilder();
            builder.setAddress(new Address(serverName, nodeName));
            setCommandCommunicator(builder.initializeTestMode());

            getLogger().debug("Registering Node Handler at address {}.", getCommandCommunicator().getAddress());

            getCommandCommunicator().registerCommandHandler(
                    getCommandCommunicator().getAddress(),
                    new NodeCommandHandler(this));

            getCommandCommunicator().connect();

            logger.info("Node Started: " + nodeName);
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
    }

    public void start() {
        try {
            String nodeName = System.getProperty(SystemVariables.NODE_NAME_KEY);

            getLogger().info("Starting Node: " + nodeName);
            try {
                loadConfigurationProperties();

                if (nodeName.equals("Core")) {
                    coreService = true;
                }

                if (System.getProperty(SystemVariables.CONFIG_JSON_KEY) != null) {
                    configuration.loadConfigurationFromJSON(
                            new ByteArrayInputStream(System.getProperty(SystemVariables.CONFIG_JSON_KEY).getBytes()));
                    configuration.loadMetadataAndAppConfigurations(SystemVariables.SERVICE_MANAGER_ROOT_PATH);
                } else {
                    loadConfigurations();
                }
                if (configuration.getNode(nodeName) == null || configuration.getNode(nodeName).getServices().size() == 0) {
                    getLogger().error("* Unable to locate any Services to load...");
                }
            } catch (PropertiesException ex) {
                getLogger().error("* Failed to load configuration files. Shutting down node...", ex);
                System.exit(1);
            } catch (Exception ex) {
                getLogger().error("* No Node found with the name {}. Shutting down node...", nodeName);
                System.exit(1);
            }

            // Connect to the Cluster
            String serverName = configuration.getServerName();
            connectToCluster(serverName, nodeName);

            getLogger().debug("Registering Node Handler at address {}.", getCommandCommunicator().getAddress());

            getCommandCommunicator().registerCommandHandler(getCommandCommunicator().getAddress(),
                    new NodeCommandHandler(this));

            // TODO: Start Security Service

            if (!coreService) {
                // Wait for Core to start completely. This includes Security,
                // ContentRepository, and Doormat.
                waitForRequiredServices();

                // Create and Start pre-configured services from node
                getLogger().debug("Installing and Running Services");
                installCreateStartServices(configuration.getNode(nodeName).getServices());
            } else if (coreService) {
                // Create and Start pre-configured services from node
                getLogger().debug("Installing and Running Core Services");

                List<Service> services = configuration.getNode(nodeName).getServices();

                // Start ESM First
                Service esmService = null;
                for (Service service : services) {
                    if (service.getServiceType().equals("esm-service")) {
                        esmService = service;
                    }
                }
                if (esmService != null) {
                    installCreateService(esmService);
                    startService(esmService, true);
                    services.remove(esmService);
                }

                login();

                installCreateStartServices(services);
            }

            logger.info("Node Started: " + nodeName);
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
    }

    public void installCartridge(File cartridgeFile) throws PropertiesException {
        if (cartridgeFile.getName().endsWith(".zip")) {
            // TODO: Unzip the contents into temp folder
            throw new UnsupportedOperationException("ZIP Support not yet implemented");
        } else if (cartridgeFile.getName().endsWith(".tar.gz")) {
            // TODO: untar the contents into temp folder
            throw new UnsupportedOperationException("TAR Support not yet implemented");
        } else if (cartridgeFile.isDirectory()) {
            configuration.addAppDirectory(cartridgeFile);
        } else {
            //FIXME: Throw a useful exception
            throw new CartridgeInstallationFailure("Cartridge installation failure");
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void installCreateServices(List<Service> services) throws Exception {
        getLogger().debug("Attempting to Install [{}] services...", services.size());

        // Loop through all services to initialize them
        for (Service service : services) {
            try {
                installCreateService(service);
            } catch (Exception e) {
                getLogger().error(e.getMessage(), e);
                // TODO: This needs to send out an error status update
            }
        }
    }

    public void startServices(List<Service> services) throws Exception {
        getLogger().debug("Attempting to start [{}] services...", services.size());

        // Loop through all services to start them
        for (Service service : services) {
            try {
                startService(service, false);
            } catch (Exception e) {
                getLogger().error("Failure starting {} service", service.getName(), e);
            }
        }
    }

    public void installCreateStartServices(List<Service> services) throws Exception {
        installCreateServices(services);

        startServices(services);
    }

    public void installCreateService(Service service) throws ServiceException, PropertiesException {
        installCreateService(
                service.getName(),
                service.getServiceType(),
                service.getVersion(),
                configuration.getServiceProperties(service),
                configuration.getServiceResources(service));
    }

    public void startService(Service service, boolean waitTillStarted) throws ServiceException, InterruptedException {
        startService(service.getName(), waitTillStarted);
    }

    public void installCreateService(String serviceName, String serviceType, String version, Properties properties,
                                     Map<String, Properties> resources) throws ServiceException, PropertiesException {
        // Get the service configurations for the Service from metadata
        PhysicalComponent serviceMeta = configuration.getServiceManifestByName(serviceType);

        // Create and register service
        createService(serviceName, serviceType, version, serviceMeta, properties, resources);

        getLogger().debug("Service Successfully Created, Name: {} - Type: {} - {}", serviceName, serviceType, version);
    }

    public List<LogFile> getLogs(String serviceName)
            throws ServiceException {
        return ServiceRegistry.getLocalServiceLogFiles(serviceName);
    }

    public String getLog(String serviceName, String logFileName)
            throws ServiceException {
        try {
            return ServiceRegistry.getLocalServiceLogFile(serviceName,
                    logFileName);
        } catch (IOException e) {
            throw new ServiceException(e);
        }
    }

    public boolean startService(final String serviceName, boolean waitTillStarted)
            throws ServiceException, InterruptedException {
        ServiceStatus status = ServiceRegistry.getLocalServiceStatus(serviceName);

        if (status == null) {
            throw new ServiceException("Service not found: " + serviceName);
        }

        switch (status) {
            case Started:
                throw new ServiceException("Service already started: " + serviceName);
            case Starting:
                throw new ServiceException("Service already starting: " + serviceName);
            default:
                getLogger().info("Starting Service: {}", serviceName);

                ServiceRef registeredService = ServiceRegistry.getLocalService(serviceName);

                ServiceStartThread thread = new ServiceStartThread(
                        registeredService,
                        getCommandCommunicator().getSecurityManager().getSessionManager()
                                .getCurrentSession());
                Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
                    public void uncaughtException(Thread th, Throwable ex) {
                        getLogger().error("Service " + serviceName + " failed to start properly", ex);
                        th.interrupt();
                    }
                };
                thread.setUncaughtExceptionHandler(h);
                thread.start();
                if (waitTillStarted) {
                    getLogger().debug("Waiting for Service to start: {}", serviceName);
                    thread.join(getCommandCommunicator().getConfig().getDuration(
                            TypesafeConfigKeys.STARTUP_TIMEOUT, TimeUnit.MILLISECONDS));
                    getLogger().debug("Service started: {}", serviceName);
                }

                return true;
        }
    }

    public boolean stopAllServices() throws ServiceException {

        boolean success = true;
        for (String serviceName : ServiceRegistry.getLocalServiceNames()) {
            try {
                stopService(serviceName);
            } catch (ServiceException e) {
                getLogger().error(e.getMessage(), e);
                success = false;
            }
        }

        return success;
    }

    public boolean stopService(String serviceName) throws ServiceException {
        getLogger().debug("Attempting to stop service: {}", serviceName);

        ServiceStatus status = ServiceRegistry.getLocalServiceStatus(serviceName);

        if (status == null) {
            throw new ServiceException("Service not found: " + serviceName);
        }

        if (!status.equals(ServiceStatus.Started)) {
            throw new ServiceException("Only Started Services can be Stopped: "
                    + serviceName);
        }

        ServiceRef registeredService = ServiceRegistry.getLocalService(serviceName);

        try {
            registeredService.stop();
        } catch (Exception e) {
            throw new ServiceException(
                    "Error Stopping Service: " + serviceName, e);
        }

        getLogger().debug("Service Stopped: {}", serviceName);

        return true;
    }

    public boolean createService(String serviceName, String serviceType, String version, Properties properties,
                                 Map<String, Properties> resources) throws ServiceException {

        // Get the service configurations for the Service from metadata
        PhysicalComponent serviceMeta = configuration.getServiceManifestByName(serviceType);

        return createService(serviceName, serviceType, version, serviceMeta, properties, resources);
    }

    public boolean createService(String serviceName, String serviceType, String version, PhysicalComponent serviceMeta,
                                 Properties properties, Map<String, Properties> resources) throws ServiceException {

        getLogger().debug("Registering Service: {} - {} - {}", serviceName, serviceType, version);

        // Throw error is no configurations found.
        if (serviceMeta == null) {
            throw new ServiceException("No Service Configuration found for service: " + serviceType + " - " + version);
        }

        try {
            // Create and register a new Service within the Service Registry.
            SMClassLoader smclassLoader = new SMClassLoader(this.getClass().getClassLoader());

            // Get Metadata configuration for Service
            Cartridge cartridge = configuration.getCartridgeByServiceName(serviceType);

            if (cartridge == null) {
                throw new ServiceException("No Metadata information found for service: " + serviceName);
            }

            File path = new File(cartridge.getPath() + File.separator +
                    "services" + File.separator +
                    serviceType + File.separator +
                    SystemVariables.LIB_FOLDER_NAME);
            if (!path.exists()) {
                throw new ServiceException("No lib path found for the service '" + serviceType + "' at '" + path.getAbsolutePath() + "'");
            }
            try {
                smclassLoader.registerFile(path);
            } catch (MalformedURLException e) {
                throw new ServiceException(e);
            } catch (Exception e) {
                throw new ServiceException(e);
            }

            final String className = ((ServiceScheduling) serviceMeta.scheduling()).classname();

            try {
                final Class<? extends AbstractService> serviceClass = (Class<? extends AbstractService>) smclassLoader.loadClass(className);
                final Constructor<? extends AbstractService> constructor = serviceClass.getConstructor();
                Thread.currentThread().setContextClassLoader(smclassLoader);
                final AbstractService serviceInstance = constructor.newInstance();
                final ServiceRef createdService = processAnnotatedService(
                        serviceInstance, serviceName, getCommandCommunicator(), properties, resources);
                getLogger().debug("Registered Service: {}", createdService);
            } catch (ClassNotFoundException e) {
                throw new ServiceException("Service class not found for service type '" + serviceType + "' with classname '" + className + "'", e);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new ServiceException("Fatal error starting service '" + serviceType + "' with classname '" + className + "'", e);
            }
        } catch (Exception e) {
            throw new ServiceException("Error creating service instance", e);
        }
        return true;

    }

    public void shutdown() {
        String nodeName = getCommandCommunicator().getAddress().getNodeName();

        getLogger().info("Shutting down '{}' node...", nodeName);

        preShutdown();

        try {
            // Stop all ServiceManager services.
            for (String service : ServiceRegistry.getLocalServiceNames()) {
                try {
                    ServiceRef registeredService = ServiceRegistry.getLocalService(service);
                    registeredService.stop();
                    logger.info("Service stopped: {}", service);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }

            if (getCommandCommunicator() != null && getCommandCommunicator().isConnected()) {
                getCommandCommunicator().disconnect();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        postShutdown();

        running = false;

        logger.info("Stopped '{}' node", nodeName);
    }

    public void shutdownAsync() {
        StopNodeThread thread = new StopNodeThread(getCommandCommunicator());
        thread.start();
    }

    @Override
    public Logger getLogger() {
        return logger;
    }


}
