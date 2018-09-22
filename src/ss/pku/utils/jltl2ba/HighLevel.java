//##############################################################################
// FILE: HighLevel.java
// CONTENTS: public final class HighLevel
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

import java.io.*;
import java.util.*;

/** High-level part of JLtl2ba.
    See 
    {@link #exec(Object, jltl2ba.HighLevel.Callback, jltl2ba.HighLevel.CallersSubstitute.NeverClaim) exec},
    {@link jltl2ba.HighLevel.Input Input},
    {@link jltl2ba.HighLevel.CallersSubstitute CallersSubstitute} and
    {@link jltl2ba.HighLevel.Callback Callback}
    for the details of using it.

    @author Michael Baldamus
*/

public final class HighLevel
{
 
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Executing ltl2ba
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    /** Converts the abstract input to ltl2ba input syntax, calls ltl2ba and 
        and uses the <code>Callback</code> object
        to re-build the caller's representation of the ensuing never 
        claim within the 
        {@link jltl2ba.HighLevel.CallersSubstitute.NeverClaim </code>never claim substitute<code>}.

        @param abstractInput
            The abstract input.
        @param callback
            The callback object.
        @param neverClaimSubstitute
            The never claim substitute within the caller.
        @exception IOException
            Exception thrown when the creation or deletion of the temporary file 
            holding the never claim fails.
        @exception FileNotFoundException 
            Exception thrown when the temporary file holding the never claim
            vanishes before it is to be deleted.

        @see 
            #exec(
                Object, 
                Comparator, 
                jltl2ba.HighLevel.Callback, 
                jltl2ba.HighLevel.CallersSubstitute.NeverClaim
            ) 
            </code>the other <code>exec</code> method<code>
        @see jltl2ba.HighLevel.Input Input 
        @see jltl2ba.HighLevel.CallersSubstitute CallersSubstitute
        @see jltl2ba.HighLevel.Callback Callback      
    */
    public static
    void
    exec(Object abstractInput, Callback callback, CallersSubstitute.NeverClaim neverClaimSubstitute)
    throws IOException, FileNotFoundException
    {
        exec(Input.toLtl2baInput(abstractInput), callback, neverClaimSubstitute);
    }

//------------------------------------------------------------------------------

    /** Converts the abstract input to ltl2ba input syntax using the <code>Comparator</code> object for
        atom comparison, calls ltl2ba and 
        and uses the <code>Callback</code> object
        to re-build the caller's representation of the ensuing never 
        claim within the 
        {@link jltl2ba.HighLevel.CallersSubstitute.NeverClaim </code>never claim substitute<code>}.

        @param abstractInput
            The abstract input.
        @param atomComparator
            The atom comparator.
        @param callback
            The callback object.
        @param neverClaimSubstitute
            The never claim substitute within the caller.
        @exception IOException
            Exception thrown when the creation or deletion of the temporary file 
            holding the never claim fails.
        @exception FileNotFoundException 
            Exception thrown when the temporary file holding the never claim
            vanishes before it is to be deleted.

        @see 
            #exec(Object, jltl2ba.HighLevel.Callback, jltl2ba.HighLevel.CallersSubstitute.NeverClaim) 
            </code>the other <code>exec</code> method<code>
        @see jltl2ba.HighLevel.Input Input 
        @see jltl2ba.HighLevel.CallersSubstitute CallersSubstitute
        @see jltl2ba.HighLevel.Callback Callback      
    */
    public static
    void
    exec(
        Object abstractInput, 
        Comparator atomComparator, 
        Callback callback, 
        CallersSubstitute.NeverClaim neverClaimSubstitute
    ) throws IOException, FileNotFoundException
    {
        exec(Input.toLtl2baInput(abstractInput, atomComparator), callback, neverClaimSubstitute);
    }

//==============================================================================

