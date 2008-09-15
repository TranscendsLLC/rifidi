package org.rifidi.edge.core.readerplugin.property;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.w3c.dom.Element;

public interface Property {

	public Element getProperty(Connection connection, MessageQueue errorQueue,
			Element propertyConfig);

	public Element setProperty(Connection connection, MessageQueue errorQueue,
			Element propertyConfig);

}
