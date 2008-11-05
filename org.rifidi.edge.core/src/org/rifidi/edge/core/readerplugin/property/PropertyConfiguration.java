package org.rifidi.edge.core.readerplugin.property;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.rifidi.edge.core.readerplugin.commands.CommandConfiguration;

/**
 * This is a value object that stores one or more CommandConfigurations that
 * represent the values of reader session properties. This is used to transmit
 * properties back and forth from the client to the edge server via RMI
 * 
 * @see CommandConfiguration
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class PropertyConfiguration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The set of properties in this PropertyConfiguration
	 */
	private Set<CommandConfiguration> properties;

	public PropertyConfiguration(Set<CommandConfiguration> properties) {
		this.properties = properties;
	}

	/**
	 * 
	 * @param propertyName
	 *            The name of the property to get
	 * @return A CommandConfiguration that contains a property value
	 */
	public CommandConfiguration getProperty(String propertyName) {
		for (CommandConfiguration c : properties) {
			if (c.getCommandName().equals(propertyName)) {
				return c;
			}
		}
		return null;
	}

	/**
	 * 
	 * @return A set of strings that are the property names
	 */
	public Set<String> getPropertyNames() {
		Set<String> names = new HashSet<String>();
		for (CommandConfiguration c : properties) {
			names.add(c.getCommandName());
		}
		return names;
	}
}
