/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.filter;

import com.espertech.esper.client.EventType;
import com.espertech.esper.collection.Pair;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.expression.*;
import com.espertech.esper.epl.property.PropertyEvaluator;
import com.espertech.esper.epl.property.PropertyEvaluatorFactory;
import com.espertech.esper.epl.spec.PropertyEvalSpec;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.event.property.NestedProperty;
import com.espertech.esper.event.property.Property;
import com.espertech.esper.event.property.PropertyParser;
import com.espertech.esper.event.property.IndexedProperty;
import com.espertech.esper.schedule.TimeProvider;
import com.espertech.esper.type.RelationalOpEnum;
import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.util.SimpleNumberCoercer;
import com.espertech.esper.util.SimpleNumberCoercerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.io.StringWriter;

/**
 * Helper to compile (validate and optimize) filter expressions as used in pattern and filter-based streams.
 */
public final class FilterSpecCompiler
{
    private static final Log log = LogFactory.getLog(FilterSpecCompiler.class);

    /**
     * Assigned for filter parameters that are based on boolean expression and not on
     * any particular property name.
     * <p>
     * Keeping this artificial property name is a simplification as optimized filter parameters
     * generally keep a property name.
     */
    public final static String PROPERTY_NAME_BOOLEAN_EXPRESSION = ".boolean_expression";

    /**
     * Factory method for compiling filter expressions into a filter specification
     * for use with filter service.
     * @param eventType is the filtered-out event type
     * @param eventTypeName is the name of the event type
     * @param filterExpessions is a list of filter expressions
     * @param taggedEventTypes is a map of stream names (tags) and event types available
     * @param arrayEventTypes is a map of name tags and event type per tag for repeat-expressions that generate an array of events
     * @param streamTypeService is used to set rules for resolving properties
     * @param methodResolutionService resolved imports for static methods and such
     * @param timeProvider - provides engine current time
     * @param variableService - provides access to variables
     * @param eventAdapterService - for event type and bean generation
     * @param optionalStreamName - the stream name, if provided
     * @param engineURI - the engine uri
     * @param optionalPropertyEvalSpec - specification for evaluating properties
     * @return compiled filter specification
     * @throws ExprValidationException if the expression or type validations failed
     */
    public static FilterSpecCompiled makeFilterSpec(EventType eventType,
                                                    String eventTypeName,
                                                    List<ExprNode> filterExpessions,
                                                    PropertyEvalSpec optionalPropertyEvalSpec,
                                                    LinkedHashMap<String, Pair<EventType, String>> taggedEventTypes,
                                                    LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes,
                                                    StreamTypeService streamTypeService,
                                                    MethodResolutionService methodResolutionService,
                                                    TimeProvider timeProvider,
                                                    VariableService variableService,
                                                    EventAdapterService eventAdapterService,
                                                    String engineURI,
                                                    String optionalStreamName)
            throws ExprValidationException
    {
        // Validate all nodes, make sure each returns a boolean and types are good;
        // Also decompose all AND super nodes into individual expressions
        List<ExprNode> constituents = FilterSpecCompiler.validateAndDecompose(filterExpessions, streamTypeService, methodResolutionService, timeProvider, variableService);

        // From the constituents make a filter specification
        FilterParamExprMap filterParamExprMap = new FilterParamExprMap();

        // Make filter parameter for each expression node, if it can be optimized
        for (ExprNode constituent : constituents)
        {
            FilterSpecParam param = makeFilterParam(constituent, taggedEventTypes, arrayEventTypes);
            filterParamExprMap.put(constituent, param); // accepts null values as the expression may not be optimized
        }

        // Consolidate entries as possible, i.e. (a != 5 and a != 6) is (a not in (5,6))
        // Removes duplicates for same property and same filter operator for filter service index optimizations
        consolidate(filterParamExprMap);

        // Use all filter parameter and unassigned expressions
        List<FilterSpecParam> filterParams = new ArrayList<FilterSpecParam>();
        filterParams.addAll(filterParamExprMap.getFilterParams());
        List<ExprNode> remainingExprNodes = filterParamExprMap.getUnassignedExpressions();

        // any unoptimized expression nodes are put under one AND
        ExprNode exprNode = null;
        if (!remainingExprNodes.isEmpty())
        {
            if (remainingExprNodes.size() == 1)
            {
                exprNode = remainingExprNodes.get(0);
            }
            else
            {
                ExprAndNode andNode = new ExprAndNode();
                for (ExprNode unoptimized : remainingExprNodes)
                {
                    andNode.addChildNode(unoptimized);
                }
                exprNode = andNode;
            }
        }

        // if there are boolean expressions, add
        if (exprNode != null)
        {
            FilterSpecParamExprNode param = new FilterSpecParamExprNode(PROPERTY_NAME_BOOLEAN_EXPRESSION, FilterOperator.BOOLEAN_EXPRESSION, exprNode, taggedEventTypes, arrayEventTypes, variableService, eventAdapterService);
            filterParams.add(param);
        }

        PropertyEvaluator optionalPropertyEvaluator = null;
        if (optionalPropertyEvalSpec != null)
        {
            optionalPropertyEvaluator = PropertyEvaluatorFactory.makeEvaluator(optionalPropertyEvalSpec, eventType, optionalStreamName, eventAdapterService, methodResolutionService, timeProvider, variableService, engineURI);
        }

        FilterSpecCompiled spec = new FilterSpecCompiled(eventType, eventTypeName, filterParams, optionalPropertyEvaluator);

        if (log.isDebugEnabled())
        {
            log.debug(".makeFilterSpec spec=" + spec);
        }

        return spec;
    }

