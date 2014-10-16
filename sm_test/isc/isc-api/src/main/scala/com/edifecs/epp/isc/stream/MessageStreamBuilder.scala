package com.edifecs.epp.isc.stream

import java.io.OutputStream

import akka.util.CompactByteString 
import com.edifecs.epp.isc.async.{MessageFuture, TerminalMessageFuture}

import scala.util.{Failure, Success, Try}

class MessageStreamBuilder {
  private var contentType = "application/octet-stream"
  private var filename : Option[String] = Option.empty
  private var lastModified = System.currentTimeMillis()
  private var chunkSize = MessageStream.chunkSize
  private val source = new PipedStreamSource()

  def maxChunkSize(size: Int): MessageStreamBuilder = {
    this.chunkSize = size
    this
  }

  def httpContentType(contentType: String): MessageStreamBuilder = {
    this.contentType = contentType
    this
  }

  def filename(filename: String): MessageStreamBuilder = {
    this.filename = Option.apply(filename)
    this
  }

  def lastModifiedTimestamp(timestamp: Long): MessageStreamBuilder = {
    this.lastModified = timestamp
    this
  }

  def chunk(data: Try[CompactByteString ]): TerminalMessageFuture = data match {
    case Success(b) => chunk(b)
    case Failure(ex) => error(ex)
  }

  def asOutputStream = new OutputStream() {
    var future: Option[TerminalMessageFuture] = None

    override def write(b: Int) = write(Array(b.toByte), 0, 1)

    override def write(buf: Array[Byte], off: Int, len: Int) = {
      val str = CompactByteString .fromArray(buf, off, len)
      future = Some(future map (_ andThenDo chunk(str)) getOrElse chunk(str))
    }

    override def close() = future = Some(future map (_ andThenDo end()) getOrElse end())
  }

  def chunk(data: CompactByteString ): TerminalMessageFuture = {
    if (data.length > chunkSize) {
      val parts = data.splitAt(chunkSize)
      chunk(parts._1.compact) andThen (chunk(parts._2.compact) andThen MessageFuture(null))
    } else MessageFuture(source.receiveChunk(data))
  }

  def error(ex: Throwable): TerminalMessageFuture = MessageFuture {
    source.receiveException(ex)
  }

  def end(): TerminalMessageFuture = MessageFuture {
    source.endStream()
  }

  def toStream: MessageStream = MessageStream(contentType, filename, lastModified, source)
}
