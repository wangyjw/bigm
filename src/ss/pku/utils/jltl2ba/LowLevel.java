//##############################################################################
// FILE: LowLevel.java
// CONTENTS: public final class LowLevel
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
import java.util.ArrayList;

/** Low-level part of JLtl2ba.
    This part of the interface passes an LTL formula to ltl2ba and returns
    a handle on a file that contains the corresponding output 
    of the tool. Three (static) methods are provided for that:  
    <ul>
    <li>
        The first method 
        emulates the command line interface of ltl2ba: It has a boolean 
        parameter for each of the seven boolean options, a parameter
        of type LowLevel that can be instantiated with the value of one of two
        pre-initialiased class variables, to determine whether the last
        parameter is a formula or a file name, and that last parameter
        itself, which is of type String.
    <li>
        The other two methods are intended for streamlining repeated calls
        with <i>permanent</i> boolean options, that is, with boolean
        options that remain the same over these calls. The first one of these methods
        has just one parameter, which is interpreted as a formula;
        the second one also has just one parameter, which is
        interpreted as the name of a 1-line file containing the formula.
        For value and retrieving permanent options, extra methods are
        provided. The default state of all permanent options is <code>false</code>.
    </ul>
    The default command passed to the system for invoking
    ltl2ba is <code>ltl2ba</code>. It is possible to override
    this value, for instance by an absolute path.
    It is also possible to reset all permanent options to <code>false</code>
    by calling just one method.
 
    @author Michael Baldamus
*/

public final class LowLevel
{

//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Constants and Associations Between Constants
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    /** The standard textual name of ltl2ba.
    */
    private static final String name = "ltl2ba";

    /** The textual representation of the option for displaying an automaton description at each step.
    */
    private static final String displayAutomatonDescriptionAtEachStepDesignator = "-d";

    /** The textual representation of the option for computing time and automata size statistics.
    */
    private static final String computeTimeAndAutomataSizeStatisticsDesignator = "-s";

    /** The textual representation of the option for disabling logic formula simplification.
    */
    private static final String disableLogicFormulaSimplificationDesignator = "-l";

    /** The textual representation of the option for disabling a posteriori simplification.
    */
    private static final String disableAPosterioriSimplificationDesignator = "-p";

    /** The textual representation of the option for disabling on-the-fly simplification.
    */
    private static final String disableOnTheFlySimplificationDesignator = "-o";

    /** The textual representation of the option for disabling 
        strongly connected component simplification.
    */
    private static final String disableStronglyConnectedComponentsSimplificationDesignator = "-c";

    /** The textual representation of the option for disabling "trick in accepting conditions."
    */
    private static final String disableTrickInAcceptingConditionsDesignator = "-a";

    /** The textual representation of the option for 
        designating the last argument of ltl2ba as a formula.
    */
    private static final String formulaOnCommandLineDesignator = "-f ";

    /** The textual representation of the option for designating the 
        last argument of ltl2ba as a file name.
    */
    private static final String formulaInFileDesignator = "-F ";

    /** The textual representation of the prefix of output file names.
    */
    private static final String namePrefixOfOutputFiles = "jltl2ba_";

//==============================================================================

    /** The number of boolean options.
     */
    private static final int nrOfBooleanOptions = 7;

    /** The index of the option for displaying an automaton description at each step.
    */
    private static final int displayAutomatonDescriptionAtEachStepIndex  = 0;

    /** The index of the option for computing time and automata size statistics.
    */
    private static final int computeTimeAndAutomataSizeStatisticsIndex = 1;

    /** The index of the option for disabling logic formula simplification.
    */
    private static final int disableLogicFormulaSimplificationIndex = 2;

    /** The index of the option for disabling a posteriori simplification.
    */
    private static final int disableAPosterioriSimplificationIndex = 3;

    /** The index of the option for disabling on-the-fly simplification.
    */
    private static final int disableOnTheFlySimplificationIndex = 4;
 
    /** The index of the option for disabling strongly connected component simplification.
    */
    private static final int disableStronglyConnectedComponentsSimplificationIndex = 5;
 
    /** The index of the option for disabling "trick in accepting conditions."
    */
    private static final int disableTrickInAcceptingConditionsIndex = 6;

//-----------------------------------------------------------------------------

    /** The association of boolean option indexes with textual option representations.
    */
    private static final String[/*nrOfBooleanOptions*/] optionDesignator 
        = new String[] {
                  displayAutomatonDescriptionAtEachStepDesignator, 
                  computeTimeAndAutomataSizeStatisticsDesignator, 
                  disableLogicFormulaSimplificationDesignator, 
                  disableAPosterioriSimplificationDesignator, 
                  disableOnTheFlySimplificationDesignator, 
                  disableStronglyConnectedComponentsSimplificationDesignator, 
                  disableTrickInAcceptingConditionsDesignator, 
              };

//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// The Command Passed to the System to Invoke ltl2ba
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    /** Holds the command used to invoke ltl2ba.
    */
    private static String command = name;