    /** Calls ltl2ba with the contents of the <code>String</code> object as input and 
        uses the <code>Callback</code> object
        to re-build the caller's representation of the ensuing never 
        claim within the 
        {@link jltl2ba.HighLevel.CallersSubstitute.NeverClaim </code>never claim substitute<code>}.

        @param ltl2baInput
            The input to ltl2ba.
        @param callback
            The callback object.
        @param neverClaimSubstitute
            The never claim substitute within the caller.
        @exception IOException
            Exception thrown when the creation or deletion of the temporary file 
            holding the never claim fails.
        @exception FileNotFoundException 
            Exception thrown when the temporary file holding the never claim
            vanishes before it is to be deleted.

        @see jltl2ba.HighLevel.Input Input 
        @see jltl2ba.HighLevel.CallersSubstitute CallersSubstitute
        @see jltl2ba.HighLevel.Callback Callback      
    */
    private static 
    void
    exec(String ltl2baInput, Callback callback, CallersSubstitute.NeverClaim neverClaimSubstitute)
    throws IOException, FileNotFoundException
    {
        StringBuffer 
            output 
                = LowLevel.exec(
                      false, false, false, false, false, false, false, LowLevel.formulaDirect, ltl2baInput
                  );
        Parser parser = new Parser(new StringReader(output.toString()));
        try {
            parser.neverClaim(callback, neverClaimSubstitute);
        } catch (Ltl2baException e) {
            // dummy handler, this exception should not occur
        } catch (ParseException e) {
            // dummy handler, this exception should not occur
        }
//        if (!file.delete())
//            throw new IOException("error in deleting " + file.getCanonicalPath());
    }

//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Abstract Input
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    /** Abstract input terms for the high-level part of JLtl2ba.
        Non-atomic abstract terms are given as instances of
        of the classes derived from {@link jltl2ba.HighLevel.Input} - an example of such a class is
        {@link jltl2ba.HighLevel.Input.Until}; atomic abstract input
        terms are given by arbitrary objects that are not instances of classes derived from 
        {@link jltl2ba.HighLevel.Input}. It is thus possible to pass
        strings as atoms, for instance. One can choose whether 
        atoms are compared with respect to object identity or with respect to
        some <code>Comparator</code> object. 
    */
    public static abstract class Input
    {
    
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // Construction
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

        /** Constructs an abstract input term.
	*/    
        Input()
        {
            // skip
        }
    
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // Conversion to ltl2ba Input
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    
    //--------------------------------------------------------------------------
    // Textual Constants not Associated with Specific Operators
    //--------------------------------------------------------------------------
   
        /** The prefix of ltl2ba-compatible representations of abstract input atoms.
	*/
        private static final String atomRepresentationPrefix = "atom_";

        /** The ltl2ba-compatible textual image of a left parenthesis.
	*/
        private static final String leftParenthesis = "(";

        /** The ltl2ba-compatible textual image of a right parenthesis.
	*/
        private static final String rightParenthesis = ")";

        /** The ltl2ba-compatible textual image of a space.
	*/
        private static final String space = " ";
 
    //--------------------------------------------------------------------------
    // Bookkeeping
    //--------------------------------------------------------------------------
   
        /** Keeps track of the mapping of abstract input atoms to their ltl2ba-compatible representations.
	*/ 
        private static /* Object to String */ Map inputAtomToAtomRepresentationMap;
  
        /** The reverse of {@link #inputAtomToAtomRepresentationMap}.
	*/ 
        private static /* String to Object */ Map atomRepresentationToInputAtomMap;

        /** Holds the index of the next newly encountered abstract input atom. 
	*/ 
        private static int curAtomIndex;
    
        /** Returns the abstract input atom corresponding to the atom representation parameter.

            @param atomRepresentation The atom represenation.
	*/
        static
        Object
        toInputAtom(String atomRepresentation)
        {
            return atomRepresentationToInputAtomMap.get(atomRepresentation);
        }
    
