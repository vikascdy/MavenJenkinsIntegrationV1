package com.edifecs.message.command.json.models;

//-----------------------------------------------------------------------------
//Copyright (c) Edifecs Inc. All Rights Reserved.
//
//This software is the confidential and proprietary information of Edifecs Inc.
//("Confidential Information").  You shall not disclose such Confidential
//Information and shall use it only in accordance with the terms of the license
//agreement you entered into with Edifecs.
//
//EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
//SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
//WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
//NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
//LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
//ITS DERIVATIVES.
//-----------------------------------------------------------------------------

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * The core interface for the HDM data model. It provides the basis for all
 * other data classes in the system.
 * 
 * Subclasses of Data which introduce new fields will not have those fields
 * automatically persisted in the repository. Any data which should be persisted
 * should be assigned to and pulled from properties.
 * 
 * The external ID provided to the Data object can be used to identify the
 * object at a later time via search or to associate the result of a bulk
 * operation with that unit of data, but the ID is not used by the repository
 * system in any way. Using the external ID in any repository operation will not
 * return the original data object and as it is likely not a match for an
 * existing Repository ID it will likely throw an exception.
 * 
 * @author josefern
 */
public interface TestData extends Serializable {

	/**
	 * @return The external ID that was provided to the object by the client.
	 *         May be null.
	 */
	String getExternalId();

	/**
	 * @return The record assigned to the object by the repository. This will be
	 *         null prior to saving, but should never be null after saving or
	 *         when data is retrieved from the repository.
	 */
	String getRecordId();

	/**
	 * @return The name of the DataType object that classifies this data
	 *         element. Will not return null.
	 */
	String getType();

	/**
	 * @return Denotes the subtype of the data element. Defaults to "Data".
	 *         Other options include "Document", "DocumentItem", or
	 *         "Attachment".
	 */
	String getClassification();

	/**
	 * @return The creation date of the data as provided by the client. If no
	 *         date was provided by the client application, the data will be
	 *         given a creation date of the current date and time.
	 */
	Date getCreationDate();

	/**
	 * Clients should set the creation date in accordance with their data
	 * retention policies. This date may be used by the repository for multiple
	 * purposes, including when purging expired data.
	 * 
	 * @param date
	 *            The date the data was originally created.
	 */
	void setCreationDate(Date date);

	/**
	 * @return The date on which the data was last modified. This may or may not
	 *         be automatically supported by the repository, but it will be
	 *         stored if provided by the client. May return null.
	 */
	Date getLastModifiedDate();

	/**
	 * Sets the last modified date if the client wishes to control this
	 * functionality manually.
	 * 
	 * @param date
	 *            The date on which the data was last modified.
	 */
	void setLastModifiedDate(Date date);

	/**
	 * Retrieves the value of the requested property. Only properties that were
	 * stored in the data can be retrieved. Clients must use the DataType to
	 * determine how to cast the property to its native class after it has been
	 * retrieved as an instance of Serializable.
	 * 
	 * @param propertyName
	 *            The name of the property being retrieved.
	 * @return Null if the property does not exist, or the Serializable value if
	 *         it does exist.
	 */
	Serializable getProperty(String propertyName);

	/**
	 * Sets the value of a given property to the value provided. This property
	 * should exist in the type mapping for the data's DataType.
	 * 
	 * Only specific values are allowed. See
	 * {@link com.edifecs.hdm.model.metadata.PropertyType} for a full
	 * explanation.
	 * 
	 * @param propertyName
	 *            The name of the property.
	 * @param value
	 *            The value of the property.
	 */
	void setProperty(String propertyName, Serializable value);

	/**
	 * @return All properties contained within the data.
	 */
	Map<String, Serializable> getProperties();

	/**
	 * Get the timestamp as provided by the repository.
	 * 
	 * @return The timestamp of the Data object.
	 */
	Date getTimestamp();

	@Override
	boolean equals(Object obj);

	@Override
	int hashCode();

}
