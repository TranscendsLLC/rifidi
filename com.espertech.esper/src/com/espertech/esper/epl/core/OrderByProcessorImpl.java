/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.core;

import com.espertech.esper.collection.MultiKeyUntyped;
import com.espertech.esper.epl.agg.AggregationService;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.epl.spec.OrderByItem;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.util.MultiKeyComparator;
import com.espertech.esper.util.ExecutionPathDebugLog;
import com.espertech.esper.util.MultiKeyCollatingComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * An order-by processor that sorts events according to the expressions
 * in the order_by clause.
 */
public class OrderByProcessorImpl implements OrderByProcessor {

	private static final Log log = LogFactory.getLog(OrderByProcessorImpl.class);

	private final List<OrderByItem> orderByList;
	private final List<ExprNode> groupByNodes;
	private final boolean needsGroupByKeys;
	private final AggregationService aggregationService;

	private final Comparator<MultiKeyUntyped> comparator;

	/**
	 * Ctor.
	 *
	 * @param orderByList -
	 *            the nodes that generate the keys to sort events on
	 * @param groupByNodes -
	 *            generate the keys for determining aggregation groups
	 * @param needsGroupByKeys -
	 *            indicates whether this processor needs to have individual
	 *            group by keys to evaluate the sort condition successfully
	 * @param aggregationService -
	 *            used to evaluate aggregate functions in the group-by and
	 *            sort-by clauses
     * @param isSortUsingCollator for string value sorting using compare or Collator
     * @throws ExprValidationException when order-by items don't divulge a type
	 */
	public OrderByProcessorImpl(final List<OrderByItem> orderByList,
								  List<ExprNode> groupByNodes,
								  boolean needsGroupByKeys,
								  AggregationService aggregationService,
                                  boolean isSortUsingCollator)
            throws ExprValidationException
    {
		this.orderByList = orderByList;
		this.groupByNodes = groupByNodes;
		this.needsGroupByKeys = needsGroupByKeys;
		this.aggregationService = aggregationService;

        comparator = getComparator(orderByList, isSortUsingCollator);
    }

    public MultiKeyUntyped getSortKey(EventBean[] eventsPerStream, boolean isNewData)
    {
        Object[] values = new Object[orderByList.size()];
        int count = 0;
        for (OrderByItem sortPair : orderByList)
        {
            ExprNode sortNode = sortPair.getExprNode();
            values[count++] = sortNode.evaluate(eventsPerStream, isNewData);
        }

        return new MultiKeyUntyped(values);
    }

    public MultiKeyUntyped[] getSortKeyPerRow(EventBean[] generatingEvents, boolean isNewData)
    {
        if (generatingEvents == null)
        {
            return null;
        }

        MultiKeyUntyped[] sortProperties = new MultiKeyUntyped[generatingEvents.length];

        int count = 0;
        EventBean[] evalEventsPerStream = new EventBean[1];
        for (EventBean event : generatingEvents)
        {
            Object[] values = new Object[orderByList.size()];
            int countTwo = 0;
            evalEventsPerStream[0] = event;
            for (OrderByItem sortPair : orderByList)
            {
                ExprNode sortNode = sortPair.getExprNode();
                values[countTwo++] = sortNode.evaluate(evalEventsPerStream, isNewData);
            }

            sortProperties[count] = new MultiKeyUntyped(values);
            count++;
        }

        return sortProperties;
    }

	public EventBean[] sort(EventBean[] outgoingEvents, EventBean[][] generatingEvents, boolean isNewData)
	{
		if (outgoingEvents == null || outgoingEvents.length < 2)
		{
			return outgoingEvents;
		}

		// Get the group by keys if needed
		MultiKeyUntyped[] groupByKeys = null;
		if (needsGroupByKeys)
		{
			groupByKeys = generateGroupKeys(generatingEvents, isNewData);
		}

		return sort(outgoingEvents, generatingEvents, groupByKeys, isNewData);
	}