    // remove duplicate propertyName + filterOperator items making a judgement to optimize or simply remove the optimized form
    private static void consolidate(List<FilterSpecParam> items, FilterParamExprMap filterParamExprMap)
    {
        FilterOperator op = items.get(0).getFilterOperator();
        if (op == FilterOperator.NOT_EQUAL)
        {
            handleConsolidateNotEqual(items, filterParamExprMap);
        }
        else
        {
            // for all others we simple remove the second optimized form (filter param with same prop name and filter op)
            // and thus the boolean expression that started this is included
            for (int i = 1; i < items.size(); i++)
            {
                filterParamExprMap.removeValue(items.get(i));
            }
        }
    }

    /**
     * Validates expression nodes and returns a list of validated nodes.
     * @param exprNodes is the nodes to validate
     * @param streamTypeService is provding type information for each stream
     * @param methodResolutionService for resolving functions
     * @param timeProvider for providing current time
     * @param variableService provides access to variables
     * @return list of validated expression nodes
     * @throws ExprValidationException for validation errors
     */
    public static List<ExprNode> validateDisallowSubquery(List<ExprNode> exprNodes, StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, TimeProvider timeProvider, VariableService variableService)
            throws ExprValidationException
    {
        List<ExprNode> validatedNodes = new ArrayList<ExprNode>();

        for (ExprNode node : exprNodes)
        {
            // Ensure there is no subselects
            ExprNodeSubselectVisitor visitor = new ExprNodeSubselectVisitor();
            node.accept(visitor);
            if (visitor.getSubselects().size() > 0)
            {
                throw new ExprValidationException("Subselects not allowed within filters");
            }

            ExprNode validated = node.getValidatedSubtree(streamTypeService, methodResolutionService, null, timeProvider, variableService);
            validatedNodes.add(validated);

            if ((validated.getType() != Boolean.class) && ((validated.getType() != boolean.class)))
            {
                throw new ExprValidationException("Filter expression not returning a boolean value: '" + validated.toExpressionString() + "'");
            }
        }

        return validatedNodes;
    }

    private static List<ExprNode> validateAndDecompose(List<ExprNode> exprNodes, StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, TimeProvider timeProvider, VariableService variableService)
            throws ExprValidationException
    {
        List<ExprNode> validatedNodes = validateDisallowSubquery(exprNodes, streamTypeService, methodResolutionService, timeProvider, variableService);

        // Break a top-level AND into constituent expression nodes
        List<ExprNode> constituents = new ArrayList<ExprNode>();
        for (ExprNode validated : validatedNodes)
        {
            if (validated instanceof ExprAndNode)
            {
                recursiveAndConstituents(constituents, validated);
            }
            else
            {
                constituents.add(validated);
            }

            // Ensure there is no aggregation nodes
            List<ExprAggregateNode> aggregateExprNodes = new LinkedList<ExprAggregateNode>();
            ExprAggregateNode.getAggregatesBottomUp(validated, aggregateExprNodes);
            if (!aggregateExprNodes.isEmpty())
            {
                throw new ExprValidationException("Aggregation functions not allowed within filters");
            }
        }

        return constituents;
    }

