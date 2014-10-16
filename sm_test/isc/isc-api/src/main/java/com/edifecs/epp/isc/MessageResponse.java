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

import com.edifecs.epp.isc.exception.MessageException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * MessageResponse is a collection of responses from servers. Valid responses
 * are set to the responseMap, while any exceptions are stored within the
 * exceptionMap. If there are any servers that did not respond, the
 * completeResponse flag is set to false.
 * 
 * @author willclem
 * @author josefern
 */
public class MessageResponse implements Serializable {
    private static final long serialVersionUID = 8_12_14;

    /**
     * Holds valid responses to message requests.
     */
    private Map<Address, Serializable> responseMap;

    /**
     * Holds all exceptions that were thrown by the receivers.
     */
    private Map<Address, MessageException> exceptionMap;

    /**
     * True if the request sent had all receivers respond with or without
     * errors. This value is true by default.
     */
    private boolean completeResponse = true;

    /**
     * Adds a response to the list of responses.
     * 
     * @param sender
     *            Address of the Sender
     * @param response
     *            Serializable object returned by the server
     */
    public final void addResponse(final Address sender, final Serializable response) {
        if (sender == null) {
            return;
        } else if (response instanceof MessageResponse) {
            addResponse(sender, (MessageResponse) response);
        } else {
            getResponseMap().put(sender, response);
        }
    }

    /**
     * Adds a response to the list of responses.
     * 
     * @param sender
     *            Address of the Sender
     * @param response
     *            MessageResponse object to merge with the current one
     */
    public final void addResponse(final Address sender, final MessageResponse response) {
        merge(response);
    }

    /**
     * Returns a map of responses.
     * 
     * @return Map of Addresses and Serializable objects.
     */
    public final Map<Address, Serializable> getResponseMap() {
        if (responseMap == null) {
            responseMap = new HashMap<Address, Serializable>();
        }
        return responseMap;
    }

    /**
     * Adds an exception to the map of Exceptions thrown by servers.
     * 
     * @param sender
     *            Address of the Sender
     * @param exception
     *            Throwable exception thrown by the server
     */
    public final void addException(final Address sender, final MessageException exception) {
        if (sender != null) {
            getExceptionMap().put(sender, exception);
        }
    }

    /**
     * Returns a map of exceptions thrown by the servers.
     * 
     * @return Map of Exceptions
     */
    public final Map<Address, MessageException> getExceptionMap() {
        if (exceptionMap == null) {
            exceptionMap = new HashMap<Address, MessageException>();
        }
        return exceptionMap;
    }

    /**
     * Returns true if all servers responded to the sent message.
     * 
     * @return isCompleteResponse
     */
    public final boolean isCompleteResponse() {
        return completeResponse;
    }

    /**
     * Sets the completeResponse flag.
     * 
     * @param completeResponse
     *            boolean value for if there is a complete response
     */
    public final void setCompleteResponse(final boolean completeResponse) {
        this.completeResponse = completeResponse;
    }

    /**
     * Returns true if the response contains an error.
     * 
     * @return True if the response contains an error.
     */
    public final boolean isErrorResponse() {
        return !getExceptionMap().isEmpty();
    }

    /**
     * Merges two MessageResponses together.
     * 
     * @param messageResponse
     *            MessageResponse to merge with the current one.
     */
    public final void merge(final MessageResponse messageResponse) {
        if (messageResponse == null) {
            return;
        }

        getResponseMap().putAll(messageResponse.getResponseMap());
        getExceptionMap().putAll(messageResponse.getExceptionMap());
        completeResponse = completeResponse && messageResponse.isCompleteResponse();
    }

    /**
     * Outputs the contents of this MessageResponse in a pseudo-JSON format.
     * 
     * @author i-adamnels
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{Responses: [");
        final Iterator<Map.Entry<Address, Serializable>> iter1 =
                getResponseMap().entrySet().iterator();
        while (iter1.hasNext()) {
            final Map.Entry<Address, Serializable> e = iter1.next();
            sb.append(e.getKey());
            sb.append(": <");
            sb.append(e.getValue());
            sb.append(">");
            if (iter1.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("], Errors: [");
        final Iterator<Map.Entry<Address, MessageException>> iter2 =
                getExceptionMap().entrySet().iterator();
        while (iter2.hasNext()) {
            final Map.Entry<Address, MessageException> e = iter2.next();
            sb.append(e.getKey());
            sb.append(": <");
            sb.append(e.getValue().getClass().getSimpleName());
            sb.append(": ");
            sb.append(e.getValue().getMessage());
            sb.append(">");
            if (iter2.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("]}");
        return sb.toString();
    }

    public boolean isEmpty() {
        return (getExceptionMap().size() == 0) && (getResponseMap().size() == 0);
    }
}
