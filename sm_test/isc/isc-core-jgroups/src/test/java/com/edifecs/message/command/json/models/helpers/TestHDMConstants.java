package com.edifecs.message.command.json.models.helpers;

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

/**
* This is a central location for values that need to remain constant across the
* entire project.
* 
* @author josefern
*
*/
public interface TestHDMConstants {

/** A configuration property to indicate a system is in test mode. In test
 *  mode data will not be permanently maintained.
 */

String TEST_MODE = "Test Mode";

/** The default encoding used by the system. */

String DEFAULT_ENCODING = "UTF-8";

/** A reserved character used to separate elements in the RecordId. It should
 *  not be used in the name or ID of any artifact within HDM.
 */

String SEPARATOR = "@";

/** This symbol is used in DataStore configuration to indicate that the store
 *  will accept files of any DataType.
 */

String DATA_TYPE_ALL = "*";

/** The default value indicating that no file segment is present. */

long NO_OFFSET = -1;

/** DataStores can be configured to accept the DataType of "Unknown" in which
 *  case they will accept file of any DataType for which there is no specific
 *  configuration elsewhere.
 */

String DATA_TYPE_UNKNOWN = "Unknown";

/** The DataType reserved for storing Attachment instances. */

String DATA_TYPE_ATTACHMENT = "Attachment";

/** The DataType used for storing un-typed InputStream data in a DataStore. */

String DATA_TYPE_FILE = "File";

/** A list of DataTypes that are reserved and cannot be used by client
 *  configurations.
 */

String[] RESERVED_DATA_TYPES = 
			{DATA_TYPE_ALL, DATA_TYPE_UNKNOWN, DATA_TYPE_ATTACHMENT, DATA_TYPE_FILE};

/**
 * Denotes data is a Data element.  
 */

String CLASSIFICATION_DATA = "Data";

/**
 * Denotes data is a Document element.  
 */

String CLASSIFICATION_DOCUMENT = "Document";

/**
 * Denotes data is a DocumentItem element.  
 */

String CLASSIFICATION_DOCUMENT_ITEM = "DocumentItem";

/**
 * Denotes data is an Attachment element.  
 */

String CLASSIFICATION_ATTACHMENT = "Attachment";

/** Used for repository updates to maintain relationships regardless of
 * Record ID changes.
 */

String PROP_INTERNAL_ID = "InternalId";

/** Used for repository updates to indicate the RecordId the object had prior
 *  to being updated.
 */

String PROP_OLD_ID = "OldId";

/** Indicates the RecordId of the parent object to an instance of ReferenceInfo. */

String PROP_OWNER_ID = "OwnerId";

/** Indicates the RecordId of the File associated with an element of Data. */

String PROP_FILE_ID = "FileId";

/** If a Data element (likely a DocumentItem) is associated with a portion
 *  of a blob stored in the repository, this property indicates the starting
 *  byte at which the file read should begin. The start byte is inclusive.
 */

String PROP_FILE_START_OFFSET = "FileStartOffset";

/** If a Data element (likely a DocumentItem) is associated with a portion
 *  of a blob stored in the repository, this property indicates the starting
 *  byte at which the file read should end. The end byte is inclusive.
 */

String PROP_FILE_END_OFFSET = "FileEndOffset";

/** Used as a property name for a child element in a hierarchical data
 * structure to indicate the recordId of the root element (likely a
 * Document).
 */

String PROP_ROOT_ID = "RootId";

/** Used as a property name for a child element in a hierarchical data
 * structure to indicate the recordId of its parent element.
 */

String PROP_PARENT_ID = "ParentId";

/** Retrieves the date the object was received by the repository. */

String PROP_TIMESTAMP = "SystemTimestamp";

/** A list of Property names that are reserved and cannot be used by client
 *  data types.
 */

String[] RESERVED_PROPERTIES = 
			{PROP_INTERNAL_ID, PROP_OLD_ID, PROP_OWNER_ID, PROP_FILE_ID, 
			PROP_FILE_START_OFFSET, PROP_FILE_END_OFFSET, PROP_ROOT_ID,
			PROP_PARENT_ID, PROP_TIMESTAMP};

}