    /** Is <code>true</code> if no call of any method belonging to the class has occurred, 
        <code>false</code> otherwise.
    */
    private static boolean untouched = true;

    /** Sets the {#touched} signal to <code>true</code>.
    */
    private static
    void
    touched()
    {
        untouched = false; 
    }

    /** Sets the command passed to the system for invoking
        ltl2ba. This method may never be called or be called
        only once, before any other method has been called.
        A LowLevel.Error is thrown if whenever this discipline
        is violated.

        @param commandValue The command to be passed to the system to invoke ltl2ba.
        @exception LowLevel.Error 
            The error thrown when the method is called after any other call of a method
            belonging to BasicLtlt2baIF, or if the method is called with a 
            <code>null</code> argument.
    */  
    public static
    void
    command(String commandValue)
    throws LowLevel.Error
    {
        if (!untouched)
            throw new Error(
                          "invocation of command(..) after class has already been touched"
                      );
        touched();
        if (commandValue == null)
            throw new Error(
                          "invocation of command(..) with a null argument"
                      );
        command = commandValue;
    }

//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Auxiliaries for Executing ltl2ba
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    /** Returns the concatenation of the textual representations 
        of all options that are designated as <code>true</code> in <code>optionValue</code>.

        @param optionValue The option value.
        @return see the general description of the method
    */
    private static
    ArrayList
    optionDesignators(boolean[] optionValue)
    {
        ArrayList result = new ArrayList();
        for (int i = 0; i < nrOfBooleanOptions; i++) 
            if (optionValue[i]) 
                result.add(optionDesignator[i]);
        return result;
    }

    /** Executes ltl2ba with transient options.

        @param optionDesignators A string containing the options of the respective call.
        @param formulaOnCommandLineOrInFileDesignator 
            A string containing the option for designating the third argument 
            as a formula or a file name.
        @param formulaOrFileName That formula or file name itself.
        @return 
            A handle on a file that contains the output of ltl2ba.
    */
    private static
    StringBuffer
    exec(
        ArrayList optionDesignators,
        String formulaOnCommandLineOrInFileDesignator, 
        String formulaOrFileName
    ) throws IOException, Ltl2baException
    {
        touched();
        
        ArrayList tokens = new ArrayList();
        tokens.add(command);
        tokens.addAll(optionDesignators);
        tokens.add(formulaOnCommandLineOrInFileDesignator);
        tokens.add(formulaOrFileName);

        return execOnSystemLevel(tokens);
    }

    /** Executes the string parameter as a system command.
     * @param commandTokens
     * 		sequence of <code>String</code>s. Each becomes an element of the
     *      <code>args</code> array passed to the exec'ed program
     * @throws InterruptedException
     * @throws IOException
    */
    private static
    StringBuffer
    execOnSystemLevel(ArrayList commandTokens)
    {
        try
        {
            String[] tokens = new String[commandTokens.size()];
            commandTokens.toArray(tokens);
            Process p = Runtime.getRuntime().exec(tokens);
            ByteArrayOutputStream stdoutByteStream = new ByteArrayOutputStream();
            ByteArrayOutputStream stderrByteStream = new ByteArrayOutputStream();
            
            Thread outputCopier = new StreamCopier(p.getInputStream(), stdoutByteStream);
            outputCopier.start();

            Thread errorCopier = new StreamCopier(p.getErrorStream(), stderrByteStream);
            errorCopier.start();
            
            int returnValue = p.waitFor();
            outputCopier.join();
            errorCopier.join();
            
            if (returnValue != 0) 
                throw new Ltl2baException(returnValue, stderrByteStream.toString());
            
            return new StringBuffer().append(stdoutByteStream.toString());
        }
        catch (IOException e)
        {
            throw new Ltl2baException(e);
        }
        catch (InterruptedException e)
        {
            throw new Ltl2baException(e);
        }
    }

//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Executing ltl2ba with Transient Options
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    /** Used for designating the last parameter
        of the <code>exec</code> method with transient options as a formula.

        @see #exec(boolean, boolean, boolean, boolean, boolean, boolean, boolean, LowLevel, String)
    */
    public static final LowLevel formulaDirect = new LowLevel();

