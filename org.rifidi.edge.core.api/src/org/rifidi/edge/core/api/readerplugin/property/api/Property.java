package org.rifidi.edge.core.api.readerplugin.property.api;

import org.rifidi.edge.core.api.communication.Connection;
import org.rifidi.edge.core.api.messageQueue.MessageQueue;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;

public interface Property {

	public CommandConfiguration getProperty(Connection connection, MessageQueue errorQueue,
			CommandConfiguration propertyConfig);

	public CommandConfiguration setProperty(Connection connection, MessageQueue errorQueue,
			CommandConfiguration propertyConfig);

}