    private static void consolidate(FilterParamExprMap filterParamExprMap)
    {
        // consolidate or place in a boolean expression (by removing filter spec param from the map)
        // any filter parameter that feature the same property name and filter operator,
        // i.e. we are looking for "a!=5 and a!=6"  to transform to "a not in (5,6)" which can match faster
        // considering that "a not in (5,6) and a not in (7,8)" is "a not in (5, 6, 7, 8)" therefore
        // we need to consolidate until there is no more work to do
        Map<Pair<String, FilterOperator>, List<FilterSpecParam>> mapOfParams =
                new HashMap<Pair<String, FilterOperator>, List<FilterSpecParam>>();

        boolean haveConsolidated;
        do
        {
            haveConsolidated = false;
            mapOfParams.clear();

            // sort into buckets of propertyName + filterOperator combination
            for (FilterSpecParam currentParam : filterParamExprMap.getFilterParams())
            {
                String propName = currentParam.getPropertyName();
                FilterOperator op = currentParam.getFilterOperator();
                Pair<String, FilterOperator> key = new Pair<String, FilterOperator>(propName, op);

                List<FilterSpecParam> existingParam = mapOfParams.get(key);
                if (existingParam == null)
                {
                    existingParam = new ArrayList<FilterSpecParam>();
                    mapOfParams.put(key, existingParam);
                }
                existingParam.add(currentParam);
            }

            for (List<FilterSpecParam> entry : mapOfParams.values())
            {
                if (entry.size() > 1)
                {
                    haveConsolidated = true;
                    consolidate(entry, filterParamExprMap);
                }
            }
        }
        while(haveConsolidated);
    }

    // consolidate "val != 3 and val != 4 and val != 5"
    // to "val not in (3, 4, 5)"
    private static void handleConsolidateNotEqual(List<FilterSpecParam> params, FilterParamExprMap filterParamExprMap)
    {
        List<FilterSpecParamInValue> values = new ArrayList<FilterSpecParamInValue>();

        ExprNode lastNotEqualsExprNode = null;
        for (FilterSpecParam param : params)
        {
            if (param instanceof FilterSpecParamConstant)
            {
                FilterSpecParamConstant constantParam = (FilterSpecParamConstant) param;
                Object constant = constantParam.getFilterConstant();
                values.add(new InSetOfValuesConstant(constant));
            }
            else if (param instanceof FilterSpecParamEventProp)
            {
                FilterSpecParamEventProp eventProp = (FilterSpecParamEventProp) param;
                values.add(new InSetOfValuesEventProp(eventProp.getResultEventAsName(), eventProp.getResultEventProperty(),
                        eventProp.isMustCoerce(), JavaClassHelper.getBoxedType(eventProp.getCoercionType())));
            }
            else if (param instanceof FilterSpecParamEventPropIndexed)
            {
                FilterSpecParamEventPropIndexed eventProp = (FilterSpecParamEventPropIndexed) param;
                values.add(new InSetOfValuesEventPropIndexed(eventProp.getResultEventAsName(), eventProp.getResultEventIndex(), eventProp.getResultEventProperty(),
                        eventProp.isMustCoerce(), JavaClassHelper.getBoxedType(eventProp.getCoercionType())));
            }
            else
            {
                throw new IllegalArgumentException("Unknown filter parameter:" + param.toString());
            }

            lastNotEqualsExprNode = filterParamExprMap.removeEntry(param);
        }

        FilterSpecParamIn param = new FilterSpecParamIn(params.get(0).getPropertyName(),
                FilterOperator.NOT_IN_LIST_OF_VALUES, values);
        filterParamExprMap.put(lastNotEqualsExprNode, param);
    }

    /**
     * For a given expression determine if this is optimizable and create the filter parameter
     * representing the expression, or null if not optimizable.
     * @param constituent is the expression to look at
     * @param taggedEventTypes event types and their tags
     * @param arrayEventTypes @return filter parameter representing the expression, or null
     * @throws ExprValidationException if the expression is invalid
     * @return FilterSpecParam filter param
     */
    protected static FilterSpecParam makeFilterParam(ExprNode constituent, LinkedHashMap<String, Pair<EventType, String>> taggedEventTypes, LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes)
            throws ExprValidationException
    {
        // Is this expresson node a simple compare, i.e. a=5 or b<4; these can be indexed
        if ((constituent instanceof ExprEqualsNode) ||
            (constituent instanceof ExprRelationalOpNode))
        {
            FilterSpecParam param = handleEqualsAndRelOp(constituent, arrayEventTypes);
            if (param != null)
            {
                return param;
            }
        }

        // Is this expresson node a simple compare, i.e. a=5 or b<4; these can be indexed
        if (constituent instanceof ExprInNode)
        {
            FilterSpecParam param = handleInSetNode((ExprInNode)constituent, arrayEventTypes);
            if (param != null)
            {
                return param;
            }
        }

        if (constituent instanceof ExprBetweenNode)
        {
            FilterSpecParam param = handleRangeNode((ExprBetweenNode)constituent, arrayEventTypes);
            if (param != null)
            {
                return param;
            }
        }

        return null;
    }

