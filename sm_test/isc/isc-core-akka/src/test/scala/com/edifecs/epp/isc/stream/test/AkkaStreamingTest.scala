package com.edifecs.epp.isc.stream.test

import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.util.concurrent.{TimeoutException, TimeUnit, CyclicBarrier}

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.util.Random

import akka.actor.{Address, ActorSystem}
import akka.pattern.ask
import akka.util.CompactByteString 

import com.typesafe.config.ConfigFactory

import org.specs2.mutable.{BeforeAfter, Specification}

import com.edifecs.epp.isc.PortFinder
import com.edifecs.epp.isc.async.{TerminalMessageFuture, MessageFuture}
import com.edifecs.epp.isc.builder.CommandCommunicatorBuilder
import com.edifecs.epp.isc.exception.MessageException
import com.edifecs.epp.isc.stream.{MessageStreamSource, MessageStream, StreamRootActor}
import com.edifecs.epp.isc.stream.StreamRootActor._
import PortFinder.findAndSetConfigPort

class AkkaStreamingTest extends Specification {

  implicit val timeout: akka.util.Timeout = DurationInt(2).seconds

  val streamSize = 1000000 // 1MB

  sequential

  "The actor-based streaming framework" should {
    "send MessageStreams" in new AkkaStreamingContext {
      val seed = System.currentTimeMillis()
      val originalStream = MessageStream("application/octet-stream", Option.empty, System.currentTimeMillis(),
        new RandomStreamSource(seed, streamSize))
      streamActor1 ! RegisterOutgoingStream(originalStream)
      val sentStream = Await.result((streamActor2 ? ExpectIncomingStream(originalStream, address1, Array.empty))
        .mapTo[IncomingStream].map(_.stream), timeout.duration)
      compareMessageStreams(
        MessageStream("application/octet-stream", Option.empty, System.currentTimeMillis(),
          new RandomStreamSource(seed, streamSize)),
        sentStream)
      success
    }
    "send InputStreams" in new AkkaStreamingContext {
      val seed = System.currentTimeMillis()
      val originalStream = new RandomStream(seed, streamSize)
      val originalMStream = MessageStream.fromInputStream(originalStream)
      streamActor1 ! RegisterOutgoingStream(originalMStream)
      val sentMStream = Await.result((streamActor2 ? ExpectIncomingStream(originalMStream, address1, Array.empty))
        .mapTo[IncomingStream].map(_.stream), timeout.duration)
      val sentStream = sentMStream.toInputStream(MessageStream.chunkSize)
      compareStreams(new RandomStream(seed, streamSize), sentStream)
      success
    }
    "preserve HTTP content-type and last-modified metadata" in new AkkaStreamingContext {
      val contentType = "application/json"
      val timestamp = System.currentTimeMillis() - 256
      val originalStream = MessageStream(contentType, Option.empty, timestamp,
        new RandomStreamSource(System.currentTimeMillis(), streamSize))
      streamActor1 ! RegisterOutgoingStream(originalStream)
      val sentStream = Await.result((streamActor2 ? ExpectIncomingStream(originalStream, address1, Array.empty))
        .mapTo[IncomingStream].map(_.stream), timeout.duration)
      (sentStream.httpContentType mustEqual contentType) and
      (sentStream.lastModifiedTimestamp mustEqual timestamp)
    }
    "send 100 simultaneous streams accurately" in new AkkaStreamingContext {
      val time = System.currentTimeMillis()
      val seeds = (0 to 100) map (time + _)
      val streams = seeds map (s => MessageStream.fromInputStream(new RandomStream(s, streamSize)))
      Random.shuffle(streams).par map { stream =>
        streamActor1 ! RegisterOutgoingStream(stream)
      }
      Random.shuffle(seeds zip streams).par map { t =>
        val (seed, stream) = t
        val sentStream = Await.result((streamActor2 ? ExpectIncomingStream(stream, address1, Array.empty))
            .mapTo[IncomingStream]
            .map(_.stream.toInputStream(MessageStream.chunkSize)),
          timeout.duration)
        Await.result(
          Future(compareStreams(new RandomStream(seed, streamSize), sentStream)),
          timeout.duration)
      }
      success
    }
  }

  /**
   * Compares two streams chunk-for-chunk, throwing an exception if they don't
   * match.
   */
  private def compareMessageStreams(stream1: MessageStream, stream2: MessageStream): Unit = {
    var chunk1: CompactByteString  = null
    var chunk2: CompactByteString  = null
    var error: Option[MessageException] = None
    val mainThread = Thread.currentThread()

    val errorCallback = (ex: MessageException) => {
      if (!error.isDefined) error = Some(ex)
      mainThread.interrupt()
    }
    val barrier = new CyclicBarrier(3)
    def wait() = barrier.await(2, TimeUnit.SECONDS)
    stream1 forEachChunk(chunk => MessageFuture {
      wait(); chunk1 = chunk; wait()
    }) andThenDo {
      wait(); chunk1 = null; wait()
    } orCatch errorCallback
    stream2.forEachChunk(chunk => MessageFuture {
      wait(); chunk2 = chunk; wait()
    }) andThenDo {
      wait(); chunk2 = null; wait()
    } orCatch errorCallback
    try {
      wait()
      while (true) {
        wait()
        if (chunk1 == null) {
          if (chunk2 == null) return
          else throw new StreamComparisonException("Stream 1 ran out before Stream 2.")
        } else if (chunk2 == null) {
          throw new StreamComparisonException("Stream 2 ran out before Stream 1.")
        } else {
          if (chunk1.length != chunk2.length) {
            throw new StreamComparisonException("Stream chunks are not of equal size.")
          } else if (!chunk1.equals(chunk2)) {
            throw new StreamComparisonException("Stream chunks are not equal.")
          }
          wait()
        }
      }
    } catch {
      case iex: InterruptedException =>
        throw error getOrElse iex
      case tex: TimeoutException =>
        throw error getOrElse tex
    }
  }

