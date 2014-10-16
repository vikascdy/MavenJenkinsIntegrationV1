package com.edifecs.epp.isc.builder;

import org.jgroups.JChannel;

import com.edifecs.epp.isc.exception.ClusterBuilderException;

public interface IProtocol {
    
    abstract void build(JChannel channel) throws ClusterBuilderException;

}
