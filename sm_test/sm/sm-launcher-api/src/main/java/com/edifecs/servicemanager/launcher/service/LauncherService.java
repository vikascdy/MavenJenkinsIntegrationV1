package com.edifecs.servicemanager.launcher.service;

import com.edifecs.core.configuration.Configuration;
import com.edifecs.core.configuration.helper.PropertiesException;
import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.core.configuration.helper.TypesafeConfigKeys;
import com.edifecs.epp.isc.Address;
import com.edifecs.epp.isc.CommandCommunicator;
import com.edifecs.epp.isc.exception.ConnectionException;
import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder;
import com.edifecs.epp.isc.exception.ClusterBuilderException;
import com.edifecs.epp.isc.exception.CommandHandlerRegistrationException;
import com.edifecs.epp.isc.exception.HandlerConfigurationException;
import com.edifecs.epp.isc.exception.RegistryUpdateException;
import com.edifecs.epp.security.SessionId;
import com.edifecs.epp.security.data.token.CertificateAuthenticationToken;
import com.edifecs.epp.security.exception.SecurityManagerException;
import com.edifecs.epp.security.utils.JKSKeyStoreManager;
import com.edifecs.servicemanager.launcher.service.exception.CoreServiceTimeoutException;
import com.edifecs.servicemanager.launcher.service.exception.NoClusterDefinedException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public abstract class LauncherService {
    protected static final int THREAD_SLEEP_DURATION = 1000;

    protected boolean running = true;

    protected Configuration configuration = new Configuration();
    private Config argsConfig = ConfigFactory.empty();

    private CommandCommunicator commandCommunicator;
    private Address address;
    private String clusterName;

    public LauncherService(String[] args) {
    	setSystemProperties(args);
    }

    public LauncherService() {}

    private void setSystemProperties(String[] args) {
		for (String arg : args) {
			if (arg.startsWith("-")) {
				String property[] = arg.replaceFirst("-", "").split("=");
				String key = property[0].trim();
                if (property.length != 2) {
                    getLogger().error(
                        "The key '{}' has an invalid value defined. Please verify that the" +
                        " correct values are being supplied:", key);
                } else {
                    String value = property[1].trim();
                    argsConfig = ConfigFactory.parseString(key + " = " + value).withFallback(argsConfig);
                }
			}
		}
	}

    /**
     * Loads the configuration and metadata configurations from the default
     * location.
     *
     * @throws PropertiesException
     *             Thrown if there is a problem loading the Properties files
     */
    protected void loadConfigurations() throws PropertiesException {
        configuration = configuration.loadConfiguration(SystemVariables.SERVICE_MANAGER_ROOT_PATH);
    }

    protected Config loadConfigurationProperties() throws IOException, NoClusterDefinedException {
        return loadConfigurationProperties(new FileInputStream(SystemVariables.CONFIGURATION_PROPERTIES));
    }

    protected Config loadConfigurationProperties(InputStream configFile) throws IOException,
            NoClusterDefinedException {
        Config config = argsConfig.withFallback(
                ConfigFactory.parseReader(new InputStreamReader(configFile)));
        try {
            clusterName = config.getString(TypesafeConfigKeys.CLUSTER_NAME);
        } catch (Exception ex) {
            throw new NoClusterDefinedException();
        }
        if (clusterName == null || clusterName.isEmpty()) {
            throw new NoClusterDefinedException();
        }
        return config;
    }

    protected void connectToCluster(String serverName) throws IOException,
            CommandHandlerRegistrationException, HandlerConfigurationException,
            ConnectionException, RegistryUpdateException, PropertiesException,
            SecurityManagerException, ClusterBuilderException {
        connectToCluster(true, serverName, null);
    }

    protected void connectToCluster(String serverName, String nodeName) throws IOException,
            CommandHandlerRegistrationException, HandlerConfigurationException,
            ConnectionException, RegistryUpdateException, PropertiesException,
            SecurityManagerException, ClusterBuilderException {
        connectToCluster(false, serverName, nodeName);
    }

    private void connectToCluster(boolean agent, String serverName, String nodeName) throws
            CommandHandlerRegistrationException, HandlerConfigurationException, IOException,
            ConnectionException, RegistryUpdateException, PropertiesException,
            SecurityManagerException, ClusterBuilderException {

        // Loads the Configuration for the message API from the config.properties file

        String clusterName = getClusterName();

        if (agent) {
            address = new Address(serverName);
        } else {
            address = new Address(serverName, nodeName);
        }

        getLogger().debug("Attempting to join Cluster with:");
        getLogger().debug("Cluster Name: {}", clusterName);
        getLogger().debug("Server Name: {}", serverName);

        if (!agent) {
            getLogger().debug("Node Name: {}", nodeName);
        }

        String configPath = SystemVariables.CONFIGURATION_PROPERTIES;

        if (agent) {
            // If there is no configuration for the server found, join the
            // cluster using default configurations for auto discovery.
            address = new Address(Configuration.getHostname());
            connectToCluster(address, new File(configPath));
        } else {
            connectToCluster(address, new File(configPath));
        }

        getLogger().debug("Successfully connected to cluster.");
    }

    private void connectToCluster(Address address, File configPath) throws
            IOException, NoClusterDefinedException, ClusterBuilderException {

        CommandCommunicatorBuilder builder = new CommandCommunicatorBuilder(
                loadConfigurationProperties(new FileInputStream(configPath))
        );
        builder.setClusterName(clusterName);
        builder.setAddress(address);

        commandCommunicator = builder.initialize();
        commandCommunicator.connect();

        getLogger().debug("Successfully connected to cluster.");
    }

    /**
     * Wait for Core to start completely. This includes both Security and
     * ContentRepository
     * 
     * @throws InterruptedException
     * @throws CoreServiceTimeoutException
     */
    protected void waitForRequiredServices() throws CoreServiceTimeoutException,
            InterruptedException {
        long startTime = new Date().getTime();

        getLogger().debug("Searching for required services...");
        waitForSecurityService(startTime);
        waitForDoormatService(startTime);
        getLogger().debug("Required Services found.");
    }

    protected void waitForSecurityService(long startTime) throws CoreServiceTimeoutException,
            InterruptedException {
        // Wait for Security to Start
        boolean connected = false;
        while (!connected && isRunning()) {
            try {
                Address address = getCommandCommunicator().getAddressRegistry().getAddressForServiceTypeName(
                        SystemVariables.SECURITY_SERVICE_TYPE_NAME);
                getLogger().debug("Security service found at address: {}", address.toString());

                SessionId userSession = getCommandCommunicator().getSecurityManager().getSessionManager().createAndRegisterNewSession();
                getLogger().debug("Node registered in with session ID: {}", userSession.getSessionId());

                userSession = login();
                getLogger().debug("Node logged in with session ID: {}", userSession.getSessionId());

                connected = true;
            } catch (Exception e) {
                getLogger().debug("Waiting for core security services to launch...");
                if ((new Date().getTime() - startTime) > getCommandCommunicator().getConfig().getDuration(
                        TypesafeConfigKeys.STARTUP_TIMEOUT, TimeUnit.MILLISECONDS)) {
                    throw new CoreServiceTimeoutException();
                }

                Thread.sleep(THREAD_SLEEP_DURATION);
            }
        }
    }

    protected SessionId login() throws InvalidKeyException, KeyStoreException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, UnrecoverableEntryException, CertificateException, IOException,
            SecurityManagerException {
    	byte[] cert = new JKSKeyStoreManager(SystemVariables.SECURITY_CERTIFICATE_FILE).getRSAEncodedKey("security-system");
        return getCommandCommunicator().getSecurityManager().getAuthenticationManager().loginToken(
                new CertificateAuthenticationToken(
                    SystemVariables.DEFAULT_TENANT_NAME,
                    SystemVariables.DEFAULT_ORG_NAME,
                    cert, SystemVariables.DEFAULT_SYSTEM_USER));
    }

    private void waitForDoormatService(long startTime) throws CoreServiceTimeoutException, InterruptedException {
        // Wait for Doormat Service to Start
        boolean connected = false;
        while (!connected && isRunning()) {
            try {
                Address address = getCommandCommunicator().getAddressRegistry().getAddressForServiceTypeName(
                        SystemVariables.NAVIGATION_SERVICE_TYPE_NAME);
                getLogger().debug("Doormat service found at address: {}", address.toString());

                connected = true;
            } catch (Exception e) {
                getLogger().debug("Waiting for core navigation services to launch...", e);
                if ((new Date().getTime() - startTime) > getCommandCommunicator().getConfig().getDuration(
                        TypesafeConfigKeys.STARTUP_TIMEOUT, TimeUnit.MILLISECONDS)) {
                    throw new CoreServiceTimeoutException();
                }

                Thread.sleep(THREAD_SLEEP_DURATION);
            }
        }
    }

    protected String getClusterName() throws PropertiesException {
        if (clusterName == null) try {
            loadConfigurationProperties();
        } catch (Exception ex) {
            return null;
        }
        return clusterName;
    }

    public abstract void shutdown() throws Exception;

    public void preShutdown() {
    }

    public void postShutdown() {
        configuration = null;
        running = false;
    }

    public abstract Logger getLogger();

    public boolean isRunning() {
        return running;
    }

    public CommandCommunicator getCommandCommunicator() {
        return commandCommunicator;
    }

    public void setCommandCommunicator(CommandCommunicator commandCommunicator) {
        this.commandCommunicator = commandCommunicator;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

}
