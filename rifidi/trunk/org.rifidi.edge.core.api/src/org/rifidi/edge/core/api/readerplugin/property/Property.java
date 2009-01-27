package org.rifidi.edge.core.api.readerplugin.property;

import org.rifidi.edge.core.api.communication.Connection;
import org.rifidi.edge.core.api.messageQueue.MessageQueue;
import org.w3c.dom.Element;

public interface Property {

	public Element getProperty(Connection connection, MessageQueue errorQueue,
			Element propertyConfig);

	public Element setProperty(Connection connection, MessageQueue errorQueue,
			Element propertyConfig);

}
