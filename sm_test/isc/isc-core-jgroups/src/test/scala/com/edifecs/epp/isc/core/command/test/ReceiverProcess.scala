package com.edifecs.epp.isc.core.command.test

import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import com.edifecs.epp.isc.{Address, ICommandCommunicator}
import com.edifecs.epp.isc.async.MessageFuture
import com.edifecs.epp.isc.async.Implicits._
import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder
import com.edifecs.epp.isc.core.command.{AbstractCommandHandler, MultiCommandReceiver}
import com.edifecs.epp.isc.core.command.CommandAnnotationProcessor._
import com.edifecs.epp.isc.annotations.{CommandHandler, AsyncCommand, JGroups}

class ReceiverProcess(
  clusterName: String,
  serverName: String,
  nodeName: String
) {
  System.setProperty("java.net.preferIPv4Stack", "true")

  private val logger = LoggerFactory.getLogger(getClass)
  
  private val addr = new Address(serverName, nodeName)
  private val builder = new CommandCommunicatorBuilder()
  builder.setClusterName(clusterName)
  builder.setAddress(addr)
  private val communicator = builder.initialize
  communicator.connect()

  communicator.registerCommandReceiver(addr,
    new MultiCommandReceiver(
      processAnnotatedCommandHandler(new TestMessageHandler()) ++
      processAnnotatedCommandHandler(ShutdownCommandHandler)))
  logger.info("ReceiverProcess ready.")

  @CommandHandler
  object ShutdownCommandHandler extends AbstractCommandHandler {
    @JGroups(enabled = true)
    @AsyncCommand(name = "shutdown")
    def shutdown: MessageFuture[Boolean] = Future {
      logger.info("Received shutdown command. Shutting down...");
      new Thread() {
        override def run: Unit = {
          try {
            Thread.sleep(200)
            communicator.disconnect()
            Thread.sleep(200)
            logger.info("Shutdown complete.")
            System.exit(0)
          } catch {
            case ex: InterruptedException =>
              ex.printStackTrace()
              Thread.currentThread.interrupt()
          }
        }
      }.start()
      true
    }
  }
}

object ReceiverProcess {
  def main(args: Array[String]): Unit = {
    if (args.length != 3) {
      println("USAGE: java ReceiverProcess clusterName serverName nodeName")
      System.exit(1)
    }
    new ReceiverProcess(args(0), args(1), args(2))
  }
}

