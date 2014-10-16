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
package com.edifecs.core.configuration.configuration;

public enum LogLevel {
    /**
     * Fatal Logging Level
     */
    FATAL("FATAL"),
    /**
     * Debug Logging Level.
     */
    DEBUG("DEBUG"),
    /**
     * Error Logging Level.
     */
    ERROR("ERROR"),
    /**
     * Warning Logging Level.
     */
    WARNING("WARNING"),
    /**
     * Info Logging Level.
     */
    INFO("INFO");

    private String text;

    private LogLevel(final String newText) {
        text = newText;
    }

    /**
     * Gets the string value of the LogLevel enum.
     * 
     * @return String representation of the LogLEvel Enum
     */
    public final String getText() {
        return text;
    }

    /**
     * Get the LogLevel enum value from a string.
     * 
     * @param text
     *            LogLevel String value
     * @return LogLevel enum value
     */
    public static final LogLevel fromString(final String text) {
        if (text != null) {
            for (LogLevel b : LogLevel.values()) {
                if (text.equalsIgnoreCase(b.text)) {
                    return b;
                }
            }
        }
        return null;
    }
}