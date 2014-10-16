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
package com.edifecs.messaging;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

import com.edifecs.epp.isc.exception.MessageException;

public class TestMessageException {
    private static final String MESSAGE = "Oops";
    private Exception lastException;

    /**
     * Utility method to throw a wrapped exception chain
     * 
     * @throws MessageException
     */
    public void throwTestException() throws MessageException {
        try {
            throw new NullPointerException(MESSAGE);
        } catch (NullPointerException npe) {
            try {
                throw new Exception(npe);
            } catch (Exception e) {
                try {
                    throw new IllegalStateException(e);
                } catch (IllegalStateException ise) {
                    lastException = ise;
                    throw new MessageException(ise);
                }
            }
        }
    }

    @After
    public void tearDown() {
        lastException = null;
    }

    @Test
    public void testStackTraceIsMaintained() {
        try {
            throwTestException();
        } catch (MessageException me) {
            assertArrayEquals(lastException.getStackTrace(), me.getStackTrace());
        }
    }

    @Test
    public void testOriginalExceptionClassNameIsMaintained() {
        try {
            throwTestException();
        } catch (MessageException me) {
            assertEquals(lastException.getClass().getCanonicalName(),
                    me.getOriginalExceptionClassName());
        }
    }

    @Test
    public void testOrigionalExceptionIsMaintained() {
        try {
            throwTestException();
        } catch (MessageException me) {
            try {
                me.throwException();
            } catch (Exception e) {
                assertEquals(e.getClass().getCanonicalName(), me.getOriginalExceptionClassName());
            }
        }
    }

    @Test
    public void testOriginalExceptionClassNameIsNullWhenNoWrappingOccurs() {
        try {
            throw new MessageException(MESSAGE);
        } catch (MessageException me) {
            assertNull(me.getOriginalExceptionClassName());
        }
    }

    @Test
    public void testDoesNotWrapItself() {
        try {
            throw new MessageException(MESSAGE);
        } catch (MessageException e1) {
            try {
                throw new MessageException(e1);
            } catch (MessageException e2) {
                assertNull(e2.getOriginalExceptionClassName());
            }
        }
    }

    @Test
    public void testAllExceptionsAreWrapped() {
        try {
            throwTestException();
        } catch (MessageException me) {
            Throwable curr = me;

            while (curr != null) {
                assertTrue(curr instanceof MessageException);
                curr = curr.getCause();
            }
        }
    }

    @Test
    public void testMessageAndThrowableConstructorWrapsAllExceptions() {
        try {
            throw new NullPointerException(MESSAGE);
        } catch (NullPointerException npe) {
            try {
                throw new Exception(npe);
            } catch (Exception e) {
                try {
                    throw new IllegalStateException(e);
                } catch (IllegalStateException ise) {
                    final String testMessage = "D'oh!";
                    MessageException testException = new MessageException(testMessage, ise);
                    Throwable curr = testException;

                    while (curr != null) {
                        assertTrue(curr instanceof MessageException);
                        curr = curr.getCause();
                    }
                }
            }
        }
    }

}