    //--------------------------------------------------------------------------
    // Static Conversion Methods to be Called from the Outside
    //--------------------------------------------------------------------------
   
        /** Converts the abstract input term to ltl2ba input syntax.

            @param term The abstract input term.
            @return The result of the conversion.
	*/
        static
        String
        toLtl2baInput(Object term)
        {
            inputAtomToAtomRepresentationMap = new HashMap();
            atomRepresentationToInputAtomMap = new HashMap();
            curAtomIndex = 0;
            String representation = driveConversionToLtl2baInput(term);
            return representation;
        }
     
        /** Converts the abstract input term to ltl2ba input syntax, using the <code>Comparator</code> object for
            atom comparison.

            @param term The abstract input term.
            @param atomComparator The comparator object.
            @return The result of the conversion.
	*/
        static
        String
        toLtl2baInput(Object term, Comparator atomComparator)
        {
            inputAtomToAtomRepresentationMap = new TreeMap(atomComparator);
            atomRepresentationToInputAtomMap = new HashMap();
            curAtomIndex = 0;
            String representation = driveConversionToLtl2baInput(term);
            return representation;
        }
    
    //--------------------------------------------------------------------------
    // Static Driver Method
    //--------------------------------------------------------------------------
 
        /** Drives the conversion of the abstract input term to ltl2ba input syntax.

            @param term The abstract input term.
            @return The result of the conversion.
        */
        static
        String
        driveConversionToLtl2baInput(Object term)
        {
            String ltl2baInput;
            if (term instanceof Input) 
                ltl2baInput = leftParenthesis + ((Input) term).toLtl2baInput() + rightParenthesis;
            else {
                ltl2baInput = (String) inputAtomToAtomRepresentationMap.get(term);
                if (ltl2baInput == null) {
                    ltl2baInput 
                        = atomRepresentationPrefix.concat(new Integer(curAtomIndex).toString());
                    inputAtomToAtomRepresentationMap.put(term, ltl2baInput);
                    atomRepresentationToInputAtomMap.put(ltl2baInput, term);
                    curAtomIndex++;
                }
            }
            return ltl2baInput;
        }

    //--------------------------------------------------------------------------
    // Dynamic Driver Methods
    //--------------------------------------------------------------------------
 
        /** Converts the abstract input term to ltl2ba input syntax.

            @return The result of the conversion.
	*/
        abstract
        String
        toLtl2baInput();
    
        /** Returns the textual image of the outermost operator of the result of converting 
            the abstract input term to ltl2ba input syntax.

            @return see the general description of the method
	*/
        abstract
        String 
        operatorRepresentation();

    //--------------------------------------------------------------------------
    // Dynamic Field for Holding the Conversion Result, to
    // Speed Up the Conversion in Case the Abstract Input Term is not Tree-like
    //--------------------------------------------------------------------------
    
        /** Holds the result of converting the abstract input term
            to ltl2ba input syntax, once it is computed.            
        */
        String itsLtl2baInput = null;
    
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // Derived Classes
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    
    //==========================================================================
    // Abstract Intermediate Classes
    //==========================================================================

        /** Abstract input terms whose outermost operator
            is a binary operator.
        */
        private static abstract class Binary extends Input
        {
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Construction
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Constructs a binary abstract input term.
  
                @param arg1 The first argument of the binary operator.
                @param arg2 The first argument of the binary operator.
            */
            Binary(Object arg1, Object arg2)
            {
                super();
                itsArg1 = arg1;
                itsArg2 = arg2;
            }
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Components
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

            /** Holds the first argument of the binary operator.
            */        
            Object itsArg1;
        
            /** Returns the first argument of the binary operator.

                @return see the general description of the method
            */        
            public
            Object
            arg1()
            {
                return itsArg1;
            }
        
        //======================================================================
        
            /** Holds the second argument of the binary operator.
            */        
            Object itsArg2;
        
