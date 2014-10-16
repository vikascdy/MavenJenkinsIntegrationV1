package com.edifecs.epp.isc.stream

import java.io.{IOException, InputStream}
import java.util.concurrent.{TimeoutException, ArrayBlockingQueue}
import java.util.concurrent.TimeUnit._

import akka.util.CompactByteString
import com.edifecs.epp.isc.async.{MessageFuture, TerminalMessageFuture}

import scala.util.{Failure, Success, Try}

trait MessageStreamSource {
  protected[stream] def nextChunk(
    callback: CompactByteString  => TerminalMessageFuture
  ): MessageFuture[Boolean]

  final protected[stream] def forEachChunk(
    callback: CompactByteString  => TerminalMessageFuture
  ): MessageFuture[Boolean] =
    nextChunk(callback).then(v => if (v) forEachChunk(callback) else MessageFuture(false))

  protected[stream] def toScalaStream(chunkCacheSize: Int): Stream[CompactByteString ] = {
    val cache = new ArrayBlockingQueue[Try[Option[CompactByteString ]]](chunkCacheSize)
    forEachChunk { c =>
      MessageFuture {
        cache add Success(Some(c))
      }
    } andThenDo (cache add Success(None)) orCatch (cache add Failure(_))
    def nextChunk(): Stream[CompactByteString ] =
      cache.poll(MessageStream.chunkTimeout, MILLISECONDS).get match {
        case Some(chunk) => chunk #:: nextChunk()
        case None => Stream.Empty
        case null => throw new TimeoutException(
          "Stream timed out after " + MessageStream.chunkTimeout + "ms")
      }
    nextChunk()
  }

  @throws[IOException]
  protected[stream] def toInputStream(chunkCacheSize: Int): InputStream =
    new ByteStringInputStream(toScalaStream(chunkCacheSize))
}

@throws[IOException]
class ByteStringInputStream(var stream: Stream[CompactByteString ]) extends InputStream {
  private var chunk: Option[CompactByteString ] =
    try {
      val c = stream.headOption
      if (c.isDefined) stream = stream.tail
      c
    } catch {
      case ioex: IOException => throw ioex
      case ex: Exception => throw new IOException(ex)
    }

  private def nextChunk: Option[CompactByteString ] =
    chunk flatMap { c =>
      if (c.isEmpty) {
        chunk = stream.headOption
        if (chunk.isDefined) stream = stream.tail
        chunk
      } else Some(c)
    }

  @throws[IOException]
  override def read(): Int = try
    // TODO: This could be made more efficient.
    nextChunk map { c =>
      chunk = Some(c.drop(1).compact)
      0xff & c(0).toInt
    } getOrElse -1
  catch {
    case ioex: IOException => throw ioex
    case ex: Exception => throw new IOException(ex)
  }

  @throws[IOException]
  override def read(b: Array[Byte], offset: Int, length: Int): Int = try
    nextChunk map { c =>
      if (c.length >= length) {
        c.copyToArray(b, offset, length)
        chunk = Some(c.drop(length).compact)
        length
      } else {
        c.copyToArray(b, offset, c.length)
        chunk = stream.headOption
        if (chunk.isDefined) stream = stream.tail
        c.length + Math.max(0, read(b, offset + c.length, length - c.length))
      }
    } getOrElse -1
  catch {
    case ioex: IOException => throw ioex
    case ex: Exception => throw new IOException(ex)
  }
}