    /** Used for designating the last parameter
        of the <code>exec</code> method with transient options as a file name.

        @see #exec(boolean, boolean, boolean, boolean, boolean, boolean, boolean, LowLevel, String)
    */
    public static final LowLevel formulaInFile = new LowLevel();

    /** Executes ltl2ba with transient options.

        @param displayAutomatonDescriptionAtEachStep 
            Determines whether the output file contains an automata description for each step.
        @param computeTimeAndAutomataSizeStatistics
            Determines whether the output file contains time and automata sizes statistics.
        @param disableLogicFormulaSimplification 
            Determines whether logic formula simplification is disabled.
        @param disableAPosterioriSimplification 
            Determines whether a-posteriori simplification is disabled.
        @param disableOnTheFlySimplification 
            Determines whether whether on-the-fly simplification is disabled.
        @param disableStronglyConnectedComponentsSimplification 
            Determines whether the simplification of strongly connected components is disabled.
        @param disableTrickInAcceptingConditions
            Determines whether a trick in accepting conditions is disabled. 
        @param formulaDirectOrFormulaInFile
            Determines whether the last parameter is a formula or a file name.
            @see #formulaDirect 
            @see #formulaInFile
        @param formulaOrFilename
            A formula or a file name.
        @return
            A handle on a file that contains the output of ltl2ba.
        @exception IOException
            Exception thrown when the creation of the output file fails.
        @exception Ltl2baException
            Exception thrown when the return value of ltl2ba is non-zero.
            In this case the message contained in the exception is 
            the result of converting the return value to String.
    */
    public static
    StringBuffer
    exec(
        boolean displayAutomatonDescriptionAtEachStep, 
        boolean computeTimeAndAutomataSizeStatistics, 
        boolean disableLogicFormulaSimplification, 
        boolean disableAPosterioriSimplification, 
        boolean disableOnTheFlySimplification, 
        boolean disableStronglyConnectedComponentsSimplification, 
        boolean disableTrickInAcceptingConditions,
        LowLevel formulaDirectOrFormulaInFile,
        String formulaOrFilename
    ) throws IOException, Ltl2baException
    {
        boolean[] optionValue 
            = new boolean[/*nrOfBooleanOptions*/] {
                      displayAutomatonDescriptionAtEachStep, 
                      computeTimeAndAutomataSizeStatistics, 
                      disableLogicFormulaSimplification, 
                      disableAPosterioriSimplification, 
                      disableOnTheFlySimplification, 
                      disableStronglyConnectedComponentsSimplification, 
                      disableTrickInAcceptingConditions
                  };
        ArrayList optionDesignators = optionDesignators(optionValue);

        if (formulaDirectOrFormulaInFile == formulaDirect)
            return 
                exec(optionDesignators, formulaOnCommandLineDesignator, formulaOrFilename);
        else /*(formulaDirectOrFormulaInFile == formulaInFile)*/
            return exec(optionDesignators, formulaInFileDesignator, formulaOrFilename);
    }

//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// State of Permament Options
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    /** Holds the values of all permanent options.
    */
    private static boolean[] optionValue = new boolean[nrOfBooleanOptions];

    /** Is <code>true</code> if a permanent option has changed, <code>false</code> otherwise.
    */
    private static boolean optionChange;

    /** Resets the permanent options to false. 
    */
    public static
    void
    reset()
    {
        optionValue[displayAutomatonDescriptionAtEachStepIndex] = false;
        optionValue[computeTimeAndAutomataSizeStatisticsIndex] = false;
        optionValue[disableLogicFormulaSimplificationIndex] = false;
        optionValue[disableAPosterioriSimplificationIndex] = false;
        optionValue[disableOnTheFlySimplificationIndex] = false;
        optionValue[disableStronglyConnectedComponentsSimplificationIndex] = false;
        optionValue[disableTrickInAcceptingConditionsIndex] = false;
        optionChange = true;
    }

    /** Initialises all permament options to <code>false</code>.
    */
    static
    {
        reset();
    }

//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Value and Querying Permanent Options
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    /** Queries the value of the option specified by the option index.
  
        @param optionIndex The option index.
        @return The value of the permanent option.
    */
    private static
    boolean
    value(int optionIndex)
    {
        touched();
        return optionValue[optionIndex];
    }

    /** Toggles the value of the option specified by the option index.
  
        @param optionIndex The option index.
        @return The previous value of the permanent option.
    */
    private static
    boolean
    toggle(int optionIndex)
    {
        return value(optionIndex, !value(optionIndex));
    }

