package com.edifecs.epp.isc.async

import scala.concurrent.Future

object Implicits {

  implicit def messageFutureToScalaFuture[T](mf: MessageFuture[T]): Future[T] =
    mf.asScalaFuture
  
  implicit def scalaFutureToMessageFuture[T](sf: Future[T]): MessageFuture[T] =
    new ScalaMessageFutureWrapper(sf)
}