            /** Returns the second argument of the binary operator.

                @return see the general description of the method
            */    
            public    
            Object
            arg2()
            {
                return itsArg2;
            }
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Conversion to ltl2ba Input
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

            /** Converts the abstract input term to ltl2ba input syntax. 

                @return The result of the conversion.
            */
            final
            String
            toLtl2baInput()
            {
                if (itsLtl2baInput == null)
                    itsLtl2baInput 
                        = driveConversionToLtl2baInput(arg1()) 
                          + space + operatorRepresentation() + space
                          + driveConversionToLtl2baInput(arg2());
                return itsLtl2baInput;
            }
        
        }
    
    //--------------------------------------------------------------------------
        
        /** Abstract input terms whose outermost operator
            is a unary operator.
        */
        private static abstract class Unary extends Input
        {
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Construction
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Constructs a unary abstract input term.
  
                @param arg The argument of the unary operator.
            */
            Unary(Object arg)
            {
                itsArg = arg;
            }
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Components
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

            /** Holds the argument of the unary operator.
            */        
            Object itsArg;
        
            /** Returns the argument of the unary operator.

                @return see the general description of the method
            */        
            public
            Object
            arg()
            {
                return itsArg;
            }
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Conversion to ltl2ba Input
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Converts the abstract input term to ltl2ba input syntax. 

                @return The result of the conversion.
            */
            final
            String
            toLtl2baInput()
            {
                if (itsLtl2baInput == null)
                    itsLtl2baInput = operatorRepresentation() + space + driveConversionToLtl2baInput(arg());
                return itsLtl2baInput;
            }
        
        }
        
    //--------------------------------------------------------------------------
        
        /** Constant abstract input terms.
        */
        private static abstract class Constant extends Input
        {
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Construction
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Constructs a constant abstract input term.
	    */
            Constant()
            {
                // empty
            }
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Conversion to ltl2ba Input
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Converts the abstract input constant to ltl2ba input syntax. 

                @return The result of the conversion.
            */
            final
            String
            toLtl2baInput()
            {
                return operatorRepresentation();
            }
        
        }
        
    //==========================================================================
    // Temporal Operators
    //==========================================================================
        
        /** Abstract input terms whose outermost operator
            is an until.
        */
        public static class Until extends Binary
        {
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Construction
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Constructs an abstract until.

                @param arg1 The first argument of the until.
                @param arg2 The second argument of the until.
            */        
            public
            Until(Object arg1, Object arg2)
            {
                super(arg1, arg2);
            }
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Conversion to ltl2ba Input Syntax
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

            /** Returns the textual image of the until operator 
                according to ltl2ba input syntax.

                @return see the general description of the method
    	    */     
            final
            String 
            operatorRepresentation()
            {
                return "U";
            }
        
        }
    
    //--------------------------------------------------------------------------
        
        /** Abstract input terms whose outermost operator
            is a release.
        */
        public static class Release extends Binary
        {
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Construction
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Constructs an abstract release.

                @param arg1 The first argument of the release.
                @param arg2 The second argument of the release.
            */
            public
            Release(Object arg1, Object arg2)
            {
                super(arg1, arg2);
            }
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Conversion to ltl2ba Input 
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Returns the textual image of the release operator 
                according to ltl2ba input syntax.

                @return see the general description of the method
    	    */     
            final 
            String 
            operatorRepresentation()
            {
                return "V";
            }
        
        }
        
    //--------------------------------------------------------------------------
        
        /** Abstract input terms whose outermost operator
            is an always.
        */
        public static class Always extends Unary
        {
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Construction
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Constructs an abstract always.

                @param arg The argument of the always. */
        
            public
            Always(Object arg)
            {
                super(arg);
            }
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Conversion to ltl2ba Input 
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

            /** Returns the textual image of the always operator 
                according to ltl2ba input syntax.

                @return see the general description of the method
    	    */             
            final 
            String 
            operatorRepresentation()
            {
                return "[]";
            }
        
        }
        
