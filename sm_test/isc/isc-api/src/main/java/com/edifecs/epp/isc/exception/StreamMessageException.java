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
package com.edifecs.epp.isc.exception;

/**
 * Thrown when a
 * {@link com.edifecs.epp.isc.core.command.AbstractCommandHandler} cannot
 * be configured because its methods do not have the necessary annotations.
 * 
 * @author i-adamnels
 */
public class StreamMessageException extends Exception {
    private static final long serialVersionUID = 1L;

    public StreamMessageException() {
    }

    public StreamMessageException(String message) {
        super(message);
    }

    public StreamMessageException(Throwable cause) {
        super(cause);
    }

    public StreamMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
