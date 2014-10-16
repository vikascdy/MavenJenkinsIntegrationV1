// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.contentrepository

import com.edifecs.epp.isc.Args

class __GeneratedProxy__IContentLibraryHandler(isc: com.edifecs.epp.isc.Isc, serviceTypeName: java.lang.String) extends IContentLibraryHandler {
  def address = isc.getAddressRegistry.getAddressForServiceTypeName(serviceTypeName)
  override def addItem(path: java.lang.String, item: com.edifecs.contentrepository.api.model.Item) = isc.sendSync(address, "addItem", Map[String, java.io.Serializable](("path", path), ("item", item))).asInstanceOf[com.edifecs.contentrepository.api.model.Item]
  override def getItemsAssociatedWith(associatedItemId: java.lang.String, getItemType: java.lang.String) = isc.sendSync(address, "getItemsAssociatedWith", Map[String, java.io.Serializable](("associatedItemId", associatedItemId), ("getItemType", getItemType))).asInstanceOf[java.util.Collection[com.edifecs.contentrepository.api.model.Item]]
  override def getAllItemsOfType(path: java.lang.String, itemType: java.lang.String) = isc.sendSync(address, "getAllItemsOfType", Map[String, java.io.Serializable](("path", path), ("itemType", itemType))).asInstanceOf[java.util.Collection[com.edifecs.contentrepository.api.model.Item]]
  override def createItem(path: java.lang.String, name: java.lang.String, description: java.lang.String, `type`: java.lang.String, associations: java.util.HashMap[java.lang.String, java.util.Collection[com.edifecs.contentrepository.api.model.Item]], properties: java.util.HashMap[java.lang.String, java.lang.Object]) = isc.sendSync(address, "createItem", Map[String, java.io.Serializable](("path", path), ("name", name), ("description", description), ("type", `type`), ("associations", associations), ("properties", properties))).asInstanceOf[com.edifecs.contentrepository.api.model.Item]
  override def getItemById(id: java.lang.String) = isc.sendSync(address, "getItemById", Map[String, java.io.Serializable](("id", id))).asInstanceOf[com.edifecs.contentrepository.api.model.Item]
  override def getAllItems(path: java.lang.String) = isc.sendSync(address, "getAllItems", Map[String, java.io.Serializable](("path", path))).asInstanceOf[java.util.Map[java.lang.String, java.util.Collection[com.edifecs.contentrepository.api.model.Item]]]
  override def getItemByName(path: java.lang.String, itemType: java.lang.String, name: java.lang.String) = isc.sendSync(address, "getItemByName", Map[String, java.io.Serializable](("path", path), ("itemType", itemType), ("name", name))).asInstanceOf[com.edifecs.contentrepository.api.model.Item]
  override def updateItem(item: com.edifecs.contentrepository.api.model.Item) = isc.sendSync(address, "updateItem", Map[String, java.io.Serializable](("item", item))).asInstanceOf[com.edifecs.contentrepository.api.model.Item]
  override def deleteItem(id: java.lang.String) = isc.sendSync(address, "deleteItem", Map[String, java.io.Serializable](("id", id))).asInstanceOf[Boolean]
}