    //--------------------------------------------------------------------------
        
        /** Abstract input terms whose outermost operator
            is an eventually.
        */
        public static class Eventually extends Unary
        {
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Construction
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Constructs an abstract eventually.

                @param arg The argument of the eventually. 
            */
            public
            Eventually(Object arg)
            {
                super(arg);
            }
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Conversion to ltl2ba Input 
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Returns the textual image of the eventually operator 
                according to ltl2ba input syntax.

                @return see the general description of the method
    	    */             
            final 
            String 
            operatorRepresentation()
            {
                return "<>";
            }
        
        }
        
    //--------------------------------------------------------------------------
        
        /** Abstract input terms whose outermost operator
            is a next.
        */
        public static class Next extends Unary
        {
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Construction
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Constructs an abstract next.

                @param arg The argument of the next. 
            */
            public
            Next(Object arg)
            {
                super(arg);
            }
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Conversion to ltl2ba Input 
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Returns the textual image of the next operator 
                according to ltl2ba input syntax.

                @return see the general description of the method
    	    */             
            final 
            String 
            operatorRepresentation()
            {
                return "X";
            }
        
        }
        
    //==========================================================================
    // Boolean Operators
    //==========================================================================
        
        /** Abstract input terms whose outermost operator
            is an equivalence.
        */
        public static class Equivalence extends Binary
        {
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Construction
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Constructs an abstract equivalence.

                @param arg1 The first argument of the equivalence.
                @param arg2 The second argument of the equivalence.
            */        
            public
            Equivalence(Object arg1, Object arg2)
            {
                super(arg1, arg2);
            }
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Conversion to ltl2ba Input 
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Returns the textual image of the equivalence operator 
                according to ltl2ba input syntax.

                @return see the general description of the method
    	    */             
            final 
            String 
            operatorRepresentation()
            {
                return "<->";
            }
        
        }
        
    //--------------------------------------------------------------------------
        
        /** Abstract input terms whose outermost operator
            is an implication.
        */
        public static class Implication extends Binary
        {
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Construction
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Constructs an abstract implication.

                @param arg1 The first argument of the implication.
                @param arg2 The second argument of the implication.
            */        
            public
            Implication(Object arg1, Object arg2)
            {
                super(arg1, arg2);
            }
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Conversion to ltl2ba Input 
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Returns the textual image of the implication operator 
                according to ltl2ba input syntax.

                @return see the general description of the method
    	    */             
            final 
            String 
            operatorRepresentation()
            {
                return "->";
            }
        
        }
        
    //--------------------------------------------------------------------------
        
        /** Abstract input terms whose outermost operator
            is a disjunction.
        */
        public static class Disjunction extends Binary implements CallersSubstitute.Guard.Disjunction
        {
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Construction
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Constructs an abstract disjunction.

                @param arg1 The first argument of the disjunction.
                @param arg2 The second argument of the disjunction.
            */        
            public
            Disjunction(Object arg1, Object arg2)
            {
                super(arg1, arg2);
            }
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Conversion to ltl2ba Input 
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Returns the textual image of the disjunction operator 
                according to ltl2ba input syntax.

                @return see the general description of the method
    	    */             
            final 
            String 
            operatorRepresentation()
            {
                return "||";
            }
        
        }
        
    //--------------------------------------------------------------------------
        
        /** Abstract input terms whose outermost operator
            is a conjunction.
        */
        public static class Conjunction extends Binary implements CallersSubstitute.Guard.Conjunction
        {
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Construction
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Constructs an abstract conjunction.

                @param arg1 The first argument of the conjunction.
                @param arg2 The second argument of the conjunction.
            */        
            public
            Conjunction(Object arg1, Object arg2)
            {
                super(arg1, arg2);
            }
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Conversion to ltl2ba Input 
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Returns the textual image of the conjunction operator 
                according to ltl2ba input syntax.

