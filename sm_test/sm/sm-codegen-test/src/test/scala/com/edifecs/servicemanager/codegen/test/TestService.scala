package com.edifecs.servicemanager.codegen.test

import scala.collection.immutable._

import com.edifecs.epp.isc.async.MessageFuture
import com.edifecs.epp.isc.async.Implicits._
import com.edifecs.epp.isc.annotations._
import com.edifecs.epp.isc.core.command.AbstractCommandHandler
import com.edifecs.servicemanager.annotations._
import com.edifecs.servicemanager.api.AbstractService
import com.edifecs.epp.isc.Address

@Service(
  name        = "Test Service",
  version     = "1.0",
  description = "Test Service")
trait ITestService {
  
  @Handler
  def commandHandler: ITestServiceCommandHandler
}

@CommandHandler
@NullSessionAllowed
trait ITestServiceCommandHandler {

  @SyncCommand
  def testCommand: Boolean

  @SyncCommand
  def appendNumber(
    @Arg(name="message") message: String
  ): String

  @AsyncCommand
  def appendNumberFromOtherService(
    @Arg(name="message")     message: String,
    @Arg(name="destination") destination: Address  
  ): MessageFuture[String]
}

class TestService(
  number: Int
) extends AbstractService with ITestService {

  override def start = getLogger.debug(s"Starting Test Service #$number.")
  override def stop  = getLogger.debug(s"Stopping Test Service #$number.")
  override def commandHandler = new TestServiceCommandHandler(number)
}

class TestServiceCommandHandler(
  number: Int
) extends AbstractCommandHandler with ITestServiceCommandHandler {

  override def testCommand: Boolean = true

  override def appendNumber(message: String): String =
    s"$message $number"

  override def appendNumberFromOtherService(
    message: String,
    destination: Address  
  ): MessageFuture[String] =
    isc.send(
      destination,
      "appendNumber",
      Map("message" -> message)
    ).mapTo[String]
}

