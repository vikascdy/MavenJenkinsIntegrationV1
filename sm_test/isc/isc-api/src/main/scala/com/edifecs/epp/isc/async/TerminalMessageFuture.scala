package com.edifecs.epp.isc.async

import com.edifecs.epp.isc.exception.MessageException

import scala.concurrent.Future

/**
 * A special case of {@link MessageFuture} where the return value is unknown or
 * unavailable. Although no return value can be obtained, new exception
 * handlers and/or no-argument (nullary) callbacks may still be added to the
 * callback chain.
 *
 * @author c-adamnels
 */
abstract class TerminalMessageFuture {

  /**
   * Registers `callback` as a listener which will be called when this future
   * is completed, but which does not need to know this future's result.
   *
   * If `callback` throws an exception, all chained callbacks up to the next
   * `orCatch`/`orCatchThen` callback will be skipped.
   *
   * @param callback The callback that should be executed when this future is
   *                 completed.
   * @param disambiguator Dummy argument used to prevent type inference
   *                      ambiguities in Java 8.
   * @return A {@link TerminalMessageFuture}, which does not provide any return
   *         value but can still have exception handlers or nullary callbacks
   *         attached.
   * @see MessageFuture#thenDo(Callback)
   */
  def andThenDo(callback: => Any)(implicit disambiguator: ScalaOnly): TerminalMessageFuture

  /**
   * Registers `callback` as a listener which will be called when this future
   * is completed, but which does not need to know this future's result.
   *
   * If `callback` throws an exception, all chained callbacks up to the next
   * `orCatch`/`orCatchThen` callback will be skipped.
   *
   * @param callback The callback whose {@link NullaryCallback#call()} method
   *                 should be called when this future is completed.
   * @return A {@link TerminalMessageFuture}, which does not provide any return
   *         value but can still have exception handlers or nullary callbacks
   *         attached.
   * @see MessageFuture#thenDo(Callback)
   */
  final def andThenDo(callback: NullaryCallback): TerminalMessageFuture =
    andThenDo(callback.call())

  /**
   * Registers `callback` as a listener which will be called when this future
   * is completed, but which does not need to know this future's result. The
   * callback is expected to return another `MessageFuture`, usually by sending
   * another asynchronous message. Multiple calls to this method can be
   * chained, allowing sequential asynchronous messages to be sent without
   * blocking.
   *
   * If `callback` throws an exception, all chained callbacks up to the next
   * `orCatch`/`orCatchThen` callback will be skipped.
   *
   * @param callback The callback that should be executed when this future's
   *                 result is ready.
   * @param disambiguator Dummy argument used to prevent type inference
   *                      ambiguities in Java 8.
   * @return A `MessageFuture` which will be completed when both `callback`'s
   *         execution and its returned `MessageFuture` are completed.
   * @see MessageFuture#then(ChainCallback)
   */
  def andThen[U](
    callback: => MessageFuture[U])(
    implicit disambiguator: ScalaOnly
  ): MessageFuture[U]

  /**
   * Registers `callback` as a listener which will be called when this future
   * is completed, but which does not need to know this future's result. The
   * callback is expected to return another `MessageFuture`, usually by sending
   * another asynchronous message. Multiple calls to this method can be
   * chained, allowing sequential asynchronous messages to be sent without
   * blocking.
   *
   * If `callback` throws an exception, all chained callbacks up to the next
   * `orCatch`/`orCatchThen` callback will be skipped.
   *
   * @param callback The callback whose {@link NullaryChainCallback#call()}
   *                 method should be called when this future's result is
   *                 ready.
   * @return A `MessageFuture` which will be completed when both `callback`'s
   *         execution and its returned `MessageFuture` are completed.
   * @see MessageFuture#then(ChainCallback)
   */
  final def andThen[U](callback: NullaryChainCallback[U]): MessageFuture[U] =
    andThen(callback.call())

  /**
   * Registers `callback` as an exception handler which will be called if any
   * callbacks earlier in the callback chain throw an exception.
   *
   * If the thrown exception was a {@link RuntimeException}, it will be wrapped
   * in a {@link MessageException} before being passed to `callback`.
   *
   * `callback` may also throw an exception, which will trigger another
   * exception handler further down the chain, if one exists.
   *
   * @param callback The callback that should be executed if previous callback
   *                 throws an exception.
   * @param disambiguator Dummy argument used to prevent type inference
   *                      ambiguities in Java 8.
   * @return A {@link TerminalMessageFuture}, which does not provide any return
   *         value but can still have exception handlers or nullary callbacks
   *         attached.
   * @see MessageFuture#orCatchThen(ChainCallback)
   */
  def orCatch(
    callback: MessageException => Any)(
    implicit disambiguator: ScalaOnly
  ): TerminalMessageFuture

  /**
   * Registers `callback` as an exception handler which will be called if any
   * callbacks earlier in the callback chain throw an exception.
   *
   * If the thrown exception was a {@link RuntimeException}, it will be wrapped
   * in a {@link MessageException} before being passed to `callback`.
   *
   * `callback` may also throw an exception, which will trigger another
   * exception handler further down the chain, if one exists.
   *
   * @param callback The callback whose {@link Callback#call(Object)} method
   *                 should be called if a previous callback throws an
   *                 exception.
   * @return A {@link TerminalMessageFuture}, which does not provide any return
   *         value but can still have exception handlers or nullary callbacks
   *         attached.
   * @see MessageFuture#orCatchThen(ChainCallback)
   */
  final def orCatch(callback: Callback[MessageException]): TerminalMessageFuture =
    orCatch(callback.call(_))

  /** Returns a Scala {@link Future} equivalent to this future. */
  def asScalaFuture: Future[_]
}