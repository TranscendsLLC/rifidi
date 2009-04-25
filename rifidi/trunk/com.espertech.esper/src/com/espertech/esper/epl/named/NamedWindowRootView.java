/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.named;

import com.espertech.esper.collection.Pair;
import com.espertech.esper.epl.join.plan.FilterExprAnalyzer;
import com.espertech.esper.epl.join.plan.QueryGraph;
import com.espertech.esper.epl.join.table.EventTable;
import com.espertech.esper.epl.join.table.PropertyIndexedEventTable;
import com.espertech.esper.epl.lookup.IndexedTableLookupStrategy;
import com.espertech.esper.epl.lookup.IndexedTableLookupStrategyCoercing;
import com.espertech.esper.epl.lookup.TableLookupStrategy;
import com.espertech.esper.epl.lookup.JoinedPropDesc;
import com.espertech.esper.epl.spec.OnTriggerDesc;
import com.espertech.esper.epl.spec.OnTriggerType;
import com.espertech.esper.epl.core.ResultSetProcessor;
import com.espertech.esper.epl.expression.ExprNode;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.event.vaevent.ValueAddEventProcessor;
import com.espertech.esper.util.ExecutionPathDebugLog;
import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.view.StatementStopService;
import com.espertech.esper.view.ViewSupport;
import com.espertech.esper.view.Viewable;
import com.espertech.esper.core.InternalEventRouter;
import com.espertech.esper.core.EPStatementHandle;
import com.espertech.esper.core.StatementResultService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The root window in a named window plays multiple roles: It holds the indexes for deleting rows, if any on-delete statement
 * requires such indexes. Such indexes are updated when events arrive, or remove from when a data window
 * or on-delete statement expires events. The view keeps track of on-delete statements their indexes used.
 */
public class NamedWindowRootView extends ViewSupport
{
    private static final Log log = LogFactory.getLog(NamedWindowRootView.class);

    private EventType namedWindowEventType;
    private final NamedWindowIndexRepository indexRepository;
    private Iterable<EventBean> dataWindowContents;
    private final Map<LookupStrategy, PropertyIndexedEventTable> tablePerStrategy;
    private final ValueAddEventProcessor revisionProcessor;

    /**
     * Ctor.
     * @param revisionProcessor handle update events if supplied, or null if not handling revisions
     */
    public NamedWindowRootView(ValueAddEventProcessor revisionProcessor)
    {
        this.indexRepository = new NamedWindowIndexRepository();
        this.tablePerStrategy = new HashMap<LookupStrategy, PropertyIndexedEventTable>();
        this.revisionProcessor = revisionProcessor;
    }

    /**
     * Sets the iterator to use to obtain current named window data window contents.
     * @param dataWindowContents iterator over events help by named window
     */
    public void setDataWindowContents(Iterable<EventBean> dataWindowContents)
    {
        this.dataWindowContents = dataWindowContents;
    }

    /**
     * Called by tail view to indicate that the data window view exired events that must be removed from index tables.
     * @param oldData removed stream of the data window
     */
    public void removeOldData(EventBean[] oldData)
    {
        if (revisionProcessor != null)
        {
            revisionProcessor.removeOldData(oldData, indexRepository);
        }
        else
        {
            for (EventTable table : indexRepository.getTables())
            {
                table.remove(oldData);
            }
        }
    }

