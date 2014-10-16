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
package com.edifecs.epp.isc;

/**
 * Optional message options to set message sending options. If no options are
 * given, then the default settings are used.
 * 
 * @author willclem
 */
public class MessageOption {

    private long timeout;

    /**
     * Sets the time to wait for message delivery.
     * 
     * @return long - timeout for message delivery.
     */
    public final long getTimeout() {
        return timeout;
    }

    /**
     * Gets the set time to wait for message delivery.
     * 
     * @param newTimeout
     *            long - timeout for message delivery.
     */
    public final void setTimeout(final long newTimeout) {
        timeout = newTimeout;
    }

}
