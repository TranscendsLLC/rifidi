/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.core;

import com.espertech.esper.core.StatementResultService;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.spec.*;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.event.vaevent.ValueAddEventService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Factory for select expression processors.
 */
public class SelectExprProcessorFactory
{
    private static final Log log = LogFactory.getLog(SelectExprProcessorFactory.class);

    /**
     * Returns the processor to use for a given select-clause.
     * @param selectionList - the list of select clause elements/items, which are expected to have been validated
     * @param isUsingWildcard - true if the wildcard (*) occurs in the select clause
     * @param insertIntoDesc - contains column names for the optional insert-into clause (if supplied)
     * @param typeService - serves stream type information
     * @param eventAdapterService - for generating wrapper instances for events
     * @param statementResultService handles listeners/subscriptions awareness to reduce output result generation
     * @param valueAddEventService - service that handles update events and variant events
     * @param selectExprEventTypeRegistry - registry for event type to statements
     * @return select-clause expression processor
     * @throws ExprValidationException to indicate the select expression cannot be validated
     */
    public static SelectExprProcessor getProcessor(List<SelectClauseElementCompiled> selectionList,
                                                   boolean isUsingWildcard,
                                                   InsertIntoDesc insertIntoDesc,
                                                   StreamTypeService typeService, 
                                                   EventAdapterService eventAdapterService,
                                                   StatementResultService statementResultService,
                                                   ValueAddEventService valueAddEventService,
                                                   SelectExprEventTypeRegistry selectExprEventTypeRegistry)
        throws ExprValidationException
    {
        SelectExprProcessor synthetic = getProcessorInternal(selectionList, isUsingWildcard, insertIntoDesc, typeService, eventAdapterService, valueAddEventService, selectExprEventTypeRegistry);

        // Handle binding as an optional service
        if (statementResultService != null)
        {
            BindProcessor bindProcessor = new BindProcessor(selectionList, typeService.getEventTypes(), typeService.getStreamNames());
            statementResultService.setSelectClause(bindProcessor.getExpressionTypes(), bindProcessor.getColumnNamesAssigned());
            return new SelectExprResultProcessor(statementResultService, synthetic, bindProcessor);
        }

        return synthetic;        
    }

    private static SelectExprProcessor getProcessorInternal(
                                                   List<SelectClauseElementCompiled> selectionList,
                                                   boolean isUsingWildcard,
                                                   InsertIntoDesc insertIntoDesc,
                                                   StreamTypeService typeService,
                                                   EventAdapterService eventAdapterService,
                                                   ValueAddEventService valueAddEventService,
                                                   SelectExprEventTypeRegistry selectExprEventTypeRegistry)
        throws ExprValidationException
    {
        // Wildcard not allowed when insert into specifies column order
    	if(isUsingWildcard && insertIntoDesc != null && !insertIntoDesc.getColumnNames().isEmpty())
    	{
    		throw new ExprValidationException("Wildcard not allowed when insert-into specifies column order");
    	}

        // Determine wildcard processor (select *)
        if (isWildcardsOnly(selectionList))
        {
            // For joins
            if (typeService.getStreamNames().length > 1)
            {
                log.debug(".getProcessor Using SelectExprJoinWildcardProcessor");
                return new SelectExprJoinWildcardProcessor(typeService.getStreamNames(), typeService.getEventTypes(), eventAdapterService, insertIntoDesc, selectExprEventTypeRegistry);
            }
            // Single-table selects with no insert-into
            // don't need extra processing
            else if (insertIntoDesc == null)
            {
            	log.debug(".getProcessor Using wildcard processor");
                return new SelectExprWildcardProcessor(typeService.getEventTypes()[0]);
            }
        }

        // Verify the assigned or name used is unique
        verifyNameUniqueness(selectionList);

        // Construct processor
        log.debug(".getProcessor Using SelectExprEvalProcessor");
        List<SelectClauseExprCompiledSpec> expressionList = getExpressions(selectionList);
        List<SelectClauseStreamCompiledSpec> streamWildcards = getStreamWildcards(selectionList);
        if (streamWildcards.size() == 0)
        {
            // This one only deals with wildcards and expressions in the selection
            return new SelectExprEvalProcessor(expressionList, insertIntoDesc, isUsingWildcard, typeService, eventAdapterService, valueAddEventService, selectExprEventTypeRegistry);
        }
        else
        {
            // This one also deals with stream selectors (e.g. select *, p1, s0.* from S0 as s0)
            return new SelectExprEvalProcessorStreams(expressionList, streamWildcards, insertIntoDesc, isUsingWildcard, typeService, eventAdapterService, selectExprEventTypeRegistry);
        }
    }

    /**
     * Verify that each given name occurs exactly one.
     * @param selectionList is the list of select items to verify names
     * @throws com.espertech.esper.epl.expression.ExprValidationException thrown if a name occured more then once
     */
    protected static void verifyNameUniqueness(List<SelectClauseElementCompiled> selectionList) throws ExprValidationException
    {
        Set<String> names = new HashSet<String>();
        for (SelectClauseElementCompiled element : selectionList)
        {
            if (element instanceof SelectClauseExprCompiledSpec)
            {
                SelectClauseExprCompiledSpec expr = (SelectClauseExprCompiledSpec) element;
                if (names.contains(expr.getAssignedName()))
                {
                    throw new ExprValidationException("Column name '" + expr.getAssignedName() + "' appears more then once in select clause");
                }
                names.add(expr.getAssignedName());
            }
            else if (element instanceof SelectClauseStreamCompiledSpec)
            {
                SelectClauseStreamCompiledSpec stream = (SelectClauseStreamCompiledSpec) element;
                if (stream.getOptionalName() == null)
                {
                    continue; // ignore no-name stream selectors
                }
                if (names.contains(stream.getOptionalName()))
                {
                    throw new ExprValidationException("Column name '" + stream.getOptionalName() + "' appears more then once in select clause");
                }
                names.add(stream.getOptionalName());
            }
        }
    }

    private static boolean isWildcardsOnly(List<SelectClauseElementCompiled> elements)
    {
        for (SelectClauseElementCompiled element : elements)
        {
            if (!(element instanceof SelectClauseElementWildcard))
            {
                return false;
            }
        }
        return true;
    }

    private static List<SelectClauseExprCompiledSpec> getExpressions(List<SelectClauseElementCompiled> elements)
    {
        List<SelectClauseExprCompiledSpec> result = new ArrayList<SelectClauseExprCompiledSpec>();
        for (SelectClauseElementCompiled element : elements)
        {
            if (element instanceof SelectClauseExprCompiledSpec)
            {
                result.add((SelectClauseExprCompiledSpec)element);
            }
        }
        return result;
    }

    private static List<SelectClauseStreamCompiledSpec> getStreamWildcards(List<SelectClauseElementCompiled> elements)
    {
        List<SelectClauseStreamCompiledSpec> result = new ArrayList<SelectClauseStreamCompiledSpec>();
        for (SelectClauseElementCompiled element : elements)
        {
            if (element instanceof SelectClauseStreamCompiledSpec)
            {
                result.add((SelectClauseStreamCompiledSpec)element);
            }
        }
        return result;
    }
}
