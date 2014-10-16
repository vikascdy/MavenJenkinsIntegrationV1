package com.edifecs.epp.isc.core.command.test

import java.util.concurrent.TimeoutException

import com.edifecs.epp.isc.CommandCommunicator
import com.edifecs.epp.isc.annotations._
import com.edifecs.epp.isc.async.MessageFuture
import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder
import com.edifecs.epp.isc.command.{CommandMessage, CommandSource}
import com.edifecs.epp.isc.core.command._
import com.edifecs.epp.isc.exception.{HandlerConfigurationException, InvalidCommandException, InvalidSourceException}
import com.edifecs.test.BeforeAfterSpecification

import scala.collection.immutable._
import scala.concurrent.Await
import scala.concurrent.duration._

class CommandAnnotationTest extends BeforeAfterSpecification {

  private var cc: CommandCommunicator = null

  import com.edifecs.epp.isc.command.CommandSource._

  "The command annotation processor" should {
     "process annotations on interfaces" in {
        val r = activate(HandlerInterfaceImpl)
        (sendTo(r, "command1", AKKA) mustEqual "This is command 1.") and
        (sendTo(r, "command2", AKKA) mustEqual "This is command 2.")
    }
    "register annotated commands from extra interfaces (without @CommandHandler)" in {
        val r = activate(HandlerWithExtraCommands)
        (sendTo(r, "command1", AKKA) mustEqual "This is command 1.") and
        (sendTo(r, "command2", AKKA) mustEqual "This is command 2.") and
        (sendTo(r, "extraCommand1", AKKA) mustEqual "This is extra command 1.") and
        (sendTo(r, "extraCommand2", AKKA) mustEqual "This is extra command 2. It is overridden.")
    }
    "not register the same command receiver multiple times" in {
      CommandAnnotationProcessor.processAnnotatedCommandHandler(
        HandlerWithExtraCommands, Some(cc)) must have size 4
    }
    "support asynchronous commands" in {
        val r = activate(AsyncHandler)
        (sendTo(r, "asyncCommand", AKKA) mustEqual "This is an async command.") and
        (sendTo(r, "delayedCommand", AKKA) mustEqual "This is a delayed command.")
    }
    "support timeouts for asynchronous commands" in {
        val r = activate(AsyncHandler)
        (sendTo(r, "successfulTimeoutCommand", AKKA) mustEqual "This should not time out.") and
        (sendTo(r, "failedTimeoutCommand", AKKA) must throwA[TimeoutException])
    }
    "require asynchronous commands to return MessageFutures" in {
        activate(InvalidAsyncHandler) must throwA[HandlerConfigurationException]
    }
    "support command namespaces" in {
        val r = activate(NamespaceHandler)
        (sendTo(r, "ns.command1", AKKA) mustEqual "This is command 1.") and
        (sendTo(r, "ns.command2", AKKA) mustEqual "This is command 2.") and
        (sendTo(r, "command1", AKKA) must throwAn[InvalidCommandException])
    }
    "support root commands in namespaces" in {
        val r = activate(NamespaceHandler)
        sendTo(r, "ns", AKKA) mustEqual "This is the root command."
    }
    "process class-level @Akka(enabled=false)" in {
        val r = activate(AkkaDisabledHandler)
        sendTo(r, "inaccessible", AKKA) must throwAn[InvalidSourceException]
    }
    "process method-level @Akka(enabled=false)" in {
        val r = activate(RestEnabledHandler)
        sendTo(r, "restAccessible", AKKA) must throwAn[InvalidSourceException]
    }
    "allow method-level @Akka(enabled=true) to override class-level @Akka(enabled=false)" in {
        val r = activate(AkkaDisabledHandler)
        sendTo(r, "akkaAccessible", AKKA) mustEqual "Accessible via Akka."
    }
    "process class-level @Rest(enabled=true)" in {
        val r = activate(RestEnabledHandler)
        sendTo(r, "restAccessible", REST) mustEqual "Accessible via REST."
    }
    "process method-level @Rest(enabled=true)" in {
        val r = activate(AkkaDisabledHandler)
        sendTo(r, "restAccessible", REST) mustEqual "Accessible via REST."
    }
    "allow method-level @Rest(enabled=false) to override class-level @Rest(enabled=true)" in {
        val r = activate(RestEnabledHandler)
        sendTo(r, "restInaccessible", REST) must throwAn[InvalidSourceException]
    }
  }

