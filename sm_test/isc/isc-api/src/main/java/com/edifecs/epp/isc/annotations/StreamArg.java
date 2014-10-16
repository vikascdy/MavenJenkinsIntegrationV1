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
 * Specifies the name and other metadata for a stream argument to a method of a
 * command handler (a subclass of `AbstractCommandHandler`). Stream
 * arguments are always required (cannot be {@code null}), and must be of type
 * {@link java.io.InputStream}. A command method may have only one stream argument.
 * 
 * @author c-adamnels
 * @see Arg
 * @deprecated Command messages now support multiple stream arguments, which
 *     can be ordinary `@Arg` arguments of type {@link
 *     com.edifecs.epp.isc.stream.MessageStream}. Do not use `@StreamArg` or
 *     `InputStream` arguments in new code.
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
public @interface StreamArg {
    String name();

    String description() default "Stream argument. No description available.";
}
