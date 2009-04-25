/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.spec;

import com.espertech.esper.client.EventType;
import com.espertech.esper.collection.Pair;
import com.espertech.esper.core.StatementContext;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.StreamTypeServiceImpl;
import com.espertech.esper.epl.core.ViewResourceDelegate;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.epl.property.PropertyEvaluatorFactory;
import com.espertech.esper.epl.property.PropertyEvaluator;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.event.EventTypeSPI;
import com.espertech.esper.filter.FilterSpecCompiled;
import com.espertech.esper.filter.FilterSpecCompiler;
import com.espertech.esper.pattern.*;
import com.espertech.esper.pattern.guard.GuardFactory;
import com.espertech.esper.pattern.guard.GuardParameterException;
import com.espertech.esper.pattern.observer.ObserverFactory;
import com.espertech.esper.pattern.observer.ObserverParameterException;
import com.espertech.esper.schedule.TimeProvider;
import com.espertech.esper.util.UuidGenerator;

import java.util.*;

/**
 * Pattern specification in unvalidated, unoptimized form.
 */
public class PatternStreamSpecRaw extends StreamSpecBase implements StreamSpecRaw
{
    private final EvalNode evalNode;

    /**
     * Ctor.
     * @param evalNode - pattern evaluation node representing pattern statement
     * @param viewSpecs - specifies what view to use to derive data
     * @param optionalStreamName - stream name, or null if none supplied
     * @param streamSpecOptions - additional options, such as unidirectional stream in a join
     */
    public PatternStreamSpecRaw(EvalNode evalNode, List<ViewSpec> viewSpecs, String optionalStreamName, StreamSpecOptions streamSpecOptions)
    {
        super(optionalStreamName, viewSpecs, streamSpecOptions);
        this.evalNode = evalNode;
    }

    /**
     * Returns the pattern expression evaluation node for the top pattern operator.
     * @return parent pattern expression node
     */
    public EvalNode getEvalNode()
    {
        return evalNode;
    }

    public StreamSpecCompiled compile(StatementContext context,
                                      Set<String> eventTypeReferences)
            throws ExprValidationException
    {
        // Determine all the filter nodes used in the pattern
        EvalNodeAnalysisResult evalNodeAnalysisResult = EvalNode.recursiveAnalyzeChildNodes(evalNode);

        // Determine tags that are arrays of events, all under the "match" clause
        Set<String> matchUntilArrayTags = new HashSet<String>();
        for (EvalMatchUntilNode matchUntilNode : evalNodeAnalysisResult.getRepeatNodes())
        {
            Set<String> arrayTags = null;
            EvalNodeAnalysisResult matchUntilAnalysisResult = EvalNode.recursiveAnalyzeChildNodes(matchUntilNode.getChildNodes().get(0));
            for (EvalFilterNode filterNode : matchUntilAnalysisResult.getFilterNodes())
            {
                String optionalTag = filterNode.getEventAsName();
                if (optionalTag != null)
                {
                    if (arrayTags == null)
                    {
                        arrayTags = new HashSet<String>();
                    }
                    arrayTags.add(optionalTag);
                }
            }
            if (arrayTags != null)
            {
                matchUntilArrayTags.addAll(arrayTags);
            }
            matchUntilNode.setTagsArrayedSet(arrayTags);
        }

        // Resolve all event types; some filters are tagged and we keep the order in which they are specified
        LinkedHashMap<String, Pair<EventType, String>> taggedEventTypes = new LinkedHashMap<String, Pair<EventType, String>>();
        LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes = new LinkedHashMap<String, Pair<EventType, String>>();
        for (EvalNode activeNode : evalNodeAnalysisResult.getActiveNodes())
        {
            if (activeNode instanceof EvalFilterNode)
            {
                handleFilterNode((EvalFilterNode) activeNode, context, eventTypeReferences, matchUntilArrayTags, taggedEventTypes, arrayEventTypes);
            }

            if (activeNode instanceof EvalObserverNode)
            {
                handleObserverNode((EvalObserverNode) activeNode, context,
                                      taggedEventTypes,
                                      arrayEventTypes);
            }

            if (activeNode instanceof EvalGuardNode)
            {
                handleGuardNode((EvalGuardNode) activeNode, context,
                                      taggedEventTypes,
                                      arrayEventTypes);
            }
        }

        return new PatternStreamSpecCompiled(evalNode, taggedEventTypes, arrayEventTypes, this.getViewSpecs(), this.getOptionalStreamName(), this.getOptions());
    }

