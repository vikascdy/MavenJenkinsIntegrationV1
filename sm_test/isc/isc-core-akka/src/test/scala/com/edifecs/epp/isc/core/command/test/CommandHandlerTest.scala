package com.edifecs.epp.isc.core.command.test

import java.io.{InputStream, ByteArrayInputStream, Serializable}

import com.edifecs.epp.isc.async.Implicits._
import com.edifecs.epp.isc.async.MessageFuture
import com.edifecs.epp.isc.stream.MessageStream

import scala.concurrent.Await
import scala.concurrent.duration._

import com.edifecs.epp.isc.CommandCommunicator
import com.edifecs.epp.isc.annotations._
import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder
import com.edifecs.epp.isc.core.command._
import com.edifecs.test.BeforeAfterSpecification
import com.edifecs.epp.isc.exception.InvalidCommandException
import com.edifecs.epp.isc.command.CommandMessage

class CommandHandlerTest extends BeforeAfterSpecification {

  private var cc: CommandCommunicator = null
  private var receiver: CommandReceiver = null

  "An annotated command handler" should {
    "return null from void commands" in {
      send("voidCommand") mustEqual null
    }
    "return values from commands" in {
      send("stringCommand") mustEqual "The rain in Spain stays mainly on the plain."
    }
    "return primitive values from commands" in {
      send("intCommand") mustEqual 91
    }
    "throw exceptions without wrapping them" in {
      send("exceptionCommand") must throwAn[IllegalStateException]
    }
    "prefer command names in annotations over method names" in {
      send("bob") mustEqual "My name is Bob."
    }
    "handle commands with one argument" in {
      send("sayMyName", Map("name" -> "Adam")) mustEqual "My name is Adam."
    }
    "throw InvalidCommandException when a required argument is missing" in {
      send("sayMyName") must throwAn[InvalidCommandException]
    }
    "throw InvalidCommandException when a required argument has the wrong name" in {
      send("sayMyName", Map("potato" -> "tomato")) must throwAn[InvalidCommandException]
    }
    "throw InvalidCommandException when an argument is of the wrong type" in {
      send("sayMyName", Map("name" -> int2Integer(91))) must throwAn[InvalidCommandException]
    }
    "not throw an exception for extra, unnecessary arguments" in {
      send("sayMyName", Map("name" -> "Adam", "potato" -> "tomato")) mustEqual "My name is Adam."
    }
    "throw InvalidCommandException for a nonexistent command" in {
      send("notarealcommand") must throwAn[InvalidCommandException]
    }
    "not treat unannotated methods as commands" in {
      send("unregisteredCommand") must throwAn[InvalidCommandException]
    }
    "handle commands with special characters in their names" in {
      send("*print-smiley* :)") mustEqual ":)"
    }
    "handle commands with multiple arguments" in {
      val result = send("bake_a_cake", Map(
        "flour" -> 4.25,
        "sugar" -> 3.0
      )).asInstanceOf[String]
      (result must contain("4.25 cups flour")) and
      (result must contain("3.00 cups sugar")) and
      (result must contain("0 eggs")) and
      (result must not(contain("frosting")))
    }
    "handle commands with a legacy stream argument" in {
      val prefix = "PREFIX: "
      val toStream = "This is a really really long message that's going to be sent as a stream." +
        " The rain in Spain stays mainly on the plain."
      val stream = new ByteArrayInputStream(toStream.getBytes)
      send(
        "readLegacyStream", Map(
          "prefix" -> prefix,
          "stream" -> MessageStream.fromInputStream(stream))
      ) mustEqual (prefix + toStream)
    }
    "handle sync commands with a new-style stream argument" in {
      val prefix = "PREFIX: "
      val toStream = "This is a really really long message that's going to be sent as a stream." +
        " The rain in Spain stays mainly on the plain."
      val stream = new ByteArrayInputStream(toStream.getBytes)
      send(
        "readStreamSync", Map(
          "prefix" -> prefix,
          "stream" -> MessageStream.fromInputStream(stream))
      ) mustEqual (prefix + toStream)
    }
    "handle async commands with a new-style stream argument" in {
      val prefix = "PREFIX: "
      val toStream = "This is a really really long message that's going to be sent as a stream." +
        " The rain in Spain stays mainly on the plain."
      val stream = new ByteArrayInputStream(toStream.getBytes)
      send(
        "readStreamAsync", Map(
          "prefix" -> prefix,
          "stream" -> MessageStream.fromInputStream(stream))
      ) mustEqual (prefix + toStream)
    }
  }

