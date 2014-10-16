package com.edifecs.epp.isc.builder.discovery;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.jgroups.JChannel;
import org.jgroups.protocols.TCPPING;
import org.jgroups.stack.IpAddress;

import com.edifecs.epp.isc.exception.ClusterBuilderException;

public class TCPPingDiscoveryProtocol extends DiscoveryProtocol {

    private String initialHosts;

    @Override
    public void build(JChannel channel) throws ClusterBuilderException {
        if (channel.getProtocolStack().findProtocol("MPING") != null) {
            try {
                channel.getProtocolStack().removeProtocol("MPING");
            } catch (Exception e) {
                throw new ClusterBuilderException("Unable to remove MPING protocol since TCPPING was selected.", e);
            }
        }

        TCPPING protocol = (TCPPING) channel.getProtocolStack().findProtocol("TCPPING");

        // Configuring initial_hosts for TCPPING
        String[] initialHostsArray = initialHosts.split(",");

        List<IpAddress> ipAddresses = new ArrayList<IpAddress>();
        for (String host : initialHostsArray) {
            host = host.trim();
            String[] hostsArray = host.split(":");

            if(hostsArray.length >= 2) {
                IpAddress ipAddress;
                try {
                    ipAddress = new IpAddress(hostsArray[0], Integer.valueOf(hostsArray[1]));
                    ipAddresses.add(ipAddress);
                } catch (NumberFormatException | UnknownHostException e) {
                    throw new ClusterBuilderException("Initial hosts list is in the incorrect format. Must be in the following format: <ipaddress>:port,<ipaddress>:port,<ipaddress>:port", e);
                }
            }
        }
        protocol.setInitialHosts(ipAddresses);
    }

    public DiscoveryProtocol setInitialHosts(String initialHosts) {
        this.initialHosts = initialHosts;
        return this;
    }

    
    
}
