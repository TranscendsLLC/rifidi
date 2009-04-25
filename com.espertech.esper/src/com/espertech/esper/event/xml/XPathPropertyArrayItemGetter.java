package com.espertech.esper.event.xml;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.client.PropertyAccessException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

/**
 * Getter for XPath explicit properties returning an element in an array. 
 */
public class XPathPropertyArrayItemGetter implements EventPropertyGetter
{
    private final EventPropertyGetter getter;
    private final int index;
    private final FragmentFactory fragmentFactory;

    /**
     * Ctor.
     * @param getter property getter returning the parent node
     * @param index to get item at
     * @param fragmentFactory for creating fragments, or null if not creating fragments
     */
    public XPathPropertyArrayItemGetter(EventPropertyGetter getter, int index, FragmentFactory fragmentFactory)
    {
        this.getter = getter;
        this.index = index;
        this.fragmentFactory = fragmentFactory;
    }

    public Object get(EventBean eventBean) throws PropertyAccessException
    {
        Object result = getter.get(eventBean);
        if (!(result instanceof NodeList))
        {
            return null;
        }

        NodeList nodeList = (NodeList) result;
        if (nodeList.getLength() <= index)
        {
            return null;
        }
        return nodeList.item(index);
    }

    public Object getFragment(EventBean eventBean) throws PropertyAccessException
    {
        if (fragmentFactory == null)
        {
            return null;
        }
        Node result = (Node) get(eventBean);
        if (result == null)
        {
            return null;
        }
        return fragmentFactory.getEvent(result);
    }

    public boolean isExistsProperty(EventBean eventBean)
    {
        return true; 
    }
}