    private static FilterSpecParam handleRangeNode(ExprBetweenNode betweenNode, LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes)
    {
        ExprNode left = betweenNode.getChildNodes().get(0);
        if (left instanceof ExprIdentNode)
        {
            ExprIdentNode identNode = (ExprIdentNode) left;
            String propertyName = identNode.getResolvedPropertyName();
            FilterOperator op = FilterOperator.parseRangeOperator(betweenNode.isLowEndpointIncluded(), betweenNode.isHighEndpointIncluded(),
                    betweenNode.isNotBetween());

            FilterSpecParamRangeValue low = handleRangeNodeEndpoint(betweenNode.getChildNodes().get(1), arrayEventTypes);
            FilterSpecParamRangeValue high = handleRangeNodeEndpoint(betweenNode.getChildNodes().get(2), arrayEventTypes);

            if ((low != null) && (high != null))
            {
                return new FilterSpecParamRange(propertyName, op, low, high);
            }
        }
        return null;
    }

    private static FilterSpecParamRangeValue handleRangeNodeEndpoint(ExprNode endpoint, LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes)
    {
        // constant
        if (endpoint instanceof ExprConstantNode)
        {
            ExprConstantNode node = (ExprConstantNode) endpoint;
            Number result = (Number) node.evaluate(null, true);
            return new RangeValueDouble(result.doubleValue());
        }

        // or property
        if (endpoint instanceof ExprIdentNode)
        {
            ExprIdentNode identNodeInner = (ExprIdentNode) endpoint;
            if (identNodeInner.getStreamId() == 0)
            {
                return null;
            }

            if (!arrayEventTypes.isEmpty() && arrayEventTypes.containsKey(identNodeInner.getResolvedStreamName()))
            {
                Pair<Integer, String> indexAndProp = getStreamIndex(identNodeInner.getResolvedPropertyName());
                return new RangeValueEventPropIndexed(identNodeInner.getResolvedStreamName(), indexAndProp.getFirst(), indexAndProp.getSecond());
            }
            else
            {
                return new RangeValueEventProp(identNodeInner.getResolvedStreamName(), identNodeInner.getResolvedPropertyName());
            }

        }

        return null;
    }

    private static FilterSpecParam handleInSetNode(ExprInNode constituent, LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes)
            throws ExprValidationException
    {
        ExprNode left = constituent.getChildNodes().get(0);
        if (left instanceof ExprIdentNode)
        {
            ExprIdentNode identNodeInSet = (ExprIdentNode) left;
            String propertyName = identNodeInSet.getResolvedPropertyName();
            FilterOperator op = FilterOperator.IN_LIST_OF_VALUES;
            if (constituent.isNotIn())
            {
                op = FilterOperator.NOT_IN_LIST_OF_VALUES;
            }

            List<FilterSpecParamInValue> listofValues = new ArrayList<FilterSpecParamInValue>();
            Iterator<ExprNode> it = constituent.getChildNodes().iterator();
            it.next();  // ignore the first node as it's the identifier
            while (it.hasNext())
            {
                ExprNode subNode = it.next();
                if (subNode instanceof ExprConstantNode)
                {
                    ExprConstantNode constantNode = (ExprConstantNode) subNode;
                    Object constant = constantNode.evaluate(null, true);
                    constant = handleConstantsCoercion(identNodeInSet, constant);
                    listofValues.add(new InSetOfValuesConstant(constant));
                }
                if (subNode instanceof ExprIdentNode)
                {
                    ExprIdentNode identNodeInner = (ExprIdentNode) subNode;
                    if (identNodeInner.getStreamId() == 0)
                    {
                        break; // for same event evals use the boolean expression, via count compare failing below
                    }

                    boolean isMustCoerce = false;
                    Class numericCoercionType = JavaClassHelper.getBoxedType(identNodeInSet.getType());
                    if (identNodeInner.getType() != identNodeInSet.getType())
                    {
                        if (JavaClassHelper.isNumeric(identNodeInSet.getType()))
                        {
                            if (!JavaClassHelper.canCoerce(identNodeInner.getType(), identNodeInSet.getType()))
                            {
                                throwConversionError(identNodeInner.getType(), identNodeInSet.getType(), identNodeInSet.getResolvedPropertyName());
                            }
                            isMustCoerce = true;
                        }
                    }

                    FilterSpecParamInValue inValue;
                    String streamName = identNodeInner.getResolvedStreamName();
                    if (!arrayEventTypes.isEmpty() && arrayEventTypes.containsKey(streamName))
                    {
                        Pair<Integer, String> indexAndProp = getStreamIndex(identNodeInner.getResolvedPropertyName());
                        inValue = new InSetOfValuesEventPropIndexed(identNodeInner.getResolvedStreamName(), indexAndProp.getFirst(),
                                indexAndProp.getSecond(), isMustCoerce, numericCoercionType);
                    }
                    else
                    {
                        inValue = new InSetOfValuesEventProp(identNodeInner.getResolvedStreamName(), identNodeInner.getResolvedPropertyName(), isMustCoerce, numericCoercionType);
                    }

                    listofValues.add(inValue);
                }
            }

            // Fallback if not all values in the in-node can be resolved to properties or constants
            if (listofValues.size() == constituent.getChildNodes().size() - 1)
            {
                return new FilterSpecParamIn(propertyName, op, listofValues);
            }
        }
        return null;
    }

