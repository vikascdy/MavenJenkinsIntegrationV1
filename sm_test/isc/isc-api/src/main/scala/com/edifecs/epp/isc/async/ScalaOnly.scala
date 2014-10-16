package com.edifecs.epp.isc.async

sealed trait ScalaOnly

object ScalaOnly {
  implicit val self = new Object with ScalaOnly
}