    /** Queries and sets the value of the option specified by the option index.
  
        @param optionIndex The option index.
        @return The previous value of the permanent option.
    */
    private static
    boolean
    value(int optionIndex, boolean newValue)
    {   
	touched();
        boolean oldValue = value(optionIndex);
        optionValue[optionIndex] = newValue;
        if (newValue != oldValue) 
            optionChange = true;
        return oldValue;
    }

//==============================================================================

    /** Queries the value of the permanent option that governs
        whether the output files contain an automaton description for each step.

        @return The value of that option. 
    */
    public static
    boolean
    displayAutomatonDescriptionAtEachStep()
    {
        return value(displayAutomatonDescriptionAtEachStepIndex);
    }

    /** Toggles the value of the permanent option that governs
        whether the output contain an automaton description for each step.

        @return The prior value of that option. 
    */
    public static
    boolean
    toggleDisplayAutomatonDescriptionAtEachStep()
    {
        return toggle(displayAutomatonDescriptionAtEachStepIndex);
    }

    /** Sets and queries the value of the permanent option that governs
        whether the output files contain an automaton description for each step.

        @param newValue The new value of that option. 
        @return The prior value of that option. 
    */
    public static
    boolean
    displayAutomatonDescriptionAtEachStep(boolean newValue)
    {
        return value(displayAutomatonDescriptionAtEachStepIndex, newValue);
    }

//------------------------------------------------------------------------------

    /** Queries the value of the permanent option that governs
        whether the output files contain time and automata sizes statistics.

        @return The value of that option. 
    */
    public static
    boolean
    computeTimeAndAutomataSizeStatistics()
    {
        return value(computeTimeAndAutomataSizeStatisticsIndex);
    }

    /** Toggles the value of the permanent option that governs
        whether the output files contain time and automata sizes statistics.

        @return The prior value of that option. 
    */
    public static
    boolean
    toggleComputeTimeAndAutomataSizeStatistics()
    {
        return toggle(computeTimeAndAutomataSizeStatisticsIndex);
    }

    /** Sets and queries the value of the permanent option that governs
        whether the output files contain time and automata sizes statistics.

        @param newValue The new value of that option.
        @return The prior value of that option. 
    */
    public static
    boolean
    computeTimeAndAutomataSizeStatistics(boolean newValue)
    {
        return value(computeTimeAndAutomataSizeStatisticsIndex, newValue);
    }

//------------------------------------------------------------------------------
 
    /** Queries the value of the permanent option that disables
        logic formula simplification.

        @return The value of that option. 
    */
    public static
    boolean
    disableLogicFormulaSimplification()
    {
        return value(disableLogicFormulaSimplificationIndex);
    }

    /** Toggles the value of the permanent option that disables
        logic formula simplification.

        @return The prior value of that option. 
    */
    public static
    boolean
    toggleDisableLogicFormulaSimplification()
    {
        return toggle(disableLogicFormulaSimplificationIndex);
    }

    /** Sets and queries the value of the permanent option that disables
        logic formula simplification.

        @param newValue The new value of that option.
        @return The prior value of that option. 
    */
    public static
    boolean
    disableLogicFormulaSimplification(boolean newValue)
    {
        return value(disableLogicFormulaSimplificationIndex, newValue);
    }

//------------------------------------------------------------------------------

    /** Queries the value of the permanent option that disables
        a-posteriori simplification.

        @return The value of that option. 
    */
    public static
    boolean
    disableAPosterioriSimplification()
    {
        return value(disableAPosterioriSimplificationIndex);
    }

    /** Toggles the value of the permanent option that disables
        a-posteriori simplification.

        @return The prior value of that option. 
    */
    public static
    boolean
    toggleDisableAPosterioriSimplification()
    {
        return toggle(disableAPosterioriSimplificationIndex);
    }

    /** Sets and queries the value of the permanent option that disables
        a-posteriori simplification.

        @param newValue The new value of that option. 
        @return The prior value of that option. 
    */
    public static
    boolean
    disableAPosterioriSimplification(boolean newValue)
    {
        return value(disableAPosterioriSimplificationIndex, newValue);
    }

//------------------------------------------------------------------------------
 
    /** Queries the value of the permanent option that disables
        on-the-fly simplification.

        @return The value of that option. 
    */
    public static
    boolean
    disableOnTheFlySimplification()
    {
        return value(disableOnTheFlySimplificationIndex);
    }

    /** Toggles the value of the permanent option that disables
        on-the-fly simplification.

        @return The prior value of that option. 
    */
    public static
    boolean
    toggleDisableOnTheFlySimplification()
    {
        return toggle(disableOnTheFlySimplificationIndex);
    }

    /** Sets and queries the value of the permanent option that disables
        on-the-fly simplification.

        @param newValue The new value of that option. 
        @return The prior value of that option. 
    */
    public static
    boolean
    disableOnTheFlySimplification(boolean newValue)
    {
        return value(disableOnTheFlySimplificationIndex, newValue);
    }

//------------------------------------------------------------------------------

    /** Queries the value of the permanent option that disables
        the simplification of strongly connected components.

        @return The value of that option. 
    */
    public static
    boolean
    disableStronglyConnectedComponentsSimplification()
    {
        return value(disableStronglyConnectedComponentsSimplificationIndex);
    }

    /** Toggles the value of the permanent option that disables
        the simplification of strongly connected components.

        @return The prior value of that option. 
    */
    public static
    boolean
    toggleDisableStronglyConnectedComponentsSimplification()
    {
        return toggle(disableStronglyConnectedComponentsSimplificationIndex);
    }

    /** Sets and queries the value of the permanent option that disables
        the simplification of strongly connected components.

        @param newValue The new value of that option. 
        @return The prior value of that option. 
    */
    public static
    boolean
    disableStronglyConnectedComponentsSimplification(boolean newValue)
    {
        return value(disableStronglyConnectedComponentsSimplificationIndex, newValue);
    }

//------------------------------------------------------------------------------

    /** Queries the value of the permanent option that disables
        a trick in accepting conditions.

        @return The value of that option. 
    */
    public static
    boolean
    disableTrickInAcceptingConditions()
    {
        return value(disableTrickInAcceptingConditionsIndex);
    }

    /** Toggles the value of the permanent option that disables
        a trick in accepting conditions.

        @return The prior value of that option. 
    */
    public static
    boolean
    toggleDisableTrickInAcceptingConditions()
    {
        return toggle(disableTrickInAcceptingConditionsIndex);
    }

    /** Sets and queries the value of the permanent option that disables
        a trick in accepting conditions.

        @param newValue The new prior value of that option. 
        @return The prior value of that option. 
    */
    public static
    boolean
    disableTrickInAcceptingConditions(boolean newValue)
    {
        return value(disableTrickInAcceptingConditionsIndex, newValue);
    }

//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Executing ltl2ba with Permanent Options
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    /** Holds the concatenation of the textual representations
        of all permanent options that are currently <code>true</code>.
    */
    private static ArrayList curOptionDesignators;

    /** Executes ltl2ba with permanent options, interpreting the parameter as an LTL formula.

        @param formula
            The formula.
        @return
            A handle on a file that contains the output of ltl2ba.
        @exception IOException
            Exception thrown when the creation of the output file fails.
        @exception Ltl2baException
            Exception thrown when the return value of ltl2ba is non-zero.
            In this case the message contained in the exception is 
            the result of converting the return value to String.
    */
    public static
    StringBuffer
    exec(String formula)
    throws IOException, Ltl2baException
    {
        if (optionChange) {
            curOptionDesignators = optionDesignators(optionValue);
            optionChange = false;
        }   
        return exec(curOptionDesignators, formulaOnCommandLineDesignator, formula);
    }

    /** Executes ltl2ba with permanent options, interpreting the parameter as 
        the name of a 1-line file containing the LTL formula.

        @param file
            The file containing the formula.
        @return
            A handle on a file that contains the output of ltl2ba.
        @exception IOException
            Exception thrown when the creation of the output file fails.
        @exception Ltl2baException
            Exception thrown when the return value of ltl2ba is non-zero.
            In this case the message contained in the exception is 
            the result of converting the return value to String.
    */
    public static
    StringBuffer
    execReadingFromFile(File file)
    throws IOException, Ltl2baException
    {
        if (optionChange) {
            curOptionDesignators = optionDesignators(optionValue);
            optionChange = false;
        }   
        return exec(curOptionDesignators, formulaInFileDesignator, file.getAbsolutePath());
    }

//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Error Class
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    /** Runtime errors thrown when an abnormal condition within 
        {@link jltl2ba.LowLevel LowLevel} occurs.
    */

    public static class Error extends java.lang.Error
    {
        Error(String s)
        {
            super(s);
        }
    }


//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Input/output utility class
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    /**
     * Pipe data from one stream to another. 
     */
    private static final class StreamCopier extends Thread
    {
        
        private InputStream input;
        private OutputStream destination;

        private StreamCopier(InputStream source, OutputStream destination)
        {
            super();
            this.input = source;
            this.destination = destination;
        }

        public void run()
        {
            try
            {
        	    int b;
        	    while ((b = input.read()) != -1)
        	    {
        	        destination.write(b);
        	    }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// Construction
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    /** Disabling constructor. No instances of this class are to be created. 
    */
    private
    LowLevel()
    {
        // empty
    }

}
