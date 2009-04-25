/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.event.xml;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.client.PropertyAccessException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOM getter for Map-property.
 */
public class DOMMapGetter implements EventPropertyGetter, DOMPropertyGetter
{
    private final String propertyMap;
    private final String mapKey;
    private final FragmentFactory fragmentFactory;

    /**
     * Ctor.
     * @param propertyName property name
     * @param mapKey key in map
     * @param fragmentFactory for creating fragments
     */
    public DOMMapGetter(String propertyName, String mapKey, FragmentFactory fragmentFactory)
    {
        this.propertyMap = propertyName;
        this.mapKey = mapKey;
        this.fragmentFactory = fragmentFactory;
    }

    public Node[] getValueAsNodeArray(Node node)
    {
        return null;
    }

    public Object getValueAsFragment(Node node)
    {
        if (fragmentFactory == null)
        {
            return null;
        }
        
        Node result = getValueAsNode(node);
        if (result == null)
        {
            return null;
        }
        return fragmentFactory.getEvent(result);
    }

    public Node getValueAsNode(Node node)
    {
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            Node childNode = list.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE)
            {
                continue;
            }
            if (!(childNode.getNodeName().equals(propertyMap)))
            {
                continue;
            }

            Node attribute = childNode.getAttributes().getNamedItem("id");
            if (attribute == null)
            {
                continue;
            }
            if (!(attribute.getTextContent().equals(mapKey)))
            {
                continue;
            }

            return childNode;
        }
        return null;
    }

    public Object get(EventBean eventBean) throws PropertyAccessException
    {
        Object result = eventBean.getUnderlying();
        if (!(result instanceof Node))
        {
            return null;
        }
        Node node = (Node) result;

        return getValueAsNode(node);
    }

    public boolean isExistsProperty(EventBean eventBean)
    {
        Object result = eventBean.getUnderlying();
        if (!(result instanceof Node))
        {
            return false;
        }
        Node node = (Node) result;

        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            Node childNode = list.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE)
            {
                continue;
            }

            String elementName = childNode.getLocalName();
            if (elementName == null)
            {
                elementName = childNode.getNodeName();
            }

            if (!(propertyMap.equals(elementName)))
            {
                continue;
            }

            Node attribute = childNode.getAttributes().getNamedItem(mapKey);
            if (attribute == null)
            {
                continue;
            }
            if (!(attribute.getTextContent().equals(mapKey)))
            {
                continue;
            }

            return true;
        }
        return false;
    }

    public Object getFragment(EventBean eventBean)
    {
        return null;
    }
}
