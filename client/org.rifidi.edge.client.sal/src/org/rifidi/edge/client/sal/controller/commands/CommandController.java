/*
 * CommandController.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
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
