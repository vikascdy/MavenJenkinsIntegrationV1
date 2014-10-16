package com.edifecs.epp.isc.builder.discovery;

import akka.actor.ActorSystem;
import com.edifecs.epp.isc.exception.ClusterBuilderException;
import com.edifecs.epp.jgroups.protocol.ETCD;
import org.jgroups.JChannel;

public class EtcdDiscoveryProtocol extends DiscoveryProtocol {

    public static final String ETCD_ADDRESS   = "etcd.address";
    public static final String ETCD_PORT      = "etcd.port";
    public static final String ETCD_DIRECTORY = "etcd.directory";
    public static final String ETCD_TIMEOUT   = "etcd.timeout";
    public static final String ETCD_TTL       = "etcd.ttl";

    private final ActorSystem system;

    public EtcdDiscoveryProtocol(ActorSystem system) {
        this.system = system;
    }

    @Override
    public void build(JChannel channel) throws ClusterBuilderException {
        ETCD.register();
        ETCD protocol = new ETCD(system);
        if (System.getProperty(ETCD_ADDRESS) != null)
            protocol.setEtcdAddress(System.getProperty(ETCD_ADDRESS));
        if (System.getProperty(ETCD_PORT) != null)
            protocol.setEtcdPort(Integer.parseInt(System.getProperty(ETCD_PORT)));
        if (System.getProperty(ETCD_DIRECTORY) != null)
            protocol.setEtcdDirectory(System.getProperty(ETCD_DIRECTORY));
        if (System.getProperty(ETCD_TIMEOUT) != null)
            protocol.setEtcdTimeout(Long.parseLong(System.getProperty(ETCD_TIMEOUT)));
        if (System.getProperty(ETCD_TTL) != null)
            protocol.setEtcdTtl(Long.parseLong(System.getProperty(ETCD_TTL)));

        if (channel.getProtocolStack().findProtocol("MPING") != null) {
            try {
                channel.getProtocolStack().replaceProtocol(
                    channel.getProtocolStack().findProtocol("MPING"),
                    protocol);
            } catch (Exception e) {
                throw new ClusterBuilderException("Unable to remove MPING protocol since ETCD was selected.", e);
            }
        } else if (channel.getProtocolStack().findProtocol("TCPPING") != null) {
            try {
                channel.getProtocolStack().replaceProtocol(
                    channel.getProtocolStack().findProtocol("TCPPING"),
                    protocol);
            } catch (Exception e) {
                throw new ClusterBuilderException("Unable to remove TCPPING protocol since ETCD was selected.", e);
            }
        } else {
            channel.getProtocolStack().insertProtocolAtTop(protocol);
        }
    }
}