                @return see the general description of the method
    	    */             
            final 
            String 
            operatorRepresentation()
            { 
                return "&&";
            }
        
        }
    
    //--------------------------------------------------------------------------
        
        /** Abstract input terms whose outermost operator
            is a negation.
        */
        public static class Negation extends Unary implements CallersSubstitute.Guard.Negation
        {
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Construction
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Constructs an abstract negation.

                @param arg The argument of the negation. 
            */
            public
            Negation(Object arg)
            {
                super(arg);
            }

        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Conversion to ltl2ba Input 
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Returns the textual image of the negation operator 
                according to ltl2ba input syntax.

                @return see the general description of the method
    	    */             
            final 
            String 
            operatorRepresentation()
            {
                return "!";
            }
        
        }
        
    //--------------------------------------------------------------------------
        
        /** Abstract input terms representing truthood.
        */
        public static class Truthhood extends Constant implements CallersSubstitute.Guard.Truthhood
        {
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Construction
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Constructs an abstract truthhood constant.
	    */
            public
            Truthhood()
            {
                // empty
            }
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Conversion to ltl2ba Input 
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Returns the textual image of the truthhood constant 
                according to ltl2ba input syntax.

                @return see the general description of the method
    	    */             
            final 
            String 
            operatorRepresentation()
            {
                return "true";
            }
        
        }
    
    //--------------------------------------------------------------------------
        
        /** Abstract input terms representing falsehood.
        */
        public static class Falsehood extends Constant
        {
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Construction
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Constructs an abstract falsehood constant.
	    */
            public
            Falsehood()
            {
                // empty
            }
        
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Conversion to ltl2ba Input 
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
            /** Returns the textual image of the falsehood constant 
                according to ltl2ba input syntax.

                @return see the general description of the method
    	    */             
            final 
            String 
            operatorRepresentation()
            {
                return "false";
            }
        
        }
        
    }

//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Callbacks
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    
    /** Callback interface for the high-level part of JLtl2ba.
        See the
        {@link 
             #exec(
                 Object, 
                 jltl2ba.HighLevel.Callback, 
                 jltl2ba.HighLevel.CallersSubstitute.NeverClaim
             ) 
             </code><code>exec</code> method<code>} and also the 
        {@link 
             #exec(
                 Object,
                 Comparator,
                 jltl2ba.HighLevel.Callback, 
                 jltl2ba.HighLevel.CallersSubstitute.NeverClaim
             ) 
             </code><code>exec</code> method with an explicit comparator<code>}.
    */
    
    public interface Callback
    {
    
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // Creating State Specifications
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%    
    
        /** Creates the caller's substitute for a bundle with outgoing transitions,
            where these transitions are still to be added.
    
            @param sourceStateSubstitute The already-created substitute for the source state.
            @return The caller's substitute.
        */
        public
        CallersSubstitute.Bundle
        newBundleSubstitute(CallersSubstitute.State sourceStateSubstitute);
    
        /** Creates the caller's substitute for a skipping bundle, that is,
            a bundle without outgoing transitions, which simply hands over
            control to the textually following bundle.
    
            @param sourceStateSubstitute The already-created substitute for the "source state."
            @return The caller's substitute.
        */
        public
        CallersSubstitute.Bundle
        newSkippingBundleSubstitute(CallersSubstitute.State sourceStateSubstitute);
    
        /** Creates the caller's substitute for a bundle that is a dead end for
            the word matching process.
    
            @param sourceStateSubstitute The already-created substitute for the "source state."
            @return The caller's substitute.
        */
        public
        CallersSubstitute.Bundle
        newDeadEndBundleSubstitute(CallersSubstitute.State sourceStateSubstitute);
    
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // Creating Transitions
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%    
    
