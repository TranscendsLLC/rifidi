/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.core;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.FragmentEventType;
import com.espertech.esper.epl.expression.*;
import com.espertech.esper.epl.spec.InsertIntoDesc;
import com.espertech.esper.epl.spec.SelectClauseExprCompiledSpec;
import com.espertech.esper.event.*;
import com.espertech.esper.event.vaevent.ValueAddEventProcessor;
import com.espertech.esper.event.vaevent.ValueAddEventService;
import com.espertech.esper.util.ExecutionPathDebugLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Processor for select-clause expressions that handles a list of selection items represented by
 * expression nodes. Computes results based on matching events.
 */
public class SelectExprEvalProcessor implements SelectExprProcessor
{
	private static final Log log = LogFactory.getLog(SelectExprEvalProcessor.class);

    private ExprEvaluator[] expressionNodes;
    private String[] columnNames;
    private EventType resultEventType;
    private EventType vaeInnerEventType;
    private final EventAdapterService eventAdapterService;
    private final boolean isUsingWildcard;
    private boolean singleStreamWrapper;
    private boolean singleColumnCoercion;
    private SelectExprJoinWildcardProcessor joinWildcardProcessor;
    private boolean isRevisionEvent;
    private ValueAddEventProcessor vaeProcessor;
    private boolean isEmptyExpressionNodes;

    /**
     * Ctor.
     * @param selectionList - list of select-clause items
     * @param insertIntoDesc - descriptor for insert-into clause contains column names overriding select clause names
     * @param isUsingWildcard - true if the wildcard (*) appears in the select clause
     * @param typeService -service for information about streams
     * @param eventAdapterService - service for generating events and handling event types
     * @param revisionService - service that handles update events
     * @param selectExprEventTypeRegistry - service for statement to type registry
     * @throws com.espertech.esper.epl.expression.ExprValidationException thrown if any of the expressions don't validate
     */
    public SelectExprEvalProcessor(List<SelectClauseExprCompiledSpec> selectionList,
                                   InsertIntoDesc insertIntoDesc,
                                   boolean isUsingWildcard,
                                   StreamTypeService typeService,
                                   EventAdapterService eventAdapterService,
                                   ValueAddEventService revisionService,
                                   SelectExprEventTypeRegistry selectExprEventTypeRegistry) throws ExprValidationException
    {
        this.eventAdapterService = eventAdapterService;
        this.isUsingWildcard = isUsingWildcard;

        if (selectionList.size() == 0 && !isUsingWildcard)
        {
            throw new IllegalArgumentException("Empty selection list not supported");
        }

        for (SelectClauseExprCompiledSpec entry : selectionList)
        {
            if (entry.getAssignedName() == null)
            {
                throw new IllegalArgumentException("Expected name for each expression has not been supplied");
            }
        }

        // Verify insert into clause
        if (insertIntoDesc != null)
        {
            verifyInsertInto(insertIntoDesc, selectionList);
        }

        // Build a subordinate wildcard processor for joins
        if(typeService.getStreamNames().length > 1 && isUsingWildcard)
        {
        	joinWildcardProcessor = new SelectExprJoinWildcardProcessor(typeService.getStreamNames(), typeService.getEventTypes(), eventAdapterService, null, selectExprEventTypeRegistry);
        }

        // Resolve underlying event type in the case of wildcard select
        EventType underlyingType = null;
        if(isUsingWildcard)
        {
        	if(joinWildcardProcessor != null)
        	{
        		underlyingType = joinWildcardProcessor.getResultEventType();
        	}
        	else
        	{
        		underlyingType = typeService.getEventTypes()[0];
        		if(underlyingType instanceof WrapperEventType)
        		{
        			singleStreamWrapper = true;
        		}
        	}
        }

        init(selectionList, insertIntoDesc, underlyingType, eventAdapterService, typeService, revisionService, selectExprEventTypeRegistry);
    }

