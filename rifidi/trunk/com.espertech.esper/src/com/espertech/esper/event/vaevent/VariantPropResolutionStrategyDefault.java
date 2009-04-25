/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.event.vaevent;

import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.util.SimpleTypeCaster;
import com.espertech.esper.util.SimpleTypeCasterFactory;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.client.PropertyAccessException;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A property resolution strategy that allows only the preconfigured types, wherein all properties
 * that are common (name and type) to all properties are considered.
 */
public class VariantPropResolutionStrategyDefault implements VariantPropResolutionStrategy
{
    private int currentPropertyNumber;
    private VariantPropertyGetterCache propertyGetterCache;

    /**
     * Ctor.
     * @param variantSpec specified the preconfigured types
     */
    public VariantPropResolutionStrategyDefault(VariantSpec variantSpec)
    {
        propertyGetterCache = new VariantPropertyGetterCache(variantSpec.getEventTypes());
    }

    public VariantPropertyDesc resolveProperty(String propertyName, EventType[] variants)
    {
        boolean existsInAll = true;
        Class commonType = null;
        boolean mustCoerce = false;
        for (int i = 0; i < variants.length; i++)
        {
            Class type = JavaClassHelper.getBoxedType(variants[i].getPropertyType(propertyName));
            if (type == null)
            {
                existsInAll = false;
                continue;
            }

            if (commonType == null)
            {
                commonType = type;
                continue;
            }

            // compare types
            if (type.equals(commonType))
            {
                continue;
            }

            // coercion
            if (JavaClassHelper.isNumeric(type))
            {
                if (JavaClassHelper.canCoerce(type, commonType))
                {
                    mustCoerce = true;
                    continue;
                }
                if (JavaClassHelper.canCoerce(commonType, type))
                {
                    mustCoerce = true;
                    commonType = type;
                }
            }
            else if (commonType == Object.class)
            {
                continue;
            }
            // common interface or base class
            else if (!JavaClassHelper.isJavaBuiltinDataType(type))
            {
                Set<Class> supersForType = new LinkedHashSet<Class>();
                JavaClassHelper.getSuper(type, supersForType);
                supersForType.remove(Object.class);

                if (supersForType.contains(commonType))
                {
                    continue;   // type implements or extends common type
                }
                if (JavaClassHelper.isSubclassOrImplementsInterface(commonType, type))
                {
                    commonType = type;  // common type implements type
                    continue;
                }

                // find common interface or type both implement
                Set<Class> supersForCommonType = new LinkedHashSet<Class>();
                JavaClassHelper.getSuper(commonType, supersForCommonType);
                supersForCommonType.remove(Object.class);

                // Take common classes first, ignoring interfaces
                boolean found = false;
                for (Class superClassType : supersForType)
                {
                    if (!superClassType.isInterface() && (supersForCommonType.contains(superClassType)))
                    {
                        commonType = superClassType;
                        found = true;
                        break;
                    }
                }
                if (found)
                {
                    continue;
                }
                // Take common interfaces
                for (Class superClassType : supersForType)
                {
                    if (superClassType.isInterface() && (supersForCommonType.contains(superClassType)))
                    {
                        commonType = superClassType;
                        found = true;
                        break;
                    }
                }
            }

            commonType = Object.class;
        }

        if (!existsInAll)
        {
            return null;
        }

        if (commonType == null)
        {
            return null;
        }

        // property numbers should start at zero since the serve as array index
        final int assignedPropertyNumber = currentPropertyNumber;
        currentPropertyNumber++;
        propertyGetterCache.addGetters(assignedPropertyNumber, propertyName);

        EventPropertyGetter getter;
        if (mustCoerce)
        {
            final SimpleTypeCaster caster = SimpleTypeCasterFactory.getCaster(null, commonType);
            getter = new EventPropertyGetter()
            {
                public Object get(EventBean eventBean) throws PropertyAccessException
                {
                    VariantEvent variant = (VariantEvent) eventBean;
                    EventPropertyGetter getter = propertyGetterCache.getGetter(assignedPropertyNumber, variant.getUnderlyingEventBean().getEventType());
                    if (getter == null)
                    {
                        return null;
                    }
                    Object value = getter.get(variant.getUnderlyingEventBean());
                    if (value == null)
                    {
                        return value;
                    }
                    return caster.cast(value);
                }

                public boolean isExistsProperty(EventBean eventBean)
                {
                    VariantEvent variant = (VariantEvent) eventBean;
                    EventPropertyGetter getter = propertyGetterCache.getGetter(assignedPropertyNumber, variant.getUnderlyingEventBean().getEventType());
                    if (getter == null)
                    {
                        return false;
                    }
                    return getter.isExistsProperty(variant.getUnderlyingEventBean());
                }

                public Object getFragment(EventBean eventBean)
                {
                    return null;
                }
            };
        }
        else
        {
            getter = new EventPropertyGetter()
            {
                public Object get(EventBean eventBean) throws PropertyAccessException
                {
                    VariantEvent variant = (VariantEvent) eventBean;
                    EventPropertyGetter getter = propertyGetterCache.getGetter(assignedPropertyNumber, variant.getUnderlyingEventBean().getEventType());
                    if (getter == null)
                    {
                        return null;
                    }
                    return getter.get(variant.getUnderlyingEventBean());
                }

                public boolean isExistsProperty(EventBean eventBean)
                {
                    VariantEvent variant = (VariantEvent) eventBean;
                    EventPropertyGetter getter = propertyGetterCache.getGetter(assignedPropertyNumber, variant.getUnderlyingEventBean().getEventType());
                    if (getter == null)
                    {
                        return false;
                    }
                    return getter.isExistsProperty(variant.getUnderlyingEventBean());
                }

                public Object getFragment(EventBean eventBean)
                {
                    return null;
                }
            };
        }

        return new VariantPropertyDesc(commonType, getter, true);
    }
}
