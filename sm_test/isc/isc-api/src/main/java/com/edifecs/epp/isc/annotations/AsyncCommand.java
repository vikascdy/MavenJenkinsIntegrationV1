// -----------------------------------------------------------------------------
// Copyright (c) Edifecs Inc. All Rights Reserved.
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

package com.edifecs.epp.isc.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method of a command handler (a subclass of
 * {@link com.edifecs.epp.isc.command.AbstractCommandHandler}) as an
 * asynchronous command method. The annotated method will be automatically
 * registered as a valid command when the command handler is registered by a
 * service.
 *
 * Async command methods **must** return a {@link
 * com.edifecs.epp.isc.async.MessageFuture}, which is a wrapper for an
 * asynchronous computation that may not yet have been performed. When a
 * command message for this command is received, the `MessageFuture`'s result
 * or exception will be returned to the message sender as the command's result
 * once the `MessageFuture`'s computation is completed.
 *
 * Any method annotated with `@AsyncCommand` **must** have all of its arguments
 * annotated with one of `@`{@link Arg}, `@`{@link StreamArg}, or `@`{@link
 * Sender}; this is the only way its arguments' names can be known through Java
 * reflection.
 * 
 * @author c-adamnels
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsyncCommand {

  /** 
   * The name of this command. If unspecified, defaults to the name of the
   * annotated method.
   */
  String name() default "";

  /** A short description of this command. Optional. */
  String description() default "No description available.";

  /** 
   * The time, in milliseconds, that this command should wait for an available
   * response before giving up. Defaults to 10 seconds.
   */
  long timeoutMs() default 10_000L;

  /** 
   * If true, this command is a "root command" (its name is the empty string).
   * This overrides the value of {@link #name()}. If the handler containing
   * this command has a namespace, then the name of the root command is simply
   * the namespace, without a trailing dot.
   */
  boolean root() default false;
}

