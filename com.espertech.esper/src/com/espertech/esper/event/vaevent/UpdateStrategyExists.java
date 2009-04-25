/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.event.vaevent;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.util.NullableObject;

/**
 * Strategy for merging update properties using only existing property's values.
 */
public class UpdateStrategyExists extends UpdateStrategyBase
{
    /**
     * Ctor.
     * @param spec the specification
     */
    public UpdateStrategyExists(RevisionSpec spec)
    {
        super(spec);
    }

    public void handleUpdate(boolean isBaseEventType,
                              RevisionStateMerge revisionState,
                              RevisionEventBeanMerge revisionEvent,
                              RevisionTypeDesc typesDesc)
    {
        EventBean underlyingEvent = revisionEvent.getUnderlyingFullOrDelta();

        NullableObject<Object>[] changeSetValues = revisionState.getOverlays();
        if (changeSetValues == null)    // optimization - the full event sets it to null, deltas all get a new one
        {
            changeSetValues = new NullableObject[spec.getChangesetPropertyNames().length];
        }
        else
        {
            changeSetValues = arrayCopy(changeSetValues);   // preserve the last revisions
        }

        // apply all properties of the delta event
        int[] indexes = typesDesc.getChangesetPropertyIndex();
        EventPropertyGetter[] getters = typesDesc.getChangesetPropertyGetters();
        for (int i = 0; i < indexes.length; i++)
        {
            int index = indexes[i];

            if (!getters[i].isExistsProperty(underlyingEvent))
            {
                continue;
            }

            Object value = getters[i].get(underlyingEvent);
            changeSetValues[index] = new NullableObject<Object>(value);
        }

        revisionState.setOverlays(changeSetValues);
    }
}
