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
package com.edifecs.epp.isc.api;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.shiro.subject.PrincipalCollection;

/**
 * A wrapper around a {@link PrincipalCollection}, used to make the session ID
 * of the owner Subject accessible to a Realm's security methods. This is a
 * dirty hack, but necessary due to the way the security APIs work.
 * 
 * @author i-adamnels
 */
public class SessionIdPrincipalCollection implements PrincipalCollection {
    private static final long serialVersionUID = 1L;

    private final Serializable sessionId;
    private final PrincipalCollection pc;

    public SessionIdPrincipalCollection(PrincipalCollection wrapped,
            Serializable sessionId) {
        pc = wrapped;
        this.sessionId = sessionId;
    }

    @Override
    public Iterator<?> iterator() {
        return pc.iterator();
    }

    @Override
    public Object getPrimaryPrincipal() {
        return pc.getPrimaryPrincipal();
    }

    @Override
    public <T> T oneByType(Class<T> type) {
        return pc.oneByType(type);
    }

    @Override
    public <T> Collection<T> byType(Class<T> type) {
        return pc.byType(type);
    }

    @Override
    public List<?> asList() {
        return pc.asList();
    }

    @Override
    public Set<?> asSet() {
        return pc.asSet();
    }

    @Override
    public Collection<?> fromRealm(String realmName) {
        return pc.fromRealm(realmName);
    }

    @Override
    public Set<String> getRealmNames() {
        return pc.getRealmNames();
    }

    @Override
    public boolean isEmpty() {
        return pc.isEmpty();
    }

    /**
     * Returns the session ID attached to this PrincipalCollection.
     */
    public final Serializable getSessionId() {
        return sessionId;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SessionIdPrincipalCollection) {
            return pc.equals(((SessionIdPrincipalCollection) other).pc);
        }
        return pc.equals(other);
    }

    @Override
    public int hashCode() {
        return pc.hashCode();
    }

    @Override
    public String toString() {
        return pc.toString();
    }
}
