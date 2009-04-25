/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core;

import com.espertech.esper.client.EPStatement;

/**
 * Event indicating statement lifecycle management.
 */
public class StatementLifecycleEvent
{
    private EPStatement statement;
    private LifecycleEventType eventType;
    private Object[] params;

    /**
     * Event types.
     */
    public static enum LifecycleEventType {
        /**
         * Statement created.
         */
        CREATE,
        /**
         * Statement state change.
         */
        STATECHANGE,
        /**
         * listener added
         */
        LISTENER_ADD,
        /**
         * Listener removed.
         */
        LISTENER_REMOVE,
        /**
         * All listeners removed.
         */
        LISTENER_REMOVE_ALL
    }

    /**
     * Ctor.
     * @param statement the statement
     * @param eventType the tyoe if event
     * @param params event parameters
     */
    protected StatementLifecycleEvent(EPStatement statement, LifecycleEventType eventType, Object... params)
    {
        this.statement = statement;
        this.eventType = eventType;
        this.params = params;
    }

    /**
     * Returns the statement instance for the event.
     * @return statement
     */
    public EPStatement getStatement()
    {
        return statement;
    }

    /**
     * Returns the event type.
     * @return type of event
     */
    public LifecycleEventType getEventType() {
        return eventType;
    }

    /**
     * Returns event parameters.
     * @return params
     */
    public Object[] getParams() {
        return params;
    }
}
