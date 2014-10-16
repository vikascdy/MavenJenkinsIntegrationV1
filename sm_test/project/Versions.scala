package com.edifecs.build

/**
 * To help define and manage the independent component versions, this file helps define and centralize the independent
 * versions. The val names map to the project or parent directory of the versioned component using camel case.
 *
 * @author willclem
 */
object Versions {

  val default = "1.7.0.0-SNAPSHOT"
  val serviceManagerDefault = "1.7.0.0-SNAPSHOT"

  val manifest = "0.1.0.0"

  val eas = "0.1.0.0"

  val connectivity = default

  val contentRepository = serviceManagerDefault

  val coordination = default

  val datalake = default

  val ecm = default

  val esm = serviceManagerDefault

  val examples = default

  val flexfields = serviceManagerDefault

  val isc = serviceManagerDefault

  val metric = serviceManagerDefault

  val mpp = default

  val notification = serviceManagerDefault

  val sm = serviceManagerDefault

  val web = serviceManagerDefault

  val spray = web

  val tomcat = web

  val xboard = serviceManagerDefault

}