    private static FilterSpecParam handleEqualsAndRelOp(ExprNode constituent, LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes)
            throws ExprValidationException
    {
        FilterOperator op;
        if (constituent instanceof ExprEqualsNode)
        {
            op = FilterOperator.EQUAL;
            if (((ExprEqualsNode) constituent).isNotEquals())
            {
                op = FilterOperator.NOT_EQUAL;
            }
        }
        else
        {
            ExprRelationalOpNode relNode = (ExprRelationalOpNode) constituent;
            if (relNode.getRelationalOpEnum() == RelationalOpEnum.GT)
            {
                op = FilterOperator.GREATER;
            }
            else if (relNode.getRelationalOpEnum() == RelationalOpEnum.LT)
            {
                op = FilterOperator.LESS;
            }
            else if (relNode.getRelationalOpEnum() == RelationalOpEnum.LE)
            {
                op = FilterOperator.LESS_OR_EQUAL;
            }
            else if (relNode.getRelationalOpEnum() == RelationalOpEnum.GE)
            {
                op = FilterOperator.GREATER_OR_EQUAL;
            }
            else
            {
                throw new IllegalStateException("Opertor '" + relNode.getRelationalOpEnum() + "' not mapped");
            }
        }

        ExprNode left = constituent.getChildNodes().get(0);
        ExprNode right = constituent.getChildNodes().get(1);

        // check identifier and constant combination
        if ((right instanceof ExprConstantNode) && (left instanceof ExprIdentNode))
        {
            ExprIdentNode identNode = (ExprIdentNode) left;
            ExprConstantNode constantNode = (ExprConstantNode) right;
            String propertyName = identNode.getResolvedPropertyName();
            Object constant = constantNode.evaluate(null, true);
            constant = handleConstantsCoercion(identNode, constant);

            return new FilterSpecParamConstant(propertyName, op, constant);
        }
        if ((left instanceof ExprConstantNode) && (right instanceof ExprIdentNode))
        {
            ExprIdentNode identNode = (ExprIdentNode) right;
            ExprConstantNode constantNode = (ExprConstantNode) left;
            String propertyName = identNode.getResolvedPropertyName();
            Object constant = constantNode.evaluate(null, true);
            constant = handleConstantsCoercion(identNode, constant);
            return new FilterSpecParamConstant(propertyName, op, constant);
        }
        // check identifier and expression containing other streams
        if ((left instanceof ExprIdentNode) && (right instanceof ExprIdentNode))
        {
            ExprIdentNode identNodeLeft = (ExprIdentNode) left;
            ExprIdentNode identNodeRight = (ExprIdentNode) right;

            if ((identNodeLeft.getStreamId() == 0) && (identNodeRight.getStreamId() != 0))
            {
                return handleProperty(op, identNodeLeft, identNodeRight, arrayEventTypes);
            }
            if ((identNodeRight.getStreamId() == 0) && (identNodeLeft.getStreamId() != 0))
            {
                return handleProperty(op, identNodeRight, identNodeLeft, arrayEventTypes);
            }
        }
        return null;
    }

