/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.event.bean;

import com.espertech.esper.client.ConfigurationEventTypeLegacy;
import com.espertech.esper.event.bean.InternalEventPropDescriptor;
import com.espertech.esper.event.bean.PropertyHelper;

import java.util.List;

/**
 * Implementation for a property list builder that considers JavaBean-style methods
 * as the exposed event properties, plus any explicitly configured props.
 */
public class PropertyListBuilderJavaBean implements PropertyListBuilder
{
    private ConfigurationEventTypeLegacy optionalLegacyConfig;

    /**
     * Ctor.
     * @param optionalLegacyConfig configures legacy type, or null information
     * has been supplied.
     */
    public PropertyListBuilderJavaBean(ConfigurationEventTypeLegacy optionalLegacyConfig)
    {
        this.optionalLegacyConfig = optionalLegacyConfig;
    }

    public List<InternalEventPropDescriptor> assessProperties(Class clazz)
    {
        List<InternalEventPropDescriptor> result = PropertyHelper.getProperties(clazz);
        if (optionalLegacyConfig != null)
        {
            PropertyListBuilderExplicit.getExplicitProperties(result, clazz, optionalLegacyConfig);
        }
        return result;
    }
}
