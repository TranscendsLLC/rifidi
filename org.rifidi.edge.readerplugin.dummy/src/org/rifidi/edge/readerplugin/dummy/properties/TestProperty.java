/**
 * 
 */
package org.rifidi.edge.readerplugin.dummy.properties;

import java.util.HashSet;
import java.util.Set;

import org.rifidi.dynamicswtforms.xml.annotaions.Form;
import org.rifidi.dynamicswtforms.xml.annotaions.FormElement;
import org.rifidi.dynamicswtforms.xml.constants.FormElementType;
import org.rifidi.edge.core.api.readerplugin.Property;
import org.rifidi.edge.core.api.readerplugin.commands.CommandArgument;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.api.readerplugin.communication.Connection;
import org.rifidi.edge.core.api.readerplugin.messageQueue.MessageQueue;

/**
 * This is just a dummy property to make sure that properties are working
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
@Form(name = "TestProperty", formElements = { @FormElement(elementName = "prop", displayName = "Test Property", type = FormElementType.STRING, defaultValue = "default value") })
public class TestProperty implements Property {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.api.readerplugin.property.api.Property#getProperty
	 * (org.rifidi.edge.core.api.communication.Connection,
	 * org.rifidi.edge.core.api.messageQueue.MessageQueue,
	 * org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration)
	 */
	@Override
	public CommandConfiguration getProperty(Connection connection,
			MessageQueue errorQueue, CommandConfiguration propertyConfig) {
		Set<CommandArgument> returnArg = new HashSet<CommandArgument>();
		returnArg.add(new CommandArgument("prop", "default value", false));
		return new CommandConfiguration("testproperty", returnArg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.api.readerplugin.property.api.Property#setProperty
	 * (org.rifidi.edge.core.api.communication.Connection,
	 * org.rifidi.edge.core.api.messageQueue.MessageQueue,
	 * org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration)
	 */
	@Override
	public CommandConfiguration setProperty(Connection connection,
			MessageQueue errorQueue, CommandConfiguration propertyConfig) {
		return getProperty(connection, errorQueue, propertyConfig);
	}

}
