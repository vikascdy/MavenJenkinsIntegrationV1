package com.edifecs.epp.jgroups.protocol

import java.io.{DataOutputStream, ByteArrayOutputStream, DataInputStream, ByteArrayInputStream}
import java.net.URLEncoder
import java.util

import javax.xml.bind.DatatypeConverter.{parseBase64Binary, printBase64Binary}

import scala.collection.JavaConversions._
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random

import akka.actor.ActorSystem

import org.jgroups._
import org.jgroups.annotations.Property
import org.jgroups.conf.ClassConfigurator
import org.jgroups.protocols.{PingData, Discovery}
import org.jgroups.util.UUID

import org.slf4j.LoggerFactory

import spray.http.HttpResponse

import com.edifecs.epp.ecm.etcd.EtcdClient
import com.edifecs.epp.ecm.etcd.EtcdProtocol.{GetResponse, ListResponse}
import com.edifecs.epp.ecm.values.EndPoint


class ETCD(implicit val system: ActorSystem) extends Discovery {

  // Properties -----------------------------------------------------------------------------------

  @Property(
    name = "etcd_address",
    description = "IP address used to connect to etcd")
  protected var etcd_address: String = "127.0.0.1" // TODO: Change this.

  @Property(
    name = "etcd_port",
    description = "Port number used to connect to etcd")
  protected var etcd_port: Int = 4001

  @Property(
    name = "etcd_directory",
    description = "Directory path in etcd where node information is stored")
  protected var etcd_directory: String = "/ecm/cluster"

  @Property(
    name = "etcd_timeout",
    description = "The timeout, in milliseconds, to use for all etcd HTTP requests")
  protected var etcd_timeout: Long = 5000

  @Property(
    name = "etcd_ttl",
    description = "The duration, in seconds, that an etcd key should persist before being deleted")
  protected var etcd_ttl: Long = 30

  // State ----------------------------------------------------------------------------------------

  private final val logger = LoggerFactory.getLogger(getClass)
  private final val suffix = ".node"
  protected var client: EtcdClient = null

  // Getter/Setter Methods ------------------------------------------------------------------------

  def getEtcdAddress = etcd_address
  def setEtcdAddress(value: String) = etcd_address = value
  def getEtcdPort = etcd_port
  def setEtcdPort(value: Int) = etcd_port = value
  def getEtcdDirectory = etcd_directory
  def setEtcdDirectory(value: String) = etcd_directory = value
  def getEtcdTimeout = etcd_timeout
  def setEtcdTimeout(value: Long) = etcd_timeout = value
  def getEtcdTtl = etcd_ttl
  def setEtcdTtl(value: Long) = etcd_ttl = value

  // Etcd Methods ---------------------------------------------------------------------------------

  @inline
  private def url(str: String) = URLEncoder.encode(str, "UTF-8")

  protected def keyFor(clusterName: String, addr: Address) = {
    val filename = addr match {
      case u: UUID => u.toStringLong
      case a: Address => a.toString
    }
    s"$etcd_directory/${url(clusterName)}/${url(filename)}$suffix"
  }

  protected def writeToEtcd(clusterName: String, data: PingData) = {
    val key = keyFor(clusterName, local_addr)
    val value = {
      val baos = new ByteArrayOutputStream
      data.writeTo(new DataOutputStream(baos))
      printBase64Binary(baos.toByteArray)
    }
    Await.result(client.put(key + "?ttl=" + etcd_ttl, value), etcd_timeout milliseconds) match {
      case Left(r: HttpResponse) =>
        logger.error("Could not write to etcd key '{}': {}", Seq(key, r.entity.asString): _*)
      case _ =>
        logger.debug("Wrote to etcd (key='{}', value='{}')", Seq(key, value): _*)
    }
  }

  protected def listNodesInEtcd(clusterName: String): Seq[PingData] = {
    val dir = s"$etcd_directory/${url(clusterName)}"
    Await.result(client.list(dir), etcd_timeout milliseconds) match {
      case Left(r: HttpResponse) =>
        logger.error("Could not read etcd directory '{}': {}", Seq(dir, r.entity.asString): _*)
        Nil
      case Right(r: ListResponse) =>
        if (!r.node.dir) throw new IllegalStateException(s"etcd key '$dir' must be a directory.")
        r.node.nodes filter(_.key.endsWith(suffix)) flatMap { meta =>
          Await.result(client.get(meta.key), etcd_timeout milliseconds) match {
            case Left(r: HttpResponse) =>
              // This is logged as a warning instead of an error because it can happen occasionally
              // due to an otherwise harmless race condition (key may expire between list and get).
              logger.warn("Could not read etcd key '{}': {}", Seq(meta.key, r.entity.asString): _*)
              Nil
            case Right(r: GetResponse) =>
              val in=new DataInputStream(new ByteArrayInputStream(parseBase64Binary(r.node.value)))
              val pingData = new PingData; pingData.readFrom(in)
              pingData :: Nil
          }
        }
    }
  }

  // Overridden Methods ---------------------------------------------------------------------------

  override def init() = {
    super.init()
    client = new EtcdClient(new EndPoint(etcd_address, etcd_port))
  }

  override val isDynamic = true
  override val sendDiscoveryRequestsInParallel = true

  private lazy val firstRun = {
    val addr = down(new Event(Event.GET_PHYSICAL_ADDRESS, local_addr)).asInstanceOf[PhysicalAddress]
    val data = new PingData(local_addr, null, false, UUID.get(local_addr), List(addr))
    writeToEtcd(group_addr, data)
    true
  }

  override def fetchClusterMembers(clusterName: String): util.Collection[PhysicalAddress] = {
    firstRun
    listNodesInEtcd(clusterName).flatMap(_.getPhysicalAddrs).filter(_ != null).toSet[PhysicalAddress]
  }

  // The Keepalive Thread -------------------------------------------------------------------------

  private object KeepaliveThread extends Thread {
    setDaemon(true)
    setPriority(Thread.MAX_PRIORITY)

    override def run() {
      var running = true
      while (running) {
        try {
          try {
            if (local_addr != null) {
              // Write own data to etcd.
              val addr = down(new Event(Event.GET_PHYSICAL_ADDRESS, local_addr)).asInstanceOf[PhysicalAddress]
              val data = new PingData(local_addr, null, false, UUID.get(local_addr), List(addr))
              writeToEtcd(group_addr, data)
            }
            Thread.sleep(etcd_ttl * 500L) // Sleep for half the TTL.
          } catch {
            case ex: Exception =>
              logger.error(s"Could not write to etcd. Trying again in $etcd_ttl seconds.", ex)
              Thread.sleep(etcd_ttl * 1000L) // Sleep for twice as long as usual, then try again.
          }
        } catch {
          case ex: InterruptedException =>
            logger.warn("ETCD.KeepaliveThread interrupted")
            Thread.currentThread().interrupt()
            running = false
        }
      }
    }
  }
  KeepaliveThread.setName("ETCD-Keepalive-" + Random.nextInt(Int.MaxValue))
  KeepaliveThread.start()
}

object ETCD {
  final val magicNumber: Short = 0xECD
  private var registered = false
  def register(): Unit = synchronized {
    if (!registered) {
      ClassConfigurator.addProtocol(magicNumber, classOf[ETCD])
      registered = true
    }
  }
}
