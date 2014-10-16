// -----------------------------------------------------------------------------
//  Copyright (c) Edifecs Inc. All Rights Reserved.
//
// This software is the confidential and proprietary information of Edifecs Inc.
// ("Confidential Information").  You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the license
// agreement you entered into with Edifecs.
//
// EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
// SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
// NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
// LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
// ITS DERIVATIVES.
// -----------------------------------------------------------------------------
package com.edifecs.epp.security.datastore;

import java.io.Serializable;
import java.util.Collection;

import com.edifecs.epp.security.data.PaginatedList;
import com.edifecs.epp.security.exception.ItemNotFoundException;
import com.edifecs.epp.security.exception.SecurityDataException;

/**
 * General interface for any backend service (such as a database) that can be
 * used to store and manipulate user information.
 * 
 * @author willclem
 * @author i-adamnels
 */
public interface IBaseDataStore<T extends Serializable> {

	@Deprecated
	Collection<T> getAll() throws SecurityDataException;

	Collection<T> getRange(long startRecord, long recordCount) throws SecurityDataException;

	PaginatedList<T> getPaginatedRange(long startRecord, long recordCount) throws SecurityDataException;

	T getById(long id) throws ItemNotFoundException, SecurityDataException;

	void delete(T toDelete) throws SecurityDataException;
}
