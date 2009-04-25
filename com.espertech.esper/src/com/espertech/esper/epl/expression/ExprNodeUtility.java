/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.expression;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.event.EventBeanUtility;

import java.util.LinkedList;
import java.util.List;

/**
 * Utility functions around handling expressions.
 */
public class ExprNodeUtility
{
    /**
     * Apply a filter expression.
     * @param filter expression
     * @param streamZeroEvent the event that represents stream zero
     * @param streamOneEvents all events thate are stream one events
     * @return filtered stream one events
     */
    public static EventBean[] applyFilterExpression(ExprNode filter, EventBean streamZeroEvent, EventBean[] streamOneEvents)
    {
        EventBean[] eventsPerStream = new EventBean[2];
        eventsPerStream[0] = streamZeroEvent;

        EventBean[] filtered = new EventBean[streamOneEvents.length];
        int countPass = 0;

        for (EventBean eventBean : streamOneEvents)
        {
            eventsPerStream[1] = eventBean;

            Boolean result = (Boolean) filter.evaluate(eventsPerStream, true);
            if ((result != null) && result)
            {
                filtered[countPass] = eventBean;
                countPass++;
            }
        }

        if (countPass == streamOneEvents.length)
        {
            return streamOneEvents;
        }
        return EventBeanUtility.resizeArray(filtered, countPass);
    }

    /**
     * Apply a filter expression returning a pass indicator.
     * @param filter to apply
     * @param eventsPerStream events per stream
     * @return pass indicator
     */
    public static boolean applyFilterExpression(ExprNode filter, EventBean[] eventsPerStream)
    {
        Boolean result = (Boolean) filter.evaluate(eventsPerStream, true);
        return (result != null) && result;
    }

    /**
     * Compare two expression nodes and their children in exact child-node sequence,
     * returning true if the 2 expression nodes trees are equals, or false if they are not equals.
     * <p>
     * Recursive call since it uses this method to compare child nodes in the same exact sequence.
     * Nodes are compared using the equalsNode method.
     * @param nodeOne - first expression top node of the tree to compare
     * @param nodeTwo - second expression top node of the tree to compare
     * @return false if this or all child nodes are not equal, true if equal
     */
    public static boolean deepEquals(ExprNode nodeOne, ExprNode nodeTwo)
    {
        if (nodeOne.getChildNodes().size() != nodeTwo.getChildNodes().size())
        {
            return false;
        }
        if (!nodeOne.equalsNode(nodeTwo))
        {
            return false;
        }
        for (int i = 0; i < nodeOne.getChildNodes().size(); i++)
        {
            ExprNode childNodeOne = nodeOne.getChildNodes().get(i);
            ExprNode childNodeTwo = nodeTwo.getChildNodes().get(i);

            if (!ExprNodeUtility.deepEquals(childNodeOne, childNodeTwo))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Compares two expression nodes via deep comparison, considering all
     * child nodes of either side.
     * @param one array of expressions
     * @param two array of expressions
     * @return true if the expressions are equal, false if not
     */
    public static boolean deepEquals(ExprNode[] one, ExprNode[] two)
    {
        if (one.length != two.length)
        {
            return false;
        }
        for (int i = 0; i < one.length; i++)
        {
            if (!ExprNodeUtility.deepEquals(one[i], two[i]))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the expression is minimal: does not have a subselect, aggregation and does not need view resources
     * @param expression to inspect
     * @return null if minimal, otherwise name of offending sub-expression
     */
    public static String isMinimalExpression(ExprNode expression)
    {
        ExprNodeSubselectVisitor subselectVisitor = new ExprNodeSubselectVisitor();
        expression.accept(subselectVisitor);
        if (subselectVisitor.getSubselects().size() > 0)
        {
            return "a subselect";
        }

        ExprNodeViewResourceVisitor viewResourceVisitor = new ExprNodeViewResourceVisitor();
        expression.accept(viewResourceVisitor);
        if (viewResourceVisitor.getExprNodes().size() > 0)
        {
            return "a function that requires view resources (prior, prev)";
        }

        List<ExprAggregateNode> aggregateNodes = new LinkedList<ExprAggregateNode>();
        ExprAggregateNode.getAggregatesBottomUp(expression, aggregateNodes);
        if (!aggregateNodes.isEmpty())
        {
            return "an aggregation function";
        }
        return null;
    }
}