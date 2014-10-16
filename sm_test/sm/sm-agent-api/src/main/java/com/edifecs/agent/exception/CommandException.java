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
package com.edifecs.agent.exception;

/**
 * Exception Is used with Command message errors. If a command fails to execute
 * this is what is thrown.
 * 
 * @author willclem
 */
public class CommandException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * @param e Throwable Exception
     */
    public CommandException(final Throwable e) {
        super(e);
    }

    /**
     * @param message Message
     */
    public CommandException(final String message) {
        super(message);
    }

    /**
     * @param message Message
     * @param e Throwable Exception
     */
    public CommandException(final String message, final Throwable e) {
        super(message, e);
    }
}
