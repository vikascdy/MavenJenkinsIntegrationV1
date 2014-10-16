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

import com.edifecs.epp.isc.command.CommandMessage;
import com.edifecs.epp.isc.command.CommandSource;

/**
 * Thrown when a
 * {@link com.edifecs.epp.isc.core.command.AbstractCommandHandler} rejects
 * a command from an unsupported {@link CommandSource}.
 * 
 * @author i-adamnels
 */
public class InvalidSourceException extends InvalidCommandException {
    private static final long serialVersionUID = 1L;

    private final CommandSource invalidSource;

    public InvalidSourceException(CommandMessage invalidCommand,
            CommandSource invalidSource) {
        super(invalidCommand);
        this.invalidSource = invalidSource;
    }

    public InvalidSourceException(CommandMessage invalidCommand,
            CommandSource invalidSource, String message) {
        super(invalidCommand, message);
        this.invalidSource = invalidSource;
    }

    public CommandSource getInvalidSource() {
        return invalidSource;
    }
}
