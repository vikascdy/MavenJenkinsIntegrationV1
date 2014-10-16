package com.edifecs.epp.isc.async;

import com.edifecs.epp.isc.exception.MessageException;

/**
 * A callback which takes two arguments of types `I1` and `I2` and returns a
 * {@link MessageFuture} with return type `O`. This is a Java 8 functional
 * interface.
 *
 * @author c-adamnels
 */
public interface ChainCallback2<I1, I2, O> {
    /**
     * This method contains the callback's functionality.
     *
     * @param arg1 The callback's first argument.
     * @param arg2 The callback's second argument.
     * @throws com.edifecs.epp.isc.exception.MessageException Throwing this (or a {@link RuntimeException})
     *     will break a callback chain.
     */
    MessageFuture<O> call(I1 arg1, I2 arg2) throws MessageException;
}
