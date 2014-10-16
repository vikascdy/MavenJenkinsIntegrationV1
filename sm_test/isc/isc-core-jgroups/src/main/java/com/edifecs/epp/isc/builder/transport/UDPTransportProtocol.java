package com.edifecs.epp.isc.builder.transport;

import org.jgroups.JChannel;
import org.jgroups.protocols.UDP;

import com.edifecs.epp.isc.exception.ClusterBuilderException;

public class UDPTransportProtocol extends TransportProtocol {
    
    private int multicastPort;
    
    @Override
    public void build(JChannel channel) throws ClusterBuilderException {
        if (channel.getProtocolStack().findProtocol("TCP") != null) {
            throw new ClusterBuilderException("Cannot add a UDP protocol if TCP is in use.");
        }
        if (channel.getProtocolStack().findProtocol("UDP") == null) {
            throw new ClusterBuilderException("Must have a JGroups configuration loaded that contains UDP content.");
        }
        
        UDP protocol = (UDP) channel.getProtocolStack().findProtocol("UDP");
        protocol.setMulticastPort(multicastPort);
    }

    public TransportProtocol setMulticastPort(int multicastPort) {
        this.multicastPort = multicastPort;
        return this;
    }



}