	public EventBean[] sort(EventBean[] outgoingEvents, EventBean[][] generatingEvents, MultiKeyUntyped[] groupByKeys, boolean isNewData)
	{
		if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".sort");
        }

        if (outgoingEvents == null || outgoingEvents.length < 2)
		{
			return outgoingEvents;
		}

		// Create the multikeys of sort values
		List<MultiKeyUntyped> sortValuesMultiKeys = createSortProperties(generatingEvents, groupByKeys, isNewData);

		// Map the sort values to the corresponding outgoing events
		Map<MultiKeyUntyped, List<EventBean>> sortToOutgoing = new HashMap<MultiKeyUntyped, List<EventBean>>();
		int countOne = 0;
		for (MultiKeyUntyped sortValues : sortValuesMultiKeys)
		{
			List<EventBean> list = sortToOutgoing.get(sortValues);
			if (list == null)
			{
				list = new ArrayList<EventBean>();
			}
			list.add(outgoingEvents[countOne++]);
			sortToOutgoing.put(sortValues, list);
		}

		// Sort the sort values
		Collections.sort(sortValuesMultiKeys, comparator);

		// Sort the outgoing events in the same order
		Set<MultiKeyUntyped> sortSet = new LinkedHashSet<MultiKeyUntyped>(sortValuesMultiKeys);
		EventBean[] result = new EventBean[outgoingEvents.length];
		int countTwo = 0;
		for (MultiKeyUntyped sortValues : sortSet)
		{
			Collection<EventBean> output = sortToOutgoing.get(sortValues);
			for(EventBean event : output)
			{
				result[countTwo++] = event;
			}
		}

		return result;
	}

	private List<MultiKeyUntyped> createSortProperties(EventBean[][] generatingEvents, MultiKeyUntyped[] groupByKeys, boolean isNewData)
	{
		MultiKeyUntyped[] sortProperties = new MultiKeyUntyped[generatingEvents.length];

		int count = 0;
		for (EventBean[] eventsPerStream : generatingEvents)
		{
			// Make a new multikey that contains the sort-by values.
			if (needsGroupByKeys)
			{
				aggregationService.setCurrentRow(groupByKeys[count]);
			}

			Object[] values = new Object[orderByList.size()];
			int countTwo = 0;
			for (OrderByItem sortPair : orderByList)
			{
				ExprNode sortNode = sortPair.getExprNode();
				values[countTwo++] = sortNode.evaluate(eventsPerStream, isNewData);
			}

			sortProperties[count] = new MultiKeyUntyped(values);
			count++;
		}
		return Arrays.asList(sortProperties);
	}

	private MultiKeyUntyped generateGroupKey(EventBean[] eventsPerStream, boolean isNewData)
	{
		Object[] keys = new Object[groupByNodes.size()];

		int count = 0;
		for (ExprNode exprNode : groupByNodes)
		{
			keys[count] = exprNode.evaluate(eventsPerStream, isNewData);
			count++;
		}

		return new MultiKeyUntyped(keys);
	}

    public EventBean[] sort(EventBean[] outgoingEvents, MultiKeyUntyped[] orderKeys)
    {
        TreeMap<MultiKeyUntyped, Object> sort = new TreeMap<MultiKeyUntyped, Object>(comparator);

        if (outgoingEvents == null || outgoingEvents.length < 2)
        {
            return outgoingEvents;
        }

        for (int i = 0; i < outgoingEvents.length; i++)
        {
            Object entry = sort.get(orderKeys[i]);
            if (entry == null)
            {
                sort.put(orderKeys[i], outgoingEvents[i]);
            }
            else if (entry instanceof EventBean)
            {
                List<EventBean> list = new ArrayList<EventBean>();
                list.add((EventBean)entry);
                list.add(outgoingEvents[i]);
                sort.put(orderKeys[i], list);
            }
            else
            {
                List<EventBean> list = (List<EventBean>) entry;
                list.add(outgoingEvents[i]);
            }
        }

        EventBean[] result = new EventBean[outgoingEvents.length];
        int count = 0;
        for (Object entry : sort.values())
        {
            if (entry instanceof List)
            {
                List<EventBean> output = (List<EventBean>) entry;
                for(EventBean event : output)
                {
                    result[count++] = event;
                }
            }
            else
            {
                result[count++] = (EventBean) entry;
            }
        }
        return result;
    }

    private MultiKeyUntyped[] generateGroupKeys(EventBean[][] generatingEvents, boolean isNewData)
	{
		MultiKeyUntyped keys[] = new MultiKeyUntyped[generatingEvents.length];

		int count = 0;
		for (EventBean[] eventsPerStream : generatingEvents)
		{
			keys[count++] = generateGroupKey(eventsPerStream, isNewData);
		}

		return keys;
	}

    /**
     * Returns a comparator for order items that may sort string values using Collator.
     * @param orderByList order-by items
     * @param isSortUsingCollator true for Collator string sorting
     * @return comparator
     * @throws ExprValidationException if the return type of order items cannot be determined
     */
    protected static Comparator<MultiKeyUntyped> getComparator(List<OrderByItem> orderByList, boolean isSortUsingCollator) throws ExprValidationException
    {
        Comparator<MultiKeyUntyped> comparator;

        if (isSortUsingCollator)
        {
            // determine String types
            boolean hasStringTypes = false;
            boolean stringTypes[] = new boolean[orderByList.size()];
            int count = 0;
            for (OrderByItem item : orderByList)
            {
                if (item.getExprNode().getType() == String.class)
                {
                    hasStringTypes = true;
                    stringTypes[count] = true;
                }
                count++;
            }

            if (!hasStringTypes)
            {
                comparator = new MultiKeyComparator(getIsDescendingValues(orderByList));
            }
            else
            {
                comparator = new MultiKeyCollatingComparator(getIsDescendingValues(orderByList), stringTypes);
            }
        }
        else
        {
            comparator = new MultiKeyComparator(getIsDescendingValues(orderByList));
        }

        return comparator;
    }

	private static boolean[] getIsDescendingValues(List<OrderByItem> orderByList)
	{
		boolean[] isDescendingValues  = new boolean[orderByList.size()];
		int count = 0;
		for(OrderByItem pair : orderByList)
		{
			isDescendingValues[count++] = pair.isDescending();
		}
		return isDescendingValues;
	}
}
