/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.client.soda;

import com.espertech.esper.collection.Pair;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * An output limit clause defines how to limit output of statements and consists of
 * a selector specifiying which events to select to output, a frequency and a unit.
 */
public class OutputLimitClause implements Serializable
{
    private static final long serialVersionUID = 0L;

    private OutputLimitSelector selector;
    private Double frequency;
    private String frequencyVariable;
    private OutputLimitUnit unit;
    private Expression whenExpression;
    private List<Pair<String, Expression>> thenAssignments;
    private Expression[] crontabAtParameters;
    private TimePeriodExpression timePeriodExpression;

    /**
     * Creates an output limit clause.
     * @param timePeriodExpression a frequency to output at
     * @return clause
     */
    public static OutputLimitClause create(TimePeriodExpression timePeriodExpression)
    {
        return new OutputLimitClause(OutputLimitSelector.DEFAULT, timePeriodExpression);
    }

    /**
     * Creates an output limit clause.
     * @param selector is the events to select
     * @param timePeriodExpression a frequency to output at
     * @return clause
     */
    public static OutputLimitClause create(OutputLimitSelector selector, TimePeriodExpression timePeriodExpression)
    {
        return new OutputLimitClause(selector, timePeriodExpression);
    }

    /**
     * Creates an output limit clause.
     * @param selector is the events to select
     * @param frequency a frequency to output at
     * @return clause
     */
    public static OutputLimitClause create(OutputLimitSelector selector, double frequency)
    {
        return new OutputLimitClause(selector, frequency);
    }

    /**
     * Creates an output limit clause.
     * @param selector is the events to select
     * @param frequencyVariable is the variable providing the output limit frequency
     * @return clause
     */
    public static OutputLimitClause create(OutputLimitSelector selector, String frequencyVariable)
    {
        return new OutputLimitClause(selector, frequencyVariable);
    }

    /**
     * Creates an output limit clause.
     * @param frequency a frequency to output at
     * @return clause
     */
    public static OutputLimitClause create(double frequency)
    {
        return new OutputLimitClause(OutputLimitSelector.DEFAULT, frequency);
    }

    /**
     * Creates an output limit clause.
     * @param frequencyVariable is the variable name providing output rate frequency values
     * @return clause
     */
    public static OutputLimitClause create(String frequencyVariable)
    {
        return new OutputLimitClause(OutputLimitSelector.DEFAULT, frequencyVariable);
    }

    /**
     * Creates an output limit clause with a when-expression and optional then-assignment expressions to be added.
     * @param whenExpression the expression that returns true to trigger output
     * @return clause
     */
    public static OutputLimitClause create(Expression whenExpression)
    {
        return new OutputLimitClause(OutputLimitSelector.DEFAULT, whenExpression, new ArrayList<Pair<String, Expression>>());
    }

    /**
     * Creates an output limit clause with a crontab 'at' schedule parameters, see {@link com.espertech.esper.type.FrequencyParameter} and related.
     * @param scheduleParameters the crontab schedule parameters
     * @return clause
     */
    public static OutputLimitClause createSchedule(Expression[] scheduleParameters)
    {
        return new OutputLimitClause(OutputLimitSelector.DEFAULT, scheduleParameters);
    }

    /**
     * Ctor.
     * @param selector is the events to select
     * @param frequency a frequency to output at
     */
    public OutputLimitClause(OutputLimitSelector selector, Double frequency)
    {
        this.selector = selector;
        this.frequency = frequency;
        this.unit = OutputLimitUnit.EVENTS;
    }

    /**
     * Ctor.
     * @param selector is the events to select
     * @param timePeriodExpression the unit for the frequency
     */
    public OutputLimitClause(OutputLimitSelector selector, TimePeriodExpression timePeriodExpression)
    {
        this.selector = selector;
        this.timePeriodExpression = timePeriodExpression;
        this.unit = OutputLimitUnit.TIME_PERIOD;
    }

    /**
     * Ctor.
     * @param selector is the events to select
     * @param frequencyVariable is the variable name providing output rate frequency values
     */
    public OutputLimitClause(OutputLimitSelector selector, String frequencyVariable)
    {
        this.selector = selector;
        this.frequencyVariable = frequencyVariable;
        this.unit = OutputLimitUnit.EVENTS;
    }

    /**
     * Ctor.
     * @param selector is the events to select
     * @param frequency a frequency to output at
     * @param unit the unit for the frequency
     * @param frequencyVariable is the variable name providing output rate frequency values
     */
    public OutputLimitClause(OutputLimitSelector selector, Double frequency, String frequencyVariable, OutputLimitUnit unit)
    {
        this.selector = selector;
        this.frequency = frequency;
        this.frequencyVariable = frequencyVariable;
        this.unit = unit;
    }

    /**
     * Ctor.
     * @param selector is the events to select
     * @param crontabAtParameters the crontab schedule parameters
     */
    public OutputLimitClause(OutputLimitSelector selector, Expression[] crontabAtParameters)
    {
        this.selector = selector;
        this.crontabAtParameters = crontabAtParameters;
        this.unit = OutputLimitUnit.CRONTAB_EXPRESSION;
    }

