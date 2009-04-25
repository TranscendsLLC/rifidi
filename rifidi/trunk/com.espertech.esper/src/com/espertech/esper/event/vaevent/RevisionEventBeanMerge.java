/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.event.vaevent;

import com.espertech.esper.collection.MultiKeyUntyped;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.PropertyAccessException;
import com.espertech.esper.util.NullableObject;

/**
 * Merge-event for event revisions.
 */
public class RevisionEventBeanMerge implements EventBean
{
    private final RevisionEventType revisionEventType;
    private final EventBean underlyingFullOrDelta;

    private NullableObject<Object>[] overlay;
    private EventBean lastBaseEvent;
    private MultiKeyUntyped key;
    private boolean latest;

    /**
     * Ctor.
     * @param revisionEventType type
     * @param underlyingFull event wrapped
     */
    public RevisionEventBeanMerge(RevisionEventType revisionEventType, EventBean underlyingFull)
    {
        this.revisionEventType = revisionEventType;
        this.underlyingFullOrDelta = underlyingFull;
    }

    /**
     * Sets merged values.
     * @param overlay merged values
     */
    public void setOverlay(NullableObject<Object>[] overlay)
    {
        this.overlay = overlay;
    }

    /**
     * Returns flag indicated latest or not.
     * @return latest flag
     */
    public boolean isLatest()
    {
        return latest;
    }

    /**
     * Sets flag indicating latest or not.
     * @param latest flag
     */
    public void setLatest(boolean latest)
    {
        this.latest = latest;
    }

    /**
     * Returns the key.
     * @return key
     */
    public MultiKeyUntyped getKey()
    {
        return key;
    }

    /**
     * Sets the key.
     * @param key to set
     */
    public void setKey(MultiKeyUntyped key)
    {
        this.key = key;
    }

    /**
     * Returns overlay values.
     * @return overlay
     */
    public Object[] getOverlay()
    {
        return overlay;
    }

    /**
     * Returns last base event.
     * @return base event
     */
    public EventBean getLastBaseEvent()
    {
        return lastBaseEvent;
    }

    /**
     * Sets last base event.
     * @param lastBaseEvent to set
     */
    public void setLastBaseEvent(EventBean lastBaseEvent)
    {
        this.lastBaseEvent = lastBaseEvent;
    }

    public EventType getEventType()
    {
        return revisionEventType;
    }

    public Object get(String property) throws PropertyAccessException
    {
        EventPropertyGetter getter = revisionEventType.getGetter(property);
        if (getter == null)
        {
            return null;
        }
        return getter.get(this);
    }

    public Object getUnderlying()
    {
        return RevisionEventBeanMerge.class;
    }

    /**
     * Returns wrapped event
     * @return event
     */
    public EventBean getUnderlyingFullOrDelta()
    {
        return underlyingFullOrDelta;
    }

    /**
     * Returns a value from the key.
     * @param index within key
     * @return value
     */
    public Object getKeyValue(int index)
    {
        return key.getKeys()[index];
    }

    /**
     * Returns base event value.
     * @param params supplies getter
     * @return value
     */
    public Object getBaseEventValue(RevisionGetterParameters params)
    {
        return params.getBaseGetter().get(lastBaseEvent);
    }

    /**
     * Returns a versioned value.
     * @param params getter and indexes
     * @return value
     */
    public Object getVersionedValue(RevisionGetterParameters params)
    {
        int propertyNumber = params.getPropertyNumber();

        if (overlay != null)
        {
            NullableObject<Object> value = overlay[propertyNumber];
            if (value != null)
            {
                return value.getObject();
            }
        }

        EventPropertyGetter getter = params.getBaseGetter();
        if (getter == null)
        {
            return null;  // The property was added by a delta event and only exists on a delta
        }
        return getter.get(lastBaseEvent);
    }

    public Object getFragment(String propertyExpression)
    {
        EventPropertyGetter getter = revisionEventType.getGetter(propertyExpression);
        if (getter == null)
        {
            return null;
        }
        return getter.getFragment(this);
    }
}