    private static FilterSpecParam handleProperty(FilterOperator op, ExprIdentNode identNodeLeft, ExprIdentNode identNodeRight, LinkedHashMap<String, Pair<EventType, String>> arrayEventTypes)
            throws ExprValidationException
    {
        String propertyName = identNodeLeft.getResolvedPropertyName();

        boolean isMustCoerce = false;
        SimpleNumberCoercer numberCoercer = null;
        Class numericCoercionType = JavaClassHelper.getBoxedType(identNodeLeft.getType());
        if (identNodeRight.getType() != identNodeLeft.getType())
        {
            if (JavaClassHelper.isNumeric(identNodeRight.getType()))
            {
                if (!JavaClassHelper.canCoerce(identNodeRight.getType(), identNodeLeft.getType()))
                {
                    throwConversionError(identNodeRight.getType(), identNodeLeft.getType(), identNodeLeft.getResolvedPropertyName());
                }
                isMustCoerce = true;
                numberCoercer = SimpleNumberCoercerFactory.getCoercer(identNodeRight.getType(), numericCoercionType);
            }
        }

        String streamName = identNodeRight.getResolvedStreamName();
        if (!arrayEventTypes.isEmpty() && arrayEventTypes.containsKey(streamName))
        {
            Pair<Integer, String> indexAndProp = getStreamIndex(identNodeRight.getResolvedPropertyName());
            return new FilterSpecParamEventPropIndexed(propertyName, op, identNodeRight.getResolvedStreamName(), indexAndProp.getFirst(),
                    indexAndProp.getSecond(), isMustCoerce, numberCoercer, numericCoercionType);
        }
        return new FilterSpecParamEventProp(propertyName, op, identNodeRight.getResolvedStreamName(), identNodeRight.getResolvedPropertyName(),
                isMustCoerce, numberCoercer, numericCoercionType);
    }

    private static Pair<Integer, String> getStreamIndex(String resolvedPropertyName)
    {
        Property property = PropertyParser.parse(resolvedPropertyName, false);
        if (!(property instanceof NestedProperty))
        {
            throw new IllegalStateException("Expected a nested property providing an index for array match '" + resolvedPropertyName + "'");
        }
        NestedProperty nested = ((NestedProperty) property);
        if (nested.getProperties().size() < 2)
        {
            throw new IllegalStateException("Expected a nested property name for array match '" + resolvedPropertyName + "', none found");
        }
        if (!(nested.getProperties().get(0) instanceof IndexedProperty))
        {
            throw new IllegalStateException("Expected an indexed property for array match '" + resolvedPropertyName + "', please provide an index");
        }
        int index = ((IndexedProperty) nested.getProperties().get(0)).getIndex();
        nested.getProperties().remove(0);
        StringWriter writer = new StringWriter();
        nested.toPropertyEPL(writer);
        return new Pair<Integer, String>(index, writer.toString());
    }

    private static void throwConversionError(Class fromType, Class toType, String propertyName)
            throws ExprValidationException
    {
        String text = "Implicit conversion from datatype '" +
                fromType.getSimpleName() +
                "' to '" +
                toType.getSimpleName() +
                "' for property '" +
                propertyName +
                "' is not allowed (strict filter type coercion)";
        throw new ExprValidationException(text);
    }

    // expressions automatically coerce to the most upwards type
    // filters require the same type
    private static Object handleConstantsCoercion(ExprIdentNode identNode, Object constant)
            throws ExprValidationException
    {
        Class identNodeType = identNode.getType();
        if (!JavaClassHelper.isNumeric(identNodeType))
        {
            return constant;    // no coercion required, other type checking performed by expression this comes from
        }

        if (constant == null)  // null constant type
        {
            return null;
        }

        if (!JavaClassHelper.canCoerce(constant.getClass(), identNodeType))
        {
            throwConversionError(constant.getClass(), identNodeType, identNode.getResolvedPropertyName());
        }

        Class identNodeTypeBoxed = JavaClassHelper.getBoxedType(identNodeType);
        return JavaClassHelper.coerceBoxed((Number) constant, identNodeTypeBoxed);
    }

    private static void recursiveAndConstituents(List<ExprNode> constituents, ExprNode exprNode)
    {
        for (ExprNode inner : exprNode.getChildNodes())
        {
            if (inner instanceof ExprAndNode)
            {
                recursiveAndConstituents(constituents, inner);
            }
            else
            {
                constituents.add(inner);
            }
        }
    }
}
