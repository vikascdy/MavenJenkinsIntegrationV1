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
package com.edifecs.epp.isc.core;

public enum ServiceStatus {
    Created("Created"), Started("Started"), Stopped("Stopped"), Starting("Starting"), Stopping(
            "Stopping"), Error("Error");

    private String text;

    private ServiceStatus(final String newText) {
        this.text = newText;
    }

    /**
     * Gets a String representation of the ServiceStatusEnum enum.
     * 
     * @return String
     */
    public final String getText() {
        return text;
    }

    /**
     * Returns the ServiceStatusEnum enum object from a given String.
     * 
     * @param text
     *            String representation of the enum value
     * @return ServiceStatusEnum
     */
    public static final ServiceStatus fromString(final String text) {
        if (text != null) {
            for (ServiceStatus b : ServiceStatus.values()) {
                if (text.equalsIgnoreCase(b.text)) {
                    return b;
                }
            }
        }
        return null;
    }

}
