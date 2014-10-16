package com.edifecs.epp.isc.cluster

import java.util.{TimerTask, Timer}

import scala.collection.immutable
import scala.collection.mutable
import scala.collection.JavaConversions._
import scala.concurrent.duration._

import akka.actor._
import akka.pattern.ask
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

import com.edifecs.core.configuration.helper.SystemVariables
import com.edifecs.epp.isc
import com.edifecs.epp.isc.communicator.AddressRegistry
import com.edifecs.epp.isc.cluster.messages._

import scala.util.{Failure, Success}

class ClusterRootActor(
  clusterName: String,
  myAddress: com.edifecs.epp.isc.Address,
  addressRegistry: AddressRegistry,
  joinerClass: Class[_ <: ClusterJoinerActor]
) extends Actor with ActorLogging {

  import ClusterRootActor._

  val cluster = Cluster(context.system)
  private var joined = false
  private var joinerRef: ActorRef = null
  private val awaitingNotification =
    new  mutable.HashMap[isc.Address, mutable.Set[(Long, ActorRef)]]
    with mutable.MultiMap[isc.Address, (Long, ActorRef)]
  private val timer = new Timer("Cluster-Joiner-Timer", true)
  private implicit val dispatcher = context.system.dispatcher
  private implicit val timeout: akka.util.Timeout = 10 seconds

  override def preStart() {
    log.debug("--- STARTING NODE {} IN CLUSTER {} ---", myAddress, clusterName)
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
    joinerRef = context.actorOf(Props(joinerClass), ClusterJoinerActor.name)
    joinerRef ! InitializeJoiner(clusterName, cluster.selfAddress)
    timer.scheduleAtFixedRate(new TimerTask {
      override def run() = self ! FindNodes
    }, 0, 10000)
  }

  override def receive = {
    case JoinAttemptFrom(address) =>
      if (!joined) {
        log.info("Join attempt from {}; becoming seed node.", address)
        joined = true
        cluster.joinSeedNodes(immutable.Seq(cluster.selfAddress))
      }
    case JoinAttemptTo(address) =>
      if (!joined) {
        log.info("Joining to seed node: {}", address)
        joined = true
        cluster.joinSeedNodes(immutable.Seq(address))
      }
    case FindNodes =>
      if (!joined) joinerRef ! FindNodes
    case NodeAddressRequest =>
      sender() ! NodeAddressResponse(myAddress)
    case MemberUp(member) =>
      log.info("Member is Up: {}", member.address)
      val memberRef = context.actorSelection(RootActorPath(member.address) / "user" / name)
      (memberRef ? NodeAddressRequest).onComplete {
        case Success(NodeAddressResponse(address)) =>
          addressRegistry.registerNode(address, member.address)
          awaitingNotification.remove(address).map(_.foreach(_._2 ! Registered(address)))
        case Failure(ex) =>
          log.error("Failed to get node address for member " + member, ex)
      }
    case UnreachableMember(member) =>
      log.warning("Member detected as unreachable: {}", member)
      addressRegistry.getRegisteredNodeAddresses find { nodeAddress =>
        addressRegistry.getAddressForNode(nodeAddress) == member.address
      } map addressRegistry.unregisterNode
    case MemberRemoved(member, previousStatus) =>
      log.info(s"Member is Removed: {} after {}", member.address, previousStatus)
      addressRegistry.getRegisteredNodeAddresses find { nodeAddress =>
        addressRegistry.getAddressForNode(nodeAddress) == member.address
      } map addressRegistry.unregisterNode
    case NotifyWhenRegistered(address, notifyTimeout) =>
      if (addressRegistry.getAddressForNode(address) != null)
        sender() ! Registered(address)
      else {
        val tuple = (scala.util.Random.nextLong(), sender())
        awaitingNotification.addBinding(address, tuple)
        timer.schedule(new TimerTask() {
          override def run() = awaitingNotification.removeBinding(address, tuple)
        }, notifyTimeout.toMillis)
      }
    case _: MemberEvent => // ignore
  }

  override def postStop() = {
    log.debug("--- STOPPING NODE {} IN CLUSTER {} ---", myAddress, clusterName)
    timer.cancel()
    if (joined) {
      cluster.leave(cluster.selfAddress)
      joined = false
    }
    joinerRef = null
  }
}

object ClusterRootActor {
  final val name = SystemVariables.AKKA_CLUSTER_ROOT

  def createInstance(
    clusterName: String,
    address: com.edifecs.epp.isc.Address,
    addressRegistry: AddressRegistry,
    joinerClass: Class[_ <: ClusterJoinerActor]
  )(
    implicit system: ActorSystem
  ): ActorRef =
    system.actorOf(
      Props(classOf[ClusterRootActor], clusterName, address, addressRegistry, joinerClass),
      name
    )
}
