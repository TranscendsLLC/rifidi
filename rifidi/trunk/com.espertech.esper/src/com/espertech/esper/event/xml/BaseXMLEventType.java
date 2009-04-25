/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.event.xml;

import com.espertech.esper.client.*;
import com.espertech.esper.event.BaseConfigurableEventType;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.event.EventTypeMetadata;
import com.espertech.esper.event.ExplicitPropertyDescriptor;
import com.espertech.esper.util.ClassInstantiationException;
import com.espertech.esper.util.JavaClassHelper;
import org.w3c.dom.Node;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.xpath.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Base class for XML event types.
 */
public abstract class BaseXMLEventType extends BaseConfigurableEventType {

    private static final Log log = LogFactory.getLog(BaseXMLEventType.class);

    private final XPathFactory xPathFactory;
    private final String rootElementName;
    private final ConfigurationEventTypeXMLDOM configurationEventTypeXMLDOM;

    /**
     * XPath namespace context.
     */
    protected XPathNamespaceContext namespaceContext;

    /**
     * Ctor.
     * @param configurationEventTypeXMLDOM is the XML DOM configuration such as root element and schema names
     * @param metadata event type metadata
     * @param eventAdapterService for registration and lookup of types
     */
    public BaseXMLEventType(EventTypeMetadata metadata, ConfigurationEventTypeXMLDOM configurationEventTypeXMLDOM, EventAdapterService eventAdapterService)
    {
        super(eventAdapterService, metadata, Node.class);
        this.rootElementName = configurationEventTypeXMLDOM.getRootElementName();
        this.configurationEventTypeXMLDOM = configurationEventTypeXMLDOM;
        xPathFactory = XPathFactory.newInstance();

        if (configurationEventTypeXMLDOM.getXPathFunctionResolver() != null)
        {
            try
            {
                XPathFunctionResolver fresolver = (XPathFunctionResolver) JavaClassHelper.instantiate(XPathFunctionResolver.class, configurationEventTypeXMLDOM.getXPathFunctionResolver());
                xPathFactory.setXPathFunctionResolver(fresolver);
            }
            catch (ClassInstantiationException ex)
            {
                throw new ConfigurationException("Error configuring XPath function resolver for XML type '" + configurationEventTypeXMLDOM.getRootElementName() + "' : " + ex.getMessage(), ex);
            }
        }

        if (configurationEventTypeXMLDOM.getXPathVariableResolver() != null)
        {
            try
            {
                XPathVariableResolver vresolver = (XPathVariableResolver) JavaClassHelper.instantiate(XPathVariableResolver.class, configurationEventTypeXMLDOM.getXPathVariableResolver());
                xPathFactory.setXPathVariableResolver(vresolver);
            }
            catch (ClassInstantiationException ex)
            {
                throw new ConfigurationException("Error configuring XPath variable resolver for XML type '" + configurationEventTypeXMLDOM.getRootElementName() + "' : " + ex.getMessage(), ex);
            }
        }
    }

    /**
     * Returns the name of the root element.
     * @return root element name
     */
    public String getRootElementName()
    {
        return rootElementName;
    }

    /**
     * Sets the namespace context for use in XPath expression resolution.
     * @param namespaceContext for XPath expressions
     */
    protected void setNamespaceContext(XPathNamespaceContext namespaceContext)
    {
        this.namespaceContext = namespaceContext;
    }

    /**
     * Set the preconfigured event properties resolved by XPath expression.
     * @param explicitXPathProperties are preconfigured event properties
     * @param additionalSchemaProperties the explicit properties
     */
    protected void initialize(Collection<ConfigurationEventTypeXMLDOM.XPathPropertyDesc> explicitXPathProperties,
                              List<ExplicitPropertyDescriptor> additionalSchemaProperties)
    {
        List<ExplicitPropertyDescriptor> namedProperties = new ArrayList<ExplicitPropertyDescriptor>(additionalSchemaProperties);

        String xpathExpression = null;
        try {

            for (ConfigurationEventTypeXMLDOM.XPathPropertyDesc property : explicitXPathProperties)
            {
                XPath xPath = xPathFactory.newXPath();
                if (namespaceContext != null)
                {
                    xPath.setNamespaceContext(namespaceContext);
                }

                xpathExpression = property.getXpath();
                if (log.isInfoEnabled())
                {
                    log.info("Compiling XPath expression for property '" + property.getName() + "' as '" + xpathExpression + "'");
                }
                XPathExpression expression = xPath.compile(xpathExpression);

                FragmentFactoryXPathPredefinedGetter fragmentFactory = null;
                boolean isFragment = false;
                if (property.getOptionaleventTypeName() != null)
                {
                    fragmentFactory = new FragmentFactoryXPathPredefinedGetter(this.getEventAdapterService(), property.getOptionaleventTypeName(), property.getName());
                    isFragment = true;
                }
                boolean isArray = false;
                if (property.getType().equals(XPathConstants.NODESET))
                {
                    isArray = true;
                }

                EventPropertyGetter getter = new XPathPropertyGetter(property.getName(), xpathExpression, expression, property.getType(), property.getOptionalCastToType(), fragmentFactory);
                Class returnType = SchemaUtil.toReturnType(property.getType(), property.getOptionalCastToType());
                
                EventPropertyDescriptor desc = new EventPropertyDescriptor(property.getName(), returnType, false,false,isArray,false,isFragment);
                ExplicitPropertyDescriptor explicit = new ExplicitPropertyDescriptor(desc, getter, isArray, property.getOptionaleventTypeName());
                namedProperties.add(explicit);
            }
        }
        catch (XPathExpressionException ex)
        {
            throw new EPException("XPath expression could not be compiled for expression '" + xpathExpression + '\'', ex);
        }

        super.initialize(namedProperties);
    }

    /**
     * Returns the XPath factory used.
     * @return XPath factory
     */
    protected XPathFactory getXPathFactory() {
        return xPathFactory;
    }

    public EventType[] getSuperTypes() {
        return null;
    }

    public Iterator<EventType> getDeepSuperTypes()
    {
        return null;
    }

    /**
     * Returns the configuration XML for the XML type.
     * @return config XML
     */
    public ConfigurationEventTypeXMLDOM getConfigurationEventTypeXMLDOM()
    {
        return configurationEventTypeXMLDOM;
    }

    public boolean equals(Object otherObj)
    {
        if (!(otherObj instanceof BaseXMLEventType))
        {
            return false;
        }
        BaseXMLEventType other = (BaseXMLEventType) otherObj;
        return (configurationEventTypeXMLDOM.equals(other.configurationEventTypeXMLDOM));
    }

    public int hashCode()
    {
        return configurationEventTypeXMLDOM.hashCode();
    }
}