    // Called by deletion strategy and also the insert-into for new events only
    public void update(EventBean[] newData, EventBean[] oldData)
    {
        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".update Received update, " +
                    "  newData.length==" + ((newData == null) ? 0 : newData.length) +
                    "  oldData.length==" + ((oldData == null) ? 0 : oldData.length));
        }

        if (revisionProcessor != null)
        {
            revisionProcessor.onUpdate(newData, oldData, this, indexRepository);
        }
        else
        {
            // Update indexes for fast deletion, if there are any
            for (EventTable table : indexRepository.getTables())
            {
                table.add(newData);
                table.remove(oldData);
            }

            // Update child views
            updateChildren(newData, oldData);
        }
    }

    /**
     * Add an on-trigger view that, using a lookup strategy, looks up from the named window and may select or delete rows.
     * @param onTriggerDesc the specification for the on-delete
     * @param filterEventType the event type for the on-clause in the on-delete
     * @param statementStopService for stopping the statement
     * @param internalEventRouter for insert-into behavior
     * @param resultSetProcessor @return view representing the on-delete view chain, posting delete events to it's listeners
     * @param statementHandle is the handle to the statement, used for routing/insert-into
     * @param joinExpr is the join expression or null if there is none
     * @param statementResultService for coordinating on whether insert and remove stream events should be posted
     * @return base view for on-trigger expression
     */
    public NamedWindowOnExprBaseView addOnExpr(OnTriggerDesc onTriggerDesc, ExprNode joinExpr, EventType filterEventType, StatementStopService statementStopService, InternalEventRouter internalEventRouter, ResultSetProcessor resultSetProcessor, EPStatementHandle statementHandle, StatementResultService statementResultService)
    {
        // Determine strategy for deletion and index table to use (if any)
        Pair<LookupStrategy,PropertyIndexedEventTable> strategy = getStrategyPair(onTriggerDesc, joinExpr, filterEventType);

        // If a new table is required, add that table to be updated
        if (strategy.getSecond() != null)
        {
            tablePerStrategy.put(strategy.getFirst(), strategy.getSecond());
        }

        if (onTriggerDesc.getOnTriggerType() == OnTriggerType.ON_DELETE)
        {
            return new NamedWindowOnDeleteView(statementStopService, strategy.getFirst(), this, statementResultService);
        }
        else
        {
            return new NamedWindowOnSelectView(statementStopService, strategy.getFirst(), this, internalEventRouter, resultSetProcessor, statementHandle, statementResultService);
        }
    }

    /**
     * Unregister an on-delete statement view, using the strategy as a key to remove a reference to the index table
     * used by the strategy.
     * @param strategy to use for deleting events
     */
    public void removeOnExpr(LookupStrategy strategy)
    {
        PropertyIndexedEventTable table = tablePerStrategy.remove(strategy);
        if (table != null)
        {
            indexRepository.removeTableReference(table);
        }
    }

    private Pair<LookupStrategy,PropertyIndexedEventTable> getStrategyPair(OnTriggerDesc onTriggerDesc, ExprNode joinExpr, EventType filterEventType)
    {
        // No join expression means delete all
        if (joinExpr == null)
        {
            return new Pair<LookupStrategy,PropertyIndexedEventTable>(new LookupStrategyAllRows(dataWindowContents), null);
        }

        // analyze query graph; Whereas stream0=named window, stream1=delete-expr filter
        QueryGraph queryGraph = new QueryGraph(2);
        FilterExprAnalyzer.analyze(joinExpr, queryGraph);

        // index and key property names
        String[] keyPropertiesJoin = queryGraph.getKeyProperties(1, 0);
        String[] indexPropertiesJoin = queryGraph.getIndexProperties(1, 0);

        // If the analysis revealed no join columns, must use the brute-force full table scan
        if ((keyPropertiesJoin == null) || (keyPropertiesJoin.length == 0))
        {
            return new Pair<LookupStrategy,PropertyIndexedEventTable>(new LookupStrategyTableScan(joinExpr, dataWindowContents), null);
        }

        // Build a set of index descriptors with property name and coercion type
        boolean mustCoerce = false;
        Class[] coercionTypes = new Class[indexPropertiesJoin.length];
        for (int i = 0; i < keyPropertiesJoin.length; i++)
        {
            Class keyPropType = JavaClassHelper.getBoxedType(filterEventType.getPropertyType(keyPropertiesJoin[i]));
            Class indexedPropType = JavaClassHelper.getBoxedType(namedWindowEventType.getPropertyType(indexPropertiesJoin[i]));
            Class coercionType = indexedPropType;
            if (keyPropType != indexedPropType)
            {
                coercionType = JavaClassHelper.getCompareToCoercionType(keyPropType, keyPropType);
                mustCoerce = true;
            }

            coercionTypes[i] = coercionType;
        }

        // Add all joined fields to an array for sorting
        JoinedPropDesc[] joinedPropDesc = new JoinedPropDesc[keyPropertiesJoin.length];
        for (int i = 0; i < joinedPropDesc.length; i++)
        {
            joinedPropDesc[i] = new JoinedPropDesc(indexPropertiesJoin[i], coercionTypes[i], keyPropertiesJoin[i], 1);
        }
        Arrays.sort(joinedPropDesc);
        keyPropertiesJoin = JoinedPropDesc.getKeyProperties(joinedPropDesc);
        indexPropertiesJoin = JoinedPropDesc.getIndexProperties(joinedPropDesc);

        // Get the table for this index
        PropertyIndexedEventTable table = indexRepository.addTable(joinedPropDesc, dataWindowContents, namedWindowEventType, mustCoerce);

        // assign types and stream numbers
        EventType[] eventTypePerStream = new EventType[] {null, filterEventType};
        int[] streamNumbersPerProperty = new int[joinedPropDesc.length];
        for (int i = 0; i < streamNumbersPerProperty.length; i++)
        {
            streamNumbersPerProperty[i] = 1;
        }

        // create the strategy
        TableLookupStrategy lookupStrategy = null;
        if (!mustCoerce)
        {
            lookupStrategy = new IndexedTableLookupStrategy(eventTypePerStream, streamNumbersPerProperty, keyPropertiesJoin, table);
        }
        else
        {
            lookupStrategy = new IndexedTableLookupStrategyCoercing(eventTypePerStream, streamNumbersPerProperty, keyPropertiesJoin, table, coercionTypes);
        }

        return new Pair<LookupStrategy,PropertyIndexedEventTable>(new LookupStrategyIndexed(joinExpr, lookupStrategy), table);
    }

    public void setParent(Viewable parent)
    {
        super.setParent(parent);
        namedWindowEventType = parent.getEventType();
    }

    public EventType getEventType()
    {
        return namedWindowEventType;
    }

    public Iterator<EventBean> iterator()
    {
        return null;
    }

    /**
     * Destroy and clear resources.
     */
    public void destroy()
    {
        indexRepository.destroy();
        tablePerStrategy.clear();
    }
}
