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
package com.edifecs.epp.isc.core;

import com.edifecs.epp.isc.Address;

import java.io.Serializable;

// TODO: This needs to be moved out of the core Message API as it is not a core part of the message API.
public class LogFile implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final Address address;
    private final String path;
    private final Long lastEntry;
    private final Long sizeInKb;

    public LogFile(String name, Address address, String path, Long lastEntry, Long sizeInKb) {
        this.name = name;
        this.address = address;
        this.path = path;
        this.lastEntry = lastEntry;
        this.sizeInKb = sizeInKb;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public String getPath() {
        return path;
    }

    public Long getLastEntry() {
        return lastEntry;
    }

    public Long getSizeInKb() {
        return sizeInKb;
    }

}