  /**
   * Compares two streams byte-for-byte, throwing an exception if they don't
   * match.
   */
  private def compareStreams(stream1: InputStream, stream2: InputStream): Unit = {
    // http://stackoverflow.com/a/4245962/548027

    val ch1 = Channels.newChannel(stream1)
    val ch2 = Channels.newChannel(stream2)

    val buf1 = ByteBuffer.allocateDirect(1024)
    val buf2 = ByteBuffer.allocateDirect(1024)

    var size1 = 0
    var size2 = 0

    try {
      while (true) {
        val n1 = ch1.read(buf1)
        val n2 = ch2.read(buf2)
        if (n1 > -1) size1 += n1
        if (n2 > -1) size2 += n2

        if (n1 == -1 || n2 == -1) {
          if (n1 == n2) return
          else throw new StreamComparisonException(s"Stream sizes differ ($size1, $size2).")
        }

        buf1.flip(); buf2.flip()

        (0 until Math.min(n1, n2)) foreach { _ =>
          if (buf1.get() != buf2.get()) {
            throw new StreamComparisonException("Stream content does not match.")
          }
        }

        buf1.compact(); buf2.compact()
      }

    } finally {
      if (stream1 != null) stream1.close()
      if (stream2 != null) stream2.close()
    }
  }
}

object AkkaStreamingTest {

  val config = """
  akka {
    loglevel = INFO
    actor {
      provider = "akka.remote.RemoteActorRefProvider"
    }
    remote {
      enabled-transports = ["akka.remote.netty.tcp"]
      netty.tcp {
        hostname = 127.0.0.1
      }
    }
  }
  isc.messaging.stream {
    expire-timeout = 60s
    chunk-timeout = 10s
    chunk-size = 50kB
  }
  """
}

trait AkkaStreamingContext extends BeforeAfter {
  val commandCommunicator = new CommandCommunicatorBuilder().initializeTestMode()
  val system1 = ActorSystem("system1",
    findAndSetConfigPort(ConfigFactory.parseString(AkkaStreamingTest.config), 9000 to 9100))
  val system2 = ActorSystem("system2",
    findAndSetConfigPort(ConfigFactory.parseString(AkkaStreamingTest.config), 9000 to 9100))
  val address1 = Address("akka.tcp", "system1", "127.0.0.1",
    port = system1.settings.config.getInt(PortFinder.tcpPortConfigKey))
  val address2 = Address("akka.tcp", "system2", "127.0.0.1",
    port = system2.settings.config.getInt(PortFinder.tcpPortConfigKey))
  val streamActor1 = StreamRootActor.createInstance(system1)
  val streamActor2 = StreamRootActor.createInstance(system2)

  override def before = commandCommunicator.connect()

  override def after = {
    system1.shutdown()
    system2.shutdown()
    system1.awaitTermination(DurationInt(2).minutes)
    system2.awaitTermination(DurationInt(2).minutes)
    commandCommunicator.disconnect()
  }
}

/**
 * A source that generates a fixed amount of random bytes, generated from a
 * {@link Random} with a specific seed. Two `RandomStreamSource`s with the same
 * seed and length will generate the exact same stream data.
 */
class RandomStreamSource(seed: Long, length: Int) extends MessageStreamSource {
  private val rnd = new Random(seed)
  private var pos: Int = 0

  override def nextChunk(
    callback: CompactByteString  => TerminalMessageFuture
  ) = {
    val size = Math.min(length - pos, MessageStream.chunkSize)
    val bytes = Array.ofDim[Byte](size)
    rnd.nextBytes(bytes)
    pos += size
    callback(CompactByteString.apply(bytes)).andThen(MessageFuture(pos < length))
  }
}

/**
 * A stream that generates a fixed amount of random bytes, generated from a
 * {@link Random} with a specific seed. Two `RandomStream`s with the same seed
 * and length will generate the exact same stream data.
 */
class RandomStream(seed: Long, length: Int) extends InputStream {
  private val rnd = new Random(seed)
  private var pos: Int = 0

  override def read(): Int =
    if (pos < length) {
      pos += 1
      rnd.nextInt(256)
    } else -1

  override def available = length - pos
}

class StreamComparisonException(message: String) extends RuntimeException(message)