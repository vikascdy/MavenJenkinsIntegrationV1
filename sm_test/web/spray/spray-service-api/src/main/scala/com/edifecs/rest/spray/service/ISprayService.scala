package com.edifecs.rest.spray.service

import com.edifecs.servicemanager.annotations.{Handler, Service, Property}

@Service(
  name = "spray-service",
  version = "1.0",
  description = "Spray HTTP Service",
  properties = Array(
    new Property(
      name = "http.port",
      propertyType = Property.PropertyType.LONG,
      description = "The HTTP port of the Spray server",
      defaultValue = "8080",
      required = false),
    new Property(
      name = "http.interface",
      propertyType = Property.PropertyType.STRING,
      description = "The HTTP interface of the Spray server",
      defaultValue = "localhost",
      required = false)))
trait ISprayService {
//  @Handler
//  def cache: ISprayCacheHandler

  @Handler
  def urls: ISprayUrlHandler
}
