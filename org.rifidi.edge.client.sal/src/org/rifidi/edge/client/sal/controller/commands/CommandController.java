package org.rifidi.edge.client.sal.controller.commands;

import java.util.Set;

import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;

/**
 * This is the interface for the Controller for the CommandConfiguration model
 * objects (i.e. RemoteCommandConfiguration, etc)
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public interface CommandController {

	/**
	 * Creates a command configuration with the given configuration type.
	 * 
	 * @param commandConfigType
	 *            The type of Command to create
	 */
	public void createCommand(String commandConfigType);

	/**
	 * Deletes the command configuration with the given ID.
	 * 
	 * @param commandConfigID
	 */
	public void deleteCommand(String commandConfigID);

	/**
	 * Returns the command configurations.
	 * 
	 * @return
	 */
	public Set<RemoteCommandConfiguration> getCommandConfigurations();

	/**
	 * Clears the uncommitted property changes.
	 * 
	 * @param commandID
	 *            the CommandConfigurationID
	 */
	void clearPropertyChanges(String commandID);

	/**
	 * Commits the property changes to the Command Configuration on the server
	 * 
	 * @param commandID
	 *            the CommandConfiguraitonID
	 */
	void synchPropertyChanges(String commandID);
}