  protected override def beforeAll() = {
    cc = new CommandCommunicatorBuilder().initializeTestMode
    cc.connect()
  }

  protected override def afterAll() = {
    cc.disconnect()
    cc = null
  }

  @CommandHandler @NullSessionAllowed
  trait HandlerInterface {

    @SyncCommand(name="command1")
    def commandOne: String

    @SyncCommand
    def command2: String
  }

  trait ExtraCommands {

    @SyncCommand(name="extraCommand1")
    def extraCommandOne: String

    @SyncCommand
    def extraCommand2: String
  }

  abstract class ExtraCommandsImpl extends AbstractCommandHandler with ExtraCommands {
    override def extraCommandOne: String = "This is extra command 1."
    override def extraCommand2: String = "This is extra command 2."
  }

  object HandlerInterfaceImpl extends AbstractCommandHandler with HandlerInterface {
    override def commandOne: String = "This is command 1."
    override def command2: String = "This is command 2."
  }

  object HandlerWithExtraCommands extends ExtraCommandsImpl with HandlerInterface {
    override def commandOne: String = "This is command 1."
    override def command2: String = "This is command 2."
    override def extraCommand2: String = "This is extra command 2. It is overridden."
  }

  @CommandHandler @Akka(enabled=false) @NullSessionAllowed
  object AkkaDisabledHandler extends AbstractCommandHandler {
    
    @SyncCommand @Rest(enabled=true)
    def restAccessible: String = "Accessible via REST."
    
    @SyncCommand @Akka(enabled=true)
    def akkaAccessible: String = "Accessible via Akka."
    
    @SyncCommand
    def inaccessible: String = "Inaccessible."
  }

  @CommandHandler @Rest(enabled=true) @NullSessionAllowed
  object RestEnabledHandler extends AbstractCommandHandler {

    @SyncCommand @Akka(enabled=false)
    def restAccessible: String = "Accessible via REST."
    
    @SyncCommand @Rest(enabled=false)
    def restInaccessible: String = "Accessible via Akka."
  }

  @CommandHandler @NullSessionAllowed
  object AsyncHandler extends AbstractCommandHandler {

    @AsyncCommand
    def asyncCommand: MessageFuture[String] =
      MessageFuture.of("This is an async command.")

    @AsyncCommand
    def delayedCommand: MessageFuture[String] =
      MessageFuture {
        Thread.sleep(100)
        "This is a delayed command."
      }

    @AsyncCommand(timeoutMs=1000)
    def successfulTimeoutCommand: MessageFuture[String] =
      MessageFuture {
        Thread.sleep(100)
        "This should not time out."
      }

    @AsyncCommand(timeoutMs=100)
    def failedTimeoutCommand: MessageFuture[String] =
      MessageFuture {
        Thread.sleep(1000)
        "This should time out."
      }
  }

  @CommandHandler @NullSessionAllowed
  object InvalidAsyncHandler extends AbstractCommandHandler {

    @AsyncCommand
    def invalidAsyncCommand: String =
      "This is an invalid async command."
  }

  @CommandHandler(namespace="ns") @NullSessionAllowed
  object NamespaceHandler extends AbstractCommandHandler {

    @SyncCommand(name="command1")
    def command1: String = "This is command 1."

    @SyncCommand(name="command2")
    def command2: String = "This is command 2."

    @SyncCommand(root=true)
    def rootCommand: String = "This is the root command."
  }

  private def activate(handler: AbstractCommandHandler): CommandReceiver =
    new MultiCommandReceiver(CommandAnnotationProcessor.processAnnotatedCommandHandler(
      handler, Some(cc)))

  private def sendTo(
    receiver: CommandReceiver,
    command: String,
    source: CommandSource
  ): Any =
    Await.result(receiver.receiveCommand(
      cc, new CommandMessage(command, Map.empty, source)
    ), receiver.getTimeoutFor(command) getOrElse DurationInt(5).seconds)
}

