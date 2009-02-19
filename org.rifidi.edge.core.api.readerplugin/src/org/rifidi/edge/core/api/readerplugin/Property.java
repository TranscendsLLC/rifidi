package org.rifidi.edge.core.api.readerplugin;

import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.api.readerplugin.communication.Connection;
import org.rifidi.edge.core.api.readerplugin.messageQueue.EventQueue;

public interface Property {

	public CommandConfiguration getProperty(Connection connection, EventQueue errorQueue,
			CommandConfiguration propertyConfig);

	public CommandConfiguration setProperty(Connection connection, EventQueue errorQueue,
			CommandConfiguration propertyConfig);

}