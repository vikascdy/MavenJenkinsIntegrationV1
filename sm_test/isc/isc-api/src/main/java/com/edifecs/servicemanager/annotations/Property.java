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
 * Properties that are annotated, are exposed and available for configuration at many locations.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Property {

    String name();

    String defaultValue() default "";

    String description() default "";

    boolean required() default false;

    PropertyType propertyType() default PropertyType.STRING;

    String regEx() default "";

    String regExError() default "";

    String[] selectValues() default { };
    
    boolean editable() default true;

    public enum PropertyType {
        STRING("STRING"), DOUBLE("DOUBLE"), LONG("LONG"), DATE("DATE"), BOOLEAN("BOOLEAN"), SELECTONE("SELECTONE");

        private String text;

        private PropertyType(final String newText) {
            text = newText;
        }

        public final String getText() {
            return text;
        }

        public static final PropertyType fromString(final String text) {
            if (text != null) {
                for (PropertyType b : PropertyType.values()) {
                    if (text.equalsIgnoreCase(b.text)) {
                        return b;
                    }
                }
            }
            return null;
        }

    }
    
}
