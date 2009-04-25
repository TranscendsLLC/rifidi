package com.espertech.esper.epl.property;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.client.FragmentEventType;
import com.espertech.esper.client.EventType;
import com.espertech.esper.collection.ArrayDequeJDK6Backport;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.epl.expression.ExprNodeUtility;

import java.util.Collection;
import java.util.List;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A property evaluator that considers nested properties and that considers where-clauses
 * but does not consider select-clauses.
 */
public class PropertyEvaluatorNested implements PropertyEvaluator
{
    private static final Log log = LogFactory.getLog(PropertyEvaluatorNested.class);
    
    private final EventPropertyGetter[] getter;
    private final FragmentEventType[] fragmentEventType;
    private final ExprNode[] whereClauses;
    private final EventBean[] eventsPerStream;
    private final int lastLevel;
    private final List<String> propertyNames;

    /**
     * Ctor.
     * @param getter property getter
     * @param fragmentEventType the fragments
     * @param whereClauses the where clauses
     * @param propertyNames the property names that are staggered
     */
    public PropertyEvaluatorNested(EventPropertyGetter[] getter, FragmentEventType[] fragmentEventType, ExprNode[] whereClauses, List<String> propertyNames)
    {
        this.fragmentEventType = fragmentEventType;
        this.getter = getter;
        this.whereClauses = whereClauses;
        lastLevel = fragmentEventType.length - 1;
        eventsPerStream = new EventBean[fragmentEventType.length + 1];
        this.propertyNames = propertyNames;
    }

    public EventBean[] getProperty(EventBean event)
    {
        ArrayDequeJDK6Backport<EventBean> resultEvents = new ArrayDequeJDK6Backport<EventBean>();
        eventsPerStream[0] = event;
        populateEvents(event, 0, resultEvents);
        if (resultEvents.isEmpty())
        {
            return null;
        }
        return resultEvents.toArray(new EventBean[resultEvents.size()]);
    }

    private void populateEvents(EventBean branch, int level, Collection<EventBean> events)
    {
        try
        {
            Object result = getter[level].getFragment(branch);

            if (fragmentEventType[level].isIndexed())
            {
                EventBean[] fragments = (EventBean[]) result;
                if (level == lastLevel)
                {
                    if (whereClauses[level] != null)
                    {
                        for (EventBean event : fragments)
                        {
                            eventsPerStream[level+1] = event;
                            if (ExprNodeUtility.applyFilterExpression(whereClauses[level], eventsPerStream))
                            {
                                events.add(event);
                            }
                        }
                    }
                    else
                    {
                        events.addAll(Arrays.asList(fragments));
                    }
                }
                else
                {
                    if (whereClauses[level] != null)
                    {
                        for (EventBean next : fragments)
                        {
                            eventsPerStream[level+1] = next;
                            if (ExprNodeUtility.applyFilterExpression(whereClauses[level], eventsPerStream))
                            {
                                populateEvents(next, level+1, events);
                            }
                        }
                    }
                    else
                    {
                        for (EventBean next : fragments)
                        {
                            eventsPerStream[level+1] = next;
                            populateEvents(next, level+1, events);
                        }
                    }
                }
            }
            else
            {
                EventBean fragment = (EventBean) result;
                if (level == lastLevel)
                {
                    if (whereClauses[level] != null)
                    {
                        eventsPerStream[level+1] = fragment;
                        if (ExprNodeUtility.applyFilterExpression(whereClauses[level], eventsPerStream))
                        {
                            events.add(fragment);
                        }
                    }
                    else
                    {
                        events.add(fragment);
                    }
                }
                else
                {
                    if (whereClauses[level] != null)
                    {
                        eventsPerStream[level+1] = fragment;
                        if (ExprNodeUtility.applyFilterExpression(whereClauses[level], eventsPerStream))
                        {
                            populateEvents(fragment, level+1, events);
                        }
                    }
                    else
                    {
                        eventsPerStream[level+1] = fragment;
                        populateEvents(fragment, level+1, events);
                    }
                }
            }
        }
        catch (RuntimeException ex)
        {
            log.error("Unexpected error evaluating property expression for event of type '" +
                    branch.getEventType().getName() +
                    "' and property '" +
                    propertyNames.get(level + 1) + "': " + ex.getMessage(), ex);
        }
    }

    public EventType getFragmentEventType()
    {
        return fragmentEventType[lastLevel].getFragmentType();
    }

    public boolean compareTo(PropertyEvaluator otherEval)
    {
        return false;
    }
}
