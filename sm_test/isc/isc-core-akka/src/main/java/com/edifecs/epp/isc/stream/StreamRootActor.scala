package com.edifecs.epp.isc.stream

import java.util.concurrent.TimeUnit
import java.util.{TimerTask, Timer}

import com.edifecs.epp.isc.command.CommandStackFrame

import scala.collection.mutable

import akka.actor._
import akka.pattern.ask

import com.edifecs.core.configuration.helper.{TypesafeConfigKeys, SystemVariables}
import com.edifecs.epp.isc.async.Implicits._
import com.edifecs.epp.isc.async.{TerminalMessageFuture, MessageFuture}
import com.edifecs.epp.isc.exception.MessageException

import MessageStream._

class StreamRootActor extends Actor with ActorLogging {

  import StreamRootActor._

  private val outgoingStreams = new mutable.HashMap[StreamId, MessageStream]
  private implicit val dispatcher = context.system.dispatcher
  private implicit val timeout: akka.util.Timeout = context.system.settings.config.getDuration(
    TypesafeConfigKeys.STREAM_CHUNK_TIMEOUT, TimeUnit.MILLISECONDS)
  private val streamExpireMs = context.system.settings.config.getDuration(
    TypesafeConfigKeys.STREAM_EXPIRE_TIMEOUT, TimeUnit.MILLISECONDS)

  private val timer = new Timer("StreamExpirationTimer", true)

  override def preStart() =
    log.debug("Starting StreamRootActor")

  override def receive: Receive = {
    case RegisterOutgoingStream(stream) =>
      if (outgoingStreams contains stream.id) {
        log.error("Cannot register stream {}; a stream with this ID is already registered!",
          stream.id)
      } else {
        log.debug("Registered outgoing stream {}", stream.id)
        outgoingStreams.put(stream.id, stream)
        timer.schedule(new ExpireStreamTask(stream), streamExpireMs)
      }
    case ExpectIncomingStream(stream, from, stack) =>
      log.debug("Expecting incoming stream {} from {}", stream.id, from)
      val source = new PipedStreamSource
      val child = context.actorOf(Props(classOf[StreamReceiverActor], source), stream.id.toString)
      val newStream = MessageStream(stream.httpContentType, Option.empty, stream.lastModifiedTimestamp, source)
      sender() ! IncomingStream(newStream)
      context.actorSelection(RootActorPath(from) / "user" / name) ! ReadyToReceiveStream(stream.id, child, stack)
    case ReadyToReceiveStream(id, dest, stack) =>
      outgoingStreams.remove(id).map { stream =>
        log.debug("Sending stream {} to {}", stream.id, sender().path)
        stream forEachChunk { chunk =>
          (dest ? chunk) map {
            case StreamAck => MessageFuture()
            case m: Any => throw new MessageException("Expected ack, got " + m, stack)
          }
        } andThenDo {
          dest ! EndOfStream
        } orCatch { ex =>
          log.error(ex, "Stream {} failed with exception", id)
          dest ! StreamException(ex)
        }
      } getOrElse {
        log.warning("Received request for nonexistent stream {}", id)
        dest ! StreamException(new MessageException(s"No stream with the ID $id exists.", stack))
      }
  }

  override def postStop() = {
    log.debug("Stopping StreamRootActor")
    timer.cancel()
  }

  private class ExpireStreamTask(stream: MessageStream) extends TimerTask {
    override def run() = {
      outgoingStreams.remove(stream.id).map { removedStream =>
        if (removedStream != stream) outgoingStreams.put(stream.id, removedStream)
        else log.warning("Outgoing stream {} was never requested, and expired after {}ms.",
          stream.id, streamExpireMs)
      }
    }
  }
}

object StreamRootActor {
  final val name = SystemVariables.AKKA_STREAM_ROOT
  case class RegisterOutgoingStream(
      stream: MessageStream
  ) extends PossiblyHarmful
  case class ExpectIncomingStream(
      stream: MessageStream,
      from: akka.actor.Address,
      stack: Array[CommandStackFrame]
  ) extends PossiblyHarmful
  case class IncomingStream(
    stream: MessageStream
  ) extends PossiblyHarmful
  case class ReadyToReceiveStream(
    id: StreamId, ref: ActorRef, stack: Array[CommandStackFrame]
  )

  def createInstance(implicit system: ActorSystem): ActorRef =
    system.actorOf(Props[StreamRootActor], name)
}
