// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.contentrepository

class __GeneratedProxy__IContentRepositoryService(isc: com.edifecs.epp.isc.Isc) extends IContentRepositoryService {
  val serviceType = "content-repository-service"
  override lazy val getContentLibraryHandler = new com.edifecs.contentrepository.__GeneratedProxy__IContentLibraryHandler(isc, serviceType)
  override lazy val getContentRepositoryHandler = new com.edifecs.contentrepository.__GeneratedProxy__IContentRepositoryHandler(isc, serviceType)
}