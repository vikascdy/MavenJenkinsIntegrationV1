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
 * {@link com.edifecs.epp.isc.command.AbstractCommandHandler}) as a
 * synchronous command method. The annotated method will be automatically
 * registered as a valid command when the command handler is registered by a
 * service.
 *
 * Any method annotated with `@SyncCommand` **must** have all of its arguments
 * annotated with one of `@`{@link Arg}, `@`{@link StreamArg}, or `@`{@link
 * Sender}; this is the only way its arguments' names can be known through Java
 * reflection.
 *
 * `@`{@link AsyncCommand} should be preferred over `@SyncCommand` in almost
 * all cases. `@SyncCommand` only exists for backward compatibility with 
 * synchronous command handler methods written using the old (now deprecated)
 * `@Command` annotation.
 * 
 * @author c-adamnels
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SyncCommand {

  /** 
   * The name of this command. If unspecified, defaults to the name of the
   * annotated method.
   */
  String name() default "";

  /** A short description of this command. Optional. */
  String description() default "No description available.";

  /** 
   * If true, this command is a "root command" (its name is the empty string).
   * This overrides the value of {@link #name()}. If the handler containing
   * this command has a namespace, then the name of the root command is simply
   * the namespace, without a trailing dot.
   */
  boolean root() default false;
}

