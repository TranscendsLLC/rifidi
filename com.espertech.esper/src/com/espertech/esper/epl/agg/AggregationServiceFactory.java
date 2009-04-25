/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.agg;

import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.expression.ExprAggregateNode;
import com.espertech.esper.epl.expression.ExprEvaluator;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprNodeUtility;
import com.espertech.esper.client.EventBean;

import java.util.*;

/**
 * Factory for aggregation service instances.
 * <p>
 * Consolidates aggregation nodes such that result futures point to a single instance and
 * no re-evaluation of the same result occurs.
 */
public class AggregationServiceFactory
{
    /**
     * Returns an instance to handle the aggregation required by the aggregation expression nodes, depending on
     * whether there are any group-by nodes.
     * @param selectAggregateExprNodes - aggregation nodes extracted out of the select expression
     * @param havingAggregateExprNodes - aggregation nodes extracted out of the select expression
     * @param orderByAggregateExprNodes - aggregation nodes extracted out of the select expression
     * @param hasGroupByClause - indicator on whethere there is group-by required, or group-all
     * @param methodResolutionService - is required to resolve aggregation methods
     * @return instance for aggregation handling
     */
    public static AggregationService getService(List<ExprAggregateNode> selectAggregateExprNodes,
                                                List<ExprAggregateNode> havingAggregateExprNodes,
                                                List<ExprAggregateNode> orderByAggregateExprNodes,
                                                boolean hasGroupByClause,
                                                MethodResolutionService methodResolutionService)
    {
        // No aggregates used, we do not need this service
        if ((selectAggregateExprNodes.isEmpty()) && (havingAggregateExprNodes.isEmpty()))
        {
        	return new AggregationServiceNull();
        }

        // Compile a map of aggregation nodes and equivalent-to aggregation nodes.
        // Equivalent-to functions are for example "select sum(a*b), 5*sum(a*b)".
        // Reducing the total number of aggregation functions.
        Map<ExprAggregateNode, List<ExprAggregateNode>> equivalencyList = new LinkedHashMap<ExprAggregateNode, List<ExprAggregateNode>>();
        for (ExprAggregateNode selectAggNode : selectAggregateExprNodes)
        {
            addEquivalent(selectAggNode, equivalencyList);
        }
        for (ExprAggregateNode havingAggNode : havingAggregateExprNodes)
        {
            addEquivalent(havingAggNode, equivalencyList);
        }
        for (ExprAggregateNode orderByAggNode : orderByAggregateExprNodes)
        {
            addEquivalent(orderByAggNode, equivalencyList);
        }

        // Construct a list of evaluation node for the aggregation function.
        // For example "sum(2 * 3)" would make the sum an evaluation node.
        AggregationMethod aggregators[] = new AggregationMethod[equivalencyList.size()];
        ExprEvaluator[] evaluators = new ExprEvaluator[equivalencyList.size()];

        int index = 0;
        for (ExprAggregateNode aggregateNode : equivalencyList.keySet())
        {
            if (aggregateNode.getChildNodes().size() > 1)
            {
                evaluators[index] = getMultiNodeEvaluator(aggregateNode.getChildNodes());
            }
            else if (!aggregateNode.getChildNodes().isEmpty())
            {
                // Use the evaluation node under the aggregation node to obtain the aggregation value
                evaluators[index] = aggregateNode.getChildNodes().get(0);
            }
            // For aggregation that doesn't evaluate any particular sub-expression, return null on evaluation
            else
            {
                evaluators[index] = new ExprEvaluator() {
                    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData)
                    {
                        return null;
                    }
                };
            }

            aggregators[index] = aggregateNode.getPrototypeAggregator();
            index++;
        }

        AggregationService service;
        if (hasGroupByClause)
        {
            // If there is a group-by clause, then we need to keep aggregators as prototypes
            service = new AggregationServiceGroupByImpl(evaluators, aggregators, methodResolutionService);
        }
        else
        {
            // Without a group-by clause we group all into the same pot, using one set of aggregators
            service = new AggregationServiceGroupAllImpl(evaluators, aggregators);
        }

        // Hand a service reference to the aggregation nodes themselves.
        // Thus on expression evaluation time each aggregate node calls back to find out what the
        // group's state is (and thus does not evaluate by asking its child node for its result).
        int column = 0;
        for (ExprAggregateNode aggregateNode : equivalencyList.keySet())
        {
            aggregateNode.setAggregationResultFuture(service, column);

            // hand to all equivalent-to
            List<ExprAggregateNode> equivalentAggregators = equivalencyList.get(aggregateNode);
            if (equivalentAggregators != null)
            {
                for (ExprAggregateNode equivalentAggNode : equivalentAggregators)
                {
                    equivalentAggNode.setAggregationResultFuture(service, column);
                }
            }

            column++;
        }

        return service;
    }

    private static ExprEvaluator getMultiNodeEvaluator(List<ExprNode> childNodes)
    {
        final int size = childNodes.size();
        final List<ExprNode> exprNodes = childNodes;
        final Object[] prototype = new Object[size];

        // determine constant nodes
        int count = 0;
        for (ExprNode node : exprNodes)
        {
            if (node.isConstantResult())
            {
                prototype[count] = node.evaluate(null, true);
            }
            count++;
        }

        return new ExprEvaluator() {
            public Object evaluate(EventBean[] eventsPerStream, boolean isNewData)
            {
                int count = 0;
                for (ExprNode node : exprNodes)
                {
                    prototype[count] = node.evaluate(eventsPerStream, isNewData);
                    count++;
                }
                return prototype;
            }
        };
    }

    private static void addEquivalent(ExprAggregateNode aggNodeToAdd, Map<ExprAggregateNode, List<ExprAggregateNode>> equivalencyList)
    {
        // Check any same aggregation nodes among all aggregation clauses
        boolean foundEquivalent = false;
        for (Map.Entry<ExprAggregateNode, List<ExprAggregateNode>> entry : equivalencyList.entrySet())
        {
            ExprAggregateNode aggNode = entry.getKey();
            if (ExprNodeUtility.deepEquals(aggNode, aggNodeToAdd))
            {
                List<ExprAggregateNode> equivalentAggregators = entry.getValue();
                if (equivalentAggregators == null)
                {
                    equivalentAggregators = new ArrayList<ExprAggregateNode>();
                }
                equivalentAggregators.add(aggNodeToAdd);
                equivalencyList.put(aggNode, equivalentAggregators);
                foundEquivalent = true;
                break;
            }
        }

        if (!foundEquivalent)
        {
            equivalencyList.put(aggNodeToAdd, null);
        }
    }

}
