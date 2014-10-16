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
 * command method. The annotated method will be automatically registered as a
 * valid command when the command handler is registered by a service.
 *
 * Any method annotated with `@Command` **must** also have all of its arguments
 * annotated with one of `@`{@link Arg}, `@`{@link StreamArg}, or `@`{@link
 * Sender}; this is the only way its arguments' names can be known through Java
 * reflection.
 * 
 * @author c-adamnels
 *
 * @deprecated Has been replaced with `@`{@link SyncCommand} and `@`{@link
 *     AsyncCommand}. `@AsyncCommand` is now recommended, although
 *     `@SyncCommand` can be used as an exact substitute for `@Command` if a
 *     command method cannot be rewritten in an asynchronous style.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
public @interface Command {

  String name() default "";

  String description() default "No description available.";
}

