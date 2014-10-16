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
package com.edifecs.servicemanager.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adding Service Annotation to a class, registers it as a potential installable and runnable service within the application platform.
 * 
 * @author willclem
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Service {

    /**
     * Defines the unique canonical name of the service. This should be a unique descriptive name. It is used to search
     * for it within the cluster. It is required.
     */
	String name();

    /**
     * This is the default name that is displayed to the user if no other is provided.
     */
    String displayName() default "";

    /**
     * Defines the version of the service. This is used to help distinguish between to identify which version of the service
     * is running so multiple versions of the same service can be running within the platform. It is required.
     */
	String version();

    /**
     * Detailed description of the service that can be used to help identify it. It is the default value displayed if
     * there is not another one defined. It is optional
     */
	String description() default "";

    /**
     * This defines if the service can be run more then once within a cluster. This is depreciated as the configuration
     * is being moved to another configuration.
     *
     * @return
     */
    @Deprecated
	int maxInstances() default -1;

    /**
     * If the application is installed that contains this service is installed in the cluster, this service is required.
     * This is depreciated as it is being moved to another configuration.
     */
    @Deprecated
	boolean required() default false;

    /**
     * If the service is installed on a server, and requires to stay there due to some persistence requirement, or other
     * constraint that prevents it from running on another server, this should be set to true.
     * This is depreciated as it is being moved to another configuration.
     */
    @Deprecated
	boolean unmovable() default false;

    /**
     * List of properties that are required for the service to be configured by either a site, tenant, organization,
     * or user.
     */
    // TODO: Update to support scope within the properties.
	Property[] properties() default {

	};

    /**
     * List of dependent services. Needs further details.
     */
    // TODO: Update to support dependencies other then just services, but also other components.
	ServiceDependency[] services() default {

	};

    // TODO: Figure out how this is effected by the concept of PaaS as a resource really turns into a type of dependency
	Resource[] resources() default {

	};
}
