package com.edifecs.epp.isc.cluster

import akka.actor.Address

class NoOpJoiner extends ClusterJoinerActor {
  override def initializeJoiner(clusterName: String, address: Address): Unit =
    log.debug("Starting no-op cluster joiner. This node cannot join a cluster.")
  override def findNodes(): Unit = {}
}
