//##############################################################################
// FILE: Ltl2baException.java
// CONTENTS: public class Ltl2baException
//##############################################################################

/* Copyright (C) 2002-2005 Contributors.
 * 
 * This file belongs to the Java interface for LTL2BA (JLtl2ba).
 * 
 * JLtl2ba is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 * 
 * JLtl2ba is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JLtl2ba; see the file COPYING.  If not, write to
 * the Free Software Foundation, 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.  
 */

package ss.pku.utils.jltl2ba;

/** Exception thrown when ltl2ba has a non-zero return value.

    @author Michael Baldamus
*/

public class Ltl2baException extends RuntimeException
{
    
    Ltl2baException(int returnValue)
    {
        super(new Integer(returnValue).toString());
    }
    
    Ltl2baException(int returnValue, String stdErr)
    {
        super("" + returnValue + ": " + stdErr);
    }
    
    Ltl2baException(String message)
    {
        super(message);
    }
    
    Ltl2baException(Throwable cause)
    {
        super(cause);
    }

}
