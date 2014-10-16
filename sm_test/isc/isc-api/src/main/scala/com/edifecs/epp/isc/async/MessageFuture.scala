package com.edifecs.epp.isc.async

import com.edifecs.epp.isc.Isc
import com.edifecs.epp.isc.exception.MessageException

import scala.concurrent.Future

object MessageFuture {
  /**
   * Creates a `MessageFuture` wrapper around an object. Used in cases where a
   * `MessageFuture` is expected, but only a synchronous result is available.
   *
   * @param contents The object to wrap.
   */
  def of[T](contents: T): MessageFuture[T] = {
    return ScalaMessageFutureWrapper.wrapObject(contents)
  }

  def apply[T](contents: => T): MessageFuture[T] = {
    new ScalaMessageFutureWrapper(Future(contents)(Isc.get.getExecutionContext))
  }
}

/**
 * A promise-like object returned as the result of sending an asynchronous
 * message. Represents a future value of type `T` that may or may not be
 * available yet. The various forms of {@link #then(ChainCallback)} and {@link
 * #thenDo(Callback)} can be used to register a callback to handle this
 * future's value when it becomes available.
 *
 * Multiple calls to `then` can be chained, providing an easy way to send and
 * handle multiple asynchronous messages in sequence. With Java 8 lambdas, this
 * becomes a clean, readable way to write nonblocking operations:
 *
 *     sendAsyncMessage("hello, world!").then(x ->
 *       sendAsyncMessage(x)
 *     ).then(y ->
 *       sendAsyncMessage(y)
 *     ).thenDo(z ->
 *       System.out.println(z)
 *     );
 *
 * Scala users may be more comfortable with Scala's {@link Future} API;
 * `MessageFuture`s can be converted to Scala `Future`s using {@link
 * #asScalaFuture()}, or implicitly by importing
 * `com.edifecs.epp.isc.async.Implicits._`.
 *
 * @author c-adamnels
 */
abstract class MessageFuture[T] extends TerminalMessageFuture {

  /**
   * Registers `callback` as a listener which will be called when this future's
   * result is available.
   *
   * If `callback` throws an exception, all chained callbacks up to the next
   * `orCatch`/`orCatchThen` callback will be skipped.
   *
   * @param callback The callback that should be executed when this future's
   *                 result is ready.
   * @param disambiguator Dummy argument used to prevent type inference
   *                      ambiguities in Java 8.
   * @return A {@link TerminalMessageFuture}, which does not provide any return
   *         value but can still have exception handlers or nullary callbacks
   *         attached.
   */
  final def thenDo(callback: T => Any)(implicit disambiguator: ScalaOnly): TerminalMessageFuture =
    then(t => MessageFuture(callback(t)))

  /**
   * Registers `callback` as a listener which will be called when this future's
   * result is available.
   *
   * If `callback` throws an exception, all chained callbacks up to the next
   * `orCatch`/`orCatchThen` callback will be skipped.
   *
   * @param callback The callback whose {@link Callback#call(Object)} method
   *                 should be called when this future's result is ready.
   * @return A {@link TerminalMessageFuture}, which does not provide any return
   *         value but can still have exception handlers or nullary callbacks
   *         attached.
   */
  final def thenDo(callback: Callback[T]): TerminalMessageFuture = thenDo(callback.call(_))

  /**
   * Registers `callback` as a listener which will be called when this future's
   * result is available. The callback is expected to return another
   * `MessageFuture`, usually by sending another asynchronous message. Multiple
   * calls to this method can be chained, allowing sequential asynchronous
   * messages to be sent and handled without blocking.
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
   */
  def then[U](callback: T => MessageFuture[U])(implicit disambiguator: ScalaOnly): MessageFuture[U]

