/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.event;

import com.espertech.esper.client.*;
import com.espertech.esper.collection.Pair;
import com.espertech.esper.event.map.MapEventType;

import java.util.*;

/**
 * An event type that adds zero or more fields to an existing event type.
 * <p>
 * The additional fields are represented as a Map. Any queries to event properties are first
 * held against the additional fields, and secondly are handed through to the underlying event.
 * <p>
 * If this event type is to add information to another wrapper event type (wrapper to wrapper), then it is the
 * responsibility of the creating logic to use the existing event type and add to it.
 * <p>
 * Uses a the map event type {@link com.espertech.esper.event.map.MapEventType} to represent the mapped properties. This is because the additional properties
 * can also be beans or complex types and the Map event type handles these nicely.
 */
public class WrapperEventType implements EventTypeSPI
{
    /**
     * event type metadata
     */
    protected final EventTypeMetadata metadata;

    /**
     * The underlying wrapped event type.
     */
    protected final EventType underlyingEventType;

    /**
     * The map event type that provides the additional properties.
     */
    protected final MapEventType underlyingMapType;

    private final String[] propertyNames;
    private final EventPropertyDescriptor[] propertyDesc;
    private final Map<String, EventPropertyDescriptor> propertyDescriptorMap;

    private final int hashCode;
    private final boolean isNoMapProperties;
    private final Map<String, EventPropertyGetter> propertyGetterCache;
    private final EventAdapterService eventAdapterService;

    /**
     * Ctor.
     * @param typeName is the event type name
     * @param eventType is the event type of the wrapped events
     * @param properties is the additional properties this wrapper adds
     * @param metadata event type metadata
     * @param eventAdapterService is the service for resolving unknown wrapped types
     */
    public WrapperEventType(EventTypeMetadata metadata, String typeName, EventType eventType, Map<String, Object> properties, EventAdapterService eventAdapterService)
	{
		checkForRepeatedPropertyNames(eventType, properties);

        this.metadata = metadata;
		this.underlyingEventType = eventType;
        EventTypeMetadata metadataMapType = EventTypeMetadata.createAnonymous(typeName);
        this.underlyingMapType = new MapEventType(metadataMapType, typeName, eventAdapterService, properties, null, null);
        this.hashCode = underlyingMapType.hashCode() ^ underlyingEventType.hashCode();
        this.isNoMapProperties = properties.isEmpty();
        this.eventAdapterService = eventAdapterService;
        propertyGetterCache = new HashMap<String, EventPropertyGetter>();

        List<String> propertyNames = new ArrayList<String>();
        propertyNames.addAll(Arrays.asList(underlyingEventType.getPropertyNames()));
        propertyNames.addAll(Arrays.asList(underlyingMapType.getPropertyNames()));
		this.propertyNames = propertyNames.toArray(new String[propertyNames.size()]);

        List<EventPropertyDescriptor> propertyDesc = new ArrayList<EventPropertyDescriptor>();
        propertyDescriptorMap = new HashMap<String, EventPropertyDescriptor>();
		for(EventPropertyDescriptor eventProperty : underlyingEventType.getPropertyDescriptors())
		{
			propertyDesc.add(eventProperty);
            propertyDescriptorMap.put(eventProperty.getPropertyName(), eventProperty);
		}
		for(EventPropertyDescriptor mapProperty : underlyingMapType.getPropertyDescriptors())
		{
			propertyDesc.add(mapProperty);
            propertyDescriptorMap.put(mapProperty.getPropertyName(), mapProperty);
		}
		this.propertyDesc = propertyDesc.toArray(new EventPropertyDescriptor[propertyDesc.size()]);
    }

	public Iterator<EventType> getDeepSuperTypes()
	{
		return null;
	}

    public String getName()
    {
        return metadata.getPublicName();
    }

