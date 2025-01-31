/**
 *  Copyright 2003-2008 Luck Consulting Pty Ltd
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.sf.ehcache.distribution.jms;

import net.sf.ehcache.CacheException;

/**
 * An Exception indicating that the JMS Message sent is invalid.
 * <p/>
 * Ehcache requires various String properties to be set on JMS Messages and supports a subset of all
 * available JMS Message types.
 * <p/>
 * See the documentation for Ehcache replication for details.
 * @author Greg Luck
 */
public class InvalidJMSMessageException extends CacheException {

    /**
     * Constructor for the InvalidJMSMessageException object.
     */
    public InvalidJMSMessageException() {
        super();
    }

    /**
     * Constructor for the InvalidJMSMessageException object.
     *
     * @param message the exception detail message
     */
    public InvalidJMSMessageException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidJMSMessageException with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * <code>cause</code> is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A <tt>null</tt> value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     */
    public InvalidJMSMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new InvalidJMSMessageException with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A <tt>null</tt> value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     */
    public InvalidJMSMessageException(Throwable cause) {
        super(cause);
    }
}