  /**
   * Registers `callback` as a listener which will be called when this future's
   * result is available. The callback is expected to return another
   * `MessageFuture`, usually by sending another asynchronous message. Multiple
   * calls to this method can be chained, allowing sequential asynchronous
   * messages to be sent and handled without blocking.
   *
   * If `callback` throws an exception, all chained callbacks up to the next
   * `orCatch`/`orCatchThen` callback will be skipped.
   *
   * @param callback The callback whose {@link ChainCallback#call(Object)}
   *                 method should be called when this future's result is ready.
   * @return A `MessageFuture` which will be completed when both `callback`'s
   *         execution and its returned `MessageFuture` are completed.
   */
  final def then[U](callback: ChainCallback[T, U]): MessageFuture[U] =
    then(callback.call(_))

  /**
   * Registers `callback` as an exception handler which will be called if any
   * callbacks earlier in the callback chain throw an exception.  The callback
   * is expected to return another `MessageFuture`, usually by sending another
   * asynchronous message. It serves as an alternate return value (in the event
   * of an exception) for this callback chain, and, due to the limitations of
   * Java's type system, must return a value of the same type as this future's
   * return type (`T`).
   *
   * If the thrown exception was a {@link RuntimeException}, it will be wrapped
   * in a {@link MessageException} before being passed to `callback`.
   *
   * `callback` may also throw an exception, which will trigger another
   * exception handler further down the chain, if one exists.
   *
   * @param callback The callback that should be executed if a previous
   *                 callback throws an exception.
   * @param disambiguator Dummy argument used to prevent type inference
   *                      ambiguities in Java 8.
   * @return A `MessageFuture` which will be completed when both `callback`'s
   *         execution and its returned `MessageFuture` are completed.
   */
  def orCatchThen(
    callback: MessageException => MessageFuture[T])(
    implicit disambiguator: ScalaOnly
  ): MessageFuture[T]

  /**
   * Registers `callback` as an exception handler which will be called if any
   * callbacks earlier in the callback chain throw an exception.  The callback
   * is expected to return another `MessageFuture`, usually by sending another
   * asynchronous message. It serves as an alternate return value (in the event
   * of an exception) for this callback chain, and, due to the limitations of
   * Java's type system, must return a value of the same type as this future's
   * return type (`T`).
   *
   * If the thrown exception was a {@link RuntimeException}, it will be wrapped
   * in a {@link MessageException} before being passed to `callback`.
   *
   * `callback` may also throw an exception, which will trigger another
   * exception handler further down the chain, if one exists.
   *
   * @param callback The callback whose {@link ChainCallback#call(Object)}
   *                 method should be called if a previous callback throws an
   *                 exception.
   * @return A `MessageFuture` which will be completed when both `callback`'s
   *         execution and its returned `MessageFuture` are completed.
   */
  final def orCatchThen(callback: ChainCallback[MessageException, T]): MessageFuture[T] =
    orCatchThen(callback.call(_))

  final override def andThenDo(callback: => Any)(implicit disambiguator: ScalaOnly) =
    thenDo(_ => callback)

  final override def andThen[U](callback: => MessageFuture[U])(implicit disambiguator: ScalaOnly) =
    then(_ => callback)

  final override def orCatch(
    callback: MessageException => Any)(
    implicit disambiguator: ScalaOnly
  ) = orCatchThen(ex => {
    callback(ex)
    MessageFuture(null).asInstanceOf[MessageFuture[T]]
  })

  /**
   * Casts this future's result to type `cls`. Will throw a {@link
   * ClassCastException} if the cast cannot be performed.
   */
  final def as[U](cls: Class[U]): MessageFuture[U] =
    then(new ChainCallback[T, U] {
      def call(arg: T): MessageFuture[U] = {
        if (arg == null) MessageFuture(null).asInstanceOf[MessageFuture[U]]
        else if (cls.isInstance(arg)) MessageFuture(arg.asInstanceOf[U])
        else throw new ClassCastException(
          s"Cannot cast ${arg.getClass.getName} to ${cls.getName}.")
      }
    })

  def asScalaFuture: Future[T]
}
