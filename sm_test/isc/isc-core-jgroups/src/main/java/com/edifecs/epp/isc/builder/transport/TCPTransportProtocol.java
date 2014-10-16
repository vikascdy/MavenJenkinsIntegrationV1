package com.edifecs.epp.isc.builder.transport;

import org.jgroups.JChannel;
import org.jgroups.protocols.TCP;

import com.edifecs.epp.isc.builder.discovery.DiscoveryProtocol;
import com.edifecs.epp.isc.exception.ClusterBuilderException;

public class TCPTransportProtocol extends TransportProtocol {

    private DiscoveryProtocol discoveryProtocol;
    
    private int bindPort;
    
    @Override
    public void build(JChannel channel) throws ClusterBuilderException {
        if (channel.getProtocolStack().findProtocol("UDP") != null) {
            throw new ClusterBuilderException("Cannot add a TCP protocol if UDP is in use.");
        }
        if (channel.getProtocolStack().findProtocol("TCP") == null) {
            throw new ClusterBuilderException("Must have a JGroups configuration loaded that contains TCP content.");
        }
        
        TCP protocol = (TCP) channel.getProtocolStack().findProtocol("TCP");
        protocol.setBindPort(bindPort);
        
        discoveryProtocol.build(channel);
    }

    public TransportProtocol setBindPort(int bindPort) {
        this.bindPort = bindPort;
        return this;
    }
    
    public TransportProtocol setDiscoveryProtocol(DiscoveryProtocol discoveryProtocol) {
        this.discoveryProtocol = discoveryProtocol;
        return this;
    }

    
}
