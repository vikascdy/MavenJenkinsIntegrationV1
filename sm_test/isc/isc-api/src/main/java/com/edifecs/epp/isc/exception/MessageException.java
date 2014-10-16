// -----------------------------------------------------------------------------
//  Copyright (c) Edifecs Inc. All Rights Reserved.
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
package com.edifecs.epp.isc.exception;

import com.edifecs.epp.isc.Isc$;
import com.edifecs.epp.isc.command.CommandStackFrame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * Exception class that is used when there is an issue sending a message through
 * the message API. Constructor will change the original instance of Throwable
 * (if any) into an instance of MessageException before setting it as the cause.
 * This is to prevent ClassNotFoundExceptions when the stack trace includes
 * exception classes that are not visible to the receiver. The original class
 * name can be retrieved using `getOriginalExceptionClassName()`.
 *
 * As of the Akka update, `MessageException`s also contain a command stack
 * trace, which may be more useful than a JVM stack trace for debugging
 * purposes.
 * 
 * @author willclem
 * @author josefern
 * @author c-adamnels
 */
public class MessageException extends RuntimeException {
    private static final long serialVersionUID = 8_12_14;

    private String message;
    private final CommandStackFrame[] commandStack;
    private String originalExceptionClassName;
    private byte[] originalException;

    /**
     * Serializes a `Throwable` into a `MessageException`, and attaches a
     * command stack trace.
     */
    public MessageException(final Throwable t, CommandStackFrame[] commandStack) {
        if (commandStack == null) commandStack = new CommandStackFrame[0];
        this.commandStack = commandStack;
        if (t == null) {
            message = "null";
            fillInStackTrace();
        } else if (t instanceof MessageException) {
            message = t.getMessage();
            fillInStackTrace();
            initCause(t);
        } else {
            // replace throwable with an instance of MessageException
            try {
                originalException = serializeException(t);
            } catch (IOException e) {
                // If the object cannot be serialized, set to null
                originalException = null;
            }

            // Set the message
            message = t.getMessage();
            originalExceptionClassName = t.getClass().getCanonicalName();

            setStackTrace(t.getStackTrace());

            if (t.getCause() != null) {
                initCause(new MessageException(t.getCause(), new CommandStackFrame[0]));
            }
        }
    }

    public void throwException() throws Exception {
        throw getOriginalException();
    }

    public Exception getOriginalException() {
        if (originalException == null) {
            return this;
        }

        try {
            return deserializeException(originalException);
        } catch (IOException e) {
            return this;
        } catch (ClassNotFoundException e) {
            // If the receiver does not understand the thrown exception, return
            // the MessageException
            return this;
        }
    }

    /**
     * @param throwable
     *            Throwable
     */
    public MessageException(final Throwable throwable) {
        this(throwable, Isc$.MODULE$.stack());
    }

    /**
     * @param message
     *            Message
     */
    public MessageException(final String message) {
        this(message, Isc$.MODULE$.stack());
    }

    /**
     * @param message
     *            Message
     */
    public MessageException(final String message, CommandStackFrame[] commandStack) {
        super(message);
        this.message = message;
        if (commandStack == null) commandStack = new CommandStackFrame[0];
        this.commandStack = commandStack;
    }

    /**
     * @param message
     *            Message
     * @param e
     *            Throwable Exception
     */
    public MessageException(final String message, final Throwable e) {
        this(message, e, Isc$.MODULE$.stack());
    }

    /**
     * @param message
     *            Message
     * @param e
     *            Throwable Exception
     */
    public MessageException(final String message, final Throwable e,
            CommandStackFrame[] commandStack) {
        super(message, new MessageException(e, new CommandStackFrame[0]));
        this.message = message;
        if (commandStack == null) commandStack = new CommandStackFrame[0];
        this.commandStack = commandStack;
    }

    @Override
    public String getMessage() {
        String m = originalExceptionClassName == null ? message :
                "<" + originalExceptionClassName + "> " + message;
        for (CommandStackFrame frame : commandStack)
            m += "\n\tfrom command " + frame;
        return m;
    }

    public String getOriginalMessage() {
        return message;
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }

    /**
     * @return The canonical class name of an original exception, if one exists.
     *         Returns null if MessageException is the original exception class
     *         that was thrown.
     */
    public String getOriginalExceptionClassName() {
        return originalExceptionClassName;
    }

    public CommandStackFrame[] getCommandStack() {return commandStack;}

    private byte[] serializeException(Throwable t) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(t);
            return bos.toByteArray();
        } finally {
            if (out != null) {
                out.close();
            }
            bos.close();
        }
    }

    private Exception deserializeException(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            return (Exception) in.readObject();
        } finally {
            if (in != null) {
                in.close();
            }
            bis.close();
        }
    }
}
