// -----------------------------------------------------------------------------
// Copyright (c) Edifecs Inc. All Rights Reserved.
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

package com.edifecs.epp.security;

import java.io.Serializable;

/**
 * Unique identifier com.edifecs.epp.security.token for a Service Manager user session. An instance is
 * sent with every message, and is used to identify the sender.
 * 
 * @author i-adamnels
 * @author willclem
 */
public class SessionId implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Serializable sessionId;

    public SessionId(Serializable sessionId) {
        if(sessionId instanceof SessionId) {
        	this.sessionId = ((SessionId) sessionId).getSessionId();
        } else {
        	this.sessionId = sessionId;
        }
    }

    /**
     * Returns the unique ID of this session (usually a {@link String}).
     */
    public Serializable getSessionId() {
        return sessionId;
    }
}
