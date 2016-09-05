/*
 * Neurpheus - MySpell Reader
 *
 * Copyright (C) 2006 Jakub Strychowski
 *
 *  This library is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as published by the Free
 *  Software Foundation; either version 2.1 of the License, or (at your option)
 *  any later version.
 *
 *  This library is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 *  for more details.
 */
package org.neurpheus.nlp.myspell;

/**
 *  This exception is thrown when a MySpell file contains an error.
 *
 * @author Jakub Strychowski
 */
public class MySpellSyntaxException extends Exception {
    
    /**
     * Constructs a new exception with <code>null</code> as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public MySpellSyntaxException() {
        super();
    }
    
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param   message     The detail message. The detail message is saved for
     *                      later retrieval by the {@link #getMessage()} method.
     */
    public MySpellSyntaxException(final String message) {
        super(message);
    }
    
    /**
     * Constructs a new exception with the specified cause and a detail
     * message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * This constructor is useful for exceptions that are little more than
     * wrappers for other throwables (for example, {@link
     * java.security.PrivilegedActionException}).
     *
     * @param  cause    The cause (which is saved for later retrieval by the {@link #getCause()} method).  
     *                  (A <tt>null</tt> value is permitted, and indicates that the cause 
     *                  is nonexistent or unknown.)
     */
    public MySpellSyntaxException(final Throwable cause) {
        super(cause);
    }
    
    /**
     * Constructs a new exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * <code>cause</code> is <i>not</i> automatically incorporated in
     * this exception's detail message.
     *
     * @param  message      The detail message (which is saved for later retrieval
     *                      by the {@link #getMessage()} method).
     * @param  cause        The cause (which is saved for later retrieval by the
     *                      {@link #getCause()} method).  (A <tt>null</tt> value is
     *                      permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public MySpellSyntaxException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
}
