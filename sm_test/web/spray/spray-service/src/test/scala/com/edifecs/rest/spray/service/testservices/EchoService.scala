package com.edifecs.rest.spray.service.testservices

import com.edifecs.epp.isc.{CommandCommunicator, Isc}
import com.edifecs.epp.isc.annotations._
import com.edifecs.epp.isc.core.command.AbstractCommandHandler

import com.edifecs.servicemanager.annotations.{Service, Handler}
import com.edifecs.servicemanager.api.AbstractService

@Service(name = "Echo Service",
         version = "1.0",
         description = "Used to test the REST interface.")
class EchoService extends AbstractService {

  override def start() = println("Echo service started.")

  override def stop() = println("Echo service stopped.")

  @Handler
  def handler: EchoCommandHandler = new EchoCommandHandler()
}

@Akka(enabled = true)
@Rest(enabled = true)
@CommandHandler
class EchoCommandHandler extends AbstractCommandHandler {

  @SyncCommand(name = "echoArg")
  def echoArg(
    @Arg(name = "arg", required = true, description = "the argument to echo") arg: String
  ): String = arg

  @SyncCommand(name = "echoLong")
  def echoLong(
    @Arg(name = "arg", required = true, description = "the argument to echo") arg: Long
  ): Long = arg

  @SyncCommand(name = "echoDouble")
  def echoDouble(
    @Arg(name = "arg", required = true, description = "the argument to echo") arg: Double
  ): Double = arg

  @SyncCommand(name = "echoMethod")
  def echoMethod(
    @Arg(name = Isc.restMethodArg, required = true, description = "the http method") method: String
  ): String = method

  @SyncCommand(name = "echoBody")
  def echoBody(
    @Arg(name = Isc.requestBodyArg, required = true, description = "the request body") body: String
  ): String = body

  @SyncCommand(name = "echoSuffix")
  def echoSuffix(
    @Arg(name = Isc.urlSuffixArg, required = true, description = "the url suffix") suffix: String
  ): String = suffix

  @SyncCommand(name = "echoError")
  def echoError(
    @Arg(name = "message", required = true, description = "the error message") message: String
  ) = throw new RuntimeException(message)

  @SyncCommand(name = "echoUser")
  def echoUser(): String = {
    val user = getCommandCommunicator.getSecurityManager.getSubjectManager.getUser
    user.getUsername
  }
}

