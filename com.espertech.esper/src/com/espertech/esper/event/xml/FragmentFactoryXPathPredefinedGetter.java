package com.espertech.esper.event.xml;

import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.EventBean;
import org.w3c.dom.Node;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Fragment factory for use with XPath explicit properties.
 */
public class FragmentFactoryXPathPredefinedGetter implements FragmentFactory
{
    private static final Log log = LogFactory.getLog(FragmentFactoryXPathPredefinedGetter.class);

    private final EventAdapterService eventAdapterService;
    private final String eventTypeName;
    private final String propertyName;

    private volatile EventType eventType;

    /**
     * Ctor.
     * @param eventAdapterService for event type lookup
     * @param eventTypeName name to look up
     * @param propertyName property
     */
    public FragmentFactoryXPathPredefinedGetter(EventAdapterService eventAdapterService, String eventTypeName, String propertyName)
    {
        this.eventAdapterService = eventAdapterService;
        this.eventTypeName = eventTypeName;
        this.propertyName = propertyName;
    }

    public EventBean getEvent(Node result)
    {
        if (eventType == null)
        {
            EventType candidateEventType = eventAdapterService.getExistsTypeByName(eventTypeName);
            if (candidateEventType == null)
            {
                log.warn("Event type by name '" + eventTypeName + "' was not found for property '" + propertyName + "'");
                return null;
            }
            if (!(candidateEventType instanceof BaseXMLEventType))
            {
                log.warn("Event type by name '" + eventTypeName + "' is not an XML event type for property '" + propertyName + "'");
                return null;
            }
            eventType = candidateEventType;
        }

        return eventAdapterService.adapterForTypedDOM(result, eventType);
    }
}