        /** Creates the caller's substitute for a transition.
    
            @param guardSubstitute The already-created substitute for the guard of the transition.
            @param targetStateSubstitute The already-created substitute for the target state of the transition            
            @return The caller's substitute..
        */
        public
        CallersSubstitute.Transition
        newTransitionSubstitute(Object guardSubstitute, CallersSubstitute.State targetStateSubstitute);
    
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // Creating States
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%    
    
        /** Creates the caller's substitute for a 
            control flow location with a label of the form 
            accept_all.
    
            @param image The textual image of the label.
            @return The caller's substitute.
        */
        public
        CallersSubstitute.State.AcceptAll
        newStateAcceptAllSubstitute(String image);
    
        /** Creates the caller's substitute for a 
            control flow location with a label of the form 
            accept_init.
    
            @param image The textual image of the label.
            @return The caller's substitute.
        */
        public
        CallersSubstitute.State.AcceptInit
        newStateAcceptInitSubstitute(String image);
    
        /** Creates the caller's substitute for a 
            control flow location with a label of the form 
            accept_S<index>.
    
            @param image The textual image of the label.
            @return The caller's substitute.
        */
        public
        CallersSubstitute.State.AcceptSj
        newStateAcceptSjSubstitute(String image);
    
        /** Creates the caller's substitute for a 
            control flow location with a label of the form 
            T<index>_S<index>.
    
            @param image The textual image of the label.
            @return The caller's substitute.
        */
        public
        CallersSubstitute.State.TiSj
        newStateTiSjSubstitute(String image);
    
        /** Creates the caller's substitute for a 
            control flow location with a label of the form 
            T<index>_Init.
    
            @param image The textual image of the label.
            @return The caller's substitute.
        */
        public
        CallersSubstitute.State.TiInit
        newStateTiInitSubstitute(String image);
    
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // Creating Guards
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%    
    
        /** Creates the caller's substitute for a guard term
            whose outermost operator is a disjunction.
    
            @param arg1 The already-created substitute for the first argument of the disjunction.
            @param arg2 The already-created substitute for the second argument of the disjunction.
            @return The caller's substitute.
        */
        public
        CallersSubstitute.Guard.Disjunction
        newGuardDisjunctionSubstitute(CallersSubstitute.Guard arg1, CallersSubstitute.Guard arg2);
    
        /** Creates the caller's substitute for a guard term
            whose outermost operator is a conjunction.
    
            @param arg1 The already-created substitute for the first argument of the conjunction.
            @param arg2 The already-created substitute for the second argument of the conjunction.
            @return The caller's substitute.
        */
        public
        CallersSubstitute.Guard.Conjunction
        newGuardConjunctionSubstitute(CallersSubstitute.Guard arg1, CallersSubstitute.Guard arg2);
    
        /** Creates the caller's substitute for a guard term
            whose outermost operator is a negation.
    
            @param arg The already-created substitute for the argument of the negation.
            @return The caller's substitute.
        */
        public
        CallersSubstitute.Guard.Negation
        newGuardNegationSubstitute(CallersSubstitute.Guard arg);
    
        /** Creates the caller's substitute for the truth constant appearing as a guard term.

            @return The caller's substitute.
        */
        public
        CallersSubstitute.Guard.Truthhood
        newGuardTruthhoodSubstitute();
    
        /** Allows the caller to handle an atom originating in the input.
            It will often be the case that
            atoms provided as input can be directly
            re-used within guard substitutes once they re-appear as output - then the implementation of
            this method will typically perform a class cast on the parameter, and nothing more.
            This situation, however, need not always be such. Something else
            than a simple class cast
            is then required before guard buildup can continue.
    
            @param inputAtom The atom provided as input.
            @return The caller's substitute of the atom occurring as output.
        */
        public
        CallersSubstitute.Guard.Atom
        newGuardAtomSubstitute(Object inputAtom);
    
    }

//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Caller's Substitutes within Callbacks
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    
    /** Interfaces for caller's substitutes within callbacks.
        The never claim ensuing from a call to ltl2ba is 
        understood as an automaton description where 
        <ul>
        <li> labelled control flow points describe the states and
        <li> <code>if</code>-<code>fi</code>s or <code>skip</code>s following
             such points describe <i>bundles</i>, that is, the outgoing transitions
             of the respective state. 
        </ul>
        The range of substitutes comprises substitutes for never claims as such,
        bundles with and without transitions, transitions, 
        guard term operators and the various kinds of states.
    */
    
