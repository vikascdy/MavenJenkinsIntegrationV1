package com.edifecs.epp.isc.builder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import com.edifecs.core.configuration.helper.TypesafeConfigKeys;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import org.jgroups.JChannel;

import com.edifecs.epp.isc.CommandCommunicator;
import com.edifecs.epp.isc.builder.auth.AuthProtocol;
import com.edifecs.epp.isc.exception.ClusterBuilderException;
import com.edifecs.epp.isc.exception.NoClusterNameConfiguredException;
import com.edifecs.epp.isc.builder.transport.TransportProtocol;
import com.edifecs.epp.isc.Address;

public class CommandCommunicatorBuilder {

    private InputStream configFile;

    private Config config = ConfigFactory.parseReader(new InputStreamReader(getClass().getResourceAsStream("/default.conf")));
    private Address address;

    private TransportProtocol transportProtocol;
    private AuthProtocol authProtocol;

    public CommandCommunicatorBuilder() throws ClusterBuilderException {}

    public CommandCommunicatorBuilder(Config config, InputStream configFile) {
        this.configFile = configFile;
    }

    public CommandCommunicatorBuilder setTransportProtocol(TransportProtocol transportProtocol) {
        this.transportProtocol = transportProtocol;
        return this;
    }

    public CommandCommunicatorBuilder setAuthProtocol(AuthProtocol authProtocol) {
        this.authProtocol = authProtocol;
        return this;
    }

    public CommandCommunicator initialize() throws ClusterBuilderException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        
        // Initialize
        try {
            // Load JGroups default configuration
            if (configFile == null) {
                configFile = CommandCommunicator.class.getResourceAsStream("/tcp.xml");
            }
            JChannel channel = new JChannel(configFile);

            if (transportProtocol != null) {
                transportProtocol.build(channel);
            }
            
            if (authProtocol != null) {
                authProtocol.build(channel);
            }

            return new CommandCommunicator(channel, config, address);
        } catch (NoClusterNameConfiguredException e) {
        	throw e;
        } catch (Exception e) {
            throw new ClusterBuilderException(e);
        }
    }

    public CommandCommunicatorBuilder setAddress(Address address) {
        this.address = address;
        return this;
    }

    public CommandCommunicatorBuilder setClusterName(String clusterName) {
        return setProperty(TypesafeConfigKeys.CLUSTER_NAME, clusterName);
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
     * other.
     *
     * @return A new CommandCommunicator initialized with a unique address and
     *     cluster name.
     * @throws ClusterBuilderException
     */
    public CommandCommunicator initializeTestMode() throws ClusterBuilderException {
        long time = System.currentTimeMillis();
        int rand = new Random().nextInt(Integer.MAX_VALUE);
        String prefix = System.getProperty("user.name");
        String suffix = Long.toHexString(time) + Integer.toHexString(rand);
        setAddress(new Address(prefix, suffix));
        setClusterName(prefix + suffix);
        return initialize();
    }
}
