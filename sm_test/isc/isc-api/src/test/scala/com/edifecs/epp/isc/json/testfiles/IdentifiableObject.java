package com.edifecs.epp.isc.json.testfiles;

import java.io.Serializable;

/**
 * 
 * @author hongliii
 * 
 * @param <I>
 *            the resource id type.
 */
public abstract class IdentifiableObject<I> implements Serializable, Identifiable<I> {
    private static final long serialVersionUID = 4558056375014540976L;

    private I id;

    /* (non-Javadoc)
     * @see com.edifecs.bpm.domain.Identifiab#getId()
     */
    @Override
    public I getId() {
        return id;
    }

    public void setId(I id) {
        this.id = id;
    }
}
