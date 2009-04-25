/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.view;

import com.espertech.esper.collection.MultiKey;
import com.espertech.esper.collection.UniformPair;
import com.espertech.esper.core.StatementContext;
import com.espertech.esper.epl.core.ResultSetProcessor;
import com.espertech.esper.epl.spec.OutputLimitSpec;
import com.espertech.esper.epl.spec.OutputLimitLimitType;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.util.ExecutionPathDebugLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;

/**
 * A view that prepares output events, batching incoming
 * events and invoking the result set processor as necessary.
 * <p>
 * Handles output rate limiting or stabilizing.
 */
public class OutputProcessViewPolicy extends OutputProcessView
{
    private final OutputCondition outputCondition;
    private final OutputLimitLimitType outputLimitLimitType;

    // Posted events in ordered form (for applying to aggregates) and summarized per type
    // Using ArrayList as random access is a requirement.
    private List<UniformPair<EventBean[]>> viewEventsList = new ArrayList<UniformPair<EventBean[]>>();
	private List<UniformPair<Set<MultiKey<EventBean>>>> joinEventsSet = new ArrayList<UniformPair<Set<MultiKey<EventBean>>>>();

	private static final Log log = LogFactory.getLog(OutputProcessViewPolicy.class);

    /**
     * Ctor.
     * @param resultSetProcessor is processing the result set for publishing it out
     * @param streamCount is the number of streams, indicates whether or not this view participates in a join
     * @param outputLimitSpec is the specification for limiting output (the output condition and the result set processor)
     * @param statementContext is the services the output condition may depend on
     * @param isInsertInto is true if the statement is a insert-into
     * @param outputStrategy is the method to use to produce output
     * @throws ExprValidationException if validation of the output expressions fails
     */
    public OutputProcessViewPolicy(ResultSetProcessor resultSetProcessor,
                          OutputStrategy outputStrategy,
                          boolean isInsertInto,
                          int streamCount,
    					  OutputLimitSpec outputLimitSpec,
    					  StatementContext statementContext)
            throws ExprValidationException
    {
        super(resultSetProcessor, outputStrategy, isInsertInto, statementContext.getStatementResultService());
        log.debug(".ctor");

    	if(streamCount < 1)
    	{
    		throw new IllegalArgumentException("Output process view is part of at least 1 stream");
    	}

    	OutputCallback outputCallback = getCallbackToLocal(streamCount);
    	this.outputCondition = statementContext.getOutputConditionFactory().createCondition(outputLimitSpec, statementContext, outputCallback);
        outputLimitLimitType = outputLimitSpec.getDisplayLimit();
    }

    /**
     * The update method is called if the view does not participate in a join.
     * @param newData - new events
     * @param oldData - old events
     */
    public void update(EventBean[] newData, EventBean[] oldData)
    {
        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".update Received update, " +
                    "  newData.length==" + ((newData == null) ? 0 : newData.length) +
                    "  oldData.length==" + ((oldData == null) ? 0 : oldData.length));
        }

        int newDataLength = 0;
        int oldDataLength = 0;
        if(newData != null)
        {
        	newDataLength = newData.length;
        }
        if(oldData != null)
        {
        	oldDataLength = oldData.length;
        }

        // add the incoming events to the event batches
        viewEventsList.add(new UniformPair<EventBean[]>(newData, oldData));

        outputCondition.updateOutputCondition(newDataLength, oldDataLength);
    }

    /**
     * This process (update) method is for participation in a join.
     * @param newEvents - new events
     * @param oldEvents - old events
     */
    public void process(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents)
    {
        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".process Received update, " +
                    "  newData.length==" + ((newEvents == null) ? 0 : newEvents.size()) +
                    "  oldData.length==" + ((oldEvents == null) ? 0 : oldEvents.size()));
        }

        int newEventsSize = 0;
        if (newEvents != null)
        {
            newEventsSize = newEvents.size();
        }

        int oldEventsSize = 0;
        if (oldEvents != null)
        {
            oldEventsSize = oldEvents.size();
        }

        // add the incoming events to the event batches
        Set<MultiKey<EventBean>> copyNew = new LinkedHashSet<MultiKey<EventBean>>(newEvents);
        Set<MultiKey<EventBean>> copyOld = new LinkedHashSet<MultiKey<EventBean>>(oldEvents);
        joinEventsSet.add(new UniformPair<Set<MultiKey<EventBean>>>(copyNew, copyOld));

        outputCondition.updateOutputCondition(newEventsSize, oldEventsSize);
    }

	/**
	 * Called once the output condition has been met.
	 * Invokes the result set processor.
	 * Used for non-join event data.
	 * @param doOutput - true if the batched events should actually be output as well as processed, false if they should just be processed
	 * @param forceUpdate - true if output should be made even when no updating events have arrived
	 * */
	protected void continueOutputProcessingView(boolean doOutput, boolean forceUpdate)
	{
		if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".continueOutputProcessingView");
        }

        boolean isGenerateSynthetic = statementResultService.isMakeSynthetic();
        boolean isGenerateNatural = statementResultService.isMakeNatural();

        // Process the events and get the result
        UniformPair<EventBean[]> newOldEvents = resultSetProcessor.processOutputLimitedView(viewEventsList, isGenerateSynthetic, outputLimitLimitType);

        if ((!isGenerateSynthetic) && (!isGenerateNatural))
        {
            return;
        }

        if(doOutput)
		{
			output(forceUpdate, newOldEvents);
		}
		resetEventBatches();
	}

	private void output(boolean forceUpdate, UniformPair<EventBean[]> results)
	{
        // Child view can be null in replay from named window
        if (childView != null)
        {
            outputStrategy.output(forceUpdate, results, childView);
        }
	}

	private void resetEventBatches()
	{
		viewEventsList.clear();
		joinEventsSet.clear();
    }

	/**
	 * Called once the output condition has been met.
	 * Invokes the result set processor.
	 * Used for join event data.
	 * @param doOutput - true if the batched events should actually be output as well as processed, false if they should just be processed
	 * @param forceUpdate - true if output should be made even when no updating events have arrived
	 */
	protected void continueOutputProcessingJoin(boolean doOutput, boolean forceUpdate)
	{
		if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".continueOutputProcessingJoin");
        }

        boolean isGenerateSynthetic = statementResultService.isMakeSynthetic();
        boolean isGenerateNatural = statementResultService.isMakeNatural();

        // Process the events and get the result
        UniformPair<EventBean[]> newOldEvents = resultSetProcessor.processOutputLimitedJoin(joinEventsSet, isGenerateSynthetic, outputLimitLimitType);

        if ((!isGenerateSynthetic) && (!isGenerateNatural))
        {
            return;
        }

		if(doOutput)
		{
			output(forceUpdate, newOldEvents);
		}
		resetEventBatches();
	}

    private OutputCallback getCallbackToLocal(int streamCount)
    {
        // single stream means no join
        // multiple streams means a join
        if(streamCount == 1)
        {
            return new OutputCallback()
            {
                public void continueOutputProcessing(boolean doOutput, boolean forceUpdate)
                {
                    OutputProcessViewPolicy.this.continueOutputProcessingView(doOutput, forceUpdate);
                }
            };
        }
        else
        {
            return new OutputCallback()
            {
                public void continueOutputProcessing(boolean doOutput, boolean forceUpdate)
                {
                    OutputProcessViewPolicy.this.continueOutputProcessingJoin(doOutput, forceUpdate);
                }
            };
        }
    }
}
