package com.edifecs.epp.isc.stream

import java.io.{OutputStreamWriter, InputStream}
import java.util.UUID
import java.util.concurrent.TimeUnit.{MILLISECONDS => ms}
import java.util.concurrent.{ArrayBlockingQueue, CountDownLatch}

import akka.util.CompactByteString
import com.edifecs.epp.isc.Isc
import com.edifecs.epp.isc.async._

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}
import scala.xml.{XML, Node}
import scala.xml.dtd.DocType

sealed case class MessageStream(
  httpContentType: String,
  filename: Option[String],
  lastModifiedTimestamp: Long,
  @transient source: MessageStreamSource,
  id: MessageStream.StreamId = MessageStream.newId()
) {
  import MessageStream._

  def forEachChunk(callback: CompactByteString => TerminalMessageFuture): TerminalMessageFuture =
    try source.forEachChunk(callback)
    catch {case ex: Exception => MessageFuture(throw ex)}

  def forEachChunk(callback: ChainCallback[CompactByteString, _]): TerminalMessageFuture =
    forEachChunk(callback.call(_))

  def mapEachChunk[T](callback: CompactByteString => MessageFuture[T]): MessageFuture[Seq[T]] =
    fold[Seq[T]](Seq.empty)((s, c) => callback(c).then((arg:T) => MessageFuture(s ++ Seq(arg))))

  def mapEachChunk[T](
    callback: ChainCallback[CompactByteString, T]
  ): MessageFuture[java.util.List[T]] =
    mapEachChunk(callback.call(_)).then((s: Seq[T]) => MessageFuture(seqAsJavaList(s)))

  def fold[T](first: T)(callback: (T, CompactByteString) => MessageFuture[T]): MessageFuture[T] = {
    var result = MessageFuture(first)
    val latch = new CountDownLatch(1)
    forEachChunk(c => MessageFuture {
      result = result.then(callback(_, c))
    }) orCatch (ex =>
      result = result.andThen(throw ex)
    ) andThenDo latch.countDown()
    latch.await()
    result
  }

  def fold[T](first: T, callback: ChainCallback2[T, CompactByteString, T]): MessageFuture[T] =
    fold(first)(callback.call(_, _))

  def toInputStream(chunkCacheSize: Int = chunkCacheSize): InputStream =
    source.toInputStream(chunkCacheSize)

  def toInputStream: InputStream = toInputStream()

  def toScalaStream: Stream[CompactByteString] = toScalaStream()

  def toScalaStream(
    chunkSize: Int = chunkSize,
    chunkCacheSize: Int = chunkCacheSize
  ): Stream[CompactByteString] = {
    val cache = new ArrayBlockingQueue[Try[Option[CompactByteString]]](chunkCacheSize)
    forEachChunk { c =>
      MessageFuture {cache add Success(Some(c))}
    } andThenDo (cache add Success(None)) orCatch (cache add Failure(_))
    def nextChunk(): Stream[CompactByteString] =
      cache.poll(chunkTimeout, ms).get match {
        case Some(chunk) => chunk #:: nextChunk()
        case None => Stream.Empty
      }
    nextChunk()
  }

  override def toString = "<stream " + id + ">"
}

object MessageStream {

  type StreamId = UUID

  def newId(): StreamId = UUID.randomUUID()

  private[stream] def chunkSize =
    Isc.get.getConfig.getBytes("isc.messaging.stream.chunk-size").toInt
  private[stream] def chunkCacheSize =
    Isc.get.getConfig.getInt("isc.messaging.stream.chunk-cache-size")
  private[stream] def chunkTimeout =
    Isc.get.getConfig.getDuration("isc.messaging.stream.chunk-timeout", ms)

  implicit def fromInputStream(
    stream: InputStream
  ) = MessageStream(
    "application/octet-stream",
    Option.empty,
    System.currentTimeMillis(),
    new InputStreamSource(stream))

  def fromInputStream(
    stream: InputStream,
    httpContentType: String,
    lastModifiedTimestamp: Long
  ) = MessageStream(
    httpContentType,
    Option.empty,
    lastModifiedTimestamp,
    new InputStreamSource(stream))

  def fromInputStream(
    stream: InputStream,
    httpContentType: String,
    filename: String,
    lastModifiedTimestamp: Long
  ) = MessageStream(
    httpContentType,
    Option(filename),
    lastModifiedTimestamp,
    new InputStreamSource(stream))

  def fromInputStream(
    stream: InputStream,
    httpContentType: String,
    lastModifiedTimestamp: Long,
    maxChunkSize: Int
  ) = MessageStream(
    httpContentType,
    Option.empty,
    lastModifiedTimestamp,
    new InputStreamSource(stream, maxChunkSize))

  def fromString(data: String, httpContentType: String = "text/plain") = {
    val builder = new MessageStreamBuilder().httpContentType(httpContentType)
    builder.chunk(CompactByteString.apply(data)).andThenDo(builder.end())
    builder.toStream
  }

  def fromXml(
    xml: Node,
    httpContentType: String = "application/xml",
    doctype: DocType = null,
    xmlDecl: Boolean = true
  ) = {
    val builder = new MessageStreamBuilder().httpContentType(httpContentType)
    val writer = new OutputStreamWriter(builder.asOutputStream, "utf-8")
    try XML.write(writer, xml, enc="utf-8", doctype=doctype, xmlDecl=xmlDecl)
    finally writer.close()
    builder.toStream
  }

  private[stream] class InputStreamSource(
    stream: InputStream,
    maxChunkSize: Int = chunkSize
  ) extends MessageStreamSource {

    val buffer = Array.ofDim[Byte](maxChunkSize)
    var c = 0

    override def nextChunk(
      callback: CompactByteString => TerminalMessageFuture
    ) = MessageFuture().andThen{
      c = stream.read(buffer)
      if (c > -1) callback(CompactByteString.fromArray(buffer, 0, c)).andThen(MessageFuture(true))
      else MessageFuture(false)
    }

    override def toInputStream(chunkCacheSize: Int = 0) = stream
  }
}
