package com.edifecs.epp.isc.builder.discovery;

import org.jgroups.JChannel;
import org.jgroups.protocols.MPING;

import com.edifecs.epp.isc.exception.ClusterBuilderException;

public class MPingDiscoveryProtocol extends DiscoveryProtocol {

    private int multicastPort = 45588;

    @Override
    public void build(JChannel channel) throws ClusterBuilderException {
        if (channel.getProtocolStack().findProtocol("MPING") != null) {
            try {
                channel.getProtocolStack().removeProtocol("TCPPING");
            } catch (Exception e) {
                throw new ClusterBuilderException("Unable to remove TCPPING protocol since MPING was selected.", e);
            }
        }
        
        MPING protocol = (MPING) channel.getProtocolStack().findProtocol("MPING");

        protocol.setMcastPort(multicastPort);
    }

    public DiscoveryProtocol setMulticastPort(int multicastPort) {
        this.multicastPort = multicastPort;
        return this;
    }

    
}