    public EventPropertyGetter getGetter(final String property)
	{
        EventPropertyGetter cachedGetter = propertyGetterCache.get(property);
        if (cachedGetter != null)
        {
            return cachedGetter;
        }

		if(underlyingEventType.isProperty(property))
		{
            EventPropertyGetter getter = new EventPropertyGetter()
            {
                public Object get(EventBean event)
                {
                    if(!(event instanceof DecoratingEventBean))
                    {
                        throw new PropertyAccessException("Mismatched property getter to EventBean type");
                    }
                    DecoratingEventBean wrapperEvent = (DecoratingEventBean) event;
                    EventBean wrappedEvent = wrapperEvent.getUnderlyingEvent();
                    if (wrappedEvent == null)
                    {
                        return null;
                    }

                    EventPropertyGetter underlyingGetter = underlyingEventType.getGetter(property);
                    return underlyingGetter.get(wrappedEvent);
                }

                public boolean isExistsProperty(EventBean eventBean)
                {
                    return true; // Property exists as the property is not dynamic (unchecked)
                }

                public Object getFragment(EventBean event)
                {
                    if(!(event instanceof DecoratingEventBean))
                    {
                        throw new PropertyAccessException("Mismatched property getter to EventBean type");
                    }
                    DecoratingEventBean wrapperEvent = (DecoratingEventBean) event;
                    EventBean wrappedEvent = wrapperEvent.getUnderlyingEvent();
                    if (wrappedEvent == null)
                    {
                        return null;
                    }

                    EventPropertyGetter underlyingGetter = underlyingEventType.getGetter(property);
                    return underlyingGetter.getFragment(wrappedEvent);
                }
            };
            propertyGetterCache.put(property, getter);
            return getter;
        }
		else if (underlyingMapType.isProperty(property))
		{
            final EventPropertyGetter mapGetter = underlyingMapType.getGetter(property);
            EventPropertyGetter getter = new EventPropertyGetter()
            {
                public Object get(EventBean event)
                {
                    if(!(event instanceof DecoratingEventBean))
                    {
                        throw new PropertyAccessException("Mismatched property getter to EventBean type");
                    }
                    DecoratingEventBean wrapperEvent = (DecoratingEventBean) event;
                    Map map = wrapperEvent.getDecoratingProperties();
                    return mapGetter.get(eventAdapterService.adaptorForTypedMap(map, underlyingMapType));
                }

                public boolean isExistsProperty(EventBean eventBean)
                {
                    return true; // Property exists as the property is not dynamic (unchecked)
                }

                public Object getFragment(EventBean event)
                {
                    if(!(event instanceof DecoratingEventBean))
                    {
                        throw new PropertyAccessException("Mismatched property getter to EventBean type");
                    }
                    DecoratingEventBean wrapperEvent = (DecoratingEventBean) event;
                    Map map = wrapperEvent.getDecoratingProperties();
                    return mapGetter.getFragment(eventAdapterService.adaptorForTypedMap(map, underlyingMapType));
                }
            };
            propertyGetterCache.put(property, getter);
            return getter;
        }
		else
		{
			return null;
		}
	}

	public String[] getPropertyNames()
	{
		return propertyNames;
	}

	public Class getPropertyType(String property)
	{
		if(underlyingEventType.isProperty(property))
		{
			return underlyingEventType.getPropertyType(property);
		}
		else if (underlyingMapType.isProperty(property))
		{
			return underlyingMapType.getPropertyType(property);
		}
		else
		{
			return null;
		}
	}

	public EventType[] getSuperTypes()
	{
		return null;
	}

	public Class getUnderlyingType()
	{
        // If the additional properties are empty, such as when wrapping a native event by means of wildcard-only select
        // then the underlying type is simply the wrapped type.
        if (isNoMapProperties)
        {
            return underlyingEventType.getUnderlyingType();
        }
        else
        {
            return Pair.class;
        }
    }

    /**
     * Returns the wrapped event type.
     * @return wrapped type
     */
    public EventType getUnderlyingEventType()
    {
        return underlyingEventType;
    }

    /**
     * Returns the map type.
     * @return map type providing additional properties.
     */
    public MapEventType getUnderlyingMapType()
    {
        return underlyingMapType;
    }

    public boolean isProperty(String property)
	{
		return underlyingEventType.isProperty(property) ||
			underlyingMapType.isProperty(property);
	}

	public String toString()
	{
		return "WrapperEventType " +
		"underlyingEventType=" + underlyingEventType + " " +
		"underlyingMapType=" + underlyingMapType;
	}

    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof WrapperEventType))
        {
            return false;
        }

        WrapperEventType other = (WrapperEventType) obj;

        if ((other.underlyingEventType.equals(this.underlyingEventType)) &&
            (other.underlyingMapType.equals(this.underlyingMapType)))
        {
            return true;
        }
        return false;
    }

    public int hashCode()
    {
        return hashCode;
    }

    public EventTypeMetadata getMetadata()
    {
        return metadata;
    }

    public EventPropertyDescriptor[] getPropertyDescriptors()
    {
        return propertyDesc;
    }

    public EventPropertyDescriptor getPropertyDescriptor(String propertyName)
    {
        return propertyDescriptorMap.get(propertyName);
    }

    public FragmentEventType getFragmentType(String property)
    {
        FragmentEventType fragment = underlyingEventType.getFragmentType(property);
        if (fragment != null)
        {
            return fragment;
        }
        return underlyingMapType.getFragmentType(property);
    }

    private void checkForRepeatedPropertyNames(EventType eventType, Map<String, Object> properties)
	{
		for(String property : eventType.getPropertyNames())
		{
			if(properties.keySet().contains(property))
			{
				throw new EPException("Property " + property + " occurs in both the underlying event and in the additional properties");
			}
		}
	}
}
