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
package com.edifecs.core.validator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public final class ResourceValidatorFactory {

    private static ResourceValidatorFactory factory;

    private ResourceValidatorFactory() {

    }

    public static ResourceValidatorFactory getInstance() {
        if (factory == null) {
            factory = new ResourceValidatorFactory();
        }
        return factory;
    }

    public ResourceValidator createResourceValidator(String resourceName, Properties properties) throws InstantiationException,
            IllegalAccessException,
            ClassNotFoundException,
            NoSuchMethodException,
            InvocationTargetException {
        if (resourceName == null) {
            return null;
        }
        Constructor<?> constructor = Class.forName("com.edifecs.core.validator.impl." + resourceName + "Validator")
                .getConstructor(Properties.class);
        ResourceValidator validator = (ResourceValidator) constructor.newInstance(properties);
        return validator;
    }
}
