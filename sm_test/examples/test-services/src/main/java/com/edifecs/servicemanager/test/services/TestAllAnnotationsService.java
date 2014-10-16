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
import com.edifecs.servicemanager.annotations.Resource;
import com.edifecs.servicemanager.annotations.Service;
import com.edifecs.servicemanager.annotations.ServiceDependency;
import com.edifecs.servicemanager.api.AbstractService;

/**
 * Service designed to test all types of annotations.
 * 
 * @author ashipras
 */
@Service(
    name = "Test All Annotations",
    version = "1.0",
    description = "Tests all types of annotations make to the service",
    properties = { @Property(name = "str", propertyType = PropertyType.STRING, description = "String variable", defaultValue = "STRING", required = true, editable = true),
                   @Property(name = "dbl", propertyType = PropertyType.DOUBLE, description = "Double variable", defaultValue = "0.0", required = true, editable = true),
                   @Property(name = "lng", propertyType = PropertyType.LONG, description = "Long variable", defaultValue = "4294967296", required = true, editable = false),
                   @Property(name = "dte", propertyType = PropertyType.DATE, description = "Date variable", defaultValue = "01/01/2013", required = true, editable = false),
                   @Property(name = "bln", propertyType = PropertyType.BOOLEAN, description = "Boolean variable", defaultValue = "true", required = true, editable = true),
                   @Property(name = "sln", propertyType = PropertyType.SELECTONE, description = "Select one variable", selectValues = {"one", "two", "three"}, defaultValue = "one", required = true)
                },
    
    services =  { @ServiceDependency (name = "Test Command Receiver", typeName = "Test Command Receiver", version = "1.0", unique = false)},
    resources = {
    		@Resource(name = "JDBCResource1", type = "JDBC Database"),
    		@Resource(name = "JDBCResource2", type = "JDBC Database"),
    		@Resource(name = "JDBCResource3", type = "JDBC Database"),
    		@Resource(name = "SMTPServer1", type = "SMTP Server"),
    		@Resource(name = "SMTPServer2", type = "SMTP Server"),
    		@Resource(name = "SMTPServer3", type = "SMTP Server")
})
public class TestAllAnnotationsService extends AbstractService {

    @Override
    public void start() throws Exception {
        getLogger().debug("{}: Service Successfully Started", getServiceAnnotation().name());
       
        Set<String> props = getProperties().stringPropertyNames();
        Iterator<String> itr = props.iterator();
        
        while (itr.hasNext()) {
            String key = itr.next();
            getLogger().debug("Property: {} => {}", key, getProperties().getProperty(key));
        }
      
        if (getServiceAnnotation().services() != null) {
            ServiceDependency[] dependencies = getServiceAnnotation().services();
            
            for (ServiceDependency dep : dependencies) {
                getLogger().debug("Service: {}", dep);
            }
        }
        
        if (getServiceAnnotation().resources() != null) {
            Resource[] resources = getServiceAnnotation().resources();
                
            for (Resource res : resources) {
                getLogger().debug("Resource: {}", res);
            }
        }
    }

    @Override
    public void stop() throws Exception {
        getLogger().debug("{}: Service Successfully Stopped",  getServiceAnnotation().name());
    }
}