    /**
     * Ctor.
     * @param selector is the events to select
     * @param whenExpression the boolean expression to evaluate to control output
     * @param thenAssignments the variable assignments, optional or an empty list
     */
    public OutputLimitClause(OutputLimitSelector selector, Expression whenExpression, List<Pair<String, Expression>> thenAssignments)
    {
        this.selector = selector;
        this.whenExpression = whenExpression;
        this.thenAssignments = thenAssignments;
        this.unit = OutputLimitUnit.WHEN_EXPRESSION;
    }

    /**
     * Returns the selector indicating the events to output.
     * @return selector
     */
    public OutputLimitSelector getSelector()
    {
        return selector;
    }

    /**
     * Sets the selector indicating the events to output.
     * @param selector to set
     */
    public void setSelector(OutputLimitSelector selector)
    {
        this.selector = selector;
    }

    /**
     * Returns output frequency.
     * @return frequency of output
     */
    public Double getFrequency()
    {
        return frequency;
    }

    /**
     * Sets output frequency.
     * @param frequency is the frequency of output
     */
    public void setFrequency(double frequency)
    {
        this.frequency = frequency;
    }

    /**
     * Returns the unit the frequency is in.
     * @return unit for the frequency.
     */
    public OutputLimitUnit getUnit()
    {
        return unit;
    }

    /**
     * Sets the unit the frequency is in.
     * @param unit is the unit for the frequency
     */
    public void setUnit(OutputLimitUnit unit)
    {
        this.unit = unit;
    }

    /**
     * Returns the variable name of the variable providing output rate frequency values, or null if the frequency is a fixed value.
     * @return variable name or null if no variable is used
     */
    public String getFrequencyVariable()
    {
        return frequencyVariable;
    }

    /**
     * Sets the variable name of the variable providing output rate frequency values, or null if the frequency is a fixed value.
     * @param frequencyVariable variable name or null if no variable is used
     */
    public void setFrequencyVariable(String frequencyVariable)
    {
        this.frequencyVariable = frequencyVariable;
    }

    /**
     * Returns the expression that controls output for use with the when-keyword.
     * @return expression should be boolean result
     */
    public Expression getWhenExpression()
    {
        return whenExpression;
    }

    /**
     * Returns the time period, or null if none provided.
     * @return time period
     */
    public TimePeriodExpression getTimePeriodExpression()
    {
        return timePeriodExpression;
    }

    /**
     * Returns the list of optional then-keyword variable assignments, if any
     * @return list of variable assignments or null if none
     */
    public List<Pair<String, Expression>> getThenAssignments()
    {
        return thenAssignments;
    }

    /**
     * Adds a then-keyword variable assigment for use with the when-keyword.
     * @param variableName to set
     * @param assignmentExpression expression to calculate new value
     * @return clause
     */
    public OutputLimitClause addThenAssignment(String variableName, Expression assignmentExpression)
    {
        thenAssignments.add(new Pair<String, Expression>(variableName, assignmentExpression));
        return this;
    }

    /**
     * Returns the crontab parameters, or null if not using crontab-like schedule.
     * @return parameters
     */
    public Expression[] getCrontabAtParameters()
    {
        return crontabAtParameters;
    }

    /**
     * Renders the clause in textual representation.
     * @param writer to output to
     */
    public void toEPL(StringWriter writer)
    {
        if (selector != OutputLimitSelector.DEFAULT)
        {
            writer.write(selector.getText());
            writer.write(" ");
        }
        if (unit == OutputLimitUnit.WHEN_EXPRESSION)
        {
            writer.write("when ");
            whenExpression.toEPL(writer);

            if ((thenAssignments != null) && (thenAssignments.size() > 0))
            {
                writer.write(" then set ");

                String delimiter = "";
                for (Pair<String, Expression> pair : thenAssignments)
                {
                    writer.write(delimiter);
                    writer.write(pair.getFirst());
                    writer.write(" = ");
                    pair.getSecond().toEPL(writer);
                    delimiter = ", ";
                }
            }
        }
        else if (unit == OutputLimitUnit.CRONTAB_EXPRESSION)
        {
            writer.write("at (");
            String delimiter = "";
            for (int i = 0; i < crontabAtParameters.length; i++)
            {
                writer.write(delimiter);
                crontabAtParameters[i].toEPL(writer);
                delimiter = ", ";
            }
            writer.write(")");
        }
        else if (unit == OutputLimitUnit.TIME_PERIOD)
        {
            writer.write("every ");
            timePeriodExpression.toEPL(writer);
        }
        else
        {
            writer.write("every ");
            if (frequencyVariable == null)
            {
                writer.write(Integer.toString(frequency.intValue()));
            }
            else
            {
                writer.write(frequencyVariable);
            }
            writer.write(" events");
        }
    }
}
