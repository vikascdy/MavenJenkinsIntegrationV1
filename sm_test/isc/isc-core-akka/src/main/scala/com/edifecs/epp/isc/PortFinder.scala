package com.edifecs.epp.isc

import java.io.IOException
import java.net.{InetSocketAddress, ServerSocket}

import com.typesafe.config.{Config, ConfigValueFactory}

object PortFinder {

  final val tcpPortConfigKey = "akka.remote.netty.tcp.port"
  final val defaultTries = 100

  def findOpenPort(start: Int): Int = findOpenPort(start, defaultTries)

  def findOpenPort(portRange: Range): Int = findOpenPort(portRange.head, portRange.length)

  def findOpenPort(start: Int, tries: Int): Int = {
    var port = start
    val maxPort = port + tries
    var done = false
    while (!done && port <= maxPort) {
      try {
        val socket = new ServerSocket(port)
        done = true
        socket.close()
      } catch {
        case ex: IOException =>
          if (port < maxPort) port += 1
          else throw new RuntimeException(ex)
      }
    }
    port
  }

  def findAndSetConfigPort(config: Config): Config =
    findAndSetConfigPort(config, config.getInt(tcpPortConfigKey))

  def findAndSetConfigPort(config: Config, start: Int): Config =
    findAndSetConfigPort(config, start, defaultTries)

  def findAndSetConfigPort(config: Config, portRange: Range): Config =
    findAndSetConfigPort(config, portRange.head, portRange.length)

  def findAndSetConfigPort(config: Config, start: Int, tries: Int): Config =
    config.withValue(tcpPortConfigKey, ConfigValueFactory.fromAnyRef(findOpenPort(start, tries)))
}
