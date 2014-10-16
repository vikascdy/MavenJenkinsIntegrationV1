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

package com.edifecs.epp.security;

import com.edifecs.epp.isc.annotations.*;
import org.apache.shiro.session.mgt.SessionContext;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * Methods to assist in the management of the users session. It is used to
 * create new session, assign properties to the user session, and helps keep the
 * session alive.
 *
 * @author willclem
 */
@CommandHandler
public interface ISessionManager {

    /**
     * Creates a new instance of a user session. This is called internally by java api's and the login methods and does
     * not need to be called directly.
     *
     * @param context
     * @return
     */
    @SyncCommand
    @NullSessionAllowed
    SessionId start(
            @Arg(name = "context", required = true) SessionContext context);


    /**
     * *Attributes are not a replacement for a caching mechanism and is not designed to store data*
     *
     * Sets an attribute registered with EIM as an attribute.
     *
     * @param attributeKey
     * @return
     */
    @SyncCommand
    Serializable getSessionAttribute(
            @Arg(name = "attributeKey", required = true) Serializable attributeKey);

    /**
     * *Attributes are not a replacement for a caching mechanism and is not designed to store data*
     *
     * Gets a list of attributes registered with EIM as an attribute.
     *
     * @return
     */
    @SyncCommand
    Collection<Object> getSessionAttributeKeys();

    /**
     * *Attributes are not a replacement for a caching mechanism and is not designed to store data*
     *
     * Sets an attribute registered with EIM as an attribute.
     *
     * @param attributeKey
     * @return
     */
    @SyncCommand
    void setSessionAttribute(
            @Arg(name = "attributeKey", required = true) Serializable attributeKey,
            @Arg(name = "value", required = true) Serializable value);

    /**
     * *Attributes are not a replacement for a caching mechanism and is not designed to store data*
     *
     * Removes an attribute registered with EIM as an attribute.
     *
     * @param attributeKey
     * @return
     */
    @SyncCommand
    Serializable removeSessionAttribute(
            @Arg(name = "attributeKey", required = true) Serializable attributeKey);

    /**
     * Gets the time that the current session was created.
     *
     * @return
     */
    @SyncCommand
    Date getSessionStartTimestamp();

    /**
     * Gets the timeout value of the current user.
     *
     * @return The time before the users session expires.
     */
    @SyncCommand
    long getSessionTimeout();

    /**
     * Internal call that can be used to keep the users session alive. It is called automatically whenever there is
     * activity for a specific user.
     */
    @SyncCommand
    void touchSession();

}
