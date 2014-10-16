// -----------------------------------------------------------------------------
// Copyright (c) Edifecs Inc. All Rights Reserved.
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

package com.edifecs.contentrepository;

import com.edifecs.core.configuration.helper.SystemVariables;
import com.edifecs.servicemanager.annotations.Handler;
import com.edifecs.servicemanager.annotations.Property;
import com.edifecs.servicemanager.annotations.Property.PropertyType;
import com.edifecs.servicemanager.annotations.Service;

/**
 * ServiceManager Service allows the developer to push or pull content from the
 * Content repository using our messaging API.
 *
 * @author willclem
 */
@Service(name = SystemVariables.CONTENT_REPOSITORY_SERVICE_NAME,
        version = "1.0",
        description = "Content Repository Service",
        properties = {
                @Property(name = "Path", defaultValue = "", propertyType = PropertyType.STRING, description = "Custom path to the location of the Content Repository data. If Null, it will use the default location")})
public interface IContentRepositoryService {

    @Handler
    IContentRepositoryHandler getContentRepositoryHandler();

    @Handler
    IContentLibraryHandler getContentLibraryHandler();

}
