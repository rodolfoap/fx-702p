/*
 * @(#)ParseException.java	1.17 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998 - All Rights Reserved
 *
 *   The original version of this source code and documentation is copyrighted
 * and owned by Taligent, Inc., a wholly-owned subsidiary of IBM. These
 * materials are provided under terms of a License Agreement between Taligent
 * and Sun. This technology is protected by multiple US and International
 * patents. This notice and attribution to Taligent may not be removed.
 *   Taligent is a registered trademark of Taligent, Inc.
 *
 */

package com.fx702p.interpreters.formatters;

/**
 * Signals that an error has been reached unexpectedly
 * while parsing.
 * @see java.lang.Exception
 * @see java.text.Format
 * @see java.text.FieldPosition
 * @version     1.17, 11/17/05
 * @author      Mark Davis
 */
public
class ParseException extends Exception {

    /**
     * Constructs a ParseException with the specified detail message and
     * offset.
     * A detail message is a String that describes this particular exception.
     * @param s the detail message
     * @param errorOffset the position where the error is found while parsing.
     */
    public ParseException(String s, int errorOffset) {
        super(s);
        this.errorOffset = errorOffset;
    }

    /**
     * Returns the position where the error was found.
     */
    public int getErrorOffset () {
        return errorOffset;
    }

    //============ privates ============
    /**
     * The zero-based character offset into the string being parsed at which
     * the error was found during parsing.
     * @serial
     */
    private int errorOffset;
}
