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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract class for all Messages. A Message is the actual object that is
 * transfered between services.
 * 
 * @author willclem
 * @author josefern
 */
public abstract class AbstractMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private Address sender;
    private Collection<Address> receivers;

    public AbstractMessage() {

    }

    public AbstractMessage(Address sender, Address receiver) {
        this.sender = sender;
        this.receivers = new ArrayList<Address>();
        this.receivers.add(receiver);
    }

    public AbstractMessage(Address sender, Collection<Address> receivers) {
        this.sender = sender;
        this.receivers = receivers;
    }

    /**
     * Returns the sender Address for this message.
     * 
     * @return Address
     */
    public final Address getSender() {
        return sender;
    }

    /**
     * Returns all the receiver Addresses for this message.
     * 
     * @return List<Actor>
     */
    public final Collection<Address> getReceivers() {
        if (receivers == null) {
            receivers = new ArrayList<Address>();
        }
        return receivers;
    }

    /**
     * Change the sender of the message.
     * 
     * @param newAddress
     *            Address to change to
     */
    public final void setSender(final Address newAddress) {
        this.sender = newAddress;
    }

    /**
     * Adds the list of receivers to the existing list of receivers.
     * 
     * @param receivers
     *            List of Addresses
     */
    public final void addReceivers(final Collection<Address> receivers) {
        for (Address address : receivers) {
            addReceiver(address);
        }
    }

    /**
     * Adds the receiver to the existing list of receivers.
     * 
     * @param receiver
     *            Address
     */
    public final void addReceiver(final Address receiver) {
        if (receiver != null) {
            getReceivers().add(receiver.clone());
        }
    }

    /**
     * Removes the receiver from the list of receivers if it exists.
     * 
     * @param newReceiver
     *            Address
     */
    public final void removeReceiver(final Address newReceiver) {
        getReceivers().remove(newReceiver);
    }

    /**
     * Removes all receivers from the list of receivers.
     */
    public final void clearReceivers() {
        getReceivers().clear();
    }

}