    public static final class CallersSubstitute
    {
    
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // Never Claim Substitutes
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
        /** Interface for the caller's substitute for never claims.
        */
        public static interface NeverClaim
        {  
            /** Adds the substitute for a bundle to the substitute for a never claim.
    
                @param bundle The substitute for a bundle.
                @return <code>true</code>
            */  
            public
            boolean
            add(Bundle bundle);
        }
        
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // Bundle Substitutes
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
        /** Interface for the caller's substitute for bundles.
        */
        public static interface Bundle
        {
    
            /** Interface for the caller's substitute for bundles with transitions.
            */
            public static interface WithTransitions extends Bundle
            {
                /** Adds the substitute for a transition to the substitute for a bundle with transitions.
    
                    @param transition The substitute for a transition.
                    @return <code>true</code>
        	*/  
                public
                boolean
                add(Transition transition);
            }
        
            /** Interface for the caller's substitute for (trivial) bundles without transitions.
            */
            public static interface WithoutTransitions extends Bundle
            {
                // empty
            }
        }
        
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // Transition Substitutes
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
        /** Interface for the caller's substitute for transitions.
        */
        public static interface Transition
        {
            // empty
        }
        
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // State Substitutes
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
        /** Interface for the caller's substitute for labelled control flow locations.
        */
        public static interface State
        {
            /** Interface for the caller's substitute for control flow 
                locations with a label of the form accept_all.
            */
            public static interface AcceptAll extends State
            {
                // empty
            }
        
            /** Interface for the caller's substitute for control flow 
                locations with a label of the form accept_init.
            */
            public static interface AcceptInit extends State
            {
                // empty
            }
        
            /** Interface for the caller's substitute for control flow 
                locations with a label of the form accept_S<index>.
            */
            public static interface AcceptSj extends State
            {
                // empty
            }
        
            /** Interface for the caller's substitute for control flow 
                locations with a label of the form T<index>_S<index>.
            */
            public static interface TiSj extends State
            {
                // empty
            }
        
            /** Interface for the caller's substitute for control flow 
                locations with a label of the form T<index>_init.
            */
            public static interface TiInit extends State
            {
                // empty
            }
        }
        
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // Guard Substitutes
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
        /** Interface for the caller's substitute for guard terms within transitions.
        */ 
        public static interface Guard
        {
            /** Interface for the caller's substitute for disjunctions within guard terms.
    	    */
            public static interface Disjunction extends Guard
            {
                // empty
            }
        
            /** Interface for the caller's substitute for conjunctions within guard terms.
    	    */
            public static interface Conjunction extends Guard
            {
                // empty
            }
        
            /** Interface for the caller's substitute for negations within guard terms.
    	    */
            public static interface Negation extends Guard
            {
                // empty
            }
        
            /** Interface for the caller's substitute for truth constants within guard terms.
    	    */
            public static interface Truthhood extends Guard
            {
                // empty
            }
        
            /** Interface for the caller's substitute for atoms within guard terms.
    	    */
            public static interface Atom extends Guard
            {
                // empty
            }
        }
    
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // Construction
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    
        /** Disabling constructor. No instances of this class are to be created. 
        */
        private
        CallersSubstitute()
        {
            // empty
        }
    
    }

//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Construction
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    /** Disabling constructor. No instances of this class are to be created. 
    */
    private
    HighLevel()
    {
        // empty
    }

}
