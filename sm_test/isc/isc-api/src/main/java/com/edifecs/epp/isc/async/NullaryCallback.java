package com.edifecs.epp.isc.async;

import com.edifecs.epp.isc.exception.MessageException;

/**
 * A callback which takes no arguments and does not return a value. This is a
 * Java 8 functional interface.
 *
 * @author c-adamnels
 */
public interface NullaryCallback {
  /**
   * This method contains the callback's functionality.
   *
   * @throws MessageException Throwing this (or a {@link RuntimeException})
   *     will break a callback chain.
   */
  void call() throws MessageException;
}

