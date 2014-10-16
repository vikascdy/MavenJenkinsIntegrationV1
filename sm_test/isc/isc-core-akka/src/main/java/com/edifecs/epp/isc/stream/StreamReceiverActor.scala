package com.edifecs.epp.isc.stream

import akka.actor.{ActorLogging, Actor}
import akka.util.CompactByteString 

class StreamReceiverActor(pipe: PipedStreamSource) extends Actor with ActorLogging {
  override def receive = {
    case chunk: CompactByteString  =>
      //log.debug("Received chunk")
      pipe.receiveChunk(chunk)
      sender() ! StreamAck
    case EndOfStream =>
      log.debug("Terminating stream")
      pipe.endStream()
      context.stop(self)
    case StreamException(ex) =>
      log.error(ex, "Got exception, terminating stream")
      pipe.receiveException(ex)
      context.stop(self)
  }
}

case object StreamAck

case object EndOfStream
