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
package com.edifecs.servicemanager.launcher.service.exception;

import com.edifecs.agent.launcher.classloader.SMClassLoader;


/**
 * Thrown when an {@link CommandMessage} is rejected by an
 * {@link com.edifecs.messaging.message.command.AbstractCommandHandler} because
 * it either specifies a command that is not registered or has the wrong number
 * and/or type of arguments for the command that it specifies.
 * 
 * @author i-adamnels
 */
public class IncorrectSystemClassLoaderException extends Exception {
    private static final long serialVersionUID = 1L;

    public IncorrectSystemClassLoaderException() {
        super("Agent and node must be started using a custom classloader. Add the following VM Argument to set the classloader: -Djava.system.class.loader=" + SMClassLoader.class.getName());
    }
}
