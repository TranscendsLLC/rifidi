/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.event.vaevent;

import com.espertech.esper.core.EPStatementHandle;
import com.espertech.esper.epl.named.NamedWindowIndexRepository;
import com.espertech.esper.epl.named.NamedWindowRootView;
import com.espertech.esper.epl.expression.ExprValidationException;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.view.Viewable;

import java.util.Collection;

/**
 * Interface for a processor of base and delta events in a revision event type.
 */
public interface ValueAddEventProcessor
{
    /**
     * Returns the event type that this revision processor generates.
     * @return event type
     */
    public EventType getValueAddEventType();

    /**
     * For use in checking insert-into statements, validates that the given type is eligible for revision event.
     * @param eventType the type of the event participating in revision event type (or not)
     * @throws ExprValidationException if the validation fails
     */
    public void validateEventType(EventType eventType) throws ExprValidationException;

    /**
     * For use in executing an insert-into, wraps the given event applying the revision event type,
     * but not yet computing a new revision.
     * @param event to wrap
     * @return revision event bean
     */
    public EventBean getValueAddEventBean(EventBean event);

    /**
     * Upon new events arriving into a named window (new data), and upon events being deleted
     * via on-delete (old data), update child views of the root view and apply to index repository as required (fast deletion).
     * @param newData new events
     * @param oldData remove stream
     * @param namedWindowRootView the root view
     * @param indexRepository delete and select indexes
     */
    public void onUpdate(EventBean[] newData, EventBean[] oldData, NamedWindowRootView namedWindowRootView, NamedWindowIndexRepository indexRepository);

    /**
     * Handle iteration over revision event contents.
     * @param createWindowStmtHandle statement handle for safe iteration
     * @param parent the provider of data
     * @return collection to iterate
     */
    public Collection<EventBean> getSnapshot(EPStatementHandle createWindowStmtHandle, Viewable parent);

    /**
     * Called each time a data window posts a remove stream event, to indicate that a data window
     * remove an event as it expired according to a specified expiration policy.
     * @param oldData to remove
     * @param indexRepository the indexes to update
     */
    public void removeOldData(EventBean[] oldData, NamedWindowIndexRepository indexRepository);
}