    private void handleGuardNode(EvalGuardNode guardNode, StatementContext context, LinkedHashMap<String, Pair<EventType, String>> taggedEventTypes, LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes)
            throws ExprValidationException
    {
        try
        {
            GuardFactory guardFactory = context.getPatternResolutionService().create(guardNode.getPatternGuardSpec());

            StreamTypeService streamTypeService = getStreamTypeService(context.getEngineURI(), context.getEventAdapterService(), taggedEventTypes, arrayEventTypes);
            List<ExprNode> validated = validateExpressions(guardNode.getPatternGuardSpec().getObjectParameters(),
                    streamTypeService, context.getMethodResolutionService(), null, context.getSchedulingService(), context.getVariableService());

            MatchedEventConvertor convertor = new MatchedEventConvertorImpl(taggedEventTypes, arrayEventTypes, context.getEventAdapterService());

            guardNode.setGuardFactory(guardFactory);
            guardFactory.setGuardParameters(validated, convertor);
        }
        catch (GuardParameterException e)
        {
            throw new ExprValidationException("Invalid parameter for pattern guard: " + e.getMessage(), e);
        }
        catch (PatternObjectException e)
        {
            throw new ExprValidationException("Failed to resolve pattern guard: " + e.getMessage(), e);
        }
    }

    private void handleObserverNode(EvalObserverNode observerNode, StatementContext context, LinkedHashMap<String, Pair<EventType, String>> taggedEventTypes, LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes)
            throws ExprValidationException
    {
        try
        {
            ObserverFactory observerFactory = context.getPatternResolutionService().create(observerNode.getPatternObserverSpec());

            StreamTypeService streamTypeService = getStreamTypeService(context.getEngineURI(), context.getEventAdapterService(), taggedEventTypes, arrayEventTypes);
            List<ExprNode> validated = validateExpressions(observerNode.getPatternObserverSpec().getObjectParameters(),
                    streamTypeService, context.getMethodResolutionService(), null, context.getSchedulingService(), context.getVariableService());

            MatchedEventConvertor convertor = new MatchedEventConvertorImpl(taggedEventTypes, arrayEventTypes, context.getEventAdapterService());

            observerNode.setObserverFactory(observerFactory);
            observerFactory.setObserverParameters(validated, convertor);
        }
        catch (ObserverParameterException e)
        {
            throw new ExprValidationException("Invalid parameter for pattern observer: " + e.getMessage(), e);
        }
        catch (PatternObjectException e)
        {
            throw new ExprValidationException("Failed to resolve pattern observer: " + e.getMessage(), e);
        }
    }

    private void handleFilterNode(EvalFilterNode filterNode,
                                  StatementContext context,
                                  Set<String> eventTypeReferences,
                                  Set<String> matchUntilArrayTags,
                                  LinkedHashMap<String, Pair<EventType, String>> taggedEventTypes,
                                  LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes)
            throws ExprValidationException
    {
        String eventName = filterNode.getRawFilterSpec().getEventTypeName();
        EventType resolvedEventType = FilterStreamSpecRaw.resolveType(context.getEngineURI(), eventName, context.getEventAdapterService(), context.getPlugInTypeResolutionURIs());
        EventType finalEventType = resolvedEventType;
        String optionalTag = filterNode.getEventAsName();
        boolean isPropertyEvaluation = false;

        // obtain property event type, if final event type is properties
        if (filterNode.getRawFilterSpec().getOptionalPropertyEvalSpec() != null)
        {
            PropertyEvaluator optionalPropertyEvaluator = PropertyEvaluatorFactory.makeEvaluator(filterNode.getRawFilterSpec().getOptionalPropertyEvalSpec(), resolvedEventType, filterNode.getEventAsName(), context.getEventAdapterService(), context.getMethodResolutionService(), context.getSchedulingService(), context.getVariableService(), context.getEngineURI());
            finalEventType = optionalPropertyEvaluator.getFragmentEventType();
            isPropertyEvaluation = true;
        }

        if (finalEventType instanceof EventTypeSPI)
        {
            eventTypeReferences.add(((EventTypeSPI) finalEventType).getMetadata().getPrimaryName());
        }

        // If a tag was supplied for the type, the tags must stay with this type, i.e. a=BeanA -> b=BeanA -> a=BeanB is a no
        if (optionalTag != null)
        {
            Pair<EventType, String> pair = taggedEventTypes.get(optionalTag);
            EventType existingType = null;
            if (pair != null)
            {
                existingType = pair.getFirst();
            }
            if (existingType == null)
            {
                pair = arrayEventTypes.get(optionalTag);
                if (pair != null)
                {
                    throw new ExprValidationException("Tag '" + optionalTag + "' for event '" + eventName +
                            "' used in the repeat-until operator cannot also appear in other filter expressions");
                }
            }
            if ((existingType != null) && (existingType != finalEventType))
            {
                throw new ExprValidationException("Tag '" + optionalTag + "' for event '" + eventName +
                        "' has already been declared for events of type " + existingType.getUnderlyingType().getName());
            }
            pair = new Pair<EventType, String>(finalEventType, eventName);

            // add tagged type
            if (matchUntilArrayTags.contains(optionalTag) || isPropertyEvaluation)
            {
                arrayEventTypes.put(optionalTag, pair);
            }
            else
            {
                taggedEventTypes.put(optionalTag, pair);
            }
        }

        // For this filter, filter types are all known tags at this time,
        // and additionally stream 0 (self) is our event type.
        // Stream type service allows resolution by property name event if that name appears in other tags.
        // by defaulting to stream zero.
        // Stream zero is always the current event type, all others follow the order of the map (stream 1 to N).
        String selfStreamName = optionalTag;
        if (selfStreamName == null)
        {
            selfStreamName = "s_" + UuidGenerator.generate();
        }
        LinkedHashMap<String, Pair<EventType, String>> filterTypes = new LinkedHashMap<String, Pair<EventType, String>>();
        Pair<EventType, String> typePair = new Pair<EventType, String>(finalEventType, eventName);
        filterTypes.put(selfStreamName, typePair);
        filterTypes.putAll(taggedEventTypes);

        // for the filter, specify all tags used
        LinkedHashMap<String, Pair<EventType, String>> filterTaggedEventTypes = new LinkedHashMap<String, Pair<EventType, String>>(taggedEventTypes);
        filterTaggedEventTypes.remove(optionalTag);

        // handle array tags (match-until clause)
        LinkedHashMap<String, Pair<EventType, String>> arrayCompositeEventTypes = null;
        if (arrayEventTypes != null)
        {
            arrayCompositeEventTypes = new LinkedHashMap<String, Pair<EventType, String>>();
            EventType arrayTagCompositeEventType = context.getEventAdapterService().createSemiAnonymousMapType(new HashMap(), arrayEventTypes, false);
            for (Map.Entry<String, Pair<EventType, String>> entry : arrayEventTypes.entrySet())
            {
                String tag = entry.getKey();
                if (!filterTypes.containsKey(tag))
                {
                    Pair<EventType, String> pair = new Pair<EventType, String>(arrayTagCompositeEventType, tag);
                    filterTypes.put(tag, pair);
                    arrayCompositeEventTypes.put(tag, pair);
                }
            }
        }

        StreamTypeService streamTypeService = new StreamTypeServiceImpl(filterTypes, context.getEngineURI(), true, false);
        List<ExprNode> exprNodes = filterNode.getRawFilterSpec().getFilterExpressions();

        FilterSpecCompiled spec = FilterSpecCompiler.makeFilterSpec(resolvedEventType, eventName, exprNodes, filterNode.getRawFilterSpec().getOptionalPropertyEvalSpec(),  filterTaggedEventTypes, arrayCompositeEventTypes, streamTypeService, context.getMethodResolutionService(), context.getSchedulingService(), context.getVariableService(), context.getEventAdapterService(), context.getEngineURI(), null);
        filterNode.setFilterSpec(spec);
    }

    private List<ExprNode> validateExpressions(List<ExprNode> objectParameters, StreamTypeService streamTypeService,
                         MethodResolutionService methodResolutionService,
                         ViewResourceDelegate viewResourceDelegate,
                         TimeProvider timeProvider,
                         VariableService variableService)
            throws ExprValidationException
    {
        if (objectParameters == null)
        {
            return objectParameters;
        }
        List<ExprNode> validated = new ArrayList<ExprNode>();
        for (ExprNode node : objectParameters)
        {
            validated.add(node.getValidatedSubtree(streamTypeService, methodResolutionService, viewResourceDelegate, timeProvider, variableService));
        }
        return validated;
    }

    private StreamTypeService getStreamTypeService(String engineURI, EventAdapterService eventAdapterService, LinkedHashMap<String, Pair<EventType, String>> taggedEventTypes, LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes)
    {
        LinkedHashMap<String, Pair<EventType, String>> filterTypes = new LinkedHashMap<String, Pair<EventType, String>>();
        filterTypes.putAll(taggedEventTypes);

        // handle array tags (match-until clause)
        if (arrayEventTypes != null)
        {
            EventType arrayTagCompositeEventType = eventAdapterService.createSemiAnonymousMapType(new HashMap(), arrayEventTypes, false);
            for (Map.Entry<String, Pair<EventType, String>> entry : arrayEventTypes.entrySet())
            {
                String tag = entry.getKey();
                if (!filterTypes.containsKey(tag))
                {
                    Pair<EventType, String> pair = new Pair<EventType, String>(arrayTagCompositeEventType, tag);
                    filterTypes.put(tag, pair);
                }
            }
        }

        return new StreamTypeServiceImpl(filterTypes, engineURI, true, false);
    }
}
