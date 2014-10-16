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

package com.edifecs.servicemanager.test.services;

import java.util.Iterator;
import java.util.Set;

import com.edifecs.servicemanager.annotations.Property;
import com.edifecs.servicemanager.annotations.Property.PropertyType;
import com.edifecs.servicemanager.annotations.Service;
import com.edifecs.servicemanager.api.AbstractService;

/**
 * Service designed to test all types of properties. 
 * 
 * @author ashipras
 */
@Service(
    name = "Test All Properties Type",
    version = "1.0",
    description = "Tests all types of properties make to the service",        
    properties = { @Property(name = "str", propertyType = PropertyType.STRING, description = "String variable", defaultValue = "STRING", required = true),
                   @Property(name = "dbl", propertyType = PropertyType.DOUBLE, description = "Double variable", defaultValue = "0.0", required = true), 
                   @Property(name = "lng", propertyType = PropertyType.LONG, description = "Long variable", defaultValue = "4294967296", required = true),
                   @Property(name = "dte", propertyType = PropertyType.DATE, defaultValue = "dd/mm/yyyy", description = "Date variable",  required = true),
                   @Property(name = "bln", propertyType = PropertyType.BOOLEAN, description = "Boolean variable", defaultValue = "true", required = true),
                   @Property(name = "sln", propertyType = PropertyType.SELECTONE, description = "Select one variable", selectValues = {"one", "two", "three"}, defaultValue = "one", required = true)
                 }
)
public class TestAllPropertiesTypeService extends AbstractService {

    @Override
    public void start() throws Exception {   
        getLogger().debug("{}: Service Successfully Started",  getServiceAnnotation().name());
       
        Set<String> props = getProperties().stringPropertyNames();
        Iterator<String> itr = props.iterator();
        
        while (itr.hasNext()) { 
            String key = itr.next(); 
            getLogger().debug("Property: {} => {}", key, getProperties().getProperty(key));             
        }
    }

    @Override
    public void stop() throws Exception {
        getLogger().debug("{}: Service Successfully Stopped",  getServiceAnnotation().name());
    }   
}

