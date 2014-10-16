package com.edifecs.epp.isc.cluster

import java.io._
import java.net.{SocketTimeoutException, DatagramPacket, MulticastSocket, InetAddress}

import akka.actor.Address
import com.edifecs.core.configuration.helper.TypesafeConfigKeys
import com.edifecs.epp.isc.cluster.messages.{JoinAttemptFrom, JoinAttemptTo}
import org.slf4j.LoggerFactory

class UdpMulticastJoiner extends ClusterJoinerActor {
  private final val logger = LoggerFactory.getLogger(getClass)
  
  import UdpMulticastJoiner._

  val id = scala.util.Random.nextLong()
  private var clusterName: String = null
  private var address: Address = null
  private val config = context.system.settings.config
  private val tcpPort = config.getInt(TypesafeConfigKeys.AKKA_PORT)
  private val udpPort = config.getInt("isc.cluster.udp.port")
  private val udpGroupAddress = InetAddress.getByName(config.getString("isc.cluster.udp.group"))
  private var running = false
  private var mcastSocket: MulticastSocket = null
  private var receiverThread: ReceiverThread = null

  override def initializeJoiner(clusterName: String, address: Address) = {
    this.clusterName = clusterName
    this.address = address
    logger.info("Starting UdpMulticastJoiner, group {}:{}.", udpGroupAddress.getHostAddress, udpPort)
    mcastSocket = new MulticastSocket(udpPort)
    mcastSocket.setSoTimeout(500)
    mcastSocket.joinGroup(udpGroupAddress)
    running = true
    receiverThread = new ReceiverThread
    receiverThread.start()
  }

  override def findNodes() = synchronized {
    logger.debug("Finding nodes...")
    val join = serialize(JoinMessage(id, clusterName, address))
    val sendPacket = new DatagramPacket(join, 0, join.length, udpGroupAddress, udpPort)
    logger.debug(s"Sent Multicast Packet ${join} ${id} ${clusterName} ${Address} ${udpGroupAddress} ${udpPort}")
    mcastSocket.send(sendPacket)
    logger.debug(s"Sent Multicast Packet Complete")
  }

  override def postStop() = {
    running = false
    receiverThread.join()
    mcastSocket.close()
  }

  private lazy val myInetAddress =
    InetAddress.getByName(context.system.settings.config.getString(TypesafeConfigKeys.AKKA_HOSTNAME))

  private def addressFrom(name: String, addr: InetAddress, port: Int) =
    Address("akka.tcp", name, addr.getHostAddress, port)

  private class ReceiverThread extends Thread {
    setDaemon(true)
    override def run() = while (running) {
      val bytes = Array.ofDim[Byte](1024)
      val packet = new DatagramPacket(bytes, bytes.length)
      try {
        mcastSocket.receive(packet)
        deserialize(bytes) match {
          case JoinMessage(joinId, joinClusterName, theirAddress)
            if joinClusterName == clusterName && joinId != id =>
            context.parent ! JoinAttemptFrom(theirAddress)
            val accept = serialize(AcceptMessage(id, joinId, clusterName, address))
            val sendPacket = new DatagramPacket(accept, 0, accept.length, udpGroupAddress, udpPort)
            logger.debug(s"Receiver Thread Run Join Message ${id} ${joinId} ${clusterName} ${address}")
            mcastSocket.send(sendPacket)
          case AcceptMessage(theirId, myId, _, theirAddress)
            if myId == id && theirId != myId =>
            logger.debug(s"Receiver Thread Run Accept Message ${myId} ${id} ${theirId}")
            context.parent ! JoinAttemptTo(theirAddress)
          case q: AnyRef =>
            logger.debug("Discarding: {}", q)
        }
      } catch {
        case ex: SocketTimeoutException => // Do nothing.
        case ex: SerializationException =>
          logger.debug(s"Receiver Thread Run Serialization Exception ${ex.getMessage}")
          logger.warn(ex.getMessage)
      }
    }
  }
}

object UdpMulticastJoiner {

  case class JoinMessage(
    id: Long,
    clusterName: String,
    address: Address)

  case class AcceptMessage(
    myId: Long,
    yourId: Long,
    clusterName: String,
    address: Address)

  def serialize(obj: Serializable): Array[Byte] = {
    val baos = new ByteArrayOutputStream
    val oos = new ObjectOutputStream(baos)
    try {
      oos.writeObject(obj)
      baos.toByteArray
    } catch {
      case ex: IOException =>
        throw new SerializationException("Serialization failed.", ex)
    } finally oos.close()
  }

  def deserialize(bytes: Array[Byte]): AnyRef = {
    val bais = new ByteArrayInputStream(bytes)
    val ois = new ObjectInputStream(bais)
    try ois.readObject()
    catch {
      case ex: IOException =>
        throw new SerializationException("Deserialization failed: not a serialized object", ex)
    }
    finally ois.close()
  }

  class SerializationException(message: String, cause: Throwable = null)
    extends RuntimeException(message, cause)
}
