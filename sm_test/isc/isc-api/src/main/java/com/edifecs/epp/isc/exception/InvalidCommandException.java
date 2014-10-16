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

/**
 * Thrown when an {@link CommandMessage} is rejected by an
 * {@link com.edifecs.epp.isc.core.command.AbstractCommandHandler} because
 * it either specifies a command that is not registered or has the wrong number
 * and/or type of arguments for the command that it specifies.
 * 
 * @author i-adamnels
 */
public class InvalidCommandException extends Exception {
    private static final long serialVersionUID = 1L;

    private final CommandMessage invalidCommand;

    public InvalidCommandException(CommandMessage invalidCommand) {
        this.invalidCommand = invalidCommand;
    }

    public InvalidCommandException(CommandMessage invalidCommand,
            String message) {
        super(message);
        this.invalidCommand = invalidCommand;
    }

    public InvalidCommandException(CommandMessage invalidCommand,
            Throwable cause) {
        super(cause);
        this.invalidCommand = invalidCommand;
    }

    public InvalidCommandException(CommandMessage invalidCommand,
            String message, Throwable cause) {
        super(message, cause);
        this.invalidCommand = invalidCommand;
    }

    /**
     * Returns the {@link CommandMessage} that was rejected by a command handler
     * due to this exception.
     */
    public CommandMessage getInvalidCommand() {
        return invalidCommand;
    }
}
