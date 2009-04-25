/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.variable;

import com.espertech.esper.epl.spec.OnTriggerSetAssignment;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.util.JavaClassHelper;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A convenience class for dealing with reading and updating multiple variable values.
 */
public class VariableReadWritePackage
{
    private static final Log log = LogFactory.getLog(VariableReadWritePackage.class);

    private final List<OnTriggerSetAssignment> assignments;
    private final VariableReader[] readers;
    private final boolean[] mustCoerce;
    private final Map<String, Object> variableTypes;

    /**
     * Ctor.
     * @param assignments the list of variable assignments
     * @param variableService variable service
     * @throws ExprValidationException when variables cannot be found
     */
    public VariableReadWritePackage(List<OnTriggerSetAssignment> assignments, VariableService variableService)
            throws ExprValidationException
    {
        this.assignments = assignments;
        this.readers = new VariableReader[assignments.size()];
        this.mustCoerce = new boolean[assignments.size()];
        this.variableTypes = new HashMap<String, Object>();

        int count = 0;
        for (OnTriggerSetAssignment assignment : assignments)
        {
            String variableName = assignment.getVariableName();
            readers[count] = variableService.getReader(variableName);
            if (readers[count] == null)
            {
                throw new ExprValidationException("Variable by name '" + variableName + "' has not been created or configured");
            }

            // determine types
            Class variableType = readers[count].getType();
            Class expressionType = assignment.getExpression().getType();
            variableTypes.put(variableName, variableType);

            // determine if the expression type can be assigned
            if ((JavaClassHelper.getBoxedType(expressionType) != variableType) &&
                (expressionType != null))
            {
                if ((!JavaClassHelper.isNumeric(variableType)) ||
                    (!JavaClassHelper.isNumeric(expressionType)))
                {
                    throw new ExprValidationException("Variable '" + variableName
                        + "' of declared type '" + variableType.getName() +
                            "' cannot be assigned a value of type '" + expressionType.getName() + "'");
                }

                if (!(JavaClassHelper.canCoerce(expressionType, variableType)))
                {
                    throw new ExprValidationException("Variable '" + variableName
                        + "' of declared type '" + variableType.getName() +
                            "' cannot be assigned a value of type '" + expressionType.getName() + "'");
                }

                mustCoerce[count] = true;
            }
            count++;
        }
    }

    /**
     * Write new variable values and commit, evaluating assignment expressions using the given
     * events per stream.
     * <p>
     * Populates an optional map of new values if a non-null map is passed.
     * @param variableService variable service
     * @param eventsPerStream events per stream
     * @param valuesWritten null or an empty map to populate with written values
     */
    public void writeVariables(VariableService variableService,
                                 EventBean[] eventsPerStream,
                                 Map<String, Object> valuesWritten)
    {
        // We obtain a write lock global to the variable space
        // Since expressions can contain variables themselves, these need to be unchangeable for the duration
        // as there could be multiple statements that do "var1 = var1 + 1".
        variableService.getReadWriteLock().writeLock().lock();
        try
        {
            variableService.setLocalVersion();

            int count = 0;
            for (OnTriggerSetAssignment assignment : assignments)
            {
                VariableReader reader = readers[count];
                Object value = assignment.getExpression().evaluate(eventsPerStream, true);
                if ((value != null) && (mustCoerce[count]))
                {
                    value = JavaClassHelper.coerceBoxed((Number) value, reader.getType());
                }

                variableService.write(reader.getVariableNumber(), value);
                count++;

                if (valuesWritten != null)
                {
                    valuesWritten.put(assignment.getVariableName(), value);
                }
            }

            variableService.commit();
        }
        catch (RuntimeException ex)
        {
            log.error("Error evaluating on-set variable expressions: " + ex.getMessage(), ex);
            variableService.rollback();
        }
        finally
        {
            variableService.getReadWriteLock().writeLock().unlock();
        }
    }

    /**
     * Returns the readers to all variables.
     * @return readers
     */
    public VariableReader[] getReaders()
    {
        return readers;
    }

    /**
     * Returns a map of variable names and type of variable.
     * @return variables
     */
    public Map<String, Object> getVariableTypes()
    {
        return variableTypes;
    }
}
