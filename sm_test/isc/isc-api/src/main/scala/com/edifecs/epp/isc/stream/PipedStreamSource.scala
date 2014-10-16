package com.edifecs.epp.isc.stream

import java.util.concurrent.ArrayBlockingQueue

import akka.util.CompactByteString 
import com.edifecs.epp.isc.async.{MessageFuture, TerminalMessageFuture}

import scala.util.{Success, Failure, Try}

class PipedStreamSource(
  val chunkCacheSize: Int = MessageStream.chunkCacheSize
) extends MessageStreamSource {

  private lazy val cache = new ArrayBlockingQueue[Try[Option[CompactByteString ]]](chunkCacheSize)

  def receiveChunk(chunk: Try[CompactByteString ]): Unit = cache.add(Try(Some(chunk.get)))
  
  def receiveChunk(chunk: CompactByteString ): Unit = cache.add(Success(Some(chunk)))

  def receiveException(ex: Throwable): Unit = cache.add(Failure(ex))

  def endStream() = cache.add(Success(None))

  override def nextChunk(
    callback: CompactByteString  => TerminalMessageFuture
  ) = MessageFuture().andThen{
    cache.take.get match {
      case Some(chunk) => callback(chunk).andThen(MessageFuture(true))
      case None => MessageFuture(false)
    }
  }
}
