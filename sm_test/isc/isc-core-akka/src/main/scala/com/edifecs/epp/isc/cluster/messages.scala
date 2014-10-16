package com.edifecs.epp.isc.cluster

import akka.actor
import akka.actor.PossiblyHarmful
import com.edifecs.epp.isc

import scala.concurrent.duration.Duration

object messages {
  case class JoinAttemptTo(address: actor.Address) extends PossiblyHarmful
  case class JoinAttemptFrom(address: actor.Address) extends PossiblyHarmful
  case class InitializeJoiner(clusterName: String, address: actor.Address) extends PossiblyHarmful
  case object FindNodes extends PossiblyHarmful
  case object NodeAddressRequest
  case class NodeAddressResponse(address: isc.Address)
  case class NotifyWhenRegistered(address: isc.Address, timeout: Duration)
  case class Registered(address: isc.Address)
}
