package com.edifecs.build

import sbt._

// The only Resolver that should be here are edifecs only resolvers. If we need another third party resolver, it needs
// to be configured through our ennexus server.
// TODO: Remove all but edifecs resolvers
object Resolvers {
  val defaultResolvers = Seq(
    "releasesResolver" at "http://oss.sonatype.org/content/repositories/releases",
    "edifecsReleasesResolver" at "https://ennexus.corp.edifecs.com/content/repositories/releases/",
    "edifecsSnapshotResolver" at "https://ennexus.corp.edifecs.com/content/repositories/snapshots/",
    Resolver.typesafeRepo("releases")
  )

  val sprayRepo = "spray.io" at "http://repo.spray.io"

  val clojarsRepo = "storm" at "http://clojars.org/repo"

  val typesafeRepo = "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

	val twitter = "twitter4" at "http://twitter4.org/maven2"
}
