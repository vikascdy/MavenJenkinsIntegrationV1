package com.edifecs.epp.isc.async

import com.edifecs.epp.isc.Isc

import scala.concurrent.Future
import com.edifecs.epp.isc.exception.MessageException

class ScalaMessageFutureWrapper[T](wrapped: Future[T]) extends MessageFuture[T] {
  
  override def then[U](callback: T => MessageFuture[U])(implicit disambiguator: ScalaOnly) =
    new ScalaMessageFutureWrapper(
      wrapped.flatMap(callback(_).asScalaFuture)(Isc.get.getExecutionContext))

  override def orCatchThen(
    callback: MessageException => MessageFuture[T])(
    implicit disambiguator: ScalaOnly
  ) = new ScalaMessageFutureWrapper(wrapped.recoverWith {
        case e: MessageException => callback(e).asScalaFuture
        case t: Throwable => callback(new MessageException(t, Isc.stack)).asScalaFuture
      } (Isc.get.getExecutionContext))

  override def asScalaFuture: Future[T] = wrapped
}

object ScalaMessageFutureWrapper {
  def wrapObject[T](obj: T): ScalaMessageFutureWrapper[T] =
    new ScalaMessageFutureWrapper[T](Future{obj}(Isc.get.getExecutionContext))
}
