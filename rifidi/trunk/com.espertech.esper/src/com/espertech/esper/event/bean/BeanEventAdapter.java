/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.event.bean;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.ConfigurationEventTypeLegacy;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.event.EventTypeMetadata;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A fcatory for {@link BeanEventType} instances based on Java class information and using configured
 * settings for
 */
public class BeanEventAdapter implements BeanEventTypeFactory
{
    private final ConcurrentHashMap<Class, BeanEventType> typesPerJavaBean;
    private final Lock typesPerJavaBeanLock;
    private final EventAdapterService eventAdapterService;

    private Map<String, ConfigurationEventTypeLegacy> classToLegacyConfigs;
    private Configuration.PropertyResolutionStyle defaultPropertyResolutionStyle;

    /**
     * Ctor.
     * @param typesPerJavaBean shareable collection that this adapter writes to
     * for caching bean types per class
     * @param eventAdapterService factory for event beans and event types
     */
    public BeanEventAdapter(ConcurrentHashMap<Class, BeanEventType> typesPerJavaBean, EventAdapterService eventAdapterService)
    {
        this.typesPerJavaBean = typesPerJavaBean;
        typesPerJavaBeanLock = new ReentrantLock();
        classToLegacyConfigs = new HashMap<String, ConfigurationEventTypeLegacy>();
        this.defaultPropertyResolutionStyle = Configuration.PropertyResolutionStyle.getDefault();
        this.eventAdapterService = eventAdapterService;
    }

    /**
     * Set the additional mappings for legacy classes.
     * @param classToLegacyConfigs legacy class information
     */
    public void setClassToLegacyConfigs(Map<String, ConfigurationEventTypeLegacy> classToLegacyConfigs)
    {
        this.classToLegacyConfigs.putAll(classToLegacyConfigs);
    }

    /**
     * Sets the default property resolution style for Java class properties.
     * @param defaultPropertyResolutionStyle resolution style
     */
    public void setDefaultPropertyResolutionStyle(Configuration.PropertyResolutionStyle defaultPropertyResolutionStyle)
    {
        this.defaultPropertyResolutionStyle = defaultPropertyResolutionStyle;
    }

    /**
     * Gets the default property resolution style for Java class properties.
     * @return resolution style
     */
    public Configuration.PropertyResolutionStyle getDefaultPropertyResolutionStyle()
    {
        return defaultPropertyResolutionStyle;
    }

    /**
     * Creates a new EventType object for a java bean of the specified class if this is the first time
     * the class has been seen. Else uses a cached EventType instance, i.e. client classes do not need to cache.
     * @param clazz is the class of the Java bean.
     * @return EventType implementation for bean class
     */
    public final BeanEventType createBeanTypeDefaultName(Class clazz)
    {
        return createBeanType(clazz.getName(), clazz, false);
    }

    /**
     * Creates a new EventType object for a java bean of the specified class if this is the first time
     * the class has been seen. Else uses a cached EventType instance, i.e. client classes do not need to cache.
     * @param clazz is the class of the Java bean.
     * @return EventType implementation for bean class
     */
    public final BeanEventType createBeanType(String name, Class clazz, boolean isConfigured)
    {
        if (clazz == null)
        {
            throw new IllegalArgumentException("Null value passed as class");
        }

        BeanEventType eventType = null;

        // not created yet, thread-safe create
        typesPerJavaBeanLock.lock();
        try
        {
            eventType = typesPerJavaBean.get(clazz);
            if (eventType != null)
            {
                return eventType;
            }

            // Check if we have a legacy type definition for this class
            ConfigurationEventTypeLegacy legacyDef = classToLegacyConfigs.get(clazz.getName());

            EventTypeMetadata metadata = EventTypeMetadata.createBeanType(name, clazz, isConfigured);
            eventType = new BeanEventType(metadata, clazz, eventAdapterService, legacyDef);
            typesPerJavaBean.put(clazz, eventType);
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        finally
        {
            typesPerJavaBeanLock.unlock();
        }

        return eventType;
    }
}
