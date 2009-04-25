package com.espertech.esper.epl.expression;

import com.espertech.esper.client.EventBean;

import java.util.Set;

/**
 * Strategy for evaluation of a subselect.
 */
public interface SubselectEvalStrategy
{
    /**
     * Evaluate.
     * @param eventsPerStream events per stream
     * @param isNewData true for new data
     * @param matchingEvents prefiltered events
     * @return eval result
     */
    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, Set<EventBean> matchingEvents);
}
