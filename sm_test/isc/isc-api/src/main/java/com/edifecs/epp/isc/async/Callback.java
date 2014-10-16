package com.edifecs.epp.isc.async;

import com.edifecs.epp.isc.exception.MessageException;

/**
 * A callback which takes one argument of type `I` and does not return a value.
 * This is a Java 8 functional interface.
 *
 * @author c-adamnels
 */
public interface Callback<I> {
  /**
   * This method contains the callback's functionality.
   *
   * @param arg The callback's argument.
   * @throws MessageException Throwing this (or a {@link RuntimeException})
   *     will break a callback chain.
   */
  void call(I arg) throws MessageException;
}

