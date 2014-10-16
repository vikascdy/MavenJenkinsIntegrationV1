package com.edifecs.epp.isc.builder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import akka.actor.ActorSystem;
import com.edifecs.core.configuration.helper.TypesafeConfigKeys;
import com.edifecs.epp.isc.CommandCommunicator;
import com.edifecs.epp.isc.cluster.NoOpJoiner;
import com.edifecs.epp.isc.exception.ClusterBuilderException;
import com.edifecs.epp.isc.exception.NoClusterNameConfiguredException;
import com.edifecs.epp.isc.Address;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;

public class CommandCommunicatorBuilder {

    private Address address;
    private Config config;

    public CommandCommunicatorBuilder() throws ClusterBuilderException {
        config = ConfigFactory.empty();
    }

    public CommandCommunicatorBuilder(Config config) {
        this.config = config;
    }
    public CommandCommunicatorBuilder(InputStream configFile) {
        config = ConfigFactory.parseReader(new InputStreamReader(configFile));
    }

    public CommandCommunicator initialize() throws ClusterBuilderException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        
        // Initialize
        Config fallback = ConfigFactory
            .parseResources(getClass().getClassLoader(), "default.conf")
            .withFallback(ConfigFactory.load(ActorSystem.class.getClassLoader()));
        try {
            String configuredClusterName = config.getString(TypesafeConfigKeys.CLUSTER_NAME);
            if (configuredClusterName == null || configuredClusterName.equals("")) {
            	throw new NoClusterNameConfiguredException();
            }
            return new CommandCommunicator(config.withFallback(fallback), address);
        } catch (NoClusterNameConfiguredException e) {
        	throw e;
        } catch (Exception e) {
            throw new ClusterBuilderException(e);
        }
    }

    public CommandCommunicatorBuilder setClusterName(String clusterName) {
        return setProperty(TypesafeConfigKeys.CLUSTER_NAME, clusterName);
    }

    public CommandCommunicatorBuilder setAddress(Address address) {
        this.address = address;
        return this;
    }

    public CommandCommunicatorBuilder setProperty(String key, Object value) {
        config = config.withValue(key, ConfigValueFactory.fromAnyRef(
            value, "CommandCommunicatorBuilder"));
        return this;
    }

    /**
     * This initializes a randomly-generated Address and cluster name based off
     * the time connected, machine name, and {@link java.util.Random} to
     * prevent two tests from running at the same time and conflicting with each
     * other. Because of this, cluster discovery is also disabled.
     *
     * @return A new CommandCommunicator initialized with a unique address and
     *     cluster name.
     * @throws ClusterBuilderException
     */
    public CommandCommunicator initializeTestMode() throws ClusterBuilderException {
        String uuid = UUID.randomUUID().toString();
        setAddress(new Address(System.getProperty("user.name"), uuid));
        setClusterName(uuid);

        // Disable cluster joining.
        setProperty(TypesafeConfigKeys.CLUSTER_JOINER_CLASS, NoOpJoiner.class.getName());

        return initialize();
    }
}