  protected override def beforeAll() = {
    cc = new CommandCommunicatorBuilder().initializeTestMode
    cc.connect()
    receiver = new MultiCommandReceiver(
      CommandAnnotationProcessor.processAnnotatedCommandHandler(TestHandler, Some(cc)))
  }

  protected override def afterAll() = {
    cc.disconnect()
    cc = null
    receiver = null
  }

  private def send(
    command: String,
    args: Map[String, _ <: Serializable] = Map.empty
  ): Any = {
    Await.result(receiver.receiveCommand(
      cc, new CommandMessage(command, args)
    ), DurationInt(1).seconds)
  }

  @CommandHandler
  @NullSessionAllowed
  object TestHandler extends AbstractCommandHandler {

    @SyncCommand
    def voidCommand(): Unit =
      println("Executed void command.")

    @SyncCommand
    def exceptionCommand(): Unit = {
      println("Executed exception command.")
      throw new IllegalStateException(
        "This should be caught as an IllegalStateException, not an InvocationTargetException.")
    }

    @SyncCommand
    def stringCommand: String = {
      println("Executed string command.")
      "The rain in Spain stays mainly on the plain."
    }

    @SyncCommand
    def intCommand: Int = {
      println("Executed int command.")
      91
    }

    @SyncCommand
    def noSpecifiedSourceCommand: Boolean = {
      println("Executed no-specified-source command.")
      true
    }

    @SyncCommand(name = "bob")
    def renamedCommand: String = {
      println("Executed renamed command.")
      "My name is Bob."
    }

    @SyncCommand(name = "sayMyName")
    def argumentCommand(
      @Arg(name = "name", required = true) name: String
    ): String = {
      println("Executed argument command.")
      s"My name is $name."
    }

    def unregisteredCommand: String = {
      println("Executed unregistered command. Something must be wrong!")
      "42"
    }

    @SyncCommand(name = "*print-smiley* :)")
    def specialCharsCommand: String = {
      println("Executed special-characters command.")
      ":)"
    }

    @SyncCommand(name = "bake_a_cake")
    def manyArgumentsCommand(
      @Arg(name = "flour", required = true) cupsOfFlour:  Double,
      @Arg(name = "sugar", required = true) cupsOfSugar:  Double,
      @Arg(name = "eggs")                   eggs:         Int,
      @Arg(name = "frosting")               frostingType: String
    ): String = {
        println("Executed many-arguments command.")
        f"""Cake Recipe: 
  - $cupsOfFlour%1.2f cups flour
  - $cupsOfSugar%1.2f cups sugar
  - $eggs%d eggs
  """ + (if (frostingType != null) s"""- $frostingType frosting
  """ else "")
    }

    @SyncCommand(name = "readLegacyStream")
    def readLegacyStream(
      @Arg(name = "prefix", required = true) prefix: String,
      @StreamArg(name = "stream")            stream: InputStream
    ): String = {
      println("Executed legacy stream command.")
      val streamContents = io.Source.fromInputStream(stream).mkString("")
      prefix + streamContents
    }

    @SyncCommand(name = "readStreamSync")
    def readStreamSync(
      @Arg(name = "prefix", required = true) prefix: String,
      @Arg(name = "stream", required = true) stream: MessageStream
    ): String = {
      println("Executed sync stream command.")
      val streamContents = io.Source.fromInputStream(stream.toInputStream).mkString("")
      prefix + streamContents
    }

    @AsyncCommand(name = "readStreamAsync")
    def readStreamAsync(
      @Arg(name = "prefix", required = true) prefix: String,
      @Arg(name = "stream", required = true) stream: MessageStream
    ): MessageFuture[String] = {
      println("Executed async stream command.")
      stream.fold("")((str, chunk) => MessageFuture(str + chunk.decodeString("utf-8")))
            .map(prefix + _)(executionContext)
    }
  }
}