    private void init(List<SelectClauseExprCompiledSpec> selectionList,
                      InsertIntoDesc insertIntoDesc,
                      EventType eventType,
                      EventAdapterService eventAdapterService,
                      StreamTypeService typeService,
                      ValueAddEventService valueAddEventService,
                      SelectExprEventTypeRegistry selectExprEventTypeRegistry)
        throws ExprValidationException
    {
        // Get expression nodes
        expressionNodes = new ExprEvaluator[selectionList.size()];
        Object[] expressionReturnTypes = new Object[selectionList.size()];
        for (int i = 0; i < selectionList.size(); i++)
        {
            ExprNode expr = selectionList.get(i).getSelectExpression();
            expressionNodes[i] = expr;
            expressionReturnTypes[i] = expr.getType();
        }

        // Get column names
        if ((insertIntoDesc != null) && (!insertIntoDesc.getColumnNames().isEmpty()))
        {
            columnNames = insertIntoDesc.getColumnNames().toArray(new String[insertIntoDesc.getColumnNames().size()]);
        }
        else
        {
            columnNames = new String[selectionList.size()];
            for (int i = 0; i < selectionList.size(); i++)
            {
                columnNames[i] = selectionList.get(i).getAssignedName();
            }
        }

        // Find if there is any fragments selected
        EventType targetType= null;
        if (insertIntoDesc != null)
        {
            targetType = eventAdapterService.getExistsTypeByName(insertIntoDesc.getEventTypeName());
        }

        // Find if there is any fragment event types:
        // This is a special case for fragments: select a, b from pattern [a=A -> b=B]
        // We'd like to maintain 'A' and 'B' EventType in the Map type, and 'a' and 'b' EventBeans in the event bean
        for (int i = 0; i < selectionList.size(); i++)
        {
            if (!(expressionNodes[i] instanceof ExprIdentNode))
            {
                continue;
            }

            ExprIdentNode identNode = (ExprIdentNode) expressionNodes[i];
            String propertyName = identNode.getResolvedPropertyName();
            final int streamNum = identNode.getStreamId();

            EventType eventTypeStream = typeService.getEventTypes()[streamNum];
            if (eventTypeStream instanceof NativeEventType)
            {
                continue;   // we do not transpose the native type for performance reasons
            }
            
            FragmentEventType fragmentType = eventTypeStream.getFragmentType(propertyName);
            if ((fragmentType == null) || (fragmentType.isNative()))
            {
                continue;   // we also ignore native Java classes as fragments for performance reasons
            }

            // may need to unwrap the fragment if the target type has this underlying type
            FragmentEventType targetFragment = null;
            if (targetType != null)
            {
                targetFragment = targetType.getFragmentType(columnNames[i]);
            }
            if ((targetType != null) &&
                (fragmentType.getFragmentType().getUnderlyingType() == expressionReturnTypes[i]) &&
                ((targetFragment == null) || (targetFragment != null && targetFragment.isNative())) )
            {
                ExprEvaluator evaluatorFragment;

                // A match was found, we replace the expression
                final EventPropertyGetter getter = eventTypeStream.getGetter(propertyName);
                evaluatorFragment = new ExprEvaluator() {
                    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData)
                    {
                        EventBean streamEvent = eventsPerStream[streamNum];
                        if (streamEvent == null)
                        {
                            return null;
                        }
                        return getter.get(streamEvent);
                    }
                };
                expressionNodes[i] = evaluatorFragment;
            }
            else
            {
                ExprEvaluator evaluatorFragment;
                final EventPropertyGetter getter =  eventTypeStream.getGetter(propertyName);

                // A match was found, we replace the expression
                evaluatorFragment = new ExprEvaluator() {

                    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData)
                    {
                        EventBean streamEvent = eventsPerStream[streamNum];
                        if (streamEvent == null)
                        {
                            return null;
                        }
                        return getter.getFragment(streamEvent);
                    }
                };

                expressionNodes[i] = evaluatorFragment;
                if (!fragmentType.isIndexed())
                {
                    expressionReturnTypes[i] = fragmentType.getFragmentType();
                }
                else
                {
                    expressionReturnTypes[i] = new EventType[] {fragmentType.getFragmentType()};
                }
            }
        }

        // Find if there is any stream expression (ExprStreamNode) :
        // This is a special case for stream selection: select a, b from A as a, B as b
        // We'd like to maintain 'A' and 'B' EventType in the Map type, and 'a' and 'b' EventBeans in the event bean
        for (int i = 0; i < selectionList.size(); i++)
        {
            if (!(expressionNodes[i] instanceof ExprStreamUnderlyingNode))
            {
                continue;
            }

            ExprStreamUnderlyingNode undNode = (ExprStreamUnderlyingNode) expressionNodes[i];
            final int streamNum = undNode.getStreamId();
            EventType eventTypeStream = typeService.getEventTypes()[streamNum];

            // A match was found, we replace the expression
            ExprEvaluator evaluator = new ExprEvaluator() {

                public Object evaluate(EventBean[] eventsPerStream, boolean isNewData)
                {
                    return eventsPerStream[streamNum];
                }
            };

            expressionNodes[i] = evaluator;
            expressionReturnTypes[i] = eventTypeStream;
        }

        // Build event type
        Map<String, Object> selPropertyTypes = new LinkedHashMap<String, Object>();
        for (int i = 0; i < expressionNodes.length; i++)
        {
            Object expressionReturnType = expressionReturnTypes[i];
            selPropertyTypes.put(columnNames[i], expressionReturnType);
        }

        // If we have an name for this type, add it
        if (insertIntoDesc != null)
        {
            try
            {
                vaeProcessor = valueAddEventService.getValueAddProcessor(insertIntoDesc.getEventTypeName());
                if (isUsingWildcard)
                {
                    if (vaeProcessor != null)
                    {
                        resultEventType = vaeProcessor.getValueAddEventType();
                        isRevisionEvent = true;
                        vaeProcessor.validateEventType(eventType);
                    }
                    else
                    {
                        resultEventType = eventAdapterService.addWrapperType(insertIntoDesc.getEventTypeName(), eventType, selPropertyTypes, false, true);
                    }
                }
                else
                {
                    resultEventType = null;
                    if ((columnNames.length == 1) && (insertIntoDesc.getColumnNames().size() == 0))
                    {
                        EventType existingType = eventAdapterService.getExistsTypeByName(insertIntoDesc.getEventTypeName());
                        if (existingType != null)
                        {
                            // check if the existing type and new type are compatible
                            Object columnOneType = expressionReturnTypes[0];
                            if (existingType instanceof WrapperEventType)
                            {
                                WrapperEventType wrapperType = (WrapperEventType) existingType;
                                // Map and Object both supported
                                if (wrapperType.getUnderlyingEventType().getUnderlyingType() == columnOneType)
                                {
                                    singleColumnCoercion = true;
                                    resultEventType = existingType;
                                }
                            }
                        }
                    }
                    if (resultEventType == null)
                    {
                        if (vaeProcessor != null)
                        {
                            resultEventType = eventAdapterService.createAnonymousMapType(selPropertyTypes);
                        }
                        else
                        {
                            resultEventType = eventAdapterService.addNestableMapType(insertIntoDesc.getEventTypeName(), selPropertyTypes, null, false, false, true);
                        }
                    }
                    if (vaeProcessor != null)
                    {
                        vaeProcessor.validateEventType(resultEventType);
                        vaeInnerEventType = resultEventType;
                        resultEventType = vaeProcessor.getValueAddEventType();
                        isRevisionEvent = true;
                    }
                }

                // add reference to the type obtained
                selectExprEventTypeRegistry.add(resultEventType);
            }
            catch (EventAdapterException ex)
            {
                throw new ExprValidationException(ex.getMessage());
            }
        }
        else
        {
            if (isUsingWildcard)
            {
        	    resultEventType = eventAdapterService.createAnonymousWrapperType(eventType, selPropertyTypes);
            }
            else
            {
                resultEventType = eventAdapterService.createAnonymousMapType(selPropertyTypes);
            }
        }

        isEmptyExpressionNodes = expressionNodes.length == 0;

        if (log.isDebugEnabled())
        {
            log.debug(".init resultEventType=" + resultEventType);
        }
    }

    public EventBean process(EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize)
    {
        // Evaluate all expressions and build a map of name-value pairs
        Map<String, Object> props;

        if (isEmptyExpressionNodes)
        {
            props = Collections.EMPTY_MAP;
        }
        else
        {
            props = new HashMap<String, Object>();
            for (int i = 0; i < expressionNodes.length; i++)
            {
                Object evalResult = expressionNodes[i].evaluate(eventsPerStream, isNewData);
                props.put(columnNames[i], evalResult);
            }
        }

        if(isUsingWildcard)
        {
        	// In case of a wildcard and single stream that is itself a
        	// wrapper bean, we also need to add the map properties
        	if(singleStreamWrapper)
        	{
        		DecoratingEventBean wrapper = (DecoratingEventBean)eventsPerStream[0];
        		if(wrapper != null)
        		{
        			Map<String, Object> map = wrapper.getDecoratingProperties();
                    if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
                    {
        			    log.debug(".process additional properties=" + map);
                    }

                    if ((isEmptyExpressionNodes) && (!map.isEmpty()))
                    {
                        props = new HashMap<String, Object>(map);
                    }
                    else
                    {
                        props.putAll(map);
                    }
                }
        	}

            EventBean event;
            if(joinWildcardProcessor != null)
            {
                event = joinWildcardProcessor.process(eventsPerStream, isNewData, isSynthesize);
            }
            else
            {
                event = eventsPerStream[0];
            }

            if (isRevisionEvent)
            {
                return vaeProcessor.getValueAddEventBean(event);
            }
            else
            {
                // Using a wrapper bean since we cannot use the same event type else same-type filters match.
                // Wrapping it even when not adding properties is very inexpensive.
                return eventAdapterService.adaptorForWrapper(event, props, resultEventType);
            }
        }
        else
        {
            if (singleColumnCoercion)
            {
                Object result = props.get(columnNames[0]);
                EventBean wrappedEvent;
                if (result instanceof Map)
                {
                    wrappedEvent = eventAdapterService.adaptorForTypedMap((Map)result, resultEventType);
                }
                else
                {
                    wrappedEvent = eventAdapterService.adapterForBean(result);
                }
                props.clear();
                if (!isRevisionEvent)
                {
                    return eventAdapterService.adaptorForWrapper(wrappedEvent, props, resultEventType);
                }
                else
                {
                    return vaeProcessor.getValueAddEventBean(eventAdapterService.adaptorForWrapper(wrappedEvent, props, vaeInnerEventType));
                }
            }
            else
            {
                if (!isRevisionEvent)
                {
                    return eventAdapterService.adaptorForTypedMap(props, resultEventType);
                }
                else
                {
                    return vaeProcessor.getValueAddEventBean(eventAdapterService.adaptorForTypedMap(props, vaeInnerEventType));
                }
            }
        }
    }

    public EventType getResultEventType()
    {
        return resultEventType;
    }

    private static void verifyInsertInto(InsertIntoDesc insertIntoDesc,
                                         List<SelectClauseExprCompiledSpec> selectionList)
        throws ExprValidationException
    {
        // Verify all column names are unique
        Set<String> names = new HashSet<String>();
        for (String element : insertIntoDesc.getColumnNames())
        {
            if (names.contains(element))
            {
                throw new ExprValidationException("Property name '" + element + "' appears more then once in insert-into clause");
            }
            names.add(element);
        }

        // Verify number of columns matches the select clause
        if ( (!insertIntoDesc.getColumnNames().isEmpty()) &&
             (insertIntoDesc.getColumnNames().size() != selectionList.size()) )
        {
            throw new ExprValidationException("Number of supplied values in the select clause does not match insert-into clause");
        }
    }
}
