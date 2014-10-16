package com.edifecs.epp.isc.cluster

import akka.actor.{Address, Actor, ActorLogging}
import com.edifecs.core.configuration.helper.SystemVariables
import com.edifecs.epp.isc.cluster.messages.{InitializeJoiner, FindNodes}

trait ClusterJoinerActor extends Actor with ActorLogging {
  override def receive = {
    case InitializeJoiner(clusterName, address) =>
      initializeJoiner(clusterName, address)
    case FindNodes =>
      findNodes()
  }
  def initializeJoiner(clusterName: String, address: Address): Unit
  def findNodes(): Unit
}

object ClusterJoinerActor {
  final val name = SystemVariables.AKKA_CLUSTER_JOINER_NAME